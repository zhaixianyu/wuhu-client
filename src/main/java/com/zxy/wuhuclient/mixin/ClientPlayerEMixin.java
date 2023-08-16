package com.zxy.wuhuclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEMixin {
    @Mutable
    @Final
    @Shadow
    protected final MinecraftClient client;
    public ClientPlayerEMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(at = @At("TAIL"),method = "tick")
    public void tick(CallbackInfo ci){
//        ItemStack touchDragStack = client.player.currentScreenHandler.getsc.getCursorStack()();
//        if (touchDragStack != null && touchDragStack.getCount() > 0) {
//            System.out.println(touchDragStack);
//            client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId,-999,1, SlotActionType.PICKUP,client.player);
//        }
    }
}
