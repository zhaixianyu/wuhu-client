package com.zxy.wuhuclient.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

import java.util.List;

import static com.zxy.wuhuclient.WuHuClientMod.MOD_ID;

public class ConfigUi extends GuiConfigsBase {
    private static Tab tab = Tab.ALL;
    public ConfigUi() {
        super(10, 50, MOD_ID, null, "wuhu-client");
    }
    @Override
    public void initGui(){
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        for (Tab tab : Tab.values()) {
            x += this.createButton(x,y,-1,tab);
        }

    }

    private int createButton(int x, int y, int width, Tab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.name);
        button.setEnabled(ConfigUi.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    //按钮宽度
//    @Override
//    protected int getConfigWidth()
//    {
//        Tab tab = ConfigUi.tab;
//
//        if (tab == Tab.ALL)
//        {
//            return 120;
//        }
//        else if (tab == Tab.GENERAL)
//        {
//            return 60;
//        }
//        return 260;
//    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<? extends IConfigBase> configs;
        Tab tab = ConfigUi.tab;
        if(tab == Tab.ALL){
            configs = Configs.ALL_CONFIGS;
        }else {
            configs = Configs.ALL_CONFIGS;
        }
        return ConfigOptionWrapper.createFor(configs);
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final ConfigUi parent;
        private final Tab tab;

        public ButtonListener(Tab tab, ConfigUi parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            ConfigUi.tab = this.tab;
            this.parent.reCreateListWidget();
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum Tab{
    ALL("全部"),
    GENERAL("通用"),

    ;

    public final String name;
    Tab(String str){
        name = str;
    }
    }
}
