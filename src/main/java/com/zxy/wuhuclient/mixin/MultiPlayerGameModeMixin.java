package com.zxy.wuhuclient.mixin;


import com.zxy.wuhuclient.Utils.InventoryUtils;
import com.zxy.wuhuclient.Utils.Messager;
import com.zxy.wuhuclient.Utils.ScreenManagement;
import com.zxy.wuhuclient.features_list.Synthesis;
import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.zxy.wuhuclient.features_list.Synthesis.*;


@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(at = @At("HEAD"),method = "useItemOn")
    public void interactBlock(LocalPlayer player,
                              //#if MC > 11802

                              //#else
                              //$$ ClientLevel world,
                              //#endif
                              InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir){
//        System.out.println("interactBlock");
        if(isLoadMod && Configs.SYNTHESIS.getBooleanValue() && step != 1){
            if (InventoryUtils.isInventory(hitResult.getBlockPos())) {
                if(autoStorage){
                    storagePos = hitResult.getBlockPos();
                }else {
                    invUpdated = false;
                    step = 3;
                    ScreenManagement.closeScreen = 1;
                }
            }
        }
    }
    @Inject(at = @At("TAIL"),method = "startDestroyBlock")
    public void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir){
        if(isLoadMod && Configs.SYNTHESIS.getBooleanValue()){
            if(pos.equals(Synthesis.pos)){
                Synthesis.pos = null;
                step = 0;
                Messager.actionBar("合成停止");
                minecraft.player.closeContainer();
                return;
            }
            Synthesis.start(pos);
        }
    }
}
