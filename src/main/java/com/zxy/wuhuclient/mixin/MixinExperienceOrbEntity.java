package com.zxy.wuhuclient.mixin;


import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.featuresList.AutoMending;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (ExperienceOrbEntity.class)
public class MixinExperienceOrbEntity {
        @Inject(at = @At("HEAD"),method = "onPlayerCollision")
        public void onPlayerCollision(PlayerEntity player, CallbackInfo ci){
            if(AutoMending.getAuto()!=null && Configs.AUTO_MENDING.getBooleanValue()) AutoMending.getAuto().autoMenDing(player);
    }
}
