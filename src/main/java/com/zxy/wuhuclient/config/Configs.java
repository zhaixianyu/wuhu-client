package com.zxy.wuhuclient.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.zxy.wuhuclient.Utils.ZxyUtils.isLoadChestTracker;
import static com.zxy.wuhuclient.Utils.ZxyUtils.isLoadQuiShulker;
import static com.zxy.wuhuclient.WuHuClientMod.MOD_ID;
import static fi.dy.masa.malilib.hotkeys.KeybindSettings.*;

public class Configs implements IConfigHandler {
    public static Configs INSTANCE = new Configs();
    private static final String FILE_PATH = "./config/" + MOD_ID + ".json";
    private static final File CONFIG_DIR = new File("./config");

    //mod
    public static final ConfigHotkey WUHU_CLIENT = new ConfigHotkey( "打开设置菜单", "Z,X","");
    public static final ConfigBooleanHotkeyed TEST = new ConfigBooleanHotkeyed( "test",false,"","");
    //功能
    public static final ConfigBooleanHotkeyed SYNTHESIS = new ConfigBooleanHotkeyed( "合成助手", false, "Z,C","");
    public static final ConfigHotkey SYNC_INVENTORY = new ConfigHotkey( "容器同步","","按下热键后会记录看向容器的物品。\n将投影选区内的同类型容器中的物品，同步至记录的容器。");
    public static final ConfigBooleanHotkeyed SYNC_INVENTORY_CHECK = new ConfigBooleanHotkeyed( "容器同步是否检查背包",true,"","开启时会检测背包中的物品是否满足填充条件，物品不足时不会打开容器。");
    public static final ConfigBooleanHotkeyed AUTO_MENDING = new ConfigBooleanHotkeyed( "自动经验修补", false, "","在获取经验的时候可以将背包中带有经验修补且未满耐久的物品放到副手，"+"\n" + "修补完成后或一段时间未获得经验后放回原位。如果经验不是持续获得 可能不稳定");
    public static final ConfigHotkey QUICK_FIREWORK = new ConfigHotkey( "快捷烟花", "",PRESS_ALLOWEXTRA,"在飞行状态时，按下热键会将背包内的烟花放到副手，使用烟花后再放回原位\n不会使用会爆炸的烟花");
    public static final ConfigHotkey TAKE_OUT_THE_LAST_ITEM = new ConfigHotkey( "取出最后一格物品", "",GUI,"在ui中按下热键后可以视为按住shift点击了打开容器的最后一个格子\n在铁砧，工作台，砂轮等ui中可以帮助你更便捷的取出输出栏物品");
    public static final ConfigBooleanHotkeyed SEARCH_BLOCK = new ConfigBooleanHotkeyed( "搜索选取内指定方块", false,"","按下后将选区内列表中的方块高亮，再次按下取消高亮");
    public static final ConfigBoolean SEARCH_BLOCK_LIMIT = new ConfigBoolean( "搜索方块渲染层数限制", false,"是否受到投影渲染层数限制影响");
    public static final ConfigStringList SEARCH_BLOCK_LIST = new ConfigStringList( "搜索方块列表",ImmutableList.of() ,"");
    public static final ConfigBooleanHotkeyed LITEMATICA_HELPER = new ConfigBooleanHotkeyed("投影材料助手",false,"","开启后将允许选区选中投影方块，会根据框选的投影更新材料列表。打开容器自动拿取对应材料\n安装箱子追踪后可高亮箱子\n"+"取物品功能来自 Fallen_Breath的TweakerMore");
    public static final ConfigBoolean LITEMATICA_HELPER_TIPS = new ConfigBoolean("投影材料助手提示",true,"","交互容器后的存放提示");
    public static final ConfigHotkey REFRESH_MATERIALS = new ConfigHotkey("刷新材料列表","","相当于点击了材料列表的刷新按钮");
    public static final ConfigHotkey ADD_INVENTORY = new ConfigHotkey( "打开容器后关闭", "","按下后将打开选区内的容器然后关闭，再次按下取消高亮");

