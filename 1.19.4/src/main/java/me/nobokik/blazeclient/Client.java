package me.nobokik.blazeclient;
import me.nobokik.blazeclient.api.config.ConfigManager;
import me.nobokik.blazeclient.api.event.events.WorldRenderEvent;
import me.nobokik.blazeclient.api.event.orbit.EventBus;
import me.nobokik.blazeclient.api.event.orbit.IEventBus;
import me.nobokik.blazeclient.api.helpers.CPSHelper;
import me.nobokik.blazeclient.menu.*;
import me.nobokik.blazeclient.mod.ModManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public final class Client implements ModInitializer {
	public static String name = "Blaze";
	public static String packagePrefix = "me.nobokik.blazeclient";

	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static IEventBus EVENTBUS = new EventBus();
	public static Client INSTANCE;
	public static final Logger LOGGER = LoggerFactory.getLogger("blaze-client");
	public static ModManager modManager() {
		return INSTANCE.modManager;
	}

	public static ConfigManager configManager() {
		return INSTANCE.configManager;
	}

	public Client() {
		INSTANCE = this;
	}

	public ModManager modManager;
	public ConfigManager configManager;

	public void init() {
		this.modManager = new ModManager();
		this.configManager = new ConfigManager();

		EVENTBUS.registerLambdaFactory(packagePrefix, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		EVENTBUS.subscribe(modManager);
		EVENTBUS.subscribe(MainMenuButtons.class);
		EVENTBUS.subscribe(CPSHelper.class);
		WorldRenderEvents.END.register((context) -> { EVENTBUS.post(WorldRenderEvent.get(context)); });

		MainMenuButtons.toggleVisibility();
		FirstMenu.toggleVisibility();
		ModMenu.toggleVisibility();
		ModSettings.toggleVisibility();
		SideMenu.toggleVisibility();
		this.configManager.loadConfig();
	}

	@Override
	public void onInitialize() {
		//init();
	}
}
