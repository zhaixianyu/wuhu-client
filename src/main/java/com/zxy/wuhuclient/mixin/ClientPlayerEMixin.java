package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.InventoryUtils;
import com.zxy.wuhuclient.Utils.ZxyUtils;
import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.features_list.AutoMending;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEMixin {
    @Mutable
    @Final
    @Shadow
    protected final Minecraft minecraft;

    public ClientPlayerEMixin(Minecraft client) {
        this.minecraft = client;
    }

    @Inject(at = @At("TAIL"),method = "tick")
    public void tick(CallbackInfo ci){
        ZxyUtils.tick();

        AutoMending.AUTO_MENDING.tick();
    }

    @Inject(at = @At("TAIL"),method = "closeContainer")
    public void closeScreen(CallbackInfo ci){
        InventoryUtils.openIng = false;
        InventoryUtils.switchItem = false;
    }


}
