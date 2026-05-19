package com.zxy.wuhuclient.mixin;


import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.features_list.AutoMending;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (ExperienceOrb.class)
public class MixinExperienceOrb {
        @Inject(at = @At("HEAD"),method = "playerTouch")
        public void onPlayerCollision(Player player, CallbackInfo ci){
             if(player instanceof LocalPlayer && Configs.AUTO_MENDING.getBooleanValue()) {
                AutoMending.AUTO_MENDING.tick = 0;
            }
    }
}
