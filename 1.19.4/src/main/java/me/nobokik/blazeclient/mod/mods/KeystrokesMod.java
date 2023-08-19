package me.nobokik.blazeclient.mod.mods;

import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.api.helpers.KeystrokeHelper;
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
import org.lwjgl.glfw.GLFW;

import static me.nobokik.blazeclient.Client.modManager;

public class KeystrokesMod extends Mod implements Renderable {
    private boolean firstFrame = true;
    public final ColorSetting background = new ColorSetting("Background Color", this, new JColor(0f, 0f, 0f, 0.75f), true);
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final ColorSetting pressedBackground = new ColorSetting("Pressed Background Color", this, new JColor(1f, 1f, 1f, 0.75f), true);
    public final ColorSetting pressedText = new ColorSetting("Pressed Text Color", this, new JColor(0f, 0f, 0f), false);
    public final BooleanSetting roundedCorners = new BooleanSetting("Rounded Corners", this, false);

    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final BooleanSetting mouseButtons = new BooleanSetting("Mouse Buttons", this, true);
    public final BooleanSetting spaceBar = new BooleanSetting("Space Bar", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0.5, 2, 0.1);
    public final NumberSetting fadeTime = new NumberSetting("Fade Time", this, 1, 0, 1000, 1);
    public final ModeSetting fontSetting = new ModeSetting("Font", this, "Minecraft", "Minecraft", "Dosis", "Mono");
    public KeystrokesMod() {
        super("Keystrokes", "Shows your keystrokes.", "\uF11C");
        toggleVisibility();

        new KeystrokeHelper(GLFW.GLFW_KEY_W, "W");
        new KeystrokeHelper(GLFW.GLFW_KEY_A, "A");
        new KeystrokeHelper(GLFW.GLFW_KEY_S, "S");
        new KeystrokeHelper(GLFW.GLFW_KEY_D, "D");
        new KeystrokeHelper(GLFW.GLFW_MOUSE_BUTTON_LEFT, "LMB");
        new KeystrokeHelper(GLFW.GLFW_MOUSE_BUTTON_RIGHT, "RMB");
        new KeystrokeHelper(GLFW.GLFW_KEY_SPACE, "Space");
    }

    public void toggleVisibility() {
        ImguiLoader.addRenderable(this);
    }


    @Override
    public void render() {
        if(!Client.modManager().getMod("Keystrokes").isEnabled()) {
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
        if(!FirstMenu.getInstance().isVisible) {
            imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
            imGuiWindowFlags |= ImGuiWindowFlags.NoBackground;
            ImGui.getStyle().setWindowBorderSize(0f);
        } else {
            ImGui.getStyle().setWindowBorderSize(1f);
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
        ImGui.setNextWindowSize(163 * scale.getFValue(), 220 * scale.getFValue());
        ImGui.pushFont(font);
        ImGui.getStyle().setWindowRounding(0);
        ImGui.getStyle().setFrameRounding(0);

        ImGui.pushStyleColor(ImGuiCol.Border, 1f,1f,1f,1f);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 1f,1f,1f,0.3f);
        if(roundedCorners.isEnabled()) ImGui.getStyle().setFrameRounding(8f * scale.getFValue());
        ImGui.begin(this.getName(), imGuiWindowFlags);
        ImGui.popStyleColor(2);

        ImGui.pushStyleColor(ImGuiCol.Text, 1f,1f,1f, 1f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0f,0f,0f, 0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0f,0f,0f, 0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0f,0f,0f, 0f);
        ImGui.button("##", 50f * scale.getFValue(), 50f * scale.getFValue());
        ImGui.popStyleColor(4);

        ImGui.sameLine();
        KeystrokeHelper.getHelper(GLFW.GLFW_KEY_W).drawButton();
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Text, 1f,1f,1f, 1f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0f,0f,0f, 0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0f,0f,0f, 0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0f,0f,0f, 0f);
        ImGui.button("##", 50f * scale.getFValue(), 50f * scale.getFValue());
        ImGui.popStyleColor(4);
        KeystrokeHelper.getHelper(GLFW.GLFW_KEY_A).drawButton();
        ImGui.sameLine();
        KeystrokeHelper.getHelper(GLFW.GLFW_KEY_S).drawButton();
        ImGui.sameLine();
        KeystrokeHelper.getHelper(GLFW.GLFW_KEY_D).drawButton();
        if(mouseButtons.isEnabled()) {
            KeystrokeHelper.getHelper(GLFW.GLFW_MOUSE_BUTTON_LEFT).drawButton();
            ImGui.sameLine();
            KeystrokeHelper.getHelper(GLFW.GLFW_MOUSE_BUTTON_RIGHT).drawButton();
        }
        if(spaceBar.isEnabled())
            KeystrokeHelper.getHelper(GLFW.GLFW_KEY_SPACE).drawButton();
        ImGui.popFont();
        this.position = ImGui.getWindowPos();
        isFocused = ImGui.isWindowFocused();
        ImGui.getStyle().setFrameRounding(4f);
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
