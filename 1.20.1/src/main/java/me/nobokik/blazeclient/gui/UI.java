package me.nobokik.blazeclient.gui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import me.nobokik.blazeclient.api.font.JColor;
import net.minecraft.util.math.ColorHelper;

public class UI {
    public static void shadowText(String text, int size, float r, float g, float b, float a) {
        float offset = size * 0.07f;

        ImVec2 pos = ImGui.getCursorPos();
        float r1 = r/4;
        float g1 = g/4;
        float b1 = b/4;
        ImGui.pushStyleColor(ImGuiCol.Text, r1, g1, b1, a);
        ImGui.text(text);
        ImGui.popStyleColor(1);
        ImGui.setCursorPos(pos.x - offset, pos.y - offset);
        ImGui.pushStyleColor(ImGuiCol.Text, r, g, b, a);
        ImGui.text(text);
        ImGui.popStyleColor(1);
    }

    public static JColor getFadeBetweenColors(JColor c1, JColor c2, double stage) {
        float[] cr = c1.getFloatColor();
        float[] cf1 = c1.getFloatColor();
        float[] cf2 = c2.getFloatColor();

        float r = cf1[0] - cf2[0];
        float g = cf1[1] - cf2[1];
        float b = cf1[2] - cf2[2];
        float a = cf1[3] - cf2[3];
        cr[0] = (float) (cr[0] + r * stage); if(cr[0] < 0) cr[0] = 0;
        cr[1] = (float) (cr[1] + g * stage); if(cr[1] < 0) cr[1] = 0;
        cr[2] = (float) (cr[2] + b * stage); if(cr[2] < 0) cr[2] = 0;
        cr[3] = (float) (cr[3] + a * stage); if(cr[3] < 0) cr[3] = 0;

        System.out.println(cr[0] + ", " + cr[1] + ", " + cr[2] + ", " + cr[3]);
        //return new JColor(cr[0], cr[1], cr[2], cr[3]);

        return c2;
    }

    public static JColor blendColors(JColor color1, JColor color2, float ratio) {
        final float inverseRatio = 1 - ratio;
        float a = color1.getFloatColor()[3] * inverseRatio + color2.getFloatColor()[3] * ratio;
        float r = color1.getFloatColor()[0] * inverseRatio + color2.getFloatColor()[0] * ratio;
        float g = color1.getFloatColor()[1] * inverseRatio + color2.getFloatColor()[1] * ratio;
        float b = color1.getFloatColor()[2] * inverseRatio + color2.getFloatColor()[2] * ratio;
        if(ratio == 1f) return color2;
        return new JColor(r,g,b,a);
    }
}
