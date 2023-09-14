package me.nobokik.blazeclient.mod.mods;

import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.api.helpers.CPSHelper;
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

import static me.nobokik.blazeclient.Client.mc;
import static me.nobokik.blazeclient.Client.modManager;

public class ArmorMod extends Mod implements Renderable {
    //public final ModeSetting position = new ModeSetting("Position", this, "Hotbar", "Hotbar", "Top Center", "Top", "Bottom");
    public final BooleanSetting showDura = new BooleanSetting("Durability", this, true);
    public final ModeSetting duraMode = new ModeSetting("Durability Mode", this, "Numbers", "Numbers", "Percentages");
    public final ModeSetting fontSetting = new ModeSetting("Font", this, "Minecraft", "Minecraft", "Dosis", "Mono");
    public final ColorSetting textSetting = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final ModeSetting direction = new ModeSetting("Direction", this, "Vertical", "Vertical", "Horizontal");

    public boolean firstFrame = true;
    //public ImVec2 position = new ImVec2(82, 200);
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
        if(firstFrame) {
            ImGui.setNextWindowPos(this.position.x, this.position.y);
        }
        ImGui.getStyle().setWindowRounding(0);
        ImGui.getStyle().setWindowBorderSize(1);
        ImGui.begin(this.getName(), imGuiWindowFlags);
        this.position = ImGui.getWindowPos();
        ImGui.popStyleColor(2);
        ImGui.getStyle().setWindowBorderSize(0);
        ImGui.getStyle().setWindowRounding(4f);

        if(showDura.isEnabled() && mc.player != null && direction.is("Vertical")) {
            ImFont font = ImguiLoader.getMonoFont18();
            if(fontSetting.is("Minecraft")) {
                font = ImguiLoader.getMcFont18();
            } else if (fontSetting.is("Dosis")) {
                font = ImguiLoader.getDosisFont18();
            } else if (fontSetting.is("Mono")) {
                font = ImguiLoader.getMonoFont18();
            }
            for (int i = 0; i < 4; i++) {
                String text = mc.player.getInventory().armor.get(3 - i).getDamage() != 0 ?

                        (duraMode.getMode().equals("Numbers") ?
                                mc.player.getInventory().armor.get(3 - i).getMaxDamage() - mc.player.getInventory().armor.get(3 - i).getDamage() + "" :
                                (100 - (mc.player.getInventory().armor.get(3 - i).getMaxDamage() / 100 * mc.player.getInventory().armor.get(3 - i).getDamage())) + "%")

                        : " ";

                c = textSetting.getColor().getFloatColor();

                ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);
                ImGui.pushFont(font);
                float oldScale = ImGui.getFont().getScale();
                ImGui.getFont().setScale(oldScale * mc.options.getGuiScale().getValue());

                float windowWidth = 22 * mc.options.getGuiScale().getValue();
                float windowHeight = 22 * mc.options.getGuiScale().getValue();
                float textWidth = ImGui.calcTextSize(text).x;
                float textHeight = ImGui.calcTextSize(text).y;

                ImGui.setCursorPos((windowWidth - textWidth) * 0.5f, (16 * mc.options.getGuiScale().getValue()) + (i * 16 * mc.options.getGuiScale().getValue() - ((windowHeight - textHeight) * 0.5f)));

                if (textShadow.isEnabled()) UI.shadowText(text, 32, c[0], c[1], c[2], c[3]);
                else ImGui.text(text);
                ImGui.getFont().setScale(oldScale);
                ImGui.popFont();
                ImGui.popStyleColor(1);
            }
        }


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
