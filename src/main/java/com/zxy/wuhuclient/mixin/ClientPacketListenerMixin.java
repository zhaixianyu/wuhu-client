package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.InventoryUtils;
import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.features_list.AutoMending;
import com.zxy.wuhuclient.features_list.CloseTheContainerAfterOpening;
import com.zxy.wuhuclient.features_list.Synthesis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.zxy.wuhuclient.Utils.InventoryUtils.client;
import static com.zxy.wuhuclient.Utils.InventoryUtils.switchInv;
import static com.zxy.wuhuclient.features_list.SyncInventory.num;
import static com.zxy.wuhuclient.features_list.SyncInventory.syncInv;

@Mixin(value = ClientPacketListener.class)
@Environment(EnvType.CLIENT)
public class ClientPacketListenerMixin {
    @Inject(at = @At("TAIL"),method = "handleContainerContent")
    public void onInventory(ClientboundContainerSetContentPacket packet, CallbackInfo ci){
        if(Synthesis.step!=0)Synthesis.onInventory();
        if(InventoryUtils.switchItem)switchInv();
        if(num == 3 || num == 1)syncInv();
        if (CloseTheContainerAfterOpening.step == 2) {
            CloseTheContainerAfterOpening.step();
        }
    }
    @Inject(at = @At("TAIL"),method = "handleMovePlayer")
    public void onPlayerPositionLook(ClientboundPlayerPositionPacket packet, CallbackInfo ci){
//        System.out.println("speed");
    }
    @Inject(at = @At("TAIL"),method = "handleTakeItemEntity")
    public void onItemPickupAnimation(ClientboundTakeItemEntityPacket packet, CallbackInfo ci){
    }
    @Inject(at = @At("HEAD"),method = "handleSetExperience")
    public void onExperienceBarUpdate(ClientboundSetExperiencePacket packet, CallbackInfo ci){
//        int experience = packet.getExperience();
//        float barProgress = packet.getBarProgress();
//        int experienceLevel = packet.getExperienceLevel();
//        if(Configs.AUTO_MENDING.getBooleanValue()) {
//            AutoMending.AUTO_MENDING.tick = 0;
//        }
    }
}
