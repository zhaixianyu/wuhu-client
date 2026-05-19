package com.zxy.wuhuclient.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC < 12001
//$$ import net.minecraft.world.inventory.CraftingContainer;
//#else
import net.minecraft.world.inventory.CraftingContainer;
//#endif

//#if MC < 12104
//$$ import net.minecraft.world.inventory.CraftingMenu;
//#else
import net.minecraft.world.inventory.AbstractCraftingMenu;
//#endif

@Mixin(
        //#if MC < 12104
        //$$ CraftingMenu.class
        //#else
        AbstractCraftingMenu.class
        //#endif
)
public interface CraftingMenuMixin {
    //#if MC < 12104
    //$$ @Accessor("craftSlots")
    //#else
    @Accessor("craftSlots")
    //#endif

    //#if MC < 12001
    //$$ CraftingContainer
    //#else
    CraftingContainer
    //#endif
    getCraftSlots();
}
