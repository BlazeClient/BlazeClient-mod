package me.nobokik.blazeclient.mod.mods;

import me.nobokik.blazeclient.api.event.events.KeyPressEvent;
import me.nobokik.blazeclient.api.event.orbit.EventHandler;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZoomMod extends Mod {
    public final KeybindSetting button = new KeybindSetting("Keybinding", GLFW.GLFW_KEY_C, this);
    public final NumberSetting scale = new NumberSetting("Zoom Amount", this, 3, 1.5, 5, 0.1);
    //public final BooleanSetting smoothZoom = new BooleanSetting("Smooth Zoom", this, true);
    public final BooleanSetting smoothCamera = new BooleanSetting("Smooth Camera", this, false);

    private long pressTime = 0;
    public int scroll = 0;

    public boolean zoomEnabled = false;
    private boolean cachedSmoothCamera;
    public ZoomMod() {
        super("Zoom", "Allows you to zoom.", "\uF002");
    }

    @EventHandler
    private void onKey(KeyPressEvent event) {
        if(!this.isEnabled()) return;
        if(event.key != button.getKeyCode()) return;

        pressTime = System.currentTimeMillis();
        if (event.action == GLFW.GLFW_RELEASE) {
            scroll = 0;
            zoomEnabled = false;
            if(smoothCamera.isEnabled()) {
                mc.options.smoothCameraEnabled = cachedSmoothCamera;
            }
        } else if (event.action == GLFW.GLFW_PRESS) {
            zoomEnabled = true;
            if(smoothCamera.isEnabled()) {
                cachedSmoothCamera = mc.options.smoothCameraEnabled;
                mc.options.smoothCameraEnabled = true;
            }
        }
    }

    public double getFOV(double fov) {
        if(scroll > 15) scroll = 15;
        if(scroll < 0) scroll = 0;
        if(!zoomEnabled || !this.isEnabled()) return fov;

        fov = fov / (scale.getValue() + scroll / 2d);
        return fov;
    }
}
