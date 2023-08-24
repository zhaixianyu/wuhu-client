package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.featuresList.Synthesis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static com.zxy.wuhuclient.Utils.ScreenManagement.closeScreen;
import static com.zxy.wuhuclient.featuresList.Synthesis.*;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("HEAD"),method = "setScreen", cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci){
        if(closeScreen > 0 && screen != null){
//            System.out.println(screen.getClass());
            closeScreen--;
            ci.cancel();
        }
    }
}
