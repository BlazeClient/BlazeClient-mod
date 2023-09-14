package me.nobokik.blazeclient.mod;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.KeyPressEvent;
import me.nobokik.blazeclient.api.event.orbit.EventHandler;
import me.nobokik.blazeclient.api.helpers.KeystrokeHelper;
import me.nobokik.blazeclient.menu.FirstMenu;
import me.nobokik.blazeclient.menu.ModMenu;
import me.nobokik.blazeclient.mod.mods.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static me.nobokik.blazeclient.Client.mc;

@SuppressWarnings("unchecked")
public class ModManager {
	
	public ArrayList<Mod> mods;
	
	public ModManager() {
		mods = new ArrayList<>();
		addMods();
	}

	public boolean isModEnabled(Class<? extends Mod> modClass) {
		Mod m = getMod(modClass);
		return m != null ? m.isEnabled() : false;
	}

	public boolean isModEnabled(String name) {
		Mod m = mods.stream().filter(mm->mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		return m != null ? m.isEnabled() : false;
	}

	@Nullable
	public <T extends Mod> T getMod(Class<T> modClass) {
		for (Mod mod : mods) {
			if (modClass.isAssignableFrom(mod.getClass())) {
				return (T) mod;
			}
		}
		return null;
	}

	@Nullable
	public Mod getMod(String name) {
		for (Mod m : mods) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}

		return null;
	}

	public ArrayList<Mod> getMods() {
		return new ArrayList<>(mods);
	}
	public void addMod(Mod mod) {
		mods.add(mod);
	}

	public void addMods() {
		addMod(new GeneralSettings());

		addMod(new ScoreboardMod());
		addMod(new FPSMod());
		addMod(new CPSMod());
		addMod(new PingMod());
		addMod(new KeystrokesMod());
		addMod(new ReachDisplayMod());
		addMod(new CoordsMod());
		addMod(new ServerIPMod());
		addMod(new ToggleSprintMod());
		addMod(new ToggleSneakMod());
		addMod(new HurtCamMod());
		addMod(new PotionMod());
		addMod(new ArrowCountMod());
		addMod(new PotCountMod());
		addMod(new TotemCountMod());
		addMod(new ArmorMod());
		addMod(new CrosshairMod());
		addMod(new NametagsMod());
		addMod(new ZoomMod());
		addMod(new HitColorMod());
		addMod(new HitboxMod());
		addMod(new FreelookMod());
		addMod(new TimeMod());
		addMod(new TimeChangerMod());
	}

	@EventHandler
	private void onKeyPress(KeyPressEvent event) {
		for(KeystrokeHelper k : KeystrokeHelper.list) {
			if (event.key == k.getKey() && event.action != GLFW.GLFW_RELEASE) {
				k.setPressTime(System.currentTimeMillis());
				k.setPressed(true);
				return;
			}
			if (event.key == k.getKey()) {
				k.setPressTime(System.currentTimeMillis());
				k.setPressed(false);
				return;
			}
		}
		if (event.action == GLFW.GLFW_RELEASE)
			return;

		if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen)
			return;

		if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3))
			return;

		//mods.stream().filter(m -> m.getKey() == event.key).forEach(Mod::toggle);
		if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_BACKSPACE)) {
			ModMenu.getInstance().search.clear();
		}

		if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), Client.modManager().getMod(GeneralSettings.class).openMenu.getKeyCode())) {
			if(ModMenu.getInstance().isVisible) ModMenu.getInstance().isVisible = false;
			else FirstMenu.toggle(!FirstMenu.getInstance().isVisible);
		}


		for(Mod mod : Client.modManager().getMods()) {
			if(mod.isFocused) {
				if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
					if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT)) {
						mod.updatedPos.x = 5;
					} else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT)) {
						mod.updatedPos.x = -5;
					} else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_UP)) {
						mod.updatedPos.y = -5;
					} else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_DOWN)) {
						mod.updatedPos.y = 5;
					}
				} else {
					if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT)) {
						mod.updatedPos.x = 1;
					} else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT)) {
						mod.updatedPos.x = -1;
					} else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_UP)) {
						mod.updatedPos.y = -1;
					} else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_DOWN)) {
						mod.updatedPos.y = 1;
					}
				}
			}
		}

	}
}
