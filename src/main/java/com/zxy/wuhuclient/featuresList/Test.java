package com.zxy.wuhuclient.featuresList;

import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class Test {
    public static void tick(){
        if(!Configs.TEST.getBooleanValue())return;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ScreenHandler sc = player.currentScreenHandler;
        for (int i = 9; i < sc.slots.size(); i++) {
            client.interactionManager.clickSlot(sc.syncId, i, 0, SlotActionType.PICKUP, player);
            client.interactionManager.clickSlot(sc.syncId, i, 1, SlotActionType.PICKUP, player);
            client.interactionManager.clickSlot(sc.syncId, -999, 0, SlotActionType.PICKUP, player);
        }
    }
}
