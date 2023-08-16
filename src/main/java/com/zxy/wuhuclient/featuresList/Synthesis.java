package com.zxy.wuhuclient.featuresList;


import fi.dy.masa.itemscroller.recipes.RecipePattern;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.zxy.wuhuclient.Utils.ScreenManagement.closeScreen;
import static net.minecraft.block.ShulkerBoxBlock.FACING;


public class Synthesis {
    public static boolean isLoadMod = FabricLoader.getInstance().isModLoaded("itemscroller");
    public static MinecraftClient mc = MinecraftClient.getInstance();
    static RecipePattern recipe = null;
    public static int step = 0;
    //1 丢出容器物品 2 工作台合成

    public static Screen screen = null;

    public static boolean isTargetInventory() {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            if (mc.world == null) return false;
            BlockState blockState = mc.world.getBlockState(pos);
            BlockEntity blockEntity = mc.world.getBlockEntity(pos);
            try {
                if (((BlockWithEntity) blockState.getBlock()).createScreenHandlerFactory(blockState, mc.world, pos) == null ||
                        (blockEntity instanceof ShulkerBoxBlockEntity entity &&
                                !mc.world.isSpaceEmpty(ShulkerEntity.calculateBoundingBox(blockState.get(FACING), 0.0f, 0.5f).offset(pos).contract(1.0E-6)) &&
                                entity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED)) {
                    mc.inGameHud.setOverlayMessage(new TranslatableText("八嘎，目标容器无法打开"), false);
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static void start() {
        if (!updateRecipe()) {
            mc.inGameHud.setOverlayMessage(new TranslatableText("当前快捷合成配方为空"), false);
            return;
        }
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            if (mc.world != null) {
                BlockState blockState = mc.world.getBlockState(pos);
                if (blockState.isOf(Blocks.CRAFTING_TABLE)) {
                    //工作台合成
                    step = 2;
//                    closeScreen = 1;
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, (BlockHitResult) mc.crosshairTarget);
                    return;
                }
                if (!isTargetInventory()) {
                    //背包合成
                    if (recipe.getRecipeItems().length == 4) {
                        synthesis();
                    }
                    return;
                }
                step = 1;
                closeScreen = 1;
                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, (BlockHitResult) mc.crosshairTarget);
            }
        }
    }

