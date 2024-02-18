package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.PinYinSearch;
import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.item.ItemStack;
//#if MC < 11900
//$$ import net.minecraft.util.registry.Registry;
//#else
import net.minecraft.registry.Registries;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SuffixArray.class)
public class SuffixArrayMixin<T> {
    @Inject(at = @At("TAIL"),method = "findAll")
    public void findAll1(String text, CallbackInfoReturnable<List<T>> cir){
        if (!Configs.PINYIN.getBooleanValue())return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.currentScreenHandler instanceof CreativeInventoryScreen.CreativeScreenHandler) {
            //#if MC < 11900
            //$$ Registry.ITEM.stream().forEach(item -> {
            //#else
            Registries.ITEM.stream().forEach(item -> {
            //#endif
                if (PinYinSearch.getPinYin(item.getName().getString().toLowerCase()).stream().anyMatch(str -> str.contains(text)) || item.toString().contains(text)) {
                    ((CreativeInventoryScreen.CreativeScreenHandler) player.currentScreenHandler).itemList.add(new ItemStack(item));
                }
            });
        }
    }
}
