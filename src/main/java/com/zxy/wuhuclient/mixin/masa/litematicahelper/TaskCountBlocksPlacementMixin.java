package com.zxy.wuhuclient.mixin.masa.litematicahelper;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zxy.wuhuclient.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.scheduler.tasks.TaskCountBlocksPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement;
import fi.dy.masa.litematica.selection.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TaskCountBlocksPlacement.class)
public class TaskCountBlocksPlacementMixin {
    //获取投影框 修改为选区

    //#if MC > 12005
    //$$ @WrapOperation(method = "<init>(Lfi/dy/masa/litematica/schematic/placement/SchematicPlacement;Lfi/dy/masa/litematica/materials/IMaterialList;Z)V",at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/schematic/placement/SchematicPlacement;getSubRegionBoxes(Lfi/dy/masa/litematica/schematic/placement/SubRegionPlacement$RequiredEnabled;)Lcom/google/common/collect/ImmutableMap;"),remap = false)
    //#else
    @WrapOperation(method = "<init>",at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/schematic/placement/SchematicPlacement;getSubRegionBoxes(Lfi/dy/masa/litematica/schematic/placement/SubRegionPlacement$RequiredEnabled;)Lcom/google/common/collect/ImmutableMap;"),remap = false)
    //#endif
    private ImmutableMap<String, Box> getSubRegionBoxes(SchematicPlacement instance, SubRegionPlacement.RequiredEnabled boxOriginAbsolute, Operation<ImmutableMap<String, Box>> original){
        if(Configs.LITEMATICA_HELPER.getBooleanValue() && DataManager.getSelectionManager().getCurrentSelection() != null){
            return DataManager.getSelectionManager().getCurrentSelection().getAllSubRegions();
        }else return original.call(instance, boxOriginAbsolute);
    }
}
