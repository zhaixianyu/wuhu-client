package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.ScreenManagement;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.zxy.wuhuclient.Utils.ScreenManagement.closeScreen;


@Mixin(Gui.class)
public class GuiMixin {
    @Inject(at = @At(value = "HEAD"),method = "setScreen", cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci){
        if(closeScreen > 0 && screen instanceof AbstractContainerScreen<?>){
            closeScreen--;
            ScreenManagement.screen = screen;
            ci.cancel();
        }
    }
}
