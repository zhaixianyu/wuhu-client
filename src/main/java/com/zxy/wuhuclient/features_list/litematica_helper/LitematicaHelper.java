package com.zxy.wuhuclient.features_list.litematica_helper;

import com.zxy.wuhuclient.Utils.HighlightBlockRenderer;
import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.mixin.masa.litematicahelper.MaterialListHudRendererAccessor;
import fi.dy.masa.itemscroller.util.AccessorUtils;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListUtils;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

//#if MC > 11904
import red.jackf.chesttracker.api.memory.CommonKeys;
import red.jackf.chesttracker.api.memory.Memory;
import red.jackf.chesttracker.impl.memory.MemoryBankAccessImpl;
import red.jackf.chesttracker.impl.memory.MemoryBankImpl;
import red.jackf.whereisit.api.SearchRequest;
import red.jackf.whereisit.client.api.events.SearchRequestPopulator;
//#else
//$$ import red.jackf.chesttracker.memory.Memory;
//$$ import red.jackf.chesttracker.memory.MemoryDatabase;
//#endif

import java.util.*;
import java.util.stream.Collectors;

import static com.zxy.wuhuclient.WuHuClientMod.client;
import static com.zxy.wuhuclient.config.Configs.LITEMATICA_HELPER_TIPS;

public class LitematicaHelper {
    //投影施工助手
    //开启后选区预施工范围 可以以投影为目标
    //统计材料 读取箱子追踪库存
    //高亮箱子
    //点击高亮的箱子后拿出对应物品


    //待施工
    //按住shift点击箱子放回选区内已完成的多余物品
    //
    //
    public String litematicaHelper = "litematicaHelper";
    public static LitematicaHelper instance = new LitematicaHelper();
    public LitematicaHelper(){
        HighlightBlockRenderer.createHighlightBlockList(litematicaHelper, Configs.LITEMATICA_HELPER_COLOR);
    }
    public Map<ItemStack,Map<BlockPos,Integer>> inventoryList = new HashMap<>();

    public static void process(ScreenHandler container) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        HandledScreen<?> containerScreen;
        if(client.currentScreen instanceof HandledScreen){
            containerScreen = (HandledScreen<?>)client.currentScreen;
        }else return;
        List<Slot> playerInvSlots = container.slots.stream().filter(slot -> slot.inventory instanceof PlayerInventory).collect(Collectors.toList());
        List<Slot> containerInvSlots = container.slots.stream().filter(slot -> areSlotsInSameInventory(slot, container.slots.get(0),false)).collect(Collectors.toList());

