package com.zxy.wuhuclient.featuresList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;


public class AutoMending {
    static AutoMending auto;
    MinecraftClient mc;
    ClientPlayerEntity player;
    int fushou = -1;
    boolean run = false;
    int tick = 0;

    public void autoMenDing(PlayerEntity player) {
        tick = 0;
        ScreenHandler sc = player.currentScreenHandler;
        if (!sc.equals(player.playerScreenHandler)) return;

        if (run) {
            if(!sc.slots.get(45).getStack().isDamaged()) reSwitch();
            else return;
        }
        if (mc == null || !player.equals(mc.player)) return;

        for (int i = 0; i < sc.slots.size(); i++) {
            ItemStack item = sc.slots.get(i).getStack();
            if (
                    item.isOf(Items.AIR) ||
                            EnchantmentHelper.getLevel(Enchantments.MENDING, item) <= 0 ||
                            !item.isDamaged() ||
                            item.equals(player.getMainHandStack()) ||
                            i == 45 || i == 5 || i == 6 || i == 7 || i == 8
            ) continue;
            autoSwitch(i, 45);
            fushou = i;
            run = true;
            break;
        }
    }

    public void tick() {
        if (!run) return;
        if (0 == ++tick % 10) reSwitch();
        tick %= Integer.MAX_VALUE;
    }

    public boolean autoSwitch(int a, int b) {
        if (mc.interactionManager == null) {
            return false;
        }
        if (player.currentScreenHandler != player.playerScreenHandler) {
            return false;
        }
        ScreenHandler screenHandler = player.currentScreenHandler;
        mc.interactionManager.clickSlot(screenHandler.syncId, a, 0, SlotActionType.PICKUP, player);
        mc.interactionManager.clickSlot(screenHandler.syncId, b, 0, SlotActionType.PICKUP, player);
        mc.interactionManager.clickSlot(screenHandler.syncId, a, 0, SlotActionType.PICKUP, player);
        return true;
    }

    public void reSwitch() {
        ScreenHandler sc = player.currentScreenHandler;
        if (run && sc.equals(player.playerScreenHandler)) {
//            System.out.println("副手归位: " + temp);
            if (fushou != -1 && autoSwitch(fushou, 45)) {
                tick = 0;
                fushou = -1;
                run = false;
            }
        }
    }


    public AutoMending(MinecraftClient client) {
        this.mc = client;
        this.player = client.player;
        auto = this;
    }

    public static AutoMending getAuto() {
        return auto;
    }
}
