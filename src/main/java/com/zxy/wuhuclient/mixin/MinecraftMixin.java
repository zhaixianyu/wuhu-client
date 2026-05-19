package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.ScreenManagement;
import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.features_list.Synthesis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static com.zxy.wuhuclient.Utils.ScreenManagement.closeScreen;
import static com.zxy.wuhuclient.features_list.Synthesis.*;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Nullable public LocalPlayer player;

    @Inject(at = @At(value = "HEAD"),method = "setScreen", cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci){
        if(closeScreen > 0 && screen instanceof AbstractContainerScreen<?>){
            closeScreen--;
            ScreenManagement.screen = screen;
            ci.cancel();
        }
    }
    @Inject(at = @At("HEAD"),method = "startUseItem")
    public void doItemUse(CallbackInfo ci){
        if ((Synthesis.autoStorage = Configs.SYNTHESIS.getBooleanValue()) && player != null && player.isShiftKeyDown()) {
            client.gui.setOverlayMessage(Component.literal("合成装容已标记"), false);
        }
    }
}
