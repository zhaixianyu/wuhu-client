package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.featuresList.AutoMending;
import com.zxy.wuhuclient.featuresList.Synthesis;
import com.zxy.wuhuclient.featuresList.Test;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEMixin {
    @Mutable
    @Final
    @Shadow
    protected final MinecraftClient client;

    public ClientPlayerEMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(at = @At("TAIL"),method = "tick")
    public void tick(CallbackInfo ci){
        Synthesis.tick();
        Test.tick();

        if(AutoMending.getAuto()==null) {
            new AutoMending(client);
        }else{
            AutoMending.getAuto().tick();
        }
    }
    @Inject(at = @At("TAIL"),method = "closeHandledScreen")
    public void closeHandledScreen(CallbackInfo ci){

    }
}
