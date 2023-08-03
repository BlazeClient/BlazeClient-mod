package me.nobokik.blazeclient.mod.setting.settings;

import imgui.ImGui;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.RenderableSetting;
import me.nobokik.blazeclient.mod.setting.Setting;

public class ButtonSetting extends Setting implements RenderableSetting {
    public Runnable runnable;
    public boolean sameLine;

    public ButtonSetting(String name, Mod parent, Runnable runnable) {
        this.name = name;
        this.parent = parent;
        this.runnable = runnable;
        this.sameLine = false;

        parent.addSettings(this);
    }

    public ButtonSetting(String name, Mod parent, Runnable runnable, boolean sameLine) {
        this.name = name;
        this.parent = parent;
        this.runnable = runnable;
        this.sameLine = sameLine;

        parent.addSettings(this);
    }

    @Override
    public void render() {
        ImGui.pushID(parent.getName()+"/"+this.getName());

        if (ImGui.button(name))
            runnable.run();

        if (sameLine)
            ImGui.sameLine();

        ImGui.popID();
    }
}
