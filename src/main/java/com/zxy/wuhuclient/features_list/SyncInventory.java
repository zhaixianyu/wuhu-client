package com.zxy.wuhuclient.features_list;

import com.zxy.wuhuclient.Utils.HighlightBlockRenderer;
import com.zxy.wuhuclient.Utils.InventoryUtils;
import com.zxy.wuhuclient.Utils.ScreenManagement;
import com.zxy.wuhuclient.mixin.ShulkerBoxBlockAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import java.util.*;

import static com.zxy.wuhuclient.Utils.InventoryUtils.equalsItem;
import static com.zxy.wuhuclient.Utils.ZxyUtils.siftBlock;
import static com.zxy.wuhuclient.WuHuClientMod.client;
import static com.zxy.wuhuclient.config.Configs.*;
import static net.minecraft.world.level.block.ShulkerBoxBlock.FACING;

public class SyncInventory {
    public static LinkedList<BlockPos> syncPosList = new LinkedList<>();
    public static ArrayList<ItemStack> targetBlockInv;
    public static int num = 0;
    static BlockPos blockPos = null;
    static String syncInventoryId = "syncInventory";
    static Set<BlockPos> highlightPosList = new LinkedHashSet<>();
    static Map<ItemStack, Integer> targetItemsCount = new HashMap<>();
    static Map<ItemStack, Integer> playerItemsCount = new HashMap<>();

    public static void getReadyColor() {
        HighlightBlockRenderer.createHighlightBlockList(syncInventoryId,SYNC_INVENTORY_COLOR);
        highlightPosList = HighlightBlockRenderer.getHighlightBlockPosList(syncInventoryId);
    }

    public static void startOrOffSyncInventory() {
        getReadyColor();
        if (client.hitResult != null && client.hitResult.getType() == HitResult.Type.BLOCK && syncPosList.isEmpty()) {
            BlockPos pos = ((BlockHitResult) client.hitResult).getBlockPos();
            BlockState blockState = client.level.getBlockState(pos);
            Block block = null;
            if (client.level != null) {
                block = client.level.getBlockState(pos).getBlock();
                BlockEntity blockEntity = client.level.getBlockEntity(pos);
                boolean inventory = InventoryUtils.isInventory(pos);
                try {
                    if ((inventory && blockState.getMenuProvider(client.level,pos) == null)  ||
                            (blockEntity instanceof ShulkerBoxBlockEntity entity &&
                                    !ShulkerBoxBlockAccessor.canOpen(blockState,client.level,pos,entity))) {
                        client.gui.setOverlayMessage(Component.literal("容器无法打开"), false);
                        return;
                    }else if(!inventory) {
                        client.gui.setOverlayMessage(Component.literal("这不是容器 无法同步"), false);
                        return;
                    }
                } catch (Exception e) {
                    client.gui.setOverlayMessage(Component.literal("这不是容器 无法同步"), false);
                    return;
                }
            }
            String blockName = BuiltInRegistries.BLOCK.getKey(block).toString();
            syncPosList.addAll(siftBlock(blockName));
            if (!syncPosList.isEmpty()) {
                if (client.player == null) return;
                client.player.closeContainer();
                if (!openInv(pos, false)) {
                    syncPosList = new LinkedList<>();
                    return;
                }
                highlightPosList.addAll(syncPosList);
                ScreenManagement.closeScreen++;
                num = 1;
            }
        } else if(!syncPosList.isEmpty()){
            highlightPosList.removeAll(syncPosList);
            syncPosList = new LinkedList<>();
            if (client.player != null) client.player.clientSideCloseContainer();
            num = 0;
            client.gui.setOverlayMessage(Component.literal("已取消同步"), false);
        }
    }

