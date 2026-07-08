package com.zxy.wuhuclient.features_list;


import com.zxy.wuhuclient.Utils.Messager;
import com.zxy.wuhuclient.Utils.ScreenManagement;
import com.zxy.wuhuclient.Utils.ZxyUtils;
import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.mixin.CraftingMenuMixin;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;


import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

//#if MC < 12001
//$$ import net.minecraft.world.inventory.CraftingContainer;
//#elseif MC == 12001
//$$ import net.minecraft.world.inventory.CraftingContainer;
//#else
import net.minecraft.world.inventory.CraftingContainer;
//#endif

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.zxy.wuhuclient.Utils.InventoryUtils.isInventory;
import static com.zxy.wuhuclient.Utils.InventoryUtils.refreshPlayerInventory;
import static com.zxy.wuhuclient.Utils.ScreenManagement.closeScreen;


public class Synthesis {
    public static boolean isLoadMod = FabricLoader.getInstance().isModLoaded("itemscroller");
    public static Minecraft client = Minecraft.getInstance();
    public static RecipePattern recipe = null;
    //1 丢出容器物品 2 合成 3 装箱
    public static int step = 0;
    //记录合成点击的方块位置
    public static BlockPos pos;
    //    public static Set<Integer> updatedSlot = new HashSet<>();
    public static boolean invUpdated = false;
    public static int tick = 0;
    public static void tick() {
//        tick++;
//        tick %= Integer.MAX_VALUE;
//        if(step != 0) System.out.println(step);
        if(!Configs.SYNTHESIS.getBooleanValue()) return;
        if(invUpdated){
            switch (step) {
                case 1 -> {
                    dropInventory();
                    step = 0;
                }
                case 3 -> {
                    storage();
                    step = 0;
                }
            }
        }
        if (storagePos != null && autoStorage && step != 1) {
            autoStorage();
            if(step == 3) return;
        }
        if(step == 0 || step == 2) continueSynthesis();
    }

    public static void onInventory() {
        if(!Configs.SYNTHESIS.getBooleanValue()) return;
        invUpdated = true;
    }

    public static void start(BlockPos pos) {
        tick = 0;
        if (!updateRecipe()) {
            Messager.actionBar("当前快捷合成配方为空");
            return;
        }
//        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
//            BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
        if (client.level != null) {
            BlockState blockState = client.level.getBlockState(pos);
            if (blockState.is(Blocks.CRAFTING_TABLE)) {
                //工作台合成
//                step = 2;
                Synthesis.pos = pos;
                return;
            }
            if (!isInventory(pos)) {
                //背包合成
                if (recipe.getRecipeItems().length == 4) {
//                    step = 2;
                    Synthesis.pos = pos;
                }
                return;
            }
            if (client.player.isShiftKeyDown()) {
                Messager.actionBar("合成取物已标记");
                autoDrop = true;
            }else{
                autoDrop = false;
            }
            if(closeScreen != 0) return;
            dropPos = pos;
            dropStart();
        }
//        }
    }

