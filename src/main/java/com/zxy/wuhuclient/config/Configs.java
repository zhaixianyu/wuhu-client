package com.zxy.wuhuclient.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;

import static com.zxy.wuhuclient.WuHuClientMod.MOD_ID;

public class Configs implements IConfigHandler {
    public static Configs INSTANCE = new Configs();
    private static final String FILE_PATH = "./config/" + MOD_ID + ".json";
    private static final File CONFIG_DIR = new File("./config");
    public static final ConfigHotkey WUHU_CLIENT = new ConfigHotkey( "打开设置菜单", "Z,X","");
    public static final ConfigBooleanHotkeyed SYNTHESIS = new ConfigBooleanHotkeyed( "合成助手", false, "Z,C","");
    public static final ConfigBooleanHotkeyed AUTO_MENDING = new ConfigBooleanHotkeyed( "自动经验修补", false, "","在获取经验的时候可以将背包中带有经验修补且未满耐久的物品放到副手，"+"\n" +
            "修补完成后或一段时间未获得经验后放回原位。如果经验不是持续获得 可能不稳定");
    public static final ConfigBoolean PINYIN = new ConfigBoolean( "拼音搜索",true,"拼音，首字母简拼搜索 支持masa系列和创造搜索栏");
    public static final ConfigBooleanHotkeyed QUICK_SHULKER = new ConfigBooleanHotkeyed( "快捷盒子支持", false, "","需要服务端有快捷盒子mod，否则会出现无法打开UI的情况\n");
    public static final ConfigBooleanHotkeyed REMOTE_INVENTORY = new ConfigBooleanHotkeyed( "远程库存", false, "","需要服务端有远程库存mod，否则会出现无法打开UI的情况\n");
    public static final ConfigBooleanHotkeyed TEST = new ConfigBooleanHotkeyed( "test",false,"","");

    public static final ImmutableList<IConfigBase> ALL_CONFIGS = ImmutableList.of(
            WUHU_CLIENT,
            SYNTHESIS,
            AUTO_MENDING,
            PINYIN,
            QUICK_SHULKER,
            TEST
    );
    //按下时激活
    public static final ImmutableList<ConfigHotkey> KEY_LIST = ImmutableList.of(
            WUHU_CLIENT
    );
    //切换型开关
    public static final ImmutableList<IHotkeyTogglable> SWITCH_KEY = ImmutableList.of(
            SYNTHESIS,
            QUICK_SHULKER,
//            AUTO_STORAGE,
            TEST
    );
    @Override
    public void load() {
        File settingFile = new File(FILE_PATH);
        if (settingFile.isFile() && settingFile.exists()) {
            JsonElement jsonElement = JsonUtils.parseJsonFile(settingFile);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject obj = jsonElement.getAsJsonObject();
                ConfigUtils.readConfigBase(obj, MOD_ID, ALL_CONFIGS);
            }
        }
    }

    @Override
    public void save() {
        if ((CONFIG_DIR.exists() && CONFIG_DIR.isDirectory()) || CONFIG_DIR.mkdirs()) {
            JsonObject configRoot = new JsonObject();
            ConfigUtils.writeConfigBase(configRoot, MOD_ID, ALL_CONFIGS);
            JsonUtils.writeJsonToFile(configRoot, new File(FILE_PATH));
        }
    }

}
