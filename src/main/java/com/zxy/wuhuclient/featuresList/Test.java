package com.zxy.wuhuclient.featuresList;

import com.zxy.wuhuclient.Utils.PinYinSearch;
import com.zxy.wuhuclient.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.item.Items;

import java.util.Locale;

public class Test {
    public static void tick(){
        if(!Configs.TEST.getBooleanValue())return;
        MinecraftClient.getInstance().player.setVelocity(0,0,0);
//        mezz.jei.core.search.CombinedSearchables
//        ZombieVillagerEntity.
//        System.out.println(Items.STONE.asItem().getTranslationKey());
//        System.out.println(Items.STONE.asItem().getName().getString().toLowerCase());
    }
}
