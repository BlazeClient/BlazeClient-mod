package me.nobokik.blazeclient.api.helpers;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import lombok.Getter;
import lombok.Setter;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.gui.UI;
import me.nobokik.blazeclient.mod.mods.KeystrokesMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeystrokeHelper {
    @Getter @Setter
    private int key;
    @Getter @Setter
    private String display;
    @Getter @Setter
    private long pressTime;
    @Getter @Setter
    private boolean pressed;
    public static List<KeystrokeHelper> list = new ArrayList<>();
    public KeystrokeHelper(int key, String display) {
        this.key = key;
        this.display = display;
        list.add(this);
    }

    public static KeystrokeHelper getHelper(int key) {
        for(KeystrokeHelper k : list) {
            if(k.key == key) return k;
        }
        return null;
    }

    public void drawButton() {
        double percent = Math.sin((((double) Math.min(System.currentTimeMillis() - this.pressTime, Client.modManager().getMod(KeystrokesMod.class).fadeTime.getFValue()) / Client.modManager().getMod(KeystrokesMod.class).fadeTime.getFValue()) * Math.PI) / 2);
        //double percent = 1;
        if(this.pressTime == 0) percent = 1;

        float[] bgF;
        float[] textF;
        if(pressed) {
            JColor bg = UI.blendColors(Client.modManager().getMod(KeystrokesMod.class).background.getColor(), Client.modManager().getMod(KeystrokesMod.class).pressedBackground.getColor(), (float) percent);
            bgF = bg.getFloatColor();
            JColor text = UI.blendColors(Client.modManager().getMod(KeystrokesMod.class).text.getColor(), Client.modManager().getMod(KeystrokesMod.class).pressedText.getColor(), (float) percent);
            textF = text.getFloatColor();
        } else {
            JColor bg = UI.blendColors(Client.modManager().getMod(KeystrokesMod.class).pressedBackground.getColor(), Client.modManager().getMod(KeystrokesMod.class).background.getColor(), (float) percent);
            bgF = bg.getFloatColor();
            JColor text = UI.blendColors(Client.modManager().getMod(KeystrokesMod.class).pressedText.getColor(), Client.modManager().getMod(KeystrokesMod.class).text.getColor(), (float) percent);
            textF = text.getFloatColor();
        }

        ImVec2 pos = ImGui.getCursorPos();
        if(Client.modManager().getMod(KeystrokesMod.class).textShadow.isEnabled()) {
            ImGui.setCursorPos(pos.x + 32 * 0.07f, pos.y + 32 * 0.07f);
            ImGui.pushStyleColor(ImGuiCol.Text, textF[0]/2, textF[1]/2, textF[2]/2, textF[3]);
            ImGui.pushStyleColor(ImGuiCol.Button, bgF[0], bgF[1], bgF[2], 0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, bgF[0], bgF[1], bgF[2], 0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, bgF[0], bgF[1], bgF[2], 0f);
            if (key == GLFW.GLFW_MOUSE_BUTTON_LEFT || key == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                ImGui.button(this.display, 77f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue(), 50f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue());
            } else if (key == GLFW.GLFW_KEY_SPACE) {
                ImGui.button(this.display, 158f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue(), 50f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue());
            } else {
                ImGui.button(this.display, 50f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue(), 50f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue());
            }
            ImGui.popStyleColor(4);
        }
        ImGui.setCursorPos(pos.x, pos.y);
        ImGui.pushStyleColor(ImGuiCol.Text, textF[0], textF[1], textF[2], textF[3]);
        ImGui.pushStyleColor(ImGuiCol.Button, bgF[0], bgF[1], bgF[2], bgF[3]);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, bgF[0], bgF[1], bgF[2], bgF[3]);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, bgF[0], bgF[1], bgF[2], bgF[3]);
        if (key == GLFW.GLFW_MOUSE_BUTTON_LEFT || key == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            ImGui.button(this.display, 77f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue(), 50f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue());
        } else if (key == GLFW.GLFW_KEY_SPACE) {
            ImGui.button(this.display, 158f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue(), 50f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue());
        } else {
            ImGui.button(this.display, 50f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue(), 50f * Client.modManager().getMod(KeystrokesMod.class).scale.getFValue());
        }
        ImGui.popStyleColor(4);


    }
    public void drawButton1() {}
}