    public static void synthesis() {

        step = 0;
        ClientPlayerEntity player = mc.player;
        //检查是否满足合成条件
        if(!isRequired()){
            player.closeHandledScreen();
            return;
        }

        ScreenHandler sc = player.currentScreenHandler;

        ItemStack[] recipeItems = recipe.getRecipeItems();
        for (int i = 0; i < sc.slots.size(); i++) {

            for (int i1 = 0; i1 < recipeItems.length; i1++) {
                if (InventoryUtils.areStacksEqual(sc.slots.get(i).getStack(),recipeItems[i1])) {
                    //扔掉跟随鼠标物品
                    if(!sc.getCursorStack().isEmpty()){
                        mc.interactionManager.clickSlot(sc.syncId,-999,0,SlotActionType.PICKUP,player);
                    }

                    if(sc.slots.get(i1+1).getStack().getCount() >= sc.slots.get(i1+1).getStack().getMaxCount()) continue;
                    //从背包拿取物品到合成位置
                    mc.interactionManager.clickSlot(sc.syncId, i, 0, SlotActionType.PICKUP, player);
                    mc.interactionManager.clickSlot(sc.syncId, i, 1, SlotActionType.PICKUP, player);


//                    mc.interactionManager.clickSlot(sc.syncId, i1+1, 0, SlotActionType.PICKUP, player);


//                    int x = sc.slots.get(i).x;
//                    int y = sc.slots.get(i).y;
                    ItemStack cursorStack = sc.getCursorStack().copy();
                    AtomicInteger i3 = new AtomicInteger();
                    Stream<ItemStack> stream = Arrays.stream(recipeItems);
                    stream.filter(o->InventoryUtils.areStacksEqual(o,cursorStack)).forEach(o-> i3.addAndGet(o.getCount()));
                    for (int i2 = 0; i2 < recipeItems.length; i2++) {
                        if (InventoryUtils.areStacksEqual(cursorStack,sc.slots.get(i2+1).getStack())) {
                            i3.addAndGet(sc.slots.get(i2 + 1).getStack().getCount());
                        }
//                        int x1 = sc.slots.get(i2+1).x;
//                        int y1 = sc.slots.get(i2+1).y;
//                        screen.mouseDragged(x,y,0,x1,y1);
                    }

                    int num = (cursorStack.getCount()- i3.get()) / recipeItems.length;
                    int remainder = (cursorStack.getCount()- i3.get()) % recipeItems.length;

                    for (int i2 = 0; i2 < recipeItems.length; i2++) {
                        if (InventoryUtils.areStacksEqual(cursorStack,sc.slots.get(i2+1).getStack())) {
                            sc.slots.get(i2+1).getStack().setCount(num);
                        }
                    }
                    sc.getCursorStack().setCount(remainder);
                    if(remainder != 0) mc.interactionManager.clickSlot(sc.syncId, i, 0, SlotActionType.PICKUP, player);

//                    b1:
//                    if(!sc.getCursorStack().isEmpty()){
//                        for (int i2 = 0; i2 < recipeItems.length; i2++) {
//                            if(InventoryUtils.areStacksEqual(recipeItems[i2],sc.getCursorStack()))
//                                mc.interactionManager.clickSlot(sc.syncId, i2+1, 0, SlotActionType.PICKUP, player);
//                            if(sc.getCursorStack().isEmpty()) break b1;
//                        }
//                        for (int i2 = 0; i2 < sc.slots.size(); i2++) {
//                            if(InventoryUtils.areStacksEqual(sc.slots.get(i2).getStack(),sc.getCursorStack()))
//                                mc.interactionManager.clickSlot(sc.syncId, i2, 0, SlotActionType.PICKUP, player);
//                            if(sc.getCursorStack().isEmpty()) break;
//                        }
//                        mc.interactionManager.clickSlot(sc.syncId,-999,0,SlotActionType.PICKUP,player);
//                    }
                }
            }
        }
//        while (InventoryUtils.areStacksEqual(sc.slots.get(0).getStack(),recipe.getResult())){
//            mc.interactionManager.clickSlot(sc.syncId, 0, 0, SlotActionType.PICKUP, player);
//            mc.interactionManager.clickSlot(sc.syncId,-999,0,SlotActionType.PICKUP,player);
//        }
//        player.closeHandledScreen();
    }
    public static boolean isRequired() {
        ItemStack[] recipeItems = recipe.getRecipeItems();
        Map<Item, Integer> recipeMap = new HashMap<>();
        Map<Item, Integer> must = new HashMap<>();
        for (ItemStack recipeItem : recipeItems) {
            Item item = recipeItem.getItem();
            if (must.containsKey(item)) {
                must.put(item, must.get(item) + recipeItem.getCount());
            }else {
                must.put(item,recipeItem.getCount());
            }
            recipeMap.put(item, 0);
        }
        for (Slot slot : mc.player.currentScreenHandler.slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty() || stack.hasNbt()) continue;
            recipeMap.forEach((k, v) -> {
                int num = stack.getMaxCount() == 1 ? 0 : 1;
                if (stack.getItem().equals(k)) recipeMap.put(k, v + (stack.getCount() - num));
            });
        }
        AtomicReference<Boolean> result = new AtomicReference<>(true);
        recipeMap.forEach((k, v) -> {
            if (v < must.get(k) ) {
                result.set(false);
                return;
            }
//            System.out.println(k + "\n" + v);
        });
        return result.get();
    }

    public static void dropItem(ItemStack itemStack, boolean isPlayerInventory) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;
        ScreenHandler sc = player.currentScreenHandler;
        int size = 0;
        if (isPlayerInventory && sc.equals(mc.player.playerScreenHandler)) {
            size = sc.slots.size();
        } else if (!isPlayerInventory && !sc.equals(player.playerScreenHandler)) {
            size = sc.slots.get(0).inventory.size();
        }
        if (size == 0) return;
        for (int i = 0; i < size; i++) {
            if (InventoryUtils.areStacksEqual(sc.slots.get(i).getStack(), itemStack)) {
                mc.interactionManager.clickSlot(sc.syncId, i, 1, SlotActionType.THROW, player);
            }
        }
    }

    public static void dropInventory() {
        step = 0;
        if (!updateRecipe()) return;
        mc = MinecraftClient.getInstance();
        ClientPlayerEntity player;
        if (mc.player == null) return;
        player = mc.player;
        if (player.currentScreenHandler.equals(player.playerScreenHandler)) return;
        for (ItemStack recipeItem : recipe.getRecipeItems()) {
            dropItem(recipeItem, false);
        }
        mc.player.closeHandledScreen();
    }

    private static boolean updateRecipe() {
        recipe = RecipeStorage.getInstance().getSelectedRecipe();
        return !recipe.getResult().isEmpty();
    }
}
