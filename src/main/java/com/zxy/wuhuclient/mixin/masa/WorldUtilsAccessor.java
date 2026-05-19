package com.zxy.wuhuclient.mixin.masa;

import fi.dy.masa.litematica.util.WorldUtils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldUtils.class)
public interface WorldUtilsAccessor {
    @Invoker
    static boolean invokePlacementRestrictionInEffect(Minecraft mc) {
        throw new UnsupportedOperationException();
    }
}