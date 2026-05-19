package com.zxy.wuhuclient.mixin.masa.litematica_easy_place_fix;

import com.zxy.wuhuclient.features_list.EasyPlaceFix;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.malilib.util.LayerRange;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.zxy.wuhuclient.config.Configs.EASY_PLACED_FIX;

@Mixin(WorldUtils.class)
public class WorldUtilsMixin {
    //#if MC > 11802
    @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", remap = true), require = 2)
    //#else
    //$$ @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", remap = true), require = 2)
    //#endif
    private static void preInteractBlock(CallbackInfoReturnable<InteractionResult> cir) {
        EasyPlaceFix.isPlacingWithEasyPlace = true;
    }
    //#if MC > 11802
    @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", shift = At.Shift.AFTER, remap = true), require = 2)
    //#else
    //$$ @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", shift = At.Shift.AFTER, remap = true), require = 2)
    //#endif
    private static void postInteractBlock(CallbackInfoReturnable<InteractionResult> cir) {
        EasyPlaceFix.isPlacingWithEasyPlace = false;
    }

    @Inject(method = "placementRestrictionInEffect", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/materials/MaterialCache;getInstance()Lfi/dy/masa/litematica/materials/MaterialCache;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD,remap = false)
    private static void stopEasyPlaceWhenBlockAlreadyCorrect(Minecraft mc, CallbackInfoReturnable<Boolean> cir, HitResult trace, ItemStack stack, BlockHitResult blockHitResult, BlockPlaceContext ctx, BlockPos pos, BlockState stateClient, Level worldSchematic, LayerRange range, boolean schematicHasAir, BlockState stateSchematic) {
        if (EASY_PLACED_FIX.getBooleanValue() && stateClient == stateSchematic) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
