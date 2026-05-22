package com.zxy.wuhuclient.Utils;

import fi.dy.masa.malilib.util.InventoryUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.zxy.wuhuclient.Utils.ScreenManagement.closeScreen;
import static com.zxy.wuhuclient.features_list.Synthesis.client;

public class SwitchItem {
//    public static boolean switchIng = false;
    public static ItemStack reSwitchItem = null;
    public static Map<ItemStack,ItemStatistics> itemStacks = new HashMap<>();
    public static void removeItem(ItemStack itemStack){
        itemStacks.remove(itemStack);
    }
    public static void syncUseTime(ItemStack itemStack){
        ItemStatistics itemStatistics = itemStacks.get(itemStack);
        if(itemStatistics !=null) itemStatistics.syncUseTime();
    }
    public static void newItem(ItemStack itemStack, BlockPos pos, ResourceKey<Level> key, int slot, int shulkerBox){
        itemStacks.put(itemStack,new ItemStatistics(key,pos,slot,shulkerBox));
    }
    public static void openInv(ItemStack itemStack){
        if(!client.player.containerMenu.equals(client.player.inventoryMenu) || closeScreen > 0){
            return;
        }
        AbstractContainerMenu sc1 = client.player.containerMenu;
        if (sc1.slots.stream().skip(9).limit(sc1.slots.size()-10)
                .noneMatch(slot -> InventoryUtils.areStacksEqual(slot.getItem(),reSwitchItem))) {
            itemStacks.remove(reSwitchItem);
            reSwitchItem = null;
            return;
        }
        ItemStatistics itemStatistics = itemStacks.get(itemStack);
        if(itemStatistics != null){
            AbstractContainerMenu sc = client.player.containerMenu;
            for (int i = 9; i < sc.slots.size() && itemStatistics.shulkerBoxSlot != -1; i++) {
                ItemStack stack = sc.slots.get(i).getItem();
                if (InventoryUtils.getStoredItems(stack,-1).stream().anyMatch(stack1 -> stack1.isEmpty() ||
                        (InventoryUtils.areStacksEqual(stack1,reSwitchItem) && stack1.getCount() < stack1.getMaxStackSize()))
                ) {
                    try {
                        Class quickShulker = Class.forName("net.kyrptonaught.quickshulker.client.ClientUtil");
                        Method checkAndSend = quickShulker.getDeclaredMethod("CheckAndSend", ItemStack.class,int.class);
                        checkAndSend.invoke(checkAndSend,sc.slots.get(itemStatistics.shulkerBoxSlot).getItem(),i);
                        closeScreen++;
                        return;
                    } catch (Exception ignored){}
                }
            }
            removeItem(reSwitchItem);
            reSwitchItem = null;
        }
    }
    public static void checkItems(){
        final long[] min = {System.currentTimeMillis()};
        AtomicReference<ItemStack> key = new AtomicReference<>();
        itemStacks.keySet().forEach(k ->{
            long useTime = itemStacks.get(k).useTime;
            if(useTime < min[0]){
                min[0] = useTime;
                key.set(k);
            }
        });
        ItemStack itemStack = key.get();
        if(itemStack != null) {
            reSwitchItem = itemStack;
            openInv(itemStack);
        }else client.gui.setOverlayMessage(Component.literal("背包已满，请先清理"),false);
    }
    public static void reSwitchItem(){
        if(client.player == null || reSwitchItem == null) return;
        LocalPlayer player = client.player;
        AbstractContainerMenu sc = player.containerMenu;
        if (sc.equals(player.inventoryMenu)) return;
        List<Integer> sameItem = new ArrayList<>();
        for (int i = 0; i < sc.slots.size(); i++) {
            Slot slot = sc.slots.get(i);
            if(!(slot.container instanceof Inventory) &&
                    InventoryUtils.areStacksEqual(reSwitchItem,slot.getItem()) &&
                    slot.getItem().getCount() < slot.getItem().getMaxStackSize()
            ) sameItem.add(i);
            if(slot.container instanceof Inventory && client.gameMode != null && InventoryUtils.areStacksEqual(slot.getItem(),reSwitchItem)){
                int slot1 = itemStacks.get(reSwitchItem).slot;
                boolean reInv = false;
                //检查记录的槽位是否有物品
                if(sc.slots.get(slot1).getItem().isEmpty()){
                    sc.clicked(i, 0, ClickType.PICKUP, client.player);
                    sc.clicked(slot1, 0, ClickType.PICKUP, client.player);
                    reInv = true;
                } else {
                    int count = reSwitchItem.getCount();
                    sc.clicked(i, 0, ClickType.PICKUP, client.player);
                    for (Integer integer : sameItem) {
                        int count1 = sc.slots.get(integer).getItem().getCount();
                        int maxCount = sc.slots.get(integer).getItem().getMaxStackSize();
                        int i1 = maxCount - count1;
                        count -= i1;
                        sc.clicked(integer, 0, ClickType.PICKUP, client.player);
                        if (count<=0) reInv = true;
                    }
                }
                removeItem(reSwitchItem);
                reSwitchItem = null;
                player.closeContainer();
                if(!reInv) client.gui.setOverlayMessage(Component.literal("复原库存物品失败"),false);
                sc.clicked(i, 0, ClickType.PICKUP, client.player);
                return;
            }
        }
    }
    public static void reSet(){
        reSwitchItem = null;
        itemStacks = new HashMap<>();
    }
    public static class ItemStatistics {
        public ResourceKey<Level> key;
        public BlockPos pos;
        public int slot;
        public int shulkerBoxSlot;
        public long useTime = System.currentTimeMillis();
        public ItemStatistics(ResourceKey<Level> key, BlockPos pos, int slot, int shulkerBox) {
            this.key = key;
            this.pos = pos;
            this.slot = slot;
            this.shulkerBoxSlot = shulkerBox;
        }
        public void syncUseTime(){
            this.useTime = System.currentTimeMillis();
        }
    }
}