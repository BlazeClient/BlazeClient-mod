package me.nobokik.blazeclient.menu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.gui.Renderable;
import me.nobokik.blazeclient.gui.Theme;
import me.nobokik.blazeclient.mod.Mod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.nobokik.blazeclient.Client.mc;
import static me.nobokik.blazeclient.Client.nextTick;
import static me.nobokik.blazeclient.api.helpers.CapeHelper.*;

public class CosmeticsMenu implements Renderable {
    private boolean firstFrame = true;
    private long openTime = 0;
    public boolean isVisible = false;
    public ImString search = new ImString();
    private static CosmeticsMenu instance;
    public float scrollY = 0;

    public static CosmeticsMenu getInstance() {
        if (instance == null) {
            instance = new CosmeticsMenu();
        }
        return instance;
    }

    public static void toggleVisibility() {
        if (ImguiLoader.isRendered(getInstance())) {
            ImguiLoader.queueRemove(getInstance());
        } else {
            ImguiLoader.addRenderable(getInstance());
        }
    }

    public static void toggle(Boolean b) {
        getInstance().isVisible = b;
    }


    @Override
    public String getName() {
        return "Cosmetics";
    }
    private final Gson GSON = new Gson();

    @Override
    public void render() {
        if(!isVisible) {
            firstFrame = true;
            return;
        }
        if(firstFrame) {
            firstFrame = false;
            openTime = System.currentTimeMillis();
            search.clear();
            mc.mouse.unlockCursor();
        }
        double percent = Math.sin((((double) Math.min(System.currentTimeMillis() - openTime, 750L) / 750L) * Math.PI) / 2);

        int imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
        imGuiWindowFlags |= ImGuiWindowFlags.NoFocusOnAppearing;
        imGuiWindowFlags |= ImGuiWindowFlags.NoInputs;

        // BG
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.07f, 0.07f, 0.11f, 0.3f);
        ImGui.begin(this.getName() + "-BG", imGuiWindowFlags);
        ImGui.setWindowPos(-5, -5);
        ImGui.setWindowSize(mc.getWindow().getWidth() * 4, mc.getWindow().getHeight() * 4);
        ImGui.end();
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.07f, 0.07f, 0.11f, 1.00f);

        imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
        imGuiWindowFlags |= ImGuiWindowFlags.NoFocusOnAppearing;
        imGuiWindowFlags |= ImGuiWindowFlags.NoScrollbar;
        // Header
        ImGui.setNextWindowSize(900f, 80f, 0);
        ImGui.setNextWindowPos((float) mc.getWindow().getWidth() / 2 - 450, (float) mc.getWindow().getHeight() / 2 - 300);
        ImGui.getStyle().setWindowRounding(40f);
        ImGui.getStyle().setWindowPadding(0,0);
        ImGui.pushStyleColor(ImGuiCol.Border, 0f,0f,0f,0f);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.07f, 0.07f, 0.11f, (float) percent);
        ImGui.getStyle().setWindowBorderSize(0f);
        ImGui.begin(this.getName() + "-Header", imGuiWindowFlags);
        ImGui.getStyle().setWindowBorderSize(1f);

        float[] color = JColor.getGuiColor().getFloatColor();
        ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], (float) percent);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], (float) percent);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], (float) percent);
        ImGui.pushFont(ImguiLoader.getBoldDosisFont48());

        ImGui.getStyle().setFrameRounding(40f);

        ImGui.setCursorPos(ImGui.getCursorPosX()-1, ImGui.getCursorPosY()-1);
        ImGui.button("Cosmetics", 252f, 82f);
        ImGui.getStyle().setFrameRounding(4f);
        ImGui.popFont();
        ImGui.popStyleColor(4);
        ImGui.sameLine();

        ImGui.pushFont(ImguiLoader.getDosisFont32());
        ImGui.pushStyleColor(ImGuiCol.Button, 0.31f, 0.98f, 0.48f, (float) (0.4f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.31f, 0.98f, 0.48f, (float) (0.65f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.31f, 0.98f, 0.48f, (float) (0.7f * percent));
        ImGui.getStyle().setFrameRounding(20f);
        ImGui.setCursorPos(ImGui.getCursorPosX()+20,ImGui.getCursorPosY()+20);

        ImGui.sameLine();
        ImGui.popStyleColor(3);

        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.2f, 0.2f, 0.26f, (float) (0.3f * percent));
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.2f, 0.2f, 0.26f, (float) (0.3f * percent));
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.2f, 0.2f, 0.26f, (float) (0.3f * percent));
        ImGui.setCursorPos(ImGui.getCursorPosX()+10,ImGui.getCursorPosY()+20);
        ImGui.setNextItemWidth(607f);
        ImGui.getStyle().setFramePadding(4f,4f);
        boolean input = ImGui.inputTextWithHint("##", " \uF002 Search cosmetics...", search);
        ImGui.getStyle().setFrameRounding(4f);
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.end();
        ImGui.getStyle().setWindowPadding(4f,4f);
        ImGui.popStyleColor(1);

        // Profiles
        ImGui.setNextWindowSize(900f, 500f, 0);
        ImGui.setNextWindowPos((float) mc.getWindow().getWidth() / 2 - 450, (float) mc.getWindow().getHeight() / 2 - 210f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0f,0f,0f,0f);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.07f, 0.07f, 0.11f, (float) (0.8f * percent));

        imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
        imGuiWindowFlags |= ImGuiWindowFlags.NoFocusOnAppearing;

        ImGui.getStyle().setWindowRounding(30f);
        ImGui.getStyle().setWindowPadding(20f,20f);
        ImGui.getStyle().setFramePadding(20f,20f);
        ImGui.getStyle().setItemSpacing(15f,15f);

        ImGui.pushStyleColor(ImGuiCol.ScrollbarBg, 0f, 0f, 0f, 0f);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrab, 0.80f, 0.84f, 0.96f, 0.8f);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabHovered, 0.80f, 0.84f, 0.96f, 0.9f);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabActive, 0.80f, 0.84f, 0.96f, 1.00f);

        ImGui.begin(this.getName() + "-Profiles", imGuiWindowFlags);

        if (scrollY > ImGui.getScrollMaxY()) {
            scrollY = ImGui.getScrollMaxY();
        } else if (scrollY < 0) {
            scrollY = 0;
        }
        ImGui.setScrollY(scrollY);

        int i = 1;
        for (String s : ownedCapes) {
            if(search.get() != null && !search.get().equals("") && !s.toLowerCase().contains(search.get().toLowerCase())) continue;
            cosmeticButton(s, percent);
            i++;
            if (i != 4) ImGui.sameLine();
            else i = 1;
        }

        ImGui.end();
        ImGui.getStyle().setFramePadding(4f,4f);
        ImGui.getStyle().setFrameRounding(4f);
        ImGui.getStyle().setItemSpacing(4f,4f);
        ImGui.getStyle().setWindowPadding(4f,4f);
        ImGui.popStyleColor(6);
    }

    private static void cosmeticButton(String id, double percent) {
        String text = capes.get(id);
        if(text == null) return;
        boolean isEnabled = equippedCape.equals(id);

        ImGui.pushFont(ImguiLoader.getDosisFont32());
        ImGui.getStyle().setFramePadding(20f,20f);
        ImGui.getStyle().setFrameRounding(11f);
        if(isEnabled) {
            float[] color = JColor.getGuiColor().getFloatColor();
            ImGui.pushStyleColor(ImGuiCol.Button, 0.65f, 0.89f, 0.63f, (float) (0.5f * percent));
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.65f, 0.89f, 0.63f, (float) (0.65f * percent));
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.65f, 0.89f, 0.63f, (float) (0.8f * percent));
        } else {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.2f, 0.26f, (float) (0.5f * percent));
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.2f, 0.26f, (float) (0.65f * percent));
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.2f, 0.26f, (float) (0.8f * percent));
        }
        ImVec2 pos = ImGui.getCursorPos();

        boolean bg = ImGui.button("##", 273f, 74f);
        if (ImGui.isItemHovered()) {
            if (ImGui.isMouseClicked(0)) {
                if(!equippedCape.equalsIgnoreCase(id)) {
                    equippedCape = id;
                    equipCosmetic(id);
                } else {
                    equippedCape = "";
                    equipCosmetic("");
                }
            }
        }

        ImGui.popStyleColor(3);
        ImGui.setCursorPos(pos.x +2, pos.y +2);

        ImGui.getStyle().setFrameRounding(10f);
        if(isEnabled) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, (float) percent);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.75f, 0.79f, 0.91f, (float) percent);
        }
        ImGui.pushStyleColor(ImGuiCol.Button, 0.08f, 0.08f, 0.12f, (float) (0.65f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.1f, 0.1f, 0.16f, (float) (0.7f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.11f, 0.11f, 0.17f, (float) (0.8f * percent));

        ImGui.button(isEnabled ? "\uF00C  " + text : "\uF553 " + text, 269f, 70f);

        pos = ImGui.getCursorPos();
        ImGui.setCursorPos(pos.x, pos.y - 4);
        ImGui.getStyle().setFramePadding(4f,4f);
        ImGui.getStyle().setFrameRounding(4f);
        ImGui.popStyleColor(4);
        ImGui.popFont();
    }


    @Override
    public Theme getTheme() {
        return new Theme() {
            @Override
            public void preRender() {
                float[][] colors = ImGui.getStyle().getColors();

                float[] color = JColor.getGuiColor().getFloatColor();
                float[] bColor = JColor.getGuiColor().jBrighter().getFloatColor();
                float[] dColor = JColor.getGuiColor().jDarker().getFloatColor();

                colors[ImGuiCol.Text]                   = new float[]{0.80f, 0.84f, 0.96f, 1.00f};
                colors[ImGuiCol.TextDisabled]           = new float[]{0.42f, 0.44f, 0.53f, 1.00f};
                colors[ImGuiCol.WindowBg]               = new float[]{0.07f, 0.07f, 0.11f, 1.00f};
                colors[ImGuiCol.ChildBg]                = new float[]{0.09f, 0.09f, 0.13f, 1.00f};
                colors[ImGuiCol.PopupBg]                = new float[]{0.09f, 0.09f, 0.15f, 0.94f};
                colors[ImGuiCol.Border]                 = new float[]{0.42f, 0.44f, 0.53f, 0.50f};
                colors[ImGuiCol.BorderShadow]           = new float[]{0.07f, 0.07f, 0.11f, 0.00f};
                colors[ImGuiCol.FrameBg]                = new float[]{color[0], color[1], color[2], 0.54f};
                colors[ImGuiCol.FrameBgHovered]         = new float[]{color[0], color[1], color[2], 0.40f};
                colors[ImGuiCol.FrameBgActive]          = new float[]{color[0], color[1], color[2], 0.67f};
                colors[ImGuiCol.TitleBg]                = new float[]{0.09f, 0.09f, 0.15f, 1.00f};
                colors[ImGuiCol.TitleBgActive]          = new float[]{0.12f, 0.12f, 0.18f, 1.00f};
                colors[ImGuiCol.TitleBgCollapsed]       = new float[]{0.09f, 0.09f, 0.15f, 0.75f};
                colors[ImGuiCol.MenuBarBg]              = new float[]{0.16f, 0.17f, 0.24f, 1.00f};
                colors[ImGuiCol.ScrollbarBg]            = new float[]{0f, 0f, 0f, 0f};
                colors[ImGuiCol.ScrollbarGrab]          = new float[]{0.80f, 0.84f, 0.96f, 0.8f};
                colors[ImGuiCol.ScrollbarGrabHovered]   = new float[]{0.80f, 0.84f, 0.96f, 0.9f};
                colors[ImGuiCol.ScrollbarGrabActive]    = new float[]{0.80f, 0.84f, 0.96f, 1.00f};
                colors[ImGuiCol.CheckMark]              = new float[]{bColor[0], bColor[1], bColor[2], 1.00f};
                colors[ImGuiCol.SliderGrab]             = new float[]{color[0], color[1], color[2], 0.9f};
                colors[ImGuiCol.SliderGrabActive]       = new float[]{color[0], color[1], color[2], 0.95f};
                colors[ImGuiCol.Button]                 = new float[]{color[0], color[1], color[2], 0.59f};
                colors[ImGuiCol.ButtonHovered]          = new float[]{color[0], color[1], color[2], 0.9f};
                colors[ImGuiCol.ButtonActive]           = new float[]{color[0], color[1], color[2], 1.00f};
                colors[ImGuiCol.Header]                 = new float[]{color[0], color[1], color[2], 0.9f};
                colors[ImGuiCol.HeaderHovered]          = new float[]{color[0], color[1], color[2], 0.95f};
                colors[ImGuiCol.HeaderActive]           = new float[]{bColor[0], bColor[1], bColor[2], 1.00f};
                colors[ImGuiCol.Separator]              = new float[]{0.45f, 0.47f, 0.58f, 0.50f};
                colors[ImGuiCol.SeparatorHovered]       = new float[]{0.76f, 0.17f, 0.30f, 0.78f};
                colors[ImGuiCol.SeparatorActive]        = new float[]{0.76f, 0.17f, 0.30f, 1.00f};
                colors[ImGuiCol.ResizeGrip]             = new float[]{color[0], color[1], color[2], 0.59f};
                colors[ImGuiCol.ResizeGripHovered]      = new float[]{bColor[0], bColor[1], bColor[2], 1.00f};
                colors[ImGuiCol.ResizeGripActive]       = new float[]{color[0], color[1], color[2], 1.00f};
                colors[ImGuiCol.Tab]                    = new float[]{dColor[0], dColor[1], dColor[2], 0.86f};
                colors[ImGuiCol.TabHovered]             = new float[]{color[0], color[1], color[2], 0.80f};
                colors[ImGuiCol.TabActive]              = new float[]{bColor[0], bColor[1], bColor[2], 1.00f};
                colors[ImGuiCol.TabUnfocused]           = new float[]{0.19f, 0.20f, 0.27f, 1.00f};
                colors[ImGuiCol.TabUnfocusedActive]     = new float[]{0.51f, 0.12f, 0.20f, 1.00f};
                colors[ImGuiCol.DockingPreview]         = new float[]{0.26f, 0.59f, 0.98f, 0.70f};
                colors[ImGuiCol.DockingEmptyBg]         = new float[]{0.20f, 0.20f, 0.20f, 1.00f};
                colors[ImGuiCol.PlotLines]              = new float[]{0.61f, 0.61f, 0.61f, 1.00f};
                colors[ImGuiCol.PlotLinesHovered]       = new float[]{1.00f, 0.43f, 0.35f, 1.00f};
                colors[ImGuiCol.PlotHistogram]          = new float[]{0.90f, 0.70f, 0.00f, 1.00f};
                colors[ImGuiCol.PlotHistogramHovered]   = new float[]{1.00f, 0.60f, 0.00f, 1.00f};
                colors[ImGuiCol.TableHeaderBg]          = new float[]{0.19f, 0.19f, 0.20f, 1.00f};
                colors[ImGuiCol.TableBorderStrong]      = new float[]{0.31f, 0.31f, 0.35f, 1.00f};
                colors[ImGuiCol.TableBorderLight]       = new float[]{0.23f, 0.23f, 0.25f, 1.00f};
                colors[ImGuiCol.TableRowBg]             = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
                colors[ImGuiCol.TableRowBgAlt]          = new float[]{1.00f, 1.00f, 1.00f, 0.06f};
                colors[ImGuiCol.TextSelectedBg]         = new float[]{0.90f, 0.27f, 0.33f, 0.35f};
                colors[ImGuiCol.DragDropTarget]         = new float[]{1.00f, 1.00f, 0.00f, 0.90f};
                colors[ImGuiCol.NavHighlight]           = new float[]{0.90f, 0.27f, 0.33f, 1.00f};
                colors[ImGuiCol.NavWindowingHighlight]  = new float[]{1.00f, 1.00f, 1.00f, 0.70f};
                colors[ImGuiCol.NavWindowingDimBg]      = new float[]{0.80f, 0.80f, 0.80f, 0.20f};
                colors[ImGuiCol.ModalWindowDimBg]       = new float[]{0.80f, 0.80f, 0.80f, 0.35f};
                ImGui.getStyle().setColors(colors);

                ImGui.getStyle().setWindowRounding(8);
                ImGui.getStyle().setFrameRounding(4);
                ImGui.getStyle().setGrabRounding(4);
                ImGui.getStyle().setPopupRounding(4);
                ImGui.getStyle().setScrollbarSize(6);
                ImGui.getStyle().setScrollbarRounding(3);
                ImGui.getStyle().setTabRounding(4);
                ImGui.getStyle().setWindowTitleAlign(0.5f, 0.5f);

                //if (ImguiLoader.getCustomFont() != null) {
                //    ImGui.pushFont(ImguiLoader.getCustomFont());
                //}
                if (ImguiLoader.getFontAwesome20() != null) {
                    ImGui.pushFont(ImguiLoader.getFontAwesome20());
                }

            }

            @Override
            public void postRender() {
                if (ImguiLoader.getMonoFont18() != null) {
                    ImGui.popFont();
                }
            }
        };
    }

}

