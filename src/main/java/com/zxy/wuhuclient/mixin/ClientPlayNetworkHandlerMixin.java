package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.InventoryUtils;
import com.zxy.wuhuclient.featuresList.SyncInventory;
import com.zxy.wuhuclient.featuresList.Synthesis;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.zxy.wuhuclient.Utils.InventoryUtils.switchInv;
import static com.zxy.wuhuclient.featuresList.SyncInventory.num;
import static com.zxy.wuhuclient.featuresList.SyncInventory.syncInv;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"),method = "onInventory")
    public void onInventory(InventoryS2CPacket packet, CallbackInfo ci){
        if(Synthesis.step!=0)Synthesis.onInventory();
        if(InventoryUtils.openIng)switchInv();
        if(num == 3 || num == 1)syncInv();
    }
    @Inject(at = @At("TAIL"),method = "onPlayerPositionLook")
    public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci){
//        System.out.println("speed");
    }
    @Inject(at = @At("TAIL"),method = "onItemPickupAnimation")
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci){
    }
}
