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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.text.Text;


//#if MC > 11802
import net.minecraft.text.MutableText;

//#else
//$$ import net.minecraft.text.TranslatableText;
//#endif

import static com.zxy.wuhuclient.Utils.BlockFilters.equalsBlockName;
import static com.zxy.wuhuclient.Utils.ZxyUtils.TempData.max;
import static com.zxy.wuhuclient.Utils.ZxyUtils.TempData.min;
import static com.zxy.wuhuclient.WuHuClientMod.client;
import static com.zxy.wuhuclient.config.Configs.*;
import static fi.dy.masa.litematica.selection.SelectionMode.NORMAL;

public class ZxyUtils {
    public static boolean isLoadQuiShulker = isLoadMod("quickshulker");
    public static boolean isLoadChestTracker = isLoadMod("chesttracker");
    public static boolean isLoadMod(String modId){
        return FabricLoader.getInstance().isModLoaded(modId);
    }
    public static void tick(){
//        searchBlockThread();
        Synthesis.tick();
        Test.tick();
        if (SyncInventory.num==2) SyncInventory.syncInv();
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
            return myBox.contains(Vec3d.of(pos));
        }

        public ClientPlayerEntity player;
        public ClientWorld world;
        public WorldSchematic worldSchematic;

        public TempData(ClientPlayerEntity player, ClientWorld world, WorldSchematic worldSchematic) {
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
                if (client.world != null) {
                    state = client.world.getBlockState(pos);
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
    public static Optional<ClientPlayerEntity> getPlayer(){
        return Optional.ofNullable(client.player);
    }

    public static void actionBar(String message){
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        //#if MC > 11802
        MutableText translatable = Text.translatable(message);
        //#else
        //$$ TranslatableText translatable = new TranslatableText(message);
        //#endif
        minecraftClient.inGameHud.setOverlayMessage(translatable,false);
    }
}
