package com.zxy.wuhuclient.Utils;

import com.zxy.wuhuclient.mixin.masa.Litematica_InventoryUtilsMixin;
import fi.dy.masa.litematica.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashSet;

import static com.zxy.wuhuclient.Utils.SwitchItem.reSwitchItem;
import static com.zxy.wuhuclient.config.Configs.QUICK_SHULKER;

public class InventoryUtils {
    public static HashSet<Item> items2 = new HashSet<>();
    @NotNull
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static boolean openIng = false;
    public static boolean switchItem = false;
    public static void switchInv(){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ScreenHandler sc = player.currentScreenHandler;
        if(sc.equals(player.playerScreenHandler)){
            return;
        }
        DefaultedList<Slot> slots = sc.slots;
        for(Item item : items2) {
            for (int y = 0; y < slots.get(0).inventory.size(); y++) {
                if (slots.get(y).getStack().getItem().equals(item)) {

                    String[] str = Configs.Generic.PICK_BLOCKABLE_SLOTS.getStringValue().split(",");
                    if(str.length==0) return;
                    for (String s : str) {
                        if (s == null) break;
                        try {
                            int c = Integer.parseInt(s) - 1;
                            if (Registries.ITEM.getId(player.getInventory().getStack(c).getItem()).toString().contains("shulker_box") &&
                                    QUICK_SHULKER.getBooleanValue()) {
                                MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("没有可替换的槽位，请将预选位的濳影盒换个位置"),false);
                                continue;
                            }
                            SwitchItem.newItem(slots.get(y).getStack(), null,null,y, shulkerBoxSlot);
                            shulkerBoxSlot = -1;
                            int a = Litematica_InventoryUtilsMixin.getEmptyPickBlockableHotbarSlot(player.getInventory()) == -1 ?
                                    Litematica_InventoryUtilsMixin.getPickBlockTargetSlot(player) :
                                    Litematica_InventoryUtilsMixin.getEmptyPickBlockableHotbarSlot(player.getInventory());
                            c = a == -1 ? c : a;
                            switchPlayerInvToHotbarAir(c);
                            fi.dy.masa.malilib.util.InventoryUtils.swapSlots(sc, y, c);
                            player.getInventory().selectedSlot = c;
                            player.closeHandledScreen();
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
        player.closeHandledScreen();
    }
    public static boolean switchItem(){
        if(items2.isEmpty()) return false;
        ClientPlayerEntity player = client.player;
        ScreenHandler sc = player.currentScreenHandler;
        if(!switchItem){
            if(!player.currentScreenHandler.equals(player.playerScreenHandler)) player.closeHandledScreen();
            if (sc.slots.stream().skip(9).limit(sc.slots.size()-10).noneMatch(slot -> slot.getStack().isEmpty())) {
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
            ScreenHandler sc = MinecraftClient.getInstance().player.playerScreenHandler;
            for (int i = 9; i < sc.slots.size(); i++) {
                ItemStack stack = sc.slots.get(i).getStack();
                String itemid = Registries.ITEM.getId(stack.getItem()).toString();
                if(itemid.contains("shulker_box")){
                    DefaultedList<ItemStack> items1 = fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(stack, -1);
                    if(items1.stream().anyMatch(s1 -> s1.getItem().equals(item))){
                        try {
                            if(reSwitchItem == null) shulkerBoxSlot = i;
                            Class quickShulker = Class.forName("net.kyrptonaught.quickshulker.client.ClientUtil");
                            Method checkAndSend = quickShulker.getDeclaredMethod("CheckAndSend",ItemStack.class,int.class);
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
        ClientPlayerEntity player = client.player;
        ScreenHandler sc = player.currentScreenHandler;
        DefaultedList<Slot> slots = sc.slots;
        int i = sc.equals(player.playerScreenHandler) ? 9 : 0;
        for (; i < slots.size(); i++) {
            if (slots.get(i).getStack().isEmpty() && slots.get(i).inventory instanceof PlayerInventory) {
                fi.dy.masa.malilib.util.InventoryUtils.swapSlots(sc, i, slot);
                return;
            }
        }
    }
}