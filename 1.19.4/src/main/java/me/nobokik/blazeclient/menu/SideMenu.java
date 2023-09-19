package me.nobokik.blazeclient.menu;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.gui.Renderable;
import me.nobokik.blazeclient.mod.GeneralSettings;

import static me.nobokik.blazeclient.Client.mc;

public class SideMenu implements Renderable {
    private boolean firstFrame = true;
    public String selectedWindow = "Mods";
    private long openTime = 0;
    public boolean isVisible = false;
    public boolean switched = false;
    private static SideMenu instance;

    public static SideMenu getInstance() {
        if (instance == null) {
            instance = new SideMenu();
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
        return "SideMenu";
    }

    @Override
    public void render() {
        if (!isVisible) {
            firstFrame = true;
            return;
        }
        if (firstFrame) {
            firstFrame = false;
            openTime = System.currentTimeMillis();
            mc.mouse.unlockCursor();
        }
        double percent = Math.sin((((double) Math.min(System.currentTimeMillis() - openTime, 750L) / 750L) * Math.PI) / 2);
        if(switched) {
            switched = false;
            percent = 1;
        }
        int imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
        imGuiWindowFlags |= ImGuiWindowFlags.NoFocusOnAppearing;
        imGuiWindowFlags |= ImGuiWindowFlags.NoScrollbar;
        // Menu
        ImGui.setNextWindowSize(100f, 585f, 0);
        ImGui.setNextWindowPos((float) mc.getWindow().getWidth() / 2 - 560, (float) mc.getWindow().getHeight() / 2 - 300);
        ImGui.getStyle().setWindowRounding(25f);
        ImGui.getStyle().setWindowPadding(0,0);
        ImGui.pushStyleColor(ImGuiCol.Border, 0f,0f,0f,0f);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.07f, 0.07f, 0.11f, (float) percent);
        ImGui.getStyle().setWindowBorderSize(0f);
        ImGui.begin(this.getName(), imGuiWindowFlags);
        renderButton("\uF58D", "Mods", percent);
        ImGui.setCursorPosY(ImGui.getCursorPosY()+5);
        renderButton("\uF013", "General", percent);
        ImGui.setCursorPosY(ImGui.getCursorPosY()+5);
        renderButton("\uF0C9", "Profiles", percent);
        ImGui.setCursorPosY(ImGui.getCursorPosY()+5);
        renderButton("\uF553", "Cosmetics", percent);
        ImGui.popStyleColor(2);
        ImGui.end();
        ImGui.getStyle().setWindowPadding(4f,4f);
        ImGui.getStyle().setFrameRounding(4f);
    }

    private void renderButton(String icon, String name, double percent) {
        ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, (float) percent);
        if(!selectedWindow.equals(name)) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.07f, 0.07f, 0.11f, (float) (0.6f * percent));
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.09f, 0.09f, 0.15f, (float) (0.7f * percent));
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.1f, 0.16f, (float) (0.8f * percent));
        } else {
            float[] color = JColor.getGuiColor().getFloatColor();
            ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], (float) (0.6f * percent));
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], (float) (0.7f * percent));
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], (float) (0.8f * percent));
        }
        float frameWidth = 100;
        ImVec2 pos = ImGui.getCursorPos();
        ImGui.getStyle().setFrameRounding(25f);
        ImGui.getStyle().setWindowPadding(0f, 0f);
        ImGui.pushFont(ImguiLoader.getDosisFont64());
        ImGui.button("##", 100f, 100f);
        if (ImGui.isItemHovered()) {
            if (ImGui.isMouseClicked(0)) {
                selectedWindow = name;
                //System.out.println(selectedWindow);
                SideMenu.getInstance().isVisible = false;
                FirstMenu.getInstance().isVisible = false;
                ModSettings.getInstance().isVisible = false;
                ModMenu.getInstance().isVisible = false;
                ProfilesMenu.getInstance().isVisible = false;
                CosmeticsMenu.getInstance().isVisible = false;
                if(name.equals("Mods")) {
                    SideMenu.getInstance().isVisible = true;
                    ModMenu.getInstance().isVisible = true;
                } else if(name.equals("General")) {
                    SideMenu.getInstance().isVisible = true;
                    ModSettings.getInstance().mod = Client.modManager().getMod(GeneralSettings.class);
                    ModSettings.getInstance().isVisible = true;
                } else if(name.equals("Profiles")) {
                    SideMenu.getInstance().isVisible = true;
                    ProfilesMenu.getInstance().isVisible = true;
                } else if(name.equals("Cosmetics")) {
                    SideMenu.getInstance().isVisible = true;
                    CosmeticsMenu.getInstance().isVisible = true;
                }
            }
        }
        ImVec2 pos1 = ImGui.getCursorPos();
        float textWidth = ImGui.calcTextSize(icon).x;
        ImGui.setCursorPos((frameWidth - textWidth) * 0.5f, pos.y+10f);
        ImGui.text(icon);
        ImGui.popFont();
        ImGui.pushFont(ImguiLoader.getDosisFont24());
        textWidth = ImGui.calcTextSize(name).x;
        ImGui.setCursorPos((frameWidth - textWidth) * 0.5f, pos.y+70f);
        ImGui.text(name);
        ImGui.setCursorPos(pos1.x, pos1.y);
        ImGui.popFont();
        ImGui.popStyleColor(4);
    }
}