    public static BlockPos dropPos;
    static boolean autoDrop;
    static void dropStart(){
        if(closeScreen != 0 || !isInventory(dropPos) || !updateRecipe()) return;
        step = 1;
        closeScreen = 1;
        invUpdated = false;
        if(!dropPos.closerToCenterThan(client.player.getEyePosition(),5)){
            dropPos = null;
            return;
        }
        ZxyUtils.setShift(false);
        useBlock(dropPos);
    }
    public static void useBlock(BlockPos pos){
        //#if MC > 11802
        client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, false));
        //#else
        //$$ client.gameMode.useItemOn(client.player,client.level, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, false));
        //#endif
    }
    private static Map<Item, Integer> must = new HashMap<>();
    public static boolean isSynthesis(){
        if (!updateRecipe()) return false;
        ItemStack[] recipeItems = recipe.getRecipeItems();

        Map<Item, Integer> recipeMap = new HashMap<>();
        must = new HashMap<>();
        for (ItemStack recipeItem : recipeItems) {
            if (recipeItem.isEmpty()) continue;
            Item item = recipeItem.getItem();
            if (must.containsKey(item)) {
                must.put(item, must.get(item) + recipeItem.getCount());
            } else {
                must.put(item, recipeItem.getCount());
            }

            recipeMap.put(item, 0);
        }
        for (Slot slot : client.player.containerMenu.slots) {
            ItemStack stack = slot.getItem();
            //#if MC > 12004

            if (stack.isEmpty() || stack.getComponents().isEmpty()) continue;
            //#else
            //$$ if (stack.isEmpty() || stack.hasTag()) continue;
            //#endif
            recipeMap.forEach((k, v) -> {
                int num = stack.getMaxStackSize() == 1 ? 0 : 1;
                if (stack.getItem().equals(k)) recipeMap.put(k, v + (stack.getCount() - num));
            });
        }
        AtomicReference<Boolean> result = new AtomicReference<>(true);
        recipeMap.forEach((k, v) -> {
//            System.out.println(k + "\t" + v);
//            System.out.println(must.get(k));
            if (v < must.get(k)) {
                result.set(false);
//                System.out.println("000");
                return;
            }
        });
//        if(!result.get() && dropPos != null && Configs.AUTO_DROP.getBooleanValue()) {
        if(!result.get() && dropPos != null && autoDrop && closeScreen <= 0) {
//            client.player.closeHandledScreen();
            dropStart();
        }
        return result.get();
    }

    public static boolean satisfyCraft(){
        if (ScreenManagement.screen instanceof AbstractContainerScreen<?> gui) {
            Slot slot = CraftingHandler.getFirstCraftingOutputSlotForGui(gui);
            fi.dy.masa.itemscroller.util.InventoryUtils.updateCraftingOutputSlot(slot);
            ItemStack stack = slot.getItem();
            return !stack.isEmpty() && stack.getItem().equals(Synthesis.recipe.getResult().getItem());
        }
        return false;
    }

    public static void synthesis2(){
        Messager.actionBar("合成中...");
        if(!pos.closerToCenterThan(client.player.getEyePosition(),5)){
            Messager.actionBar("工作台或标记的方块超出范围，已重置。请再次点击开始合成");
            pos = null;
            return;
        }
        if(!isSynthesis()) return;

        LocalPlayer player = client.player;
        AbstractContainerMenu sc = player.containerMenu;
        if (sc.equals(player.inventoryMenu)) return;

        ItemStack[] recipeItems = recipe.getRecipeItems();
//        int[] playerInv = new int[sc.slots.size()];

        int[] recInv = new int[recipeItems.length];
//        for (int i = 0; i < recInv.length; i++) {
//            recInv[i] = sc.slots.get(i+1).getStack().getCount();
//        }

        for (int i = recipeItems.length+1; i < sc.slots.size(); i++) {
            ItemStack stack = sc.slots.get(i).getItem().copy();
            if (stack.isEmpty() || (stack.getMaxStackSize() != 1 && stack.getCount() == 1)) continue;
            if (Arrays.stream(recipeItems).noneMatch(rec -> InventoryUtils.areStacksEqual(rec,stack))) continue;

            int stackCount = stack.getCount()-1;
            int cursorStackCount = stack.getCount()-1;
            client.gameMode.handleContainerInput(sc.containerId,-999, 0, ContainerInput.PICKUP, player);
            client.gameMode.handleContainerInput(sc.containerId,i, 0, ContainerInput.PICKUP, player);
            client.gameMode.handleContainerInput(sc.containerId,i, 1, ContainerInput.PICKUP, player);
            client.gameMode.handleContainerInput(sc.containerId,-999, 0, ContainerInput.QUICK_CRAFT, client.player);

            int skip = 0;
            int craft = 0;
            ArrayList<Integer> numArr = new ArrayList<>();
            for (int i1 = 1; i1 <= recipeItems.length; i1++) {
                ItemStack stack1 = sc.slots.get(i1).getItem();
                if (craft >= stackCount) break;
                if(!InventoryUtils.areStacksEqual(stack,recipeItems[i1-1])) continue;

                if (recInv[i1-1] >= stack.getMaxStackSize()){
                    skip++;
                    continue;
                }
                numArr.add(i1-1);
                craft++;
                client.gameMode.handleContainerInput(sc.containerId, i1, 1, ContainerInput.QUICK_CRAFT, client.player);
            }
            client.gameMode.handleContainerInput(sc.containerId,-999, 2, ContainerInput.QUICK_CRAFT, client.player);

            if(craft== 0){
                client.gameMode.handleContainerInput(sc.containerId,i, 0, ContainerInput.PICKUP, player);
                continue;
            }
            if(craft > cursorStackCount){
                int craftCopy = craft;
                for (Integer integer : numArr) {
                    recInv[integer] += 1;
                    if(--craftCopy <= 0) break;
                }
            }else {
                for (Integer integer : numArr) {

                    if(recInv[integer] + stackCount/craft >= stack.getMaxStackSize()){
                        int num = stack.getMaxStackSize() - recInv[integer];
                        cursorStackCount -= num;
                        recInv[integer] += num;
                    }else {
                        cursorStackCount -= stackCount/craft;
                        recInv[integer] += stackCount/craft;
                    }
                }
            }
            if(cursorStackCount > 0){
                client.gameMode.handleContainerInput(sc.containerId,i, 0, ContainerInput.PICKUP, player);
            }

            if(skip == recipeItems.length) break;
        }
//        for (int i2 = 0; InventoryUtils.areStacksEqual(sc.slots.get(0).getStack(), recipe.getResult()) && i2 < 64; i2++) {
        for (int i2 = 1; satisfyCraft() && i2 < 64; i2++) {
            client.gameMode.handleContainerInput(sc.containerId,0, 1, ContainerInput.THROW, player);
        }
        ScreenManagement.screen = null;
        player.closeContainer();
        refreshPlayerInventory();
    }

    public static void dropItem(ItemStack itemStack, boolean isPlayerInventory) {
        LocalPlayer player = client.player;
        if (player == null) return;
        AbstractContainerMenu sc = player.containerMenu;
        int size = 0;
        if (isPlayerInventory && sc.equals(client.player.inventoryMenu)) {
            size = sc.slots.size();
        } else if (!isPlayerInventory && !sc.equals(player.inventoryMenu)) {
            size = sc.slots.get(0).container.getContainerSize();
        }
        if (size == 0) return;
        for (int i = 0; i < size; i++) {
            if (InventoryUtils.areStacksEqual(sc.slots.get(i).getItem(), itemStack)) {
                client.gameMode.handleContainerInput(sc.containerId,i, 1, ContainerInput.THROW, player);
            }
        }
    }

    public static void dropInventory() {
        if (!updateRecipe()) return;
        client = Minecraft.getInstance();
        LocalPlayer player;
        if (client.player == null) return;
        player = client.player;
        if (player.containerMenu.equals(player.inventoryMenu)) return;
        for (ItemStack recipeItem : recipe.getRecipeItems()) {
            dropItem(recipeItem, false);
        }
        player.closeContainer();
    }
    public static BlockPos storagePos = null;
    public static boolean autoStorage = false;

    public static void autoStorage() {
        if (client.player == null || !updateRecipe() || !isInventory(storagePos)) return;
        LocalPlayer player = client.player;
        NonNullList<Slot> slots = player.containerMenu.slots;
        if (storagePos != null && storagePos.closerToCenterThan(player.getEyePosition(), 5) && step != 3 && closeScreen <= 0) {
            if (slots.stream()
                    .anyMatch(slot -> InventoryUtils.areStacksEqual(slot.getItem(), recipe.getResult()) && slot.getItem().getCount() > 1))
            {
//                System.out.println("autoStorage");
                closeScreen = 1;
                step = 3;
                invUpdated = false;
                ZxyUtils.setShift(false);
                useBlock(storagePos);
            }
        }
    }
    public static void storage() {
        if (!updateRecipe()) return;
        LocalPlayer player = client.player;
        if (player == null) return;
        AbstractContainerMenu sc = player.containerMenu;
        if(sc.equals(player.inventoryMenu)) return;
        NonNullList<Slot> slots = sc.slots;
        if (slots.stream()
                .limit(slots.get(0).container.getContainerSize())
                .allMatch(slot -> InventoryUtils.areStacksEqual(slot.getItem(), recipe.getResult())
                        && slot.getItem().getCount() >= slot.getItem().getMaxStackSize())) {
            Messager.actionBar("合成助手: 目标器已满");
            player.closeContainer();
            return;
        }
        //从玩家背包寻找合成物
        for (int i = slots.get(0).container.getContainerSize(); i < slots.size(); i++) {
            ItemStack stack = slots.get(i).getItem();
            if (InventoryUtils.areStacksEqual(stack, recipe.getResult()) && stack.getCount() > 1) {
                client.gameMode.handleContainerInput(sc.containerId,i, 0, ContainerInput.PICKUP, player);
                client.gameMode.handleContainerInput(sc.containerId,i, 1, ContainerInput.PICKUP, player);
                //检测容器中能放下的空位
                for (int i2 = 0; i2 < slots.get(0).container.getContainerSize(); i2++) {
                    if ((InventoryUtils.areStacksEqual(slots.get(i2).getItem(), recipe.getResult())
                            && slots.get(i2).getItem().getCount() <= slots.get(i2).getItem().getMaxStackSize())
                            || slots.get(i2).getItem().isEmpty()
                    ) client.gameMode.handleContainerInput(sc.containerId,i2, 0, ContainerInput.PICKUP, player);
                }
                if(!sc.getCarried().isEmpty()){
                    client.gameMode.handleContainerInput(sc.containerId,i, 0, ContainerInput.PICKUP, player);
                    client.gameMode.handleContainerInput(sc.containerId,-999, 0, ContainerInput.PICKUP, player);
                }
            }
        }
        player.closeContainer();
    }

    private static boolean updateRecipe() {
        recipe = RecipeStorage.getInstance().getSelectedRecipe();
        return !recipe.getResult().isEmpty();
    }

    public static void continueSynthesis() {
        if (pos != null ) {
            AbstractContainerMenu sc = client.player.containerMenu;
            ItemStack[] recipeItems = recipe.getRecipeItems();
            if (((recipeItems.length == 9 && !(sc instanceof CraftingMenu)) || (recipeItems.length == 4 && !(sc instanceof InventoryMenu)))) {
                if (recipeItems.length == 9 && sc instanceof InventoryMenu && closeScreen <= 0) {
                    if (client.level.getBlockState(pos).isAir()) return;
//                    System.out.println(".............");
                    closeScreen = 1;
//                    tick = 0;
                    step = 2;
                    invUpdated = false;
                    ZxyUtils.setShift(false);
                    useBlock(pos);
                } else if (recipeItems.length == 4) {
                    client.player.closeContainer();
                }
            }else if(invUpdated && step == 2) {
                synthesis2();
            }else{
                client.player.clientSideCloseContainer();
            }
        }
    }
}
