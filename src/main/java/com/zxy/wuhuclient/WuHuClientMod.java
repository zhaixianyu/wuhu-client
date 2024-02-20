package com.zxy.wuhuclient;


import com.zxy.wuhuclient.Utils.HighlightBlockRenderer;
import com.zxy.wuhuclient.config.Configs;
import com.zxy.wuhuclient.config.HotkeysCallback;
import com.zxy.wuhuclient.config.InputHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WuHuClientMod implements ClientModInitializer, ModInitializer {

	public static MinecraftClient client = MinecraftClient.getInstance();
	public static final String MOD_ID = "wuhuclient";
	public static final String MOD_NAME = "wuhuclient";
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		register();
	}
	void register(){
		Configs.INSTANCE.load();
		ConfigManager.getInstance().registerConfigHandler(MOD_ID, Configs.INSTANCE);

		InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
		InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
		HighlightBlockRenderer.init();

		HotkeysCallback.init();
	}
	@Override
	public void onInitializeClient() {
		register();
	}
}