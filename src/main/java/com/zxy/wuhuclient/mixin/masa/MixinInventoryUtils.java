package com.zxy.wuhuclient.mixin.masa;


import fi.dy.masa.litematica.util.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.zxy.wuhuclient.Utils.InventoryUtils.equalsItem;
import static com.zxy.wuhuclient.Utils.InventoryUtils.items2;
import static com.zxy.wuhuclient.config.Configs.QUICK_SHULKER;

@Mixin(InventoryUtils.class)
public class MixinInventoryUtils {
    @Inject(at = @At("TAIL"),method = "schematicWorldPickBlock")
    private static void schematicWorldPickBlock(ItemStack stack, BlockPos pos, Level schematicWorld, Minecraft mc, CallbackInfo ci){
//        System.out.println(cir.getReturnValue().booleanValue());
        if (mc.player != null && !equalsItem(mc.player.getMainHandItem(),stack) && (QUICK_SHULKER.getBooleanValue())) {
            items2.add(stack.getItem());
            com.zxy.wuhuclient.Utils.InventoryUtils.switchItem();
        }
    }
}
