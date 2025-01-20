package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.InventoryUtils;
import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.features_list.AutoMending;
import com.zxy.wuhuclient.features_list.CloseTheContainerAfterOpening;
import com.zxy.wuhuclient.features_list.Synthesis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.zxy.wuhuclient.Utils.InventoryUtils.client;
import static com.zxy.wuhuclient.Utils.InventoryUtils.switchInv;
import static com.zxy.wuhuclient.features_list.SyncInventory.num;
import static com.zxy.wuhuclient.features_list.SyncInventory.syncInv;

@Mixin(value = ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"),method = "onInventory")
    public void onInventory(InventoryS2CPacket packet, CallbackInfo ci){
        if(Synthesis.step!=0)Synthesis.onInventory();
        if(InventoryUtils.switchItem)switchInv();
        if(num == 3 || num == 1)syncInv();
        if (CloseTheContainerAfterOpening.step == 2) {
            CloseTheContainerAfterOpening.step();
        }
    }
    @Inject(at = @At("TAIL"),method = "onPlayerPositionLook")
    public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci){
//        System.out.println("speed");
    }
    @Inject(at = @At("TAIL"),method = "onItemPickupAnimation")
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci){
    }
    @Inject(at = @At("HEAD"),method = "onExperienceBarUpdate")
    public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet, CallbackInfo ci){
        if(Configs.AUTO_MENDING.getBooleanValue()) {
            AutoMending.AUTO_MENDING.tick = 0;
        }
    }
}
