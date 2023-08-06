package me.nobokik.blazeclient.mod.mods;

import me.nobokik.blazeclient.api.event.events.KeyPressEvent;
import me.nobokik.blazeclient.api.event.events.TickEvent;
import me.nobokik.blazeclient.api.event.orbit.EventHandler;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.ColorSetting;
import me.nobokik.blazeclient.mod.setting.settings.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class FreelookMod extends Mod {
    public final KeybindSetting button = new KeybindSetting("Keybinding", GLFW.GLFW_KEY_V, this);
    public final BooleanSetting toggle = new BooleanSetting("Toggle", this, false);
    public final BooleanSetting invert = new BooleanSetting("Invert", this, false);
    private boolean isPressed = false;
    private boolean wasPressed = false;
    public boolean active = false;
    private float yaw, pitch;

    private Perspective previousPerspective;
    public FreelookMod() {
        super("Freelook", "Be able to move camera without moving.", "\uF183");
    }

    @EventHandler
    private void onKey(KeyPressEvent event) {
        if(!this.isEnabled()) return;
        if(event.key != button.getKeyCode()) return;

        if (event.action == GLFW.GLFW_RELEASE) {
            wasPressed = false;
            isPressed = false;
        } else if (event.action == GLFW.GLFW_PRESS) {
            wasPressed = true;
            isPressed = true;
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post e) {
        if(wasPressed) wasPressed = false;
        if (!this.isEnabled())
            return;

        if (toggle.isEnabled()) {
            if (wasPressed) {
                if (active) {
                    stop();
                } else {
                    start();
                }
            }
        } else {
            if (isPressed) {
                if (!active) {
                    start();
                }
            } else if (active) {
                stop();
            }
        }
    }

    private void stop() {
        active = false;
        mc.worldRenderer.scheduleTerrainUpdate();
        setPerspective(previousPerspective);
    }

    private void start() {
        active = true;

        previousPerspective = mc.options.getPerspective();
        setPerspective(Perspective.THIRD_PERSON_BACK);

        Entity camera = mc.getCameraEntity();

        if (camera == null)
            camera = mc.player;
        if (camera == null)
            return;

        yaw = camera.getYaw();
        pitch = camera.getPitch();
    }

    private void setPerspective(Perspective perspective) {
        MinecraftClient.getInstance().options.setPerspective(perspective);
    }

    public boolean consumeRotation(double dx, double dy) {
        if (!active || !this.isEnabled())
            return false;

        if (!invert.isEnabled())
            dy = -dy;

        if (MinecraftClient.getInstance().options.getPerspective().isFrontView()
                || MinecraftClient.getInstance().options.getPerspective().isFirstPerson())
            dy *= -1;

        yaw += dx * 0.15F;
        pitch += dy * 0.15F;

        if (pitch > 90) {
            pitch = 90;
        } else if (pitch < -90) {
            pitch = -90;
        }

        mc.worldRenderer.scheduleTerrainUpdate();
        return true;
    }

    public float yaw(float defaultValue) {
        if (!active || !this.isEnabled())
            return defaultValue;

        return yaw;
    }

    public float pitch(float defaultValue) {
        if (!active || !this.isEnabled())
            return defaultValue;

        return pitch;
    }
}
