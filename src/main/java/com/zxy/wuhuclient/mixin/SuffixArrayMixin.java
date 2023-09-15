package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.PinYinSearch;
import com.zxy.wuhuclient.config.Configs;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(SuffixArray.class)
public class SuffixArrayMixin<T> {
    @Inject(at = @At("TAIL"),method = "findAll")
    public void findAll1(String text, CallbackInfoReturnable<List<T>> cir){
        if (!Configs.PinYin.getBooleanValue())return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.currentScreenHandler instanceof CreativeInventoryScreen.CreativeScreenHandler) {
            Registry.ITEM.stream().forEach(item -> {
                if (PinYinSearch.getPinYin(item.getName().getString().toLowerCase()).stream().anyMatch(str -> str.contains(text)) || item.toString().contains(text)) {
                    ((CreativeInventoryScreen.CreativeScreenHandler) player.currentScreenHandler).itemList.add(new ItemStack(item));
                }
            });
        }
    }
}
