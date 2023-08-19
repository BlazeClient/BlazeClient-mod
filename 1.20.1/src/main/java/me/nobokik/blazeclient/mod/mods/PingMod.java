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

import static me.nobokik.blazeclient.Client.modManager;

public class PingMod extends Mod implements Renderable {
    private boolean firstFrame = true;
    public final ColorSetting background = new ColorSetting("Background Color", this, new JColor(0f, 0f, 0f, 0.75f), true);
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0.5, 2, 0.1);
    public final NumberSetting width = new NumberSetting("Width", this, 150, 100, 250, 1);
    public final NumberSetting height = new NumberSetting("Height", this, 50, 32, 100, 1);
    public final BooleanSetting backgroundEnabled = new BooleanSetting("Background", this, true);
    public final BooleanSetting roundedCorners = new BooleanSetting("Rounded Corners", this, false);
    public final ModeSetting fontSetting = new ModeSetting("Font", this, "Minecraft", "Minecraft", "Dosis", "Mono");
    public PingMod() {
        super("Ping", "Shows your ping.", "\uF012");
        toggleVisibility();
    }

    public void toggleVisibility() {
        ImguiLoader.addRenderable(this);
    }

    private long getPing() {
        return Client.mc.getCurrentServerEntry() == null ? -1 : Client.mc.getCurrentServerEntry().ping;
    }

    @Override
    public void render() {

        if(!Client.modManager().getMod("Ping").isEnabled()) {
            firstFrame = true;
            return;
        }
        if(mc.player == null && !FirstMenu.getInstance().isVisible) return;
        if (mc.currentScreen instanceof ChatScreen && !modManager().getMod(GeneralSettings.class).showInChat.isEnabled())
            return;
        else if (mc.currentScreen instanceof InventoryScreen && !modManager().getMod(GeneralSettings.class).showInInventory.isEnabled())
            return;
        else if(mc.currentScreen != null && !FirstMenu.getInstance().isVisible)
            return;

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
        if(!backgroundEnabled.isEnabled()) imGuiWindowFlags |= ImGuiWindowFlags.NoBackground;
        float[] c;
        if(!FirstMenu.getInstance().isVisible) {
            imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
            ImGui.pushStyleColor(ImGuiCol.Border, 0f,0f,0f,0f);
            c = background.getColor().getFloatColor();
            ImGui.pushStyleColor(ImGuiCol.WindowBg, c[0], c[1], c[2], c[3]);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Border, 1f, 1f, 1f, 1f);
            c = background.getColor().jBrighter().getFloatColor();
            ImGui.pushStyleColor(ImGuiCol.WindowBg, c[0], c[1], c[2], c[3]);
        }
        c = text.getColor().getFloatColor();
        ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);

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
        ImGui.setNextWindowSize(width.getFValue() * scale.getFValue(), height.getFValue() * scale.getFValue());
        ImGui.pushFont(font);
        ImGui.getStyle().setWindowRounding(0);
        if(roundedCorners.isEnabled()) ImGui.getStyle().setWindowRounding(16f * scale.getFValue());
        ImGui.begin(this.getName(), imGuiWindowFlags);

        String text;
        if(backgroundEnabled.isEnabled()) text = getPing() + " ms";
        else text = "[" + getPing() + " ms]";

        float windowWidth = ImGui.getWindowSize().x;
        float windowHeight = ImGui.getWindowSize().y;
        float textWidth   = ImGui.calcTextSize(text).x;
        float textHeight   = ImGui.calcTextSize(text).y;

        ImGui.setCursorPos((windowWidth - textWidth) * 0.5f, (windowHeight - textHeight) * 0.5f);
        if(textShadow.isEnabled()) UI.shadowText(text, 32, c[0], c[1], c[2], c[3]);
        else ImGui.text(text);

        ImGui.popStyleColor(3);
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
