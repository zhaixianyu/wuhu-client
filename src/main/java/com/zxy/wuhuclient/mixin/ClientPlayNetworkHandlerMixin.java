package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.featuresList.Synthesis;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//import static com.zxy.wuhuclient.featuresList.Synthesis.invUpdated;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"),method = "onScreenHandlerSlotUpdate")
    public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci){
//        invUpdated = true;
//        updatedSlot.add(packet.getSlot());
//        if(packet.getSlot() != 0){
//            System.out.println("onScreenHandlerSlotUpdate= " + packet.getSlot());
//        }

    }
//    @Inject(at = @At("TAIL"),method = "onScreenHandlerPropertyUpdate")
//    public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci){
//        System.out.println("onScreenHandlerPropertyUpdate");
//    }
    @Inject(at = @At("TAIL"),method = "onInventory")
    public void onInventory(InventoryS2CPacket packet, CallbackInfo ci){
//        System.out.println("onInventory");
        if(Synthesis.step!=0)Synthesis.onInventory();
    }
//    @Inject(at = @At("HEAD"),method = "sendPacket")
//    public void sendPacket(Packet<?> packet, CallbackInfo ci){
//        System.out.println(packet.getClass());
//    }
    @Inject(at = @At("TAIL"),method = "onItemPickupAnimation")
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci){

    }
}
