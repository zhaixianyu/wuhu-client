package com.zxy.wuhuclient.mixin.masa.litematicahelper;

import com.zxy.wuhuclient.Utils.ZxyUtils;
import com.zxy.wuhuclient.config.Configs;
import fi.dy.masa.litematica.scheduler.tasks.TaskCountBlocksArea;
import fi.dy.masa.litematica.scheduler.tasks.TaskCountBlocksPlacement;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {TaskCountBlocksArea.class, TaskCountBlocksPlacement.class})
public class TaskCountBlocksAreaMixin {
    @Inject(at = @At(value = "HEAD"), method = "countAtPosition", cancellable = true)
    private void countAtPosition(BlockPos pos, CallbackInfo ci) {
        if(Configs.LITEMATICA_HELPER.getBooleanValue() && !ZxyUtils.TempData.xuanQuFanWeiNei_p(pos)) ci.cancel();
    }
}
