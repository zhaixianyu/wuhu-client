package com.zxy.wuhuclient.mixin.jackf.fix;

import com.zxy.wuhuclient.Utils.InventoryUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.chesttracker.api.ClientBlockSource;
import red.jackf.chesttracker.impl.providers.InteractionTrackerImpl;

import static com.zxy.wuhuclient.Utils.ZxyUtils.isLoadPrinter;


//通过快捷盒子等方式非右键打开ui的情况会导致记录错误
@Mixin(InteractionTrackerImpl.class)
public class InteractionTrackerImplMixin {
    @Shadow(remap = false)
    private @Nullable ClientBlockSource lastSource = null;

    @Inject(at = @At("TAIL"),method = "setLastBlockSource",remap = false)
    public void setLastBlockSource(ClientBlockSource source, CallbackInfo ci) {
        if (!isLoadPrinter && !InventoryUtils.isInventory(source.pos())) {
            this.lastSource = null;
        }
    }
}
