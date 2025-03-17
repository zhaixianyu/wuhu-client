package com.zxy.wuhuclient.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC < 12001
//$$ import net.minecraft.inventory.CraftingInventory;
//#else
import net.minecraft.inventory.RecipeInputInventory;
//#endif

//#if MC < 12104
import net.minecraft.screen.CraftingScreenHandler;
//#else
//$$ import net.minecraft.screen.AbstractCraftingScreenHandler;
//#endif

@Mixin(
        //#if MC < 12104
        CraftingScreenHandler.class
        //#else
        //$$ AbstractCraftingScreenHandler.class
        //#endif
)
public interface CraftingScreenHandlerMixin {
    //#if MC < 12104
    @Accessor("input")
    //#else
    //$$ @Accessor("craftingInventory")
    //#endif

    //#if MC < 12001
    //$$ CraftingInventory
    //#else
    RecipeInputInventory
    //#endif
    getInput();
}
