package com.zxy.wuhuclient.mixin;


import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.features_list.AutoMending;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (ExperienceOrbEntity.class)
public class MixinExperienceOrbEntity {
        @Inject(at = @At("HEAD"),method = "onPlayerCollision")
        public void onPlayerCollision(PlayerEntity player, CallbackInfo ci){
            if(player instanceof ClientPlayerEntity && Configs.AUTO_MENDING.getBooleanValue()) {
                AutoMending.AUTO_MENDING.tick = 0;
            }
    }
}
