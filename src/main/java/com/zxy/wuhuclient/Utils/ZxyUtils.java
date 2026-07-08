package com.zxy.wuhuclient.Utils;

import com.zxy.wuhuclient.features_list.SyncInventory;
import com.zxy.wuhuclient.features_list.Synthesis;
import com.zxy.wuhuclient.features_list.Test;
import com.zxy.wuhuclient.features_list.litematica_helper.LitematicaHelper;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.selection.Box;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.network.chat.Component;


//#if MC > 11802
import net.minecraft.network.chat.MutableComponent;
//#else
//$$ import net.minecraft.network.chat.TranslatableComponent;
//#endif

//#if MC > 12105
import net.minecraft.world.entity.player.Input;
//#endif

import static com.zxy.wuhuclient.Utils.BlockFilters.equalsBlockName;
import static com.zxy.wuhuclient.Utils.ZxyUtils.TempData.max;
import static com.zxy.wuhuclient.Utils.ZxyUtils.TempData.min;
import static com.zxy.wuhuclient.WuHuClientMod.client;
import static com.zxy.wuhuclient.config.Configs.*;
import static com.zxy.wuhuclient.features_list.CloseTheContainerAfterOpening.step;
import static fi.dy.masa.litematica.selection.SelectionMode.NORMAL;

public class ZxyUtils {
    public static boolean isLoadQuiShulker = isLoadMod("quickshulker");
    public static boolean isLoadChestTracker = isLoadMod("chesttracker");
    public static boolean isLoadPrinter = isLoadMod("litematica-printer");
    public static boolean isLoadMod(String modId){
        return FabricLoader.getInstance().isModLoaded(modId);
    }
    public static void tick(){
//        searchBlockThread();
        Synthesis.tick();
        Test.tick();
        step();
        if (SyncInventory.num==2) SyncInventory.syncInv();
    }

    public static void setShift(boolean shift){
        LocalPlayer player = client.player;
        //#if MC > 12105
        Input input = new Input(player.input.keyPresses.forward(), player.input.keyPresses.backward(), player.input.keyPresses.left(), player.input.keyPresses.right(), player.input.keyPresses.jump(), shift, player.input.keyPresses.sprint());
        ServerboundPlayerInputPacket packet = new ServerboundPlayerInputPacket(input);
        //#else
        //$$ ServerboundPlayerCommandPacket packet = new ServerboundPlayerCommandPacket(player, shift ? ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY : ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY);
        //#endif

        player.connection.send(packet);

    }
    public static class TempData {
        public static int[] min;
        public static int[] max;

        public static boolean xuanQuFanWeiNei_p(BlockPos pos) {
            AreaSelection i = DataManager.getSelectionManager().getCurrentSelection();
            if (i == null) return false;
            if (DataManager.getSelectionManager().getSelectionMode() == NORMAL) {
                boolean fw = false;
                List<Box> arr = i.getAllSubRegionBoxes();
                for (int j = 0; j < arr.size(); j++) {
                    if (comparePos(arr.get(j), pos)) {
                        return true;
                    } else {
                        fw = false;
                    }
                }
                return fw;
            } else {
                Box box = i.getSubRegionBox(DataManager.getSimpleArea().getName());
                return comparePos(box, pos);
            }
        }

        static boolean comparePos(Box box, BlockPos pos) {
            if (box == null || pos == null) return false;
            MyBox myBox = new MyBox(box);
            return myBox.contains(Vec3.atLowerCornerOf(pos));
        }

        public LocalPlayer player;
        public ClientLevel world;
        public WorldSchematic worldSchematic;

        public TempData(LocalPlayer player, ClientLevel world, WorldSchematic worldSchematic) {
            this.player = player;
            this.world = world;
            this.worldSchematic = worldSchematic;
        }
    }

    public static LinkedList<BlockPos> siftBlock(String blockName) {
        LinkedList<BlockPos> blocks = new LinkedList<>();
        AreaSelection i = DataManager.getSelectionManager().getCurrentSelection();
        List<Box> box;
        if (i == null) return blocks;
        box = i.getAllSubRegionBoxes();
        for (Box value : box) {
            MyBox myBox = new MyBox(value);
            for (BlockPos pos : myBox) {
                BlockState state = null;
                if (client.level != null) {
                    state = client.level.getBlockState(pos);
                }

                if (state != null && equalsBlockName(blockName, state, pos)) {
                    blocks.add(pos);
                }
            }
        }
        return blocks;
    }

    public static String searchBlockId = "searchBlock";
    public static boolean searchBlockIng = false;

    public synchronized static void searchBlock() {
        boolean searchBlockSwitch = SEARCH_BLOCK.getBooleanValue();

        if (!searchBlockSwitch) {
            Set<BlockPos> highlightBlockPosList = HighlightBlockRenderer.getHighlightBlockPosList(searchBlockId);
            if (highlightBlockPosList != null) {
                HighlightBlockRenderer.clear(searchBlockId);
                return;
            }
        }

        HighlightBlockRenderer.createHighlightBlockList(searchBlockId, SEARCH_BLOCK_COLOR);

        if (!searchBlockSwitch) return;
        LinkedHashSet<BlockPos> blockPos = new LinkedHashSet<>();
        List<String> strings = SEARCH_BLOCK_LIST.getStrings();

        for (String blockName : strings) {
            LinkedList<BlockPos> blockPosLinkedList = siftBlock(blockName);
            List<BlockPos> list = blockPosLinkedList.stream().distinct().toList();
            boolean booleanValue = SEARCH_BLOCK_LIMIT.getBooleanValue();
            blockPos.addAll(list.stream().filter(pos -> !booleanValue || DataManager.getRenderLayerRange().isPositionWithinRange(pos)).toList());
        }
        HighlightBlockRenderer.setPos(searchBlockId, blockPos);
    }
    public static void searchBlockThread() {
        if (!searchBlockIng) {
            try {
                searchBlockIng = true;
                searchBlock();
                if (isLoadChestTracker) LitematicaHelper.instance.highlightInventoryBlock();
            } finally {
                searchBlockIng = false;
            }
        }
    }
    public static Optional<LocalPlayer> getPlayer(){
        return Optional.ofNullable(client.player);
    }
}
