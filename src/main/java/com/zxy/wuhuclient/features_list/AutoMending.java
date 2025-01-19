package com.zxy.wuhuclient.features_list;

import com.zxy.wuhuclient.Utils.InventoryUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import static com.zxy.wuhuclient.Utils.InventoryUtils.client;


public class AutoMending {
    public static final AutoMending AUTO_MENDING = new AutoMending();
    private boolean patching = false;
    private ClientPlayerEntity player = client.player;
    private int tempSlot = -1;
    public int tick = 0;

    public void mending(){
        ScreenHandler sc = player.currentScreenHandler;
        ItemStack offHandStack = player.getOffHandStack();
        if(patching){
            if(!offHandStack.isDamaged() && InventoryUtils.getEnchantmentLevel(offHandStack,Enchantments.MENDING) > 0) restoresSlot();
            else return;
        }
        for (int i = 0; i < sc.slots.size(); i++) {
            ItemStack copy = sc.slots.get(i).getStack().copy();
            if (
                            i <= 8 ||
                            copy.isEmpty() ||
                            sc.slots.get(i).getStack() == player.getOffHandStack() ||
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
        if(tick == 0) mending();
        tick ++;
        if(patching && tick % 10 == 0 ) restoresSlot();
    }
    private void restoresSlot(){
        if(tempSlot != -1){
            switchSlot(player.currentScreenHandler,tempSlot);
            patching = false;
            tempSlot = -1;
        }
    }
    private void switchSlot(ScreenHandler sc,int i){
        client.interactionManager.clickSlot(sc.syncId, i, 40, SlotActionType.SWAP, client.player);
    }

    private AutoMending(){}
}
