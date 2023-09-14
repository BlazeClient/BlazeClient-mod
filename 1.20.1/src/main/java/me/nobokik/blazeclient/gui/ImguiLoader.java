package me.nobokik.blazeclient.gui;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.main.Main;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static me.nobokik.blazeclient.gui.TextureLoader.loadImage;
import static me.nobokik.blazeclient.gui.TextureLoader.loadTexture;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImguiLoader {
    private static final Set<Renderable> renderstack = new HashSet<>();
    private static final Set<Renderable> toRemove = new HashSet<>();

    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    @Getter
    private static int blazeLogo;

    @Getter
    private static int blazeText;

    @Getter
    private static ImFont monoFont18;
    @Getter
    private static ImFont monoFont20;
    @Getter
    private static ImFont monoFont24;
    @Getter
    private static ImFont monoFont32;
    @Getter
    private static ImFont monoFont48;


    @Getter
    private static ImFont dosisFont20;
    @Getter
    private static ImFont dosisFont18;
    @Getter
    private static ImFont dosisFont24;
    @Getter
    private static ImFont dosisFont32;
    @Getter
    private static ImFont dosisFont48;
    @Getter
    private static ImFont dosisFont64;

    @Getter
    private static ImFont boldDosisFont20;
    @Getter
    private static ImFont boldDosisFont18;
    @Getter
    private static ImFont boldDosisFont24;
    @Getter
    private static ImFont boldDosisFont32;
    @Getter
    private static ImFont boldDosisFont48;
    @Getter
    private static ImFont boldDosisFont64;

    @Getter
    private static ImFont mcFont20;
    @Getter
    private static ImFont mcFont18;
    @Getter
    private static ImFont mcFont24;
    @Getter
    private static ImFont mcFont32;
    @Getter
    private static ImFont mcFont48;
    @Getter
    private static ImFont mcFont64;

    @Getter
    private static ImFont fontAwesome18;
    @Getter
    private static ImFont fontAwesome20;
    @Getter
    private static ImFont fontAwesome24;
    @Getter
    private static ImFont fontAwesome32;
    @Getter
    private static ImFont fontAwesome48;
    @Getter
    private static ImFont fontAwesome64;
    public static void onGlfwInit(long handle) {
        initializeImGui();
        imGuiGlfw.init(handle,true);
        imGuiGl3.init();
    }

    public static void onFrameRender() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        //AsteriaSettingsModule asteria = Phosphor.moduleManager().getModule(AsteriaSettingsModule.class);
        //if (asteria != null) asteria.updateMode();

        // User render code
        for (Renderable renderable : renderstack) {
            MinecraftClient.getInstance().getProfiler().push("ImGui Render " + renderable.getName());
            renderable.getTheme().preRender();
            renderable.render();
            renderable.getTheme().postRender();
            MinecraftClient.getInstance().getProfiler().pop();
        }
        // End of user code

        ImGui.render();
        endFrame();
    }

    private static void initializeImGui() {
        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename(null);                               // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);     // Enable Docking
        //io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);   // Enable Multi-Viewport / Platform Windows
        //io.setConfigViewportsNoTaskBarIcon(true);

        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide

        final short iconRangeMin = (short) 0xe005;
        final short iconRangeMax = (short) 0xf8ff;
        final short[] iconRange = new short[]{iconRangeMin, iconRangeMax, 0};

        rangesBuilder.addRanges(iconRange);

        final short[] glyphRanges = rangesBuilder.buildRanges();

        ImFontConfig iconsConfig = new ImFontConfig();

        iconsConfig.setMergeMode(true);
        iconsConfig.setPixelSnapH(true);
        iconsConfig.setOversampleH(3);
        iconsConfig.setOversampleV(3);

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        fontAtlas.addFontDefault();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic());
        byte[] fontAwesomeData = null;
        try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/FontAwesome6-Solid.otf")) {
            if (is != null) {
                fontAwesomeData = is.readAllBytes();
            }
        } catch (IOException ignored) {
            // do nothing, we already have font :3
        }

        try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/JetBrainsMono-Regular.ttf")) {
            if (is != null) {
                byte[] fontData = is.readAllBytes();

                monoFont18 = fontAtlas.addFontFromMemoryTTF(fontData, 18);
                monoFont20 = fontAtlas.addFontFromMemoryTTF(fontData, 20);
                monoFont24 = fontAtlas.addFontFromMemoryTTF(fontData, 24);
                monoFont32 = fontAtlas.addFontFromMemoryTTF(fontData, 32);
                monoFont48 = fontAtlas.addFontFromMemoryTTF(fontData, 48);
            }
        } catch (IOException ignored) {
            // do nothing, we already have font :3
        }

        byte[] dosisFontData = null;
        try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/Dosis-Medium.ttf")) {
            if (is != null) {
                dosisFontData = is.readAllBytes();
            }
        } catch (IOException ignored) {
            // do nothing, we already have font :3
        }

        byte[] boldDosisFontData = null;
        try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/Dosis-Bold.ttf")) {
            if (is != null) {
                boldDosisFontData = is.readAllBytes();
                boldDosisFont18 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 18);
                boldDosisFont24 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 24);
                boldDosisFont32 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 32);
                boldDosisFont48 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 48);
                boldDosisFont64 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 64);
                boldDosisFont20 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 20);
            }
        } catch (IOException ignored) {
            // do nothing, we already have font :3
        }

        byte[] mcFontData = null;
        try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/Minecraftia.ttf")) {
            if (is != null) {
                mcFontData = is.readAllBytes();
                mcFont18 = fontAtlas.addFontFromMemoryTTF(mcFontData, 18);
                mcFont24 = fontAtlas.addFontFromMemoryTTF(mcFontData, 24);
                mcFont32 = fontAtlas.addFontFromMemoryTTF(mcFontData, 32);
                mcFont48 = fontAtlas.addFontFromMemoryTTF(mcFontData, 48);
                mcFont64 = fontAtlas.addFontFromMemoryTTF(mcFontData, 64);
                mcFont20 = fontAtlas.addFontFromMemoryTTF(mcFontData, 20);
            }
        } catch (IOException ignored) {
            // do nothing, we already have font :3
        }
        fontAwesome18 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 20, iconsConfig, iconRange);

        fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
        dosisFont18 = fontAtlas.addFontFromMemoryTTF(dosisFontData, 18);
        fontAwesome18 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 18, iconsConfig, iconRange);
        dosisFont24 = fontAtlas.addFontFromMemoryTTF(dosisFontData, 24);
        fontAwesome24 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 24, iconsConfig, iconRange);
        dosisFont32 = fontAtlas.addFontFromMemoryTTF(dosisFontData, 32);
        fontAwesome32 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 28, iconsConfig, iconRange);
        dosisFont48 = fontAtlas.addFontFromMemoryTTF(dosisFontData, 48);
        fontAwesome48 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 34, iconsConfig, iconRange);
        dosisFont64 = fontAtlas.addFontFromMemoryTTF(dosisFontData, 64);
        fontAwesome64 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 50, iconsConfig, iconRange);
        dosisFont20 = fontAtlas.addFontFromMemoryTTF(dosisFontData, 20);
        fontAwesome20 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 20, iconsConfig, iconRange);

        boldDosisFont18 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 18);
        fontAwesome18 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 18, iconsConfig, iconRange);
        boldDosisFont24 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 24);
        fontAwesome24 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 24, iconsConfig, iconRange);
        boldDosisFont32 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 32);
        fontAwesome32 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 32, iconsConfig, iconRange);
        boldDosisFont48 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 48);
        fontAwesome48 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 34, iconsConfig, iconRange);
        boldDosisFont64 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 64);
        fontAwesome64 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 50, iconsConfig, iconRange);
        boldDosisFont20 = fontAtlas.addFontFromMemoryTTF(boldDosisFontData, 20);
        fontAwesome20 = fontAtlas.addFontFromMemoryTTF(fontAwesomeData, 20, iconsConfig, iconRange);

        fontConfig.destroy();
        fontAtlas.build();

        try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/blaze-client/blazetext.png")) {
            if (is != null) {
                BufferedImage image = loadImage(is);
                blazeText = loadTexture(image);
            }
        } catch (IOException ignored) {

        }

        try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/blaze-client/icon.png")) {
            if (is != null) {
                BufferedImage image = loadImage(is);
                blazeLogo = loadTexture(image);
            }
        } catch (IOException ignored) {

        }

        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final ImGuiStyle style = ImGui.getStyle();
            style.setWindowRounding(0.0f);
            style.setColor(ImGuiCol.WindowBg, ImGui.getColorU32(ImGuiCol.WindowBg, 1));
        }
    }

    private static void endFrame() {
        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }

        if (!toRemove.isEmpty()) {
            toRemove.forEach(renderstack::remove);
            toRemove.clear();
        }
    }

    public static void addRenderable(Renderable renderable) {
        renderstack.add(renderable);
    }

    public static void queueRemove(Renderable renderable) {
        toRemove.add(renderable);
    }

    public static boolean isRendered(Renderable renderable) {
        return renderstack.contains(renderable);
    }

    private ImguiLoader() {}

    private static byte[] loadFromResources(String name) {
        try {
            return Files.readAllBytes(Paths.get(Main.class.getResource(name).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}