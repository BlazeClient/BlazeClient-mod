package me.nobokik.blazeclient.mod.mods;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.gui.Renderable;
import me.nobokik.blazeclient.menu.FirstMenu;
import me.nobokik.blazeclient.menu.ModSettings;
import me.nobokik.blazeclient.mod.GeneralSettings;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.ModeSetting;
import me.nobokik.blazeclient.mod.setting.settings.NumberSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import static me.nobokik.blazeclient.Client.mc;
import static me.nobokik.blazeclient.Client.modManager;

public class ArmorMod extends Mod implements Renderable {
    //public final ModeSetting position = new ModeSetting("Position", this, "Hotbar", "Hotbar", "Top Center", "Top", "Bottom");
    //public final ModeSetting side = new ModeSetting("Side", this, "Left", "Left", "Right");
    //public final BooleanSetting reversed = new BooleanSetting("Side", this, true);
    public final ModeSetting direction = new ModeSetting("Direction", this, "Vertical", "Vertical", "Horizontal");
    public boolean firstFrame = true;
    public ImVec2 position = new ImVec2(82, 200);
    public ArmorMod() {
        super("Armor Status", "Shows your armor status.", "\uF132");
        toggleVisibility();
    }

    public void toggleVisibility() {
        ImguiLoader.addRenderable(this);
    }

    @Override
    public void render() {
        if(!Client.modManager().getMod("Armor Status").isEnabled()) {
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

        firstFrame = false;
        int imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
        imGuiWindowFlags |= ImGuiWindowFlags.NoFocusOnAppearing;
        imGuiWindowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoScrollbar;
        float[] c;
        if(!FirstMenu.getInstance().isVisible) {
            imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
            imGuiWindowFlags |= ImGuiWindowFlags.NoBackground;
            ImGui.pushStyleColor(ImGuiCol.Border, 0f,0f,0f,0f);
            ImGui.pushStyleColor(ImGuiCol.WindowBg, 0f, 0f, 0f, 0f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Border, 1f, 1f, 1f, 1f);
            ImGui.pushStyleColor(ImGuiCol.WindowBg, 1f,1f,1f,0.3f);
        }

        if(direction.getMode().equals("Vertical"))
            ImGui.setNextWindowSize(22 * mc.options.getGuiScale().getValue(), 62 * mc.options.getGuiScale().getValue());
        else
            ImGui.setNextWindowSize(62+3 * mc.options.getGuiScale().getValue(), 22 * mc.options.getGuiScale().getValue());
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
        ImGui.getStyle().setWindowRounding(0);
        ImGui.getStyle().setWindowBorderSize(1);
        ImGui.begin(this.getName(), imGuiWindowFlags);
        this.position = ImGui.getWindowPos();
        ImGui.popStyleColor(2);
        ImGui.getStyle().setWindowBorderSize(0);
        ImGui.getStyle().setWindowRounding(4f);
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
    }
}
