package com.zxy.wuhuclient.Utils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class BlockFilters {

    //包含/比较
    public static boolean filters(String str1,String str2,String[] argument){
        ArrayList<String> strArr = new ArrayList<>(Arrays.asList(argument));
        AtomicBoolean b = new AtomicBoolean(false);
        strArr.forEach(str -> {
            switch (str){
                case "c" -> {
                    b.set(str1.contains(str2));
                }
            }
        });
        return b.get() || str1.equals(str2);
    }

//    public static boolean equalsBlockName(String blockName, BlockState blockState, BlockPos pos){
//        return equalsBlockName(blockName,blockState);
//    }

    public static boolean equalsBlockName(String blockName, BlockState blockState, BlockPos pos){
        String string = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
        String[] strs = blockName.split(",");
        String blockName1 = strs[0];
        if(strs.length > 1){
            strs = Arrays.copyOfRange(strs,1,strs.length);
        }else strs = new String[]{};

        if (Arrays.stream(strs).toList().contains("inv")) {
           return InventoryUtils.isInventory(pos);
        }

        //标签
        if (blockName1.length() > 1 && blockName1.charAt(0) == '#') {
            AtomicBoolean theLabelIsTheSame = new AtomicBoolean(false);
            String fix1 = blockName1.split("#")[1];
            String[] finalStrs = strs;
            blockState.
                    //#if MC > 12111
                    tags()
                    //#else
                    //$$ getTags()
                    //#endif
                    .forEach(tag -> {
                String tagName = tag.location().toString();
                if (BlockFilters.filters(tagName,fix1, finalStrs)) {
                    theLabelIsTheSame.set(true);
                }
            });
            return theLabelIsTheSame.get();
        }

        //中文 、 拼音
        String block = blockState.getBlock().getName().getString();
        ArrayList<String> pinYin = PinYinSearch.getPinYin(block);
        String[] finalStrs1 = strs;
        boolean py = pinYin.stream().anyMatch(p -> BlockFilters.filters(p,blockName1, finalStrs1));
        return BlockFilters.filters(block,blockName1,strs) || py || BlockFilters.filters(string,blockName1,strs);
    }
}
