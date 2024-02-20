package com.zxy.wuhuclient.Utils;

import com.zxy.wuhuclient.featuresList.SyncInventory;
import com.zxy.wuhuclient.featuresList.Synthesis;
import com.zxy.wuhuclient.featuresList.Test;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.selection.Box;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import java.util.LinkedList;
import java.util.List;

import static com.zxy.wuhuclient.Utils.ZxyUtils.TempData.max;
import static com.zxy.wuhuclient.Utils.ZxyUtils.TempData.min;
import static com.zxy.wuhuclient.WuHuClientMod.client;
import static fi.dy.masa.litematica.selection.SelectionMode.NORMAL;

public class ZxyUtils {
    public static boolean isLoadQuiShulker = isLoadMod("quickshulker");
    public static boolean isLoadMod(String modId){
        return FabricLoader.getInstance().isModLoaded(modId);
    }
    public static void tick(){
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
            int x = 0, y = 0, z = 0;
            if (pos != null) {
                x = pos.getX();
                y = pos.getY();
                z = pos.getZ();
            }
            if (box == null) return false;
            BlockPos kpos1 = box.getPos1();
            BlockPos kpos2 = box.getPos2();
            min = new int[]{
                    kpos1.getX() < kpos2.getX() ? kpos1.getX() : kpos2.getX(),
                    kpos1.getY() < kpos2.getY() ? kpos1.getY() : kpos2.getY(),
                    kpos1.getZ() < kpos2.getZ() ? kpos1.getZ() : kpos2.getZ()
            };
            max = new int[]{
                    kpos1.getX() > kpos2.getX() ? kpos1.getX() : kpos2.getX(),
                    kpos1.getY() > kpos2.getY() ? kpos1.getY() : kpos2.getY(),
                    kpos1.getZ() > kpos2.getZ() ? kpos1.getZ() : kpos2.getZ()
            };
            if (
                    x < min[0] || x > max[0] ||
                            y < min[1] || y > max[1] ||
                            z < min[2] || z > max[2]
            ) {
                return false;
            } else {
                return true;
            }
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
        for (int index = 0; index < box.size(); index++) {
            TempData.comparePos(box.get(index), null);
            for (int x = min[0]; x <= max[0]; x++) {
                for (int y = min[1]; y <= max[1]; y++) {
                    for (int z = min[2]; z <= max[2]; z++) {
                        BlockPos pos = new BlockPos(new BlockPos(x, y, z));
                        BlockState state = null;
                        if (client.world != null) {
                            state = client.world.getBlockState(pos);
                        }
                        Block block = state.getBlock();
                        if (Registries.BLOCK.getId(block).toString().contains(blockName)) {
                            blocks.add(pos);
                        }
                    }
                }
            }
        }
        return blocks;
    }
}
