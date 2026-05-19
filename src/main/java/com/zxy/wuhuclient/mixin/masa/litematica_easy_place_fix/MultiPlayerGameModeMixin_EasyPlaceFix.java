package com.zxy.wuhuclient.mixin.masa.litematica_easy_place_fix;

import com.zxy.wuhuclient.features_list.EasyPlaceFix;
import fi.dy.masa.litematica.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.zxy.wuhuclient.config.Configs.EASY_PLACED_FIX;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin_EasyPlaceFix {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    //#if MC > 11802
    private void onInteractBlock(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
    //#else
    //$$ private void onInteractBlock(LocalPlayer player, ClientLevel world , InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
    //#endif
        if (EASY_PLACED_FIX.getBooleanValue() && Configs.Generic.EASY_PLACE_MODE.getBooleanValue() && !EasyPlaceFix.isPlacingWithEasyPlace) {
            if (EasyPlaceFix.handleEasyPlaceRestriction(minecraft)) {
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }
}