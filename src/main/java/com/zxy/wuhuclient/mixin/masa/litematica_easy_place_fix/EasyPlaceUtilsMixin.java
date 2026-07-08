package com.zxy.wuhuclient.mixin.masa.litematica_easy_place_fix;

import fi.dy.masa.litematica.util.EasyPlaceUtils;
import fi.dy.masa.malilib.util.position.LayerRange;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.zxy.wuhuclient.config.Configs.EASY_PLACED_FIX;

@Mixin(EasyPlaceUtils.class)
public abstract class EasyPlaceUtilsMixin {

    @Inject(method = "placementRestrictionInEffect(Lnet/minecraft/client/Minecraft;)Z", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/materials/MaterialCache;getInstance()Lfi/dy/masa/litematica/materials/MaterialCache;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD,remap = false)
    private static void stopEasyPlaceWhenBlockAlreadyCorrect(Minecraft mc, CallbackInfoReturnable<Boolean> cir, HitResult trace, ItemStack stack, BlockHitResult blockHitResult, BlockPlaceContext ctx, BlockPos pos, BlockState stateClient, Level worldSchematic, LayerRange range, boolean schematicHasAir, BlockState stateSchematic) {
        if (EASY_PLACED_FIX.getBooleanValue() && stateClient == stateSchematic) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
