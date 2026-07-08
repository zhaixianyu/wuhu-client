package com.zxy.wuhuclient.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;

public class Messager {
    public static Minecraft minecraft = Minecraft.getInstance();
    public static void actionBar(String str){
        Minecraft minecraftClient = Minecraft.getInstance();
        //#if MC > 11802
        MutableComponent translatable = Component.translatable(str);
        //#else
        //$$ Component translatable = new TranslatableComponent(str);
        //#endif

        //#if MC > 260100
        minecraftClient.gui.hud.setOverlayMessage(translatable,false);
        //#else
        //$$ minecraftClient.gui.setOverlayMessage(translatable,false);
        //#endif
    }
}