    //兼容
    public static final ConfigBoolean PINYIN = new ConfigBoolean( "拼音搜索",true,"拼音/首字母简拼搜索 支持masa系列和创造搜索栏");
    public static final ConfigBoolean EASY_PLACED_FIX = new ConfigBoolean( "轻松放置修复",true,"修复轻松放置多放置方块的bug\n此功能来自 Earthcomputer");
    public static final ConfigBooleanHotkeyed QUICK_SHULKER = new ConfigBooleanHotkeyed( "快捷盒子支持", false, "","需要服务端有快捷盒子mod，否则会出现无法打开UI的情况\n" +
            "中键投影或轻松放置可以从濳影盒中直接取出物品(需要背包中有空位)");
//    public static final ConfigBooleanHotkeyed REMOTE_INVENTORY = new ConfigBooleanHotkeyed( "远程库存", false, "","需要服务端有远程库存mod，否则会出现无法打开UI的情况\n");

    //color
    public static final ConfigColor SYNC_INVENTORY_COLOR = new ConfigColor("容器同步高亮颜色","#4CFF4CE6","");
    public static final ConfigColor SEARCH_BLOCK_COLOR = new ConfigColor("搜索方块高亮颜色","#4CFFB912","");
    public static final ConfigColor LITEMATICA_HELPER_COLOR = new ConfigColor("投影材料助手高亮颜色","#4C6272E9","需要安装箱子追踪后才会高亮");

    public static final ImmutableList<IConfigBase> COMPATIBILITY = addCompatibility();
    public static ImmutableList<IConfigBase> addCompatibility(){
        List<IConfigBase> list = new ArrayList<>();
        list.add(WUHU_CLIENT);
        if(isLoadQuiShulker) list.add(QUICK_SHULKER);
//        list.add(REMOTE_INVENTORY);
        list.add(PINYIN);
        list.add(EASY_PLACED_FIX);

        return ImmutableList.copyOf(list);
    }
    public static final ImmutableList<IConfigBase> FUNCTION = addFunction();
    public static ImmutableList<IConfigBase> addFunction(){
        List<IConfigBase> list = new ArrayList<>();
        list.add(SYNTHESIS);
        list.add(AUTO_MENDING);
        list.add(SYNC_INVENTORY);
        list.add(SYNC_INVENTORY_CHECK);
        list.add(SEARCH_BLOCK);
        list.add(SEARCH_BLOCK_LIMIT);
        list.add(SEARCH_BLOCK_LIST);
        list.add(LITEMATICA_HELPER);
        list.add(LITEMATICA_HELPER_TIPS);
        list.add(REFRESH_MATERIALS);
        list.add(ADD_INVENTORY);
        list.add(QUICK_FIREWORK);
        list.add(TAKE_OUT_THE_LAST_ITEM);
//        list.add(TEST);

        return ImmutableList.copyOf(list);
    }

    public static final ImmutableList<IConfigBase> COLOR = addColor();
    public static ImmutableList<IConfigBase> addColor(){
        List<IConfigBase> list = new ArrayList<>();
        list.add(SYNC_INVENTORY_COLOR);
        list.add(SEARCH_BLOCK_COLOR);
        if(isLoadChestTracker) list.add(LITEMATICA_HELPER_COLOR);
        return ImmutableList.copyOf(list);
    }

    //按下时激活一次
    public static final ImmutableList<ConfigHotkey> KEY_LIST = ImmutableList.of(
            WUHU_CLIENT,
            SYNC_INVENTORY,
            QUICK_FIREWORK,
            TAKE_OUT_THE_LAST_ITEM,
            ADD_INVENTORY,
            REFRESH_MATERIALS
    );
    //切换型开关
    public static final ImmutableList<IHotkeyTogglable> SWITCH_KEY = ImmutableList.of(
            SYNTHESIS,
            QUICK_SHULKER,
            SYNC_INVENTORY_CHECK,
            LITEMATICA_HELPER,
            AUTO_MENDING,
            SEARCH_BLOCK,
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
            JsonElement jsonElement = JsonUtils.parseJsonFile(settingFile
                    //#if MC > 12111
                    .toPath()
                    //#endif
            );
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
            JsonUtils.writeJsonToFile(configRoot, new File(FILE_PATH)
                            //#if MC > 12111
                            .toPath()
                            //#endif
            );
        }
    }
}
