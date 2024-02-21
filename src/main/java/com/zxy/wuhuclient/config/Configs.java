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
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.zxy.wuhuclient.Utils.ZxyUtils.isLoadQuiShulker;
import static com.zxy.wuhuclient.WuHuClientMod.MOD_ID;

public class Configs implements IConfigHandler {
    public static Configs INSTANCE = new Configs();
    private static final String FILE_PATH = "./config/" + MOD_ID + ".json";
    private static final File CONFIG_DIR = new File("./config");

    //mod
    public static final ConfigHotkey WUHU_CLIENT = new ConfigHotkey( "打开设置菜单", "Z,X","");
    public static final ConfigBooleanHotkeyed TEST = new ConfigBooleanHotkeyed( "test",false,"","");
    //功能
    public static final ConfigBooleanHotkeyed SYNTHESIS = new ConfigBooleanHotkeyed( "合成助手", false, "Z,C","");
    public static final ConfigHotkey SYNC_INVENTORY = new ConfigHotkey( "容器同步","","");
    public static final ConfigBooleanHotkeyed SYNC_INVENTORY_CHECK = new ConfigBooleanHotkeyed( "容器同步是否检查背包",true,"","开启时会检测背包中的物品是否满足填充条件，物品不足时不会打开容器。");
    public static final ConfigBooleanHotkeyed AUTO_MENDING = new ConfigBooleanHotkeyed( "自动经验修补", false, "","在获取经验的时候可以将背包中带有经验修补且未满耐久的物品放到副手，"+"\n" +
            "修补完成后或一段时间未获得经验后放回原位。如果经验不是持续获得 可能不稳定");

    //兼容
    public static final ConfigBoolean PINYIN = new ConfigBoolean( "拼音搜索",true,"拼音，首字母简拼搜索 支持masa系列和创造搜索栏");
    public static final ConfigBooleanHotkeyed QUICK_SHULKER = new ConfigBooleanHotkeyed( "快捷盒子支持", false, "","需要服务端有快捷盒子mod，否则会出现无法打开UI的情况\n" +
            "中键投影可以从濳影盒中直接取出物品(需要背包中有空位)");
    public static final ConfigBooleanHotkeyed REMOTE_INVENTORY = new ConfigBooleanHotkeyed( "远程库存", false, "","需要服务端有远程库存mod，否则会出现无法打开UI的情况\n");

    //color
    public static final ConfigColor SYNC_INVENTORY_COLOR = new ConfigColor("容器同步高亮颜色","#4CFF4CE6","");

    public static final ImmutableList<IConfigBase> COMPATIBILITY = addCompatibility();
    public static ImmutableList<IConfigBase> addCompatibility(){
        List<IConfigBase> list = new ArrayList<>();
        list.add(WUHU_CLIENT);
        if(isLoadQuiShulker) list.add(QUICK_SHULKER);
        list.add(REMOTE_INVENTORY);
        list.add(PINYIN);

        return ImmutableList.copyOf(list);
    }
    public static final ImmutableList<IConfigBase> FUNCTION = addFunction();
    public static ImmutableList<IConfigBase> addFunction(){
        List<IConfigBase> list = new ArrayList<>();
        list.add(SYNTHESIS);
        list.add(AUTO_MENDING);
        list.add(SYNC_INVENTORY);
        list.add(SYNC_INVENTORY_CHECK);
        list.add(TEST);

        return ImmutableList.copyOf(list);
    }

    public static final ImmutableList<IConfigBase> COLOR = addColor();
    public static ImmutableList<IConfigBase> addColor(){
        List<IConfigBase> list = new ArrayList<>();
        list.add(SYNC_INVENTORY_COLOR);

        return ImmutableList.copyOf(list);
    }

    //按下时激活
    public static final ImmutableList<ConfigHotkey> KEY_LIST = ImmutableList.of(
            WUHU_CLIENT,
            SYNC_INVENTORY
    );
    //切换型开关
    public static final ImmutableList<IHotkeyTogglable> SWITCH_KEY = ImmutableList.of(
            SYNTHESIS,
            QUICK_SHULKER,
            SYNC_INVENTORY_CHECK,
//            AUTO_STORAGE,
            TEST
    );


    public static final ImmutableList<IConfigBase> ALL_CONFIGS = addAllConfigs();
    public static ImmutableList<IConfigBase> addAllConfigs(){
        List<IConfigBase> list = new ArrayList<>();
        list.addAll(COMPATIBILITY);
        list.addAll(FUNCTION);
        list.addAll(COLOR);

        return ImmutableList.copyOf(list);
    }
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
