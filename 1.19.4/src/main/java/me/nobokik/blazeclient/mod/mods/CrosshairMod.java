package me.nobokik.blazeclient.mod.mods;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.RenderableSetting;
import me.nobokik.blazeclient.mod.setting.Setting;
import me.nobokik.blazeclient.mod.setting.settings.ColorSetting;
import org.jetbrains.annotations.Nullable;

import static me.nobokik.blazeclient.menu.ModSettings.text;

public class CrosshairMod extends Mod {
    public final ColorSetting color = new ColorSetting("Color", this, new JColor(1f,1f,1f,1f), true);
    public final ColorSetting targetColor = new ColorSetting("Target Color", this, new JColor(1f,1f,1f,1f), true);

    public boolean[][] crosshair = new boolean[][]{
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, true, false, false, false, false, false},
            {false, false, false, false, false, true, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, true, true, false, true, false, true, true, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, true, false, false, false, false, false},
            {false, false, false, false, false, true, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false}
    };
    public CrosshairMod() {
        super("Crosshair", "Change the crosshair.", "\uF05B");
    }

    @Override
    public void renderSettings(double @Nullable ... p) {
        super.renderSettings(p);
        ImGui.pushFont(ImguiLoader.getDosisFont32());
        ImGui.indent(20f);
        ImGui.text("Crosshair");
        ImGui.popFont();
        ImGui.getStyle().setItemSpacing(0f,0f);
        ImGui.getStyle().setFrameRounding(0f);
        ImGui.getStyle().setFramePadding(0f, 0f);
        ImGui.button("##", 0f, 20f);
        int lastRow = 0;
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {
                if(lastRow != row) lastRow = row;
                else ImGui.sameLine();
                if (crosshair[row][col]) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 1f, 1f, 1f, 0.5f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1f, 1f, 1f, 0.65f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 1f, 1f, 1f, 0.75f);
                } else {
                    ImGui.pushStyleColor(ImGuiCol.Button, 1f, 1f, 1f, 0.1f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1f, 1f, 1f, 0.25f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 1f, 1f, 1f, 0.35f);
                }
                boolean b = ImGui.button("##", 20f, 20f);
                if(ImGui.isItemHovered()) {
                    if (ImGui.isMouseClicked(0)) {
                        crosshair[row][col] = !crosshair[row][col];
                    }
                }
                ImGui.popStyleColor(3);
            }
        }
        ImGui.unindent(20f);
        ImGui.getStyle().setFramePadding(4f, 4f);
        ImGui.getStyle().setFrameRounding(4f);
        ImGui.getStyle().setItemSpacing(4f,4f);
    }
}
