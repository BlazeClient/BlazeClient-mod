package me.nobokik.blazeclient.menu;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.gui.Renderable;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import static me.nobokik.blazeclient.Client.mc;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class FirstMenu implements Renderable {
    private boolean firstFrame = true;
    public boolean isVisible = false;
    private long openTime = 0;
    private static FirstMenu instance;

    public static FirstMenu getInstance() {
        if (instance == null) {
            instance = new FirstMenu();
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
        //if(b) {
        //    getInstance().isVisible = b;
        //    //if (!ImguiLoader.isRendered(getInstance())) ImguiLoader.addRenderable(getInstance());
        //} else {
        //    //if (ImguiLoader.isRendered(getInstance())) ImguiLoader.queueRemove(getInstance());
        //}
    }

    @Override
    public String getName() {
        return "FirstMenu";
    }

    @Override
    public void render() {
        if(!isVisible) {
            firstFrame = true;
            return;
        }
        ImGui.getStyle().setWindowBorderSize(1);

        if(firstFrame) {
            firstFrame = false;
            openTime = System.currentTimeMillis();
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
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.07f, 0.07f, 0.11f, (float) (0.3f * percent));
        ImGui.begin(this.getName() + "-BG", imGuiWindowFlags);
        ImGui.setWindowPos(-5, -5);
        ImGui.setWindowSize(mc.getWindow().getWidth() * 4, mc.getWindow().getHeight() * 4);
        ImGui.end();
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.07f, 0.07f, 0.11f, 1.00f);

        imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoBackground;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;

        // Button
        ImGui.begin(this.getName() + "-Button", imGuiWindowFlags);
        ImGui.getStyle().setWindowRounding(0f);
        ImGui.setWindowPos((float) mc.getWindow().getWidth() / 2 - 75, (float) mc.getWindow().getHeight() / 2 - 75);

        ImGui.getStyle().setButtonTextAlign(0.5f, 0.5f);
        ImGui.getStyle().setFrameRounding(75f);
        ImGui.pushFont(ImguiLoader.getFontAwesome64());
        ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, (float) percent);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.07f, 0.07f, 0.11f, (float) (0.65f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.09f, 0.09f, 0.15f, (float) (0.65f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.1f, 0.16f, (float) (0.8f * percent));

        if (ImGui.button("\uF085", 150f, 150f)) {
            isVisible = false;
            ModMenu.toggle(true);
        }
        if (ImGui.isItemHovered()) {
            ImGui.pushFont(ImguiLoader.getDosisFont32());
            ImGui.setTooltip("Blaze Settings");
            ImGui.popFont();
        }
        ImGui.popStyleColor(4);
        ImGui.popFont();
        ImGui.end();
    }

}

