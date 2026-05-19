package com.zxy.wuhuclient.mixin.masa.litematicahelper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zxy.wuhuclient.config.Configs;
import fi.dy.masa.litematica.selection.SelectionManager;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static fi.dy.masa.litematica.util.RayTraceUtils.*;

@Mixin(SelectionManager.class)
public class SelectionManagerMixin {
    @WrapOperation(at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/util/RayTraceUtils;getTargetedPosition(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;DZ)Lnet/minecraft/core/BlockPos;"),method = "resetSelectionToClickedPosition")
    public BlockPos resetSelectionToClickedPosition(Level world, Entity player, double maxDistance, boolean sneakToOffset, Operation<BlockPos> original){
        return getPos(world,player,maxDistance,sneakToOffset,original);
    }
    @WrapOperation(at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/util/RayTraceUtils;getTargetedPosition(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;DZ)Lnet/minecraft/core/BlockPos;"),method = "growSelectionToContainClickedPosition")
    public BlockPos growSelectionToContainClickedPosition(Level world, Entity player, double maxDistance, boolean sneakToOffset, Operation<BlockPos> original){
        return getPos(world,player,maxDistance,sneakToOffset,original);
    }
    @WrapOperation(at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/util/RayTraceUtils;getTargetedPosition(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;DZ)Lnet/minecraft/core/BlockPos;"),method = "setPositionOfCurrentSelectionToRayTrace")
    public BlockPos setPositionOfCurrentSelectionToRayTrace(Level world, Entity player, double maxDistance, boolean sneakToOffset, Operation<BlockPos> original){
        return getPos(world,player,maxDistance,sneakToOffset,original);
    }

    @Unique
    private BlockPos getPos(Level world, Entity player, double maxDistance, boolean sneakToOffset, Operation<BlockPos> original){
        WorldSchematic schematicWorld = SchematicWorldHandler.getSchematicWorld();
        if (schematicWorld == null || !Configs.LITEMATICA_HELPER.getBooleanValue()) return original.call(world,player,maxDistance,sneakToOffset);

        BlockHitResult blockHitResult = traceToSchematicWorld(player, 200, true, true);
        if(blockHitResult == null || schematicWorld.getBlockState(blockHitResult.getBlockPos()).isAir()){
            return original.call(world, player, maxDistance, sneakToOffset);
        }
        BlockPos pos = blockHitResult.getBlockPos();
        if (sneakToOffset == player.isShiftKeyDown())
        {
            pos = pos.relative(blockHitResult.getDirection());
        }
        return pos;
    }
}