        MaterialListBase materialList = DataManager.getMaterialList();
        if (materialList != null) {
            MaterialListHudRendererAccessor hudRendererAccessor = (MaterialListHudRendererAccessor) materialList.getHudRenderer();
            String guiTitle = containerScreen.getTitle().getString();

            // refresh before operation starts to make sure it's up-to-date
            MaterialListUtils.updateAvailableCounts(materialList.getMaterialsAll(), player);
            List<MaterialListEntry> missingOnly = materialList.getMaterialsMissingOnly(true);

            boolean takenSomething = false;
            for (MaterialListEntry entry : missingOnly) {
                int missing = entry.getCountMissing() * materialList.getMultiplier() - entry.getCountAvailable();
                ItemStack stack = entry.getStack();
                if (missing <= 0) {
                    continue;
                }
                int totalTaken = 0;
                for (Slot slot : containerInvSlots) {
                    if (InventoryUtils.areStacksEqual(stack, slot.getStack())) {
                        int stackAmount = slot.getStack().getCount();
                        moveToPlayerInventory(containerScreen, playerInvSlots, slot, Math.min(missing, stackAmount));
                        int moved = stackAmount - slot.getStack().getCount();
                        missing -= moved;
                        totalTaken += moved;
                        if (moved == 0) {
                            break;
                        }
                    }
                }
                if (totalTaken > 0) {

                    takenSomething = true;
                    log(Message.MessageType.INFO, "从%1$s中收集了:", guiTitle);

                    String missingColor = missing == 0 ? GuiBase.TXT_GREEN : GuiBase.TXT_GOLD;
                    String stackName = stack.getRarity().
                            //#if MC > 12004
                            getFormatting()
                            //#else
                            //$$ formatting
                            //#endif
                            + stack.getName().getString() + GuiBase.TXT_RST;

                    log(
                            Message.MessageType.INFO,
                            "- %1$s 个%2$s，仍需 %3$s",
                            GuiBase.TXT_GOLD + totalTaken + GuiBase.TXT_RST,
                            stackName,
                            missingColor + hudRendererAccessor.invokeGetFormattedCountString(missing, stack.getMaxCount()) + GuiBase.TXT_RST
                    );

                }
            }
            if (!takenSomething) log(Message.MessageType.INFO, "未在%1$s中收集任何材料列表中需要的物品", guiTitle);

            // refresh after operation ends
            hudRendererAccessor.setLastUpdateTime(-1);
        } else {
            log(Message.MessageType.WARNING, "没有生效的材料列表");
        }
        player.closeHandledScreen();
    }

    private static void moveToPlayerInventory(HandledScreen<?> containerScreen, List<Slot> playerInvSlots, Slot fromSlot, int amount) {
        ItemStack stack = fromSlot.getStack().copy();
        if (amount == stack.getCount()) {
            InventoryUtils.shiftClickSlot(containerScreen, fromSlot.id);
            return;
        } else if (amount > stack.getCount()) {
            return;
        }
        // ensure amount <= stack.getCount()

        InventoryUtils.leftClickSlot(containerScreen, fromSlot.id);
        // reversed iterating to match vanilla shift-click item putting order
        for (int idx = playerInvSlots.size() - 1; idx >= 0; idx--) {
            Slot slot = playerInvSlots.get(idx);
            int clickAmount = 0;
            if (slot.hasStack() && InventoryUtils.areStacksEqual(slot.getStack(), stack)) {
                ItemStack invStack = slot.getStack();
                clickAmount = Math.min(invStack.getMaxCount() - invStack.getCount(), amount);
            } else if (!slot.hasStack()) {
                clickAmount = amount;
            }
            for (int i = 0; i < clickAmount; i++) InventoryUtils.rightClickSlot(containerScreen, slot.id);
            amount -= clickAmount;
            if (amount == 0) {
                break;
            }
        }
        InventoryUtils.leftClickSlot(containerScreen, fromSlot.id);

    }

    private static void log(Message.MessageType type, String translationKey, Object... args) {
        if (!LITEMATICA_HELPER_TIPS.getBooleanValue()) return;
        InfoUtils.showGuiOrInGameMessage(type, translationKey, args);
    }

    private static boolean areSlotsInSameInventory(Slot slot1, Slot slot2, boolean treatHotbarAsDifferent) {
        if (slot1.inventory != slot2.inventory) {
            return false;
        } else if (treatHotbarAsDifferent && slot1.inventory instanceof PlayerInventory) {
            int index1 = AccessorUtils.getSlotIndex(slot1);
            int index2 = AccessorUtils.getSlotIndex(slot2);
            return index1 == 40 || index2 == 40 || index1 < 9 == index2 < 9;
        } else {
            return true;
        }
    }

    public void highlightInventoryBlock(){
        ClientPlayerEntity player = client.player;
        if (!Configs.LITEMATICA_HELPER.getBooleanValue() || player == null) return;
        MaterialListBase materialList = DataManager.getMaterialList();
        if (materialList == null) return;
        MaterialListUtils.updateAvailableCounts(materialList.getMaterialsAll(), player);
        List<MaterialListEntry> materialsMissingOnly = materialList.getMaterialsMissingOnly(false);
        List<BlockPos> list = new ArrayList<>();
        for (MaterialListEntry materialListEntry : materialsMissingOnly) {
            ItemStack stack = materialListEntry.getStack();
            list.addAll(searchChestTrackerMemory(stack));
        }
        if (list.isEmpty()){
            HighlightBlockRenderer.clear(litematicaHelper);
            return;
        }
        HighlightBlockRenderer.setPos(litematicaHelper,new HashSet<>(list.stream().distinct().toList()));
    }

    public List<BlockPos> searchChestTrackerMemory(ItemStack itemStack){
        List<BlockPos> blockPos = new ArrayList<>();
        ClientWorld world = client.world;
        if (world == null) return blockPos;
        Identifier registry = world.getRegistryKey().getValue();
        //#if MC > 11904
        Map<BlockPos, Memory> blockPosMemoryMap = memoriesSearch(registry, itemStack, MemoryBankAccessImpl.INSTANCE.getLoadedInternal().orElse(null));
        if (blockPosMemoryMap == null) return blockPos;
        blockPos.addAll(blockPosMemoryMap.keySet());

        //#else
        //$$ MemoryDatabase database = MemoryDatabase.getCurrent();
        //$$ if (database == null) return blockPos;
        //$$ List<BlockPos> list = database.findItems(itemStack, registry).stream().map(Memory::getPosition).distinct().toList();
        //$$ blockPos.addAll(list);
        //#endif
        return blockPos;
    }

    //#if MC > 11904
    public Map<BlockPos, Memory> memoriesSearch(Identifier key, ItemStack itemStack, MemoryBankImpl memoryBank) {
        if (key == null || itemStack == null) return null;
        ClientPlayerEntity player = client.player;
        if (player == null) return null;
        if (memoryBank != null && memoryBank.getMemories() != null &&
                memoryBank.getMemories().get(key) != null &&
                !CommonKeys.ENDER_CHEST_KEY.equals(key)) {
            SearchRequest searchRequest = new SearchRequest();
            SearchRequestPopulator.addItemStack(searchRequest, itemStack, SearchRequestPopulator.Context.FAVOURITE);
            int range = memoryBank.getMetadata().getSearchSettings().searchRange;
            double rangeSquared = range == Integer.MAX_VALUE ? Integer.MAX_VALUE : range * range;

            Map<BlockPos,Memory> itemsMap = new LinkedHashMap<>();
            for (Map.Entry<BlockPos, Memory> entry : memoryBank.getMemories().get(key).getMemories().entrySet()) {
                if (entry.getKey().getSquaredDistance(player.getPos()) > rangeSquared && range != Integer.MAX_VALUE) continue;
                if (entry.getValue().items().stream()
                        .filter(item -> SearchRequest.check(item, searchRequest))
                        .anyMatch(item -> !((Block.getBlockFromItem(item.getItem())) instanceof ShulkerBoxBlock))) {
                    itemsMap.put(entry.getKey(),entry.getValue());
                }
            }
            return itemsMap;
        }
        return null;
    }
    //#else
    //$$
    //#endif
}
