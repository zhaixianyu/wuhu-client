package com.zxy.wuhuclient.Utils;

import net.minecraft.client.gui.screens.Screen;

public class ScreenManagement {
    //阻止UI显示 如果此时已经在UI中 请设置为2因为关闭UI也会调用一次
    public static int closeScreen = 0;
    public static Screen screen = null;
}
