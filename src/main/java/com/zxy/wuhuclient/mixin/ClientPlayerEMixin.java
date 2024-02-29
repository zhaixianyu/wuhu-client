package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.InventoryUtils;
import com.zxy.wuhuclient.Utils.ZxyUtils;
import com.zxy.wuhuclient.featuresList.AutoMending;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        ZxyUtils.tick();

        if(AutoMending.getAuto()==null) {
            new AutoMending(client);
        }else{
            AutoMending.getAuto().tick();
        }
    }
    @Inject(at = @At("TAIL"),method = "closeHandledScreen")
    public void closeScreen(CallbackInfo ci){
        InventoryUtils.openIng = false;
        InventoryUtils.switchItem = false;
    }
}
