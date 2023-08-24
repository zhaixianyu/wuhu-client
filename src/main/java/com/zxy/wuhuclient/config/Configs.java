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
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;

import static com.zxy.wuhuclient.WuHuClientMod.MOD_ID;

public class Configs implements IConfigHandler {
    public static Configs INSTANCE = new Configs();
    private static final String FILE_PATH = "./config/" + MOD_ID + ".json";
    private static final File CONFIG_DIR = new File("./config");
    public static final ConfigBooleanHotkeyed SYNTHESIS = new ConfigBooleanHotkeyed( "合成助手", false, "Z,C","");
    public static final ConfigHotkey WUHU_CLIENT = new ConfigHotkey( "打开设置菜单", "Z,X","");
    public static final ConfigBooleanHotkeyed AUTO_STORAGE = new ConfigBooleanHotkeyed( "合成后自动装箱",false,"","记录第一个右键的容器，当背包有合成物且大于1的时候，就会重复尝试打开记录的容器并放入");

    public static final ImmutableList<IConfigBase> ALL_CONFIGS = ImmutableList.of(
            SYNTHESIS,
            WUHU_CLIENT,
            AUTO_STORAGE
    );
    //按下时激活
    public static final ImmutableList<ConfigHotkey> KEY_LIST = ImmutableList.of(
            WUHU_CLIENT
    );
    //切换型开关
    public static final ImmutableList<IHotkeyTogglable> SWITCH_KEY = ImmutableList.of(
            SYNTHESIS,
            AUTO_STORAGE
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
