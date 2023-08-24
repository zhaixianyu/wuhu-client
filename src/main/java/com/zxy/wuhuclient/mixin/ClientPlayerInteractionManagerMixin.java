package com.zxy.wuhuclient.mixin;


import com.zxy.wuhuclient.Utils.ScreenManagement;
import com.zxy.wuhuclient.featuresList.Synthesis;
import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.zxy.wuhuclient.featuresList.Synthesis.*;


@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("HEAD"),method = "interactBlock")
    public void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        if(isLoadMod && Configs.SYNTHESIS.getBooleanValue() && step != 1){
            if (Synthesis.isInventory(hitResult.getBlockPos())) {
                if(Configs.AUTO_STORAGE.getBooleanValue()){
                    storagePos = hitResult.getBlockPos();
                }else {
                    step = 3;
                    ScreenManagement.closeScreen = 1;
                }
            }
        }
    }
    @Inject(at = @At("TAIL"),method = "attackBlock")
    public void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir){
        if(isLoadMod && Configs.SYNTHESIS.getBooleanValue()){
            if(pos.equals(Synthesis.pos)){
                Synthesis.pos = null;
                step = 0;
                client.inGameHud.setOverlayMessage(Text.of("合成停止"),false);
                client.player.closeHandledScreen();
                return;
            }
            Synthesis.start(pos);
        }
    }
}
