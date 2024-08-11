package me.nobokik.blazeclient.menu;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import me.nobokik.blazeclient.gui.ImguiLoader;
import me.nobokik.blazeclient.gui.Renderable;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static me.nobokik.blazeclient.Client.mc;
import static me.nobokik.blazeclient.gui.TextureLoader.loadImage;
import static me.nobokik.blazeclient.gui.TextureLoader.loadTexture;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class MainMenuButtons implements Renderable {
    private boolean firstFrame = true;
    private long openTime = 0;
    private static MainMenuButtons instance;
    public static boolean reloadComplete = false;

    public static MainMenuButtons getInstance() {
        if (instance == null) {
            instance = new MainMenuButtons();
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
        if(b) {
            if (!ImguiLoader.isRendered(getInstance())) ImguiLoader.addRenderable(getInstance());
        } else {
            if (ImguiLoader.isRendered(getInstance())) ImguiLoader.queueRemove(getInstance());
        }
    }

    @Override
    public String getName() {
        return "MainMenuButtons";
    }

    @Override
    public void render() {
        if(!(mc.currentScreen instanceof TitleScreen || !reloadComplete)) {
            firstFrame = true;
            openTime = System.currentTimeMillis();
            return;
        }
        double percent = Math.sin((((double) Math.min(System.currentTimeMillis() - openTime, 750L) / 750L) * Math.PI) / 2);

        int imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoBackground;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
        imGuiWindowFlags |= ImGuiWindowFlags.AlwaysAutoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoFocusOnAppearing;
        imGuiWindowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus;

        // Buttons
        ImGui.getStyle().setFramePadding(4f,4f);
        ImGui.getStyle().setFrameRounding(4f);
        ImGui.getStyle().setItemSpacing(4f,4f);
        ImGui.getStyle().setWindowPadding(4f,4f);
        ImGui.begin(this.getName(), imGuiWindowFlags);
        ImGui.setWindowPos(-50, (float) mc.getWindow().getHeight() / 2 - 100);

        ImGui.getStyle().setButtonTextAlign(0.5f, 0.5f);
        ImGui.getStyle().setFrameRounding(50f);
        ImGui.pushFont(ImguiLoader.getFontAwesome64());

        ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, (float) percent);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.07f, 0.07f, 0.11f, (float) (0.65f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.09f, 0.09f, 0.15f, (float) (0.65f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.1f, 0.16f, (float) (0.8f * percent));
        if (ImGui.button("\uF007 Singleplayer", 450f, 100f)) {
            if (mc.getLevelStorage().getLevelList().isEmpty()) {
                Screen screen = new SelectWorldScreen(new TitleScreen());
                mc.setScreen(new DisconnectedScreen(screen, Text.of("Error!"), Text.of("World does not exist!")));
            } else {
                mc.setScreen(new SelectWorldScreen(new TitleScreen()));
            }
            //mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        if (ImGui.button("\uF0C0 Multiplayer", 425f, 100f)) {
            mc.setScreen(new MultiplayerScreen(mc.currentScreen));
            //mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        if (ImGui.button("\uF057 Quit Game", 400f, 100f)) {
            //mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            mc.scheduleStop();
        }

        ImGui.popStyleColor(4);
        ImGui.popFont();
        ImGui.getStyle().setFrameRounding(4f);
        ImGui.end();

        // Client Name
        ImGui.begin(this.getName()+"-Name", imGuiWindowFlags);
        ImGui.setWindowPos(10, (float) mc.getWindow().getHeight() - 50);

        ImGui.pushFont(ImguiLoader.getDosisFont32());
        ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, (float) (0.5f * percent));
        ImGui.text("Blaze Client 1.21 (" + FabricLoader.getInstance().getModContainer("blaze-client").get().getMetadata().getVersion() + ")");
        ImGui.popFont();
        ImGui.popStyleColor();

        ImGui.end();

        // Client Icon
        //ImGui.begin(this.getName()+"-Icon", imGuiWindowFlags);
        //ImGui.setWindowPos(50, 50);
        //try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/blaze-client/icon.png")) {
        //    if (is != null) {
        //        BufferedImage image = loadImage(is);
        //        ImGui.image(loadTexture(image), 256, 256);
        //    }
        //} catch (IOException ignored) {

        //}
        //ImGui.end();

        // Sidebar
        ImGui.begin(this.getName()+"-Side", imGuiWindowFlags);
        ImGui.getStyle().setWindowRounding(12f);
        ImGui.setWindowPos((float) mc.getWindow().getWidth() - 115, 10);

        ImGui.getStyle().setButtonTextAlign(0.5f, 0.5f);
        ImGui.getStyle().setFrameRounding(25f);
        ImGui.pushFont(ImguiLoader.getFontAwesome64());
        ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, (float) percent);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.07f, 0.07f, 0.11f, (float) (0.65f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.09f, 0.09f, 0.15f, (float) (0.65f * percent));
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.1f, 0.16f, (float) (0.8f * percent));

        ImGui.getStyle().setFrameRounding(25f);
        if (ImGui.button("\uF013", 90f, 90f)) {
            mc.setScreen(new OptionsScreen(mc.currentScreen, mc.options));
            //mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        if (ImGui.isItemHovered()) {
            ImGui.pushFont(ImguiLoader.getDosisFont32());
            ImGui.setTooltip("MC Settings");
            ImGui.popFont();
        }

        ImGui.getStyle().setFrameRounding(25f);
        if (ImGui.button("\uF0AC", 90f, 90f)) {
            mc.setScreen(new RealmsMainScreen(mc.currentScreen));
            //mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        if (ImGui.isItemHovered()) {
            ImGui.pushFont(ImguiLoader.getDosisFont32());
            ImGui.setTooltip("Realms");
            ImGui.popFont();
        }

        ImGui.popStyleColor(4);
        ImGui.popFont();
        ImGui.end();
    }

}
