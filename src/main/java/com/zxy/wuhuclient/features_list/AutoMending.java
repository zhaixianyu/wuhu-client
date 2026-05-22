package com.zxy.wuhuclient.features_list;

import com.zxy.wuhuclient.Utils.InventoryUtils;
import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;

import static com.zxy.wuhuclient.Utils.InventoryUtils.client;


public class AutoMending {
    public static final AutoMending AUTO_MENDING = new AutoMending();
    private boolean patching = false;
    private int tempSlot = -1;
    public int tick = 1;

    public void mending(){
        LocalPlayer player = client.player;
        if(player == null) return;

        AbstractContainerMenu sc = player.containerMenu;
        ItemStack offHandStack = player.getOffhandItem();
        if(patching){
            if(!offHandStack.isDamaged() && InventoryUtils.getEnchantmentLevel(offHandStack, Enchantments.MENDING) > 0) restoresSlot();
            else return;
        }
        for (int i = 0; i < sc.slots.size(); i++) {
            if (sc.slots.get(i).getItem() == player.getMainHandItem()) continue;
            ItemStack copy = sc.slots.get(i).getItem().copy();
            if (
                    i <= 8 ||
                    copy.isEmpty() ||
                    sc.slots.get(i).getItem() == player.getOffhandItem() ||
                    InventoryUtils.getEnchantmentLevel(copy, Enchantments.MENDING) <= 0 ||
                    !copy.isDamaged())
                continue;
            switchSlot(sc,i);
            patching = true;
            tempSlot = i;
            break;
        }
    }
    public void tick(){
        if(!Configs.AUTO_MENDING.getBooleanValue() || !isPlayerScreenHandler()) return;
        if(tick == 0) mending();

        if (patching) {
            tick++;
            if(tick % 30 == 0 ) {
                restoresSlot();
                patching = false;
            }
        }

    }
    private void restoresSlot(){
        if(tempSlot != -1 && client.player != null){
            switchSlot(client.player.containerMenu,tempSlot);
            tempSlot = -1;
        }
    }
    private void switchSlot(AbstractContainerMenu sc, int i){
        sc.clicked(i, 40, ClickType.SWAP, client.player);
    }

    public boolean isPlayerScreenHandler(){
        return client.player.containerMenu.equals(client.player.inventoryMenu);
    }

    private AutoMending(){}
}
