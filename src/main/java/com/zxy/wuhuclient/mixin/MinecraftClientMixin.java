package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.ScreenManagement;
import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.features_list.Synthesis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static com.zxy.wuhuclient.Utils.ScreenManagement.closeScreen;
import static com.zxy.wuhuclient.features_list.Synthesis.*;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(at = @At(value = "HEAD"),method = "setScreen", cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci){
        if(closeScreen > 0 && screen instanceof HandledScreen<?>){
            closeScreen--;
            ScreenManagement.screen = screen;
            ci.cancel();
        }
    }
    @Inject(at = @At("HEAD"),method = "doItemUse")
    public void doItemUse(CallbackInfo ci){
        if ((Synthesis.autoStorage = Configs.SYNTHESIS.getBooleanValue()) && player != null && player.isSneaking()) {
            client.inGameHud.setOverlayMessage(Text.of("合成装容已标记"), false);
        }
    }
}
