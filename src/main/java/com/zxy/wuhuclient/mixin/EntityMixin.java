package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemFrameItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    // 隐藏物品展示框
    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void hideItemFrame(CallbackInfoReturnable<Boolean> cir) {
        if (Configs.HIDE_ITEM_FRAME.getBooleanValue()) {
            if ((Object) this instanceof ItemFrameEntity itemFrameEntity) {
                // 物品展示框内没有物品或玩家手上拿着物品展示框，不隐藏
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (itemFrameEntity.getHeldItemStack().isEmpty()
                        || player.getMainHandStack().getItem() instanceof ItemFrameItem
                        || player.getOffHandStack().getItem() instanceof ItemFrameItem) {
                    return;
                }
                cir.setReturnValue(true);
            }
        }
    }
}
