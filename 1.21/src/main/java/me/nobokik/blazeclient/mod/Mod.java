package me.nobokik.blazeclient.mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.mod.setting.RenderableSetting;
import me.nobokik.blazeclient.mod.setting.Setting;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import static me.nobokik.blazeclient.menu.ModSettings.text;

public abstract class Mod {
    protected MinecraftClient mc = MinecraftClient.getInstance();

    public String name, description, icon;
    public List<Setting> settings = new ArrayList<>();
    private boolean enabled;
    private boolean showOptions;
    public boolean isFocused = false;
    public ImVec2 updatedPos = new ImVec2(0, 0);
    public ImVec2 position = new ImVec2();

    public Mod(String name, String description, @Nullable String... icon) {
        if(icon != null) this.icon = icon[0];
        this.name = name;
        this.description = description;

        enabled = false;
        showOptions = false;
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void toggle() {
        if (isEnabled()) {
            disable();
        } else {
            enable();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            Client.EVENTBUS.subscribe(this);
        } else {
            Client.EVENTBUS.unsubscribe(this);
        }
    }

    public boolean showOptions() {
        return showOptions;
    }

    public void toggleShowOptions() {
        this.showOptions = !this.showOptions;
    }

    public void enable() {
        onEnable();
        setEnabled(true);
    }

    public void disable() {
        onDisable();
        setEnabled(false);
    }

    public void onEnable() {}

    public void onDisable() {}

    public boolean nullCheck() {
        return mc.player != null && mc.world != null;
    }

    public void cleanStrings() {
        this.setName(null);
        this.setDescription(null);

        for (Setting setting : settings) {
            setting.name = null;
        }
    }

    public void renderSettings(double @Nullable ... p) {
        double percent;
        if(p == null) percent = 1;
        else percent = p[0];
        text("Settings", 1f);

        ImGui.getStyle().setChildRounding(15f);
        ImGui.getStyle().setWindowPadding(15f, 15f);

        ImGui.pushFont(ImguiLoader.getDosisFont32());
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.06f, 0.06f, 0.1f, (float) (0.6f * percent));
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.07f, 0.07f, 0.11f, (float) (0.7f * percent));
        //ImGui.beginChild("Settings", 0, 0, false, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoScrollWithMouse);
        ImVec2 pos = ImGui.getCursorPos();
        //ImGui.setCursorPos(pos.x, pos.y + 20);
        ImGui.indent(30f);

        for (Setting setting : settings) {
            if (setting instanceof RenderableSetting renderableSetting) {
                renderableSetting.render();
            }
        }

        ImGui.unindent();
        //ImGui.endChild();
        ImGui.popStyleColor(8);
        ImGui.popFont();
        ImGui.getStyle().setChildRounding(4f);
        ImGui.getStyle().setFramePadding(4f,4f);
        ImGui.getStyle().setItemSpacing(4f,4f);
        ImGui.getStyle().setWindowPadding(4f,4f);

    }
}
