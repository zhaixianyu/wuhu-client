package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.Utils.PinYinSearch;
import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.searchtree.SuffixArray;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.zxy.wuhuclient.Utils.InventoryUtils.getItemName;

@Mixin(SuffixArray.class)
public class SuffixArrayMixin<T> {
    @Inject(at = @At("TAIL"),method = "search")
    public void findAll1(String text, CallbackInfoReturnable<List<T>> cir){
        if (!Configs.PINYIN.getBooleanValue()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            BuiltInRegistries.ITEM.stream().forEach(item -> {
                if (PinYinSearch.hasPinYin(getItemName(item.getDefaultInstance()).toLowerCase(),text) || item.toString().contains(text)) {
                    ((CreativeModeInventoryScreen.ItemPickerMenu) player.containerMenu).items.add(new ItemStack(item));
                }
            });
        }
    }
}
