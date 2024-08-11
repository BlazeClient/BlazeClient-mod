package me.nobokik.blazeclient.mod.mods;


import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.api.helpers.FPSHelper;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.gui.Renderable;
import me.nobokik.blazeclient.gui.UI;
import me.nobokik.blazeclient.menu.FirstMenu;
import me.nobokik.blazeclient.menu.ModSettings;
import me.nobokik.blazeclient.mod.GeneralSettings;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.ColorSetting;
import me.nobokik.blazeclient.mod.setting.settings.ModeSetting;
import me.nobokik.blazeclient.mod.setting.settings.NumberSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static me.nobokik.blazeclient.Client.modManager;
import static me.nobokik.blazeclient.api.util.RenderUtils.isRenderable;

public class PotionMod extends Mod implements Renderable {
    private boolean firstFrame = true;
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final ColorSetting durationText = new ColorSetting("Duration Text Color", this, new JColor(0.7f, 0.7f, 0.7f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0.5, 2, 0.1);
    public final ModeSetting fontSetting = new ModeSetting("Font", this, "Minecraft", "Minecraft", "Dosis", "Mono");
    public final BooleanSetting hideVanilla = new BooleanSetting("Hide Vanilla Potions", this, true);
    public PotionMod() {
        super("Potion HUD", "Shows your potions.", "\uF0C3");
        toggleVisibility();
    }

    public void toggleVisibility() {
        ImguiLoader.addRenderable(this);
    }


    @Override
    public void render() {
        if(!Client.modManager().getMod("Potion HUD").isEnabled()) {
            firstFrame = true;
            return;
        }
        if(!isRenderable()) return;

        ImFont font = ImguiLoader.getMonoFont32();
        if(fontSetting.is("Minecraft")) {
            font = ImguiLoader.getMcFont32();
        } else if (fontSetting.is("Dosis")) {
            font = ImguiLoader.getDosisFont32();
        } else if (fontSetting.is("Mono")) {
            font = ImguiLoader.getMonoFont32();
        }
        font.setScale(scale.getFValue());

        int imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
        imGuiWindowFlags |= ImGuiWindowFlags.NoFocusOnAppearing;
        imGuiWindowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoScrollbar;
        imGuiWindowFlags |= ImGuiWindowFlags.AlwaysAutoResize;
        float[] c;
        if(!FirstMenu.getInstance().isVisible) {
            imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
            imGuiWindowFlags |= ImGuiWindowFlags.NoBackground;
            ImGui.pushStyleColor(ImGuiCol.Border, 0f,0f,0f,0f);
            ImGui.pushStyleColor(ImGuiCol.WindowBg, 1f, 1f, 1f, 1f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Border, 1f, 1f, 1f, 1f);
            ImGui.pushStyleColor(ImGuiCol.WindowBg, 1f, 1f, 1f, 0.3f);
        }

        if(this.updatedPos.x != 0) {
            this.position.x = this.position.x + this.updatedPos.x;
            this.updatedPos.x = 0;
            ImGui.setNextWindowPos(this.position.x, this.position.y);
        }
        if(this.updatedPos.y != 0) {
            this.position.y = this.position.y + this.updatedPos.y;
            this.updatedPos.y = 0;
            ImGui.setNextWindowPos(this.position.x, this.position.y);
        }
        if(firstFrame) {
            ImGui.setNextWindowPos(this.position.x, this.position.y);
        }
        ImGui.pushFont(font);
        ImGui.getStyle().setWindowRounding(0);
        ImGui.begin(this.getName(), imGuiWindowFlags);

        ImGui.indent(5f);
        if(mc.player != null) {
            List<StatusEffectInstance> effects = new ArrayList<>(mc.player.getStatusEffects());
            for (StatusEffectInstance effect : effects) {
                String name = Text.translatable(effect.getTranslationKey()).getString();
                if (textShadow.isEnabled()) {
                    c = text.getColor().getFloatColor();
                    ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                    UI.shadowText( name + " " + (effect.getAmplifier() + 1), 32, c[0], c[1], c[2], c[3]);
                    ImGui.popStyleColor(1);
                    c = durationText.getColor().getFloatColor();
                    ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                    UI.shadowText(StatusEffectUtil.getDurationText(effect, 1, 20).getString(), 32, c[0], c[1], c[2], c[3]);
                    ImGui.popStyleColor(1);
                } else {
                    c = text.getColor().getFloatColor();
                    ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                    ImGui.text(name + " " + (effect.getAmplifier() + 1));
                    ImGui.popStyleColor(1);
                    c = durationText.getColor().getFloatColor();
                    ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                    ImGui.text(StatusEffectUtil.getDurationText(effect, 1, 20).getString());
                    ImGui.popStyleColor(1);
                }
            }
        } else {
            if (textShadow.isEnabled()) {
                c = text.getColor().getFloatColor();
                ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                UI.shadowText("Speed 2", 32, c[0], c[1], c[2], c[3]);
                ImGui.popStyleColor(1);
                c = durationText.getColor().getFloatColor();
                ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                UI.shadowText("00:00", 32, c[0], c[1], c[2], c[3]);
                ImGui.popStyleColor(1);
            } else {
                c = text.getColor().getFloatColor();
                ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                ImGui.text("Speed 2");
                ImGui.popStyleColor(1);
                c = durationText.getColor().getFloatColor();
                ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                ImGui.text("00:00");
                ImGui.popStyleColor(1);
            }
        }
        ImGui.unindent();
        ImGui.popStyleColor(2);
        ImGui.popFont();
        font.setScale(1f);
        ImGui.getStyle().setWindowRounding(8);
        this.position = ImGui.getWindowPos();
        isFocused = ImGui.isWindowFocused();

        if(FirstMenu.getInstance().isVisible) {
            ImGui.pushFont(ImguiLoader.getFontAwesome18());
            ImGui.pushStyleColor(ImGuiCol.Button, 0.95f, 0.55f, 0.66f, 0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.95f, 0.55f, 0.66f, 0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.95f, 0.55f, 0.66f, 0f);
            ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, 0.9f);
            ImGui.setCursorPos(0, 0);
            if (ImGui.button("\uF013", 22f, 22f)) {
                ModSettings.getInstance().mod = this;
                ModSettings.getInstance().isVisible = true;
            }
            ImGui.setCursorPos(22, 0);
            if (ImGui.button("\uF00D", 22f, 22f)) {
                this.toggle();
            }
            ImGui.popFont();
            ImGui.popStyleColor(4);
        }
        ImGui.end();
        if(firstFrame) firstFrame = false;
    }
}
