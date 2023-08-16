package com.zxy.wuhuclient.mixin;


import com.zxy.wuhuclient.featuresList.Synthesis;
import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.zxy.wuhuclient.featuresList.Synthesis.isLoadMod;


@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Inject(at = @At("HEAD"),method = "interactBlock")
    public void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        System.out.println("右键了一个方块");
    }
    @Inject(at = @At("TAIL"),method = "attackBlock")
    public void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir){
        if(isLoadMod && Configs.SYNTHESIS.getBooleanValue()){
            Synthesis.start();
        }
    }
}
