package me.nobokik.blazeclient.mod.setting.settings;

import imgui.ImGui;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.KeyPressEvent;
import me.nobokik.blazeclient.api.event.orbit.EventHandler;
import me.nobokik.blazeclient.api.event.orbit.EventPriority;
import me.nobokik.blazeclient.api.util.KeyUtils;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.RenderableSetting;
import me.nobokik.blazeclient.mod.setting.Setting;
import org.lwjgl.glfw.GLFW;


public class KeybindSetting extends Setting implements RenderableSetting {

    public int code;
    private boolean isButtonWasPressed = false;

    public KeybindSetting(String name, int code, Mod parent) {
        this.name = name;
        this.code = code;
        this.parent = parent;
        parent.addSettings(this);
    }

    public int getKeyCode() {
        return this.code;
    }

    public void setKeyCode(int code) {
        this.code = code;
    }

    @Override
    public void render() {
        ImGui.pushID(parent.getName()+"/"+this.getName());

        ImGui.text(this.name);

        if (!isButtonWasPressed) {
            isButtonWasPressed = ImGui.button(KeyUtils.getKeyName(getKeyCode()));
        } else {
            ImGui.button("Press key...");
            Client.EVENTBUS.subscribe(this);
        }

        ImGui.popID();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKeyPress(KeyPressEvent event) {
        if (event.action != GLFW.GLFW_RELEASE) {
            isButtonWasPressed = false;
            Client.EVENTBUS.unsubscribe(this);

            if (event.key == GLFW.GLFW_KEY_ESCAPE)
                return;

            setKeyCode(event.key == GLFW.GLFW_KEY_DELETE ? 0 : event.key);
        }
    }
}