    public static boolean openInv(BlockPos pos, boolean ignoreThePrompt) {
        if (client.player != null && client.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > 25D) {
            if (!ignoreThePrompt) client.gui.setOverlayMessage(Component.literal("距离过远无法打开容器"), false);
            return false;
        }
        if (client.gameMode != null) {
            //#if MC > 11802
            client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.DOWN, pos, false));
            //#else
            //$$ client.gameMode.useItemOn(client.player,client.level, InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos), Direction.DOWN,pos,false));
            //#endif
            return true;
        } else return false;
    }

    public static void itemsCount(Map<ItemStack, Integer> itemsCount, ItemStack itemStack) {
        // 判断是否存在可合并的键
        Optional<Map.Entry<ItemStack, Integer>> entry = itemsCount.entrySet().stream()
                //#if MC > 12004
                .filter(e -> ItemStack.isSameItemSameComponents(e.getKey(), itemStack))
                //#else
                //$$ .filter(e -> ItemStack.isSameItemSameTags(e.getKey(), itemStack))
                //#endif
                .findFirst();

        if (entry.isPresent()) {
            // 更新已有键对应的值
            Integer count = entry.get().getValue();
            count += itemStack.getCount();
            itemsCount.put(entry.get().getKey(), count);
        } else {
            // 添加新键值对
            itemsCount.put(itemStack, itemStack.getCount());
        }
    }

    public static void syncInv() {
        switch (num) {
            case 1 -> {
                //按下热键后记录看向的容器 开始同步容器 只会触发一次
                targetBlockInv = new ArrayList<>();
                targetItemsCount = new HashMap<>();
                if (client.player != null && !client.player.containerMenu.equals(client.player.inventoryMenu)) {
                    for (int i = 0; i < client.player.containerMenu.slots.get(0).container.getContainerSize(); i++) {
                        ItemStack copy = client.player.containerMenu.slots.get(i).getItem().copy();
                        itemsCount(targetItemsCount, copy);
                        targetBlockInv.add(copy);
                    }
                    //上面如果不使用copy()在关闭容器后会使第一个元素号变该物品成总数 非常有趣...
//                    System.out.println("???1 "+targetBlockInv.get(0).getCount());
                    client.player.closeContainer();
//                    System.out.println("!!!1 "+targetBlockInv.get(0).getCount());
                    num = 2;
                }
            }
            case 2 -> {
                //打开列表中的容器 只要容器同步列表不为空 就会一直执行此处
                if (client.player == null) return;
                playerItemsCount = new HashMap<>();
                client.gui.setOverlayMessage(Component.literal("剩余 " + syncPosList.size() + " 个容器. 再次按下快捷键取消同步"), false);
                if (!client.player.containerMenu.equals(client.player.inventoryMenu)) return;
                NonNullList<Slot> slots = client.player.inventoryMenu.slots;
                slots.forEach(slot -> itemsCount(playerItemsCount, slot.getItem()));

                if (SYNC_INVENTORY_CHECK.getBooleanValue() && !targetItemsCount.entrySet().stream()
                        .allMatch(target -> playerItemsCount.entrySet().stream()
                                .anyMatch(player ->
                                        equalsItem(player.getKey(), target.getKey()) && target.getValue() <= player.getValue())))

                    return;


                for (BlockPos pos : syncPosList) {
                    if (!openInv(pos, true)) continue;
                    ScreenManagement.closeScreen++;
                    blockPos = pos;
                    num = 3;
                    break;
                }
                if (syncPosList.isEmpty()) {
                    num = 0;
                    client.gui.setOverlayMessage(Component.literal("同步完成"), false);
                }
            }
            case 3 -> {
                //开始同步 在打开容器后触发
                AbstractContainerMenu sc = client.player.containerMenu;
                if (sc.equals(client.player.inventoryMenu)) return;
                int size = Math.min(targetBlockInv.size(), sc.slots.get(0).container.getContainerSize());

                int times = 0;
                for (int i = 0; i < size; i++) {
                    ItemStack item1 = sc.slots.get(i).getItem();
                    ItemStack item2 = targetBlockInv.get(i).copy();
                    int currNum = item1.getCount();
                    int tarNum = item2.getCount();
                    boolean same = equalsItem(item1, item2.copy()) && !item1.isEmpty();
                    if (equalsItem(item1, item2) && currNum == tarNum) continue;
                    //不和背包交互
                    if (same) {
                        //有多
                        while (currNum > tarNum) {
                            client.gameMode.handleInventoryMouseClick(sc.containerId, i, 0, ClickType.THROW, client.player);
                            currNum--;
                        }
                    } else {
                        //不同直接扔出
                        client.gameMode.handleInventoryMouseClick(sc.containerId, i, 1, ClickType.THROW, client.player);
                        times++;
                    }
                    boolean thereAreItems = false;
                    //背包交互
                    for (int i1 = size; i1 < sc.slots.size(); i1++) {
                        ItemStack stack = sc.slots.get(i1).getItem();
                        ItemStack currStack = sc.slots.get(i).getItem();
                        currNum = currStack.getCount();
                        boolean same2 = thereAreItems = equalsItem(item2, stack);
                        if (same2 && !stack.isEmpty()) {
                            int i2 = stack.getCount();
                            client.gameMode.handleInventoryMouseClick(sc.containerId, i1, 0, ClickType.PICKUP, client.player);
                            for (; currNum < tarNum && i2 > 0; i2--) {
                                client.gameMode.handleInventoryMouseClick(sc.containerId, i, 1, ClickType.PICKUP, client.player);
                                currNum++;
                            }
                            client.gameMode.handleInventoryMouseClick(sc.containerId, i1, 0, ClickType.PICKUP, client.player);
                        }
                        //这里判断没啥用，因为一个游戏刻操作背包太多次.getStack().getCount()获取的数量不准确 下次一定优化，
                        if (currNum != tarNum) times++;
                    }
                    if (!thereAreItems) times++;
                }
                if (times == 0) {
                    syncPosList.remove(blockPos);
                    highlightPosList.remove(blockPos);
                    blockPos = null;
                }
                client.player.closeContainer();
                num = 2;
            }
        }
    }
}
