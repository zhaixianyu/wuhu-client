package com.zxy.wuhuclient.Utils;

import com.zxy.wuhuclient.mixin.ShulkerBoxBlockAccessor;
import com.zxy.wuhuclient.mixin.masa.Litematica_InventoryUtilsMixin;
import fi.dy.masa.litematica.config.Configs;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
//#if MC >= 12006
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//#else
//$$
//#endif
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

//#if MC > 12104
import net.minecraft.network.HashedStack;
//#endif

import static com.zxy.wuhuclient.Utils.SwitchItem.reSwitchItem;
import static com.zxy.wuhuclient.Utils.ZxyUtils.getPlayer;
import static com.zxy.wuhuclient.config.Configs.QUICK_SHULKER;
import static net.minecraft.world.level.block.ShulkerBoxBlock.FACING;

public class InventoryUtils {
    public static HashSet<Item> items2 = new HashSet<>();
    @NotNull
    public static Minecraft client = Minecraft.getInstance();
    public static boolean openIng = false;
    public static boolean switchItem = false;
    public static void switchInv(){
        LocalPlayer player = Minecraft.getInstance().player;
        AbstractContainerMenu sc = player.containerMenu;
        if(sc.equals(player.inventoryMenu)){
            return;
        }
        NonNullList<Slot> slots = sc.slots;
        for(Item item : items2) {
            for (int y = 0; y < slots.get(0).container.getContainerSize(); y++) {
                if (slots.get(y).getItem().getItem().equals(item)) {

                    String[] str = Configs.Generic.PICK_BLOCKABLE_SLOTS.getStringValue().split(",");
                    if(str.length==0) return;
                    for (String s : str) {
                        if (s == null) break;
                        try {
                            int c = Integer.parseInt(s) - 1;
                            if (BuiltInRegistries.ITEM.getKey(player.getInventory().getItem(c).getItem()).toString().contains("shulker_box") &&
                                    QUICK_SHULKER.getBooleanValue()) {
                                Messager.actionBar("没有可替换的槽位，请将预选位的濳影盒换个位置");
                                continue;
                            }
                            SwitchItem.newItem(slots.get(y).getItem(), null,null,y, shulkerBoxSlot);
                            shulkerBoxSlot = -1;
                            int a = Litematica_InventoryUtilsMixin.getEmptyPickBlockableHotbarSlot(player.getInventory()) == -1 ?
                                    Litematica_InventoryUtilsMixin.getPickBlockTargetSlot(player) :
                                    Litematica_InventoryUtilsMixin.getEmptyPickBlockableHotbarSlot(player.getInventory());
                            c = a == -1 ? c : a;
                            switchPlayerInvToHotbarAir(c);
                            fi.dy.masa.malilib.util.InventoryUtils.swapSlots(sc, y, c);
                            //#if MC > 12104
                            player.getInventory().setSelectedSlot(c);
                            //#else
                            //$$ player.getInventory().selected = c;
                            //#endif
                            player.closeContainer();
                            items2 = new HashSet<>();
                            return;
                        } catch (Exception e) {
                            System.out.println("切换物品异常");
                        }
                    }
                }
            }
        }
        shulkerBoxSlot = -1;
        items2 = new HashSet<>();
        player.closeContainer();
    }
    public static boolean switchItem(){
        if(items2.isEmpty()) return false;
        LocalPlayer player = client.player;
        AbstractContainerMenu sc = player.containerMenu;
        if(!switchItem){
            if(!player.containerMenu.equals(player.inventoryMenu)) player.closeContainer();
            if (sc.slots.stream().skip(9).limit(sc.slots.size()-10).noneMatch(slot -> slot.getItem().isEmpty())) {
                SwitchItem.checkItems();
                return true;
            }
            if(QUICK_SHULKER.getBooleanValue() && openShulker(items2)) return true;
        }
        return false;
    }
    static int shulkerBoxSlot = 0;
    static boolean openShulker(HashSet<Item> items){
        for (Item item : items) {
            AbstractContainerMenu sc = Minecraft.getInstance().player.inventoryMenu;
            for (int i = 9; i < sc.slots.size(); i++) {
                ItemStack stack = sc.slots.get(i).getItem();
                String itemid = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                if(itemid.contains("shulker_box")){
                    NonNullList<ItemStack> items1 = fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(stack, -1);
                    if(items1.stream().anyMatch(s1 -> s1.getItem().equals(item))){
                        try {
                            if(reSwitchItem == null) shulkerBoxSlot = i;
                            Class quickShulker = Class.forName("net.kyrptonaught.quickshulker.client.ClientUtil");
                            Method checkAndSend = quickShulker.getDeclaredMethod("CheckAndSend", ItemStack.class,int.class);
                            checkAndSend.invoke(checkAndSend,stack,i);
                            ScreenManagement.closeScreen++;
                            switchItem = true;
                            return true;
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return false;
    }
    public static void switchPlayerInvToHotbarAir(int slot){
        if(client.player == null )return;
        LocalPlayer player = client.player;
        AbstractContainerMenu sc = player.containerMenu;
        NonNullList<Slot> slots = sc.slots;
        int i = sc.equals(player.inventoryMenu) ? 9 : 0;
        for (; i < slots.size(); i++) {
            if (slots.get(i).getItem().isEmpty() && slots.get(i).container instanceof Inventory) {
                fi.dy.masa.malilib.util.InventoryUtils.swapSlots(sc, i, slot);
                return;
            }
        }
    }

    public static boolean equalsItem(ItemStack itemStack1, ItemStack itemStack2){
        //#if MC > 12004
        return ItemStack.isSameItemSameComponents(itemStack1, itemStack2);
        //#else
        //$$ return ItemStack.isSameItemSameTags(itemStack1, itemStack2);
        //#endif
    }

    public static int getEnchantmentLevel(ItemStack itemStack,
                                          //#if MC > 12006
                                          ResourceKey<Enchantment> enchantment
                                          //#else
                                          //$$ Enchantment enchantment
                                          //#endif
    ){
        //#if MC > 12006
        ItemEnchantments enchantments = itemStack.getEnchantments();
        if (enchantments.equals(ItemEnchantments.EMPTY)) return -1;
        Set<Holder<Enchantment>> enchantmentsEnchantments = enchantments.keySet();
        for (Holder<Enchantment> entry : enchantmentsEnchantments) {
            if (entry.is(enchantment)) {
                return enchantments.getLevel(entry);
            }
        }
        return -1;
        //#else
        //$$ return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING,itemStack);
        //#endif
    }
    public static void refreshPlayerInventory(){
        ClientPacketListener networkHandler = client.getConnection();
        if (getPlayer().isEmpty()) return;
        LocalPlayer player = getPlayer().get();
        if(networkHandler == null) return;
        ItemStack uniqueItem = new ItemStack(Items.STONE);

        // Tags with NaN are not equal, so the server will find an inventory desync and send an inventory refresh to the client
        //#if MC >= 12006
        var nbt = new CompoundTag();
        nbt.putDouble("force_sync", Double.NaN);
        CustomData.set(DataComponents.CUSTOM_DATA, uniqueItem, nbt);
        //#else
        //$$ uniqueItem.getOrCreateTag().putDouble("force_resync", Double.NaN);
        //#endif

        //#if MC >= 12105
        HashedStack itemStackHash = HashedStack.create(uniqueItem, networkHandler.decoratedHashOpsGenenerator());
        //#endif

        networkHandler.send(new ServerboundContainerClickPacket(
                player.containerMenu.containerId,
                player.containerMenu.getStateId(),
                (short) -999,(byte) 2,
                ContainerInput.QUICK_CRAFT,
                //#if MC < 12105
                //$$ uniqueItem,
                //$$ new Int2ObjectOpenHashMap<>()
                //#else
                new Int2ObjectOpenHashMap<>(),
                itemStackHash
                //#endif

        ));
    }

    public static boolean isInventory(BlockPos pos) {
//        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
//            BlockPos pos = ((BlockHitResult) client.crosshairTarget).getBlockPos();
        if (client.level == null) return false;
        Container inventory = fi.dy.masa.malilib.util.InventoryUtils.getInventory(client.level, pos);

        return inventory != null;
//        BlockState blockState = client.world.getBlockState(pos);
//        BlockEntity blockEntity = client.world.getBlockEntity(pos);
//        try {
//            if (((BlockWithEntityMixin) blockState.getBlock()).createScreenHandlerFactory(blockState, client.world, pos) == null ||
//                    (blockEntity instanceof ShulkerBoxBlockEntity entity &&
//                            //#if MC > 12004
//                            !client.world.isSpaceEmpty(ShulkerEntity.calculateBoundingBox(0.0f,blockState.get(FACING),  0.5f).offset(pos).contract(1.0E-6)) &&
//                            //#else
//                            //$$ !client.world.isSpaceEmpty(ShulkerEntity.calculateBoundingBox(blockState.get(FACING), 0.0f, 0.5f).offset(pos).contract(1.0E-6)) &&
//                            //#endif
//                            entity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED)) {
//                client.inGameHud.setOverlayMessage(Text.of("目标无法打开"), false);
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
    }

    public static boolean canOpenInv(BlockPos pos){
        if (client.level != null) {
            BlockState blockState = client.level.getBlockState(pos);
            BlockEntity blockEntity = client.level.getBlockEntity(pos);
            boolean isInventory = InventoryUtils.isInventory(pos);
            try {
                if ((isInventory && blockState.getMenuProvider(client.level,pos) == null) ||
                        (blockEntity instanceof ShulkerBoxBlockEntity entity &&
                                !ShulkerBoxBlockAccessor.canOpen(blockState,client.level,pos,entity))) {
                    return false;
                }else if(!isInventory){
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }else {
            return false;
        }
    }

    public static String getItemName(ItemStack itemStack){
        //#if MC > 12101
        return itemStack.getItemName().getString();
        //#else
        //$$ return itemStack.getDescriptionId();
        //#endif
    }
}