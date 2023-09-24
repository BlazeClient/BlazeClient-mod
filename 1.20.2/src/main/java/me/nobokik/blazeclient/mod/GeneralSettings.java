package me.nobokik.blazeclient.mod;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import me.nobokik.blazeclient.api.event.events.TickEvent;
import me.nobokik.blazeclient.api.event.orbit.EventHandler;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.mod.setting.RenderableSetting;
import me.nobokik.blazeclient.mod.setting.Setting;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.ColorSetting;
import me.nobokik.blazeclient.mod.setting.settings.KeybindSetting;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import static me.nobokik.blazeclient.menu.ModSettings.text;

public class GeneralSettings extends Mod {
    public final ColorSetting mainColor = new ColorSetting("Main Color", this, new JColor(0.90f, 0.27f, 0.33f), false);
    public final KeybindSetting openMenu = new KeybindSetting("Open Menu", GLFW.GLFW_KEY_RIGHT_SHIFT, this);
    public final BooleanSetting fullbright = new BooleanSetting("Fullbright", this, true);
    public final BooleanSetting minimalViewBob = new BooleanSetting("Minimal View Bob", this, false);
    public final BooleanSetting showOwnNametag = new BooleanSetting("Show Own Nametag", this, false);
    public final BooleanSetting showClientBadges = new BooleanSetting("Show Client Badges", this, true);
    public final BooleanSetting lowShield = new BooleanSetting("Low Shield", this, false);
    public final BooleanSetting lowFire = new BooleanSetting("Low Fire", this, false);
    public final BooleanSetting numericalPing = new BooleanSetting("Numerical Ping", this, false);
    public final BooleanSetting smallPing = new BooleanSetting("Small Ping", this, false);
    public final BooleanSetting msPing = new BooleanSetting("Ping MS text", this, false);
    public final BooleanSetting hourFormat = new BooleanSetting("24 Hour Format", this, true);

    public final BooleanSetting showInChat = new BooleanSetting("Show Mods in Chat", this, true);
    public final BooleanSetting showInInventory = new BooleanSetting("Show Mods in Inventory", this, true);

    public final BooleanSetting blurChat = new BooleanSetting("Chat", this, false);
    public final BooleanSetting blurInventory = new BooleanSetting("Inventory", this, false);

    public final BooleanSetting darkenInventory = new BooleanSetting("Inventory", this, true);

    public final BooleanSetting unlimitedChatHistory = new BooleanSetting("Unlimited Chat History", this, false);
    public final BooleanSetting stackChatMessages = new BooleanSetting("Stack Chat Messages", this, false);
    public final BooleanSetting enableDiscordRPC = new BooleanSetting("Enable Discord RPC", this, true);
    public final BooleanSetting showAddress = new BooleanSetting("Show Server Address", this, true);
    public GeneralSettings() {
        super("General", "General client settings.", "\uF085");
    }

    @Override
    public void renderSettings(double @Nullable ... p) {
        double percent;
        if(p == null) percent = 1;
        else percent = p[0];
        text("General Settings", 1f);

        ImGui.getStyle().setChildRounding(15f);
        ImGui.getStyle().setWindowPadding(15f, 15f);

        ImGui.pushFont(ImguiLoader.getDosisFont32());
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.06f, 0.06f, 0.1f, (float) (0.6f * percent));
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.07f, 0.07f, 0.11f, (float) (0.7f * percent));
        ImGui.indent(30f);
        mainColor.render();
        openMenu.render();
        fullbright.render();
        showOwnNametag.render();
        minimalViewBob.render();
        lowShield.render();
        lowFire.render();
        showClientBadges.render();
        numericalPing.render();
        smallPing.render();
        ImGui.unindent(30f);
        text("Mods", 1f);
        ImGui.indent(30f);
        showInChat.render();
        showInInventory.render();
        ImGui.unindent(30f);
        text("Chat", 1f);
        ImGui.indent(30f);
        unlimitedChatHistory.render();
        stackChatMessages.render();
        ImGui.unindent(30f);
        text("Darken Background", 1f);
        ImGui.indent(30f);
        darkenInventory.render();
        ImGui.unindent(30f);
        text("Discord", 1f);
        ImGui.indent(30f);
        enableDiscordRPC.render();
        showAddress.render();
        ImGui.popStyleColor(8);
        ImGui.popFont();
        ImGui.getStyle().setChildRounding(4f);
        ImGui.getStyle().setFramePadding(4f,4f);
        ImGui.getStyle().setItemSpacing(4f,4f);
        ImGui.getStyle().setWindowPadding(4f,4f);
    }
}
