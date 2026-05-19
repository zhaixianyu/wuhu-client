package com.zxy.wuhuclient.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.zxy.wuhuclient.Utils.ScreenManagement.closeScreen;
import static com.zxy.wuhuclient.features_list.Synthesis.*;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(at = @At("TAIL"),method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V")
    private static void dropStacks(BlockState state, LevelAccessor world, BlockPos pos, BlockEntity blockEntity, CallbackInfo ci){
        if(!world.isClientSide()) return;
        if((step == 1  && pos.equals(dropPos)) || (step == 3 && (pos.equals(storagePos)))){
//            System.out.println("onBreak  " + step);
            closeScreen = 0;
            //加了这一行单机会崩溃！！！？？？
//            if (client.player != null && !client.player.currentScreenHandler.equals(client.player.playerScreenHandler)) client.player.closeHandledScreen();
            step = 0;
        }
    }
}
