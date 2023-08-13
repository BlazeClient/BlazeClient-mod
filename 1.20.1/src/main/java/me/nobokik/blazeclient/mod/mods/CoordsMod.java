package me.nobokik.blazeclient.mod.mods;

import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.api.helpers.FPSHelper;
import me.nobokik.blazeclient.api.helpers.MathHelper;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.text.WordUtils;

import static me.nobokik.blazeclient.Client.modManager;

public class CoordsMod extends Mod implements Renderable {
    private boolean firstFrame = true;
    public final ColorSetting background = new ColorSetting("Background Color", this, new JColor(0f, 0f, 0f, 0.75f), true);
    public final ColorSetting text = new ColorSetting("Text Color", this, new JColor(1f, 1f, 1f), false);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, true);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 1, 2, 0.1);
    public final NumberSetting width = new NumberSetting("Width", this, 200, 150, 300, 1);
    public final NumberSetting height = new NumberSetting("Height", this, 150, 100, 200, 1);
    public final BooleanSetting roundedCorners = new BooleanSetting("Rounded Corners", this, false);
    public final ModeSetting fontSetting = new ModeSetting("Font", this, "Minecraft", "Minecraft", "Dosis", "Mono");
    public final BooleanSetting biome = new BooleanSetting("Biome", this, true);
    public final BooleanSetting direction = new BooleanSetting("Direction", this, true);
    public CoordsMod() {
        super("Coords", "Shows your coordinates.", "\uF124");
        toggleVisibility();
    }

    public void toggleVisibility() {
        ImguiLoader.addRenderable(this);
    }

    @Override
    public void render() {
        if(!Client.modManager().getMod("Coords").isEnabled()) {
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

        ImFont font = ImguiLoader.getMonoFont32();
        if(fontSetting.is("Minecraft")) {
            font = ImguiLoader.getMcFont32();
        } else if (fontSetting.is("Dosis")) {
            font = ImguiLoader.getDosisFont32();
        } else if (fontSetting.is("Mono")) {
            font = ImguiLoader.getMonoFont32();
        }
        font.setScale(scale.getFValue());

        int imGuiWindowFlags = 0;
        imGuiWindowFlags |= ImGuiWindowFlags.NoTitleBar;
        imGuiWindowFlags |= ImGuiWindowFlags.NoDocking;
        imGuiWindowFlags |= ImGuiWindowFlags.NoFocusOnAppearing;
        imGuiWindowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus;
        imGuiWindowFlags |= ImGuiWindowFlags.NoResize;
        imGuiWindowFlags |= ImGuiWindowFlags.NoScrollbar;
        float[] c;
        if(!FirstMenu.getInstance().isVisible) {
            ImGui.getStyle().setWindowBorderSize(0);
            imGuiWindowFlags |= ImGuiWindowFlags.NoMove;
            ImGui.pushStyleColor(ImGuiCol.Border, 0f,0f,0f,0f);
            c = background.getColor().getFloatColor();
            ImGui.pushStyleColor(ImGuiCol.WindowBg, c[0], c[1], c[2], c[3]);
        } else {
            ImGui.getStyle().setWindowBorderSize(1);
            ImGui.pushStyleColor(ImGuiCol.Border, 1f, 1f, 1f, 1f);
            c = background.getColor().jBrighter().getFloatColor();
            ImGui.pushStyleColor(ImGuiCol.WindowBg, c[0], c[1], c[2], c[3]);
        }
        c = text.getColor().getFloatColor();
        ImGui.pushStyleColor(ImGuiCol.Text, c[0], c[1], c[2], c[3]);

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
        ImGui.setNextWindowSize(width.getFValue() * scale.getFValue(), height.getFValue() * scale.getFValue());
        ImGui.pushFont(font);
        ImGui.getStyle().setWindowRounding(0);
        if(roundedCorners.isEnabled()) ImGui.getStyle().setWindowRounding(16f * scale.getFValue());
        ImGui.begin(this.getName(), imGuiWindowFlags);
        if(mc.player != null && mc.world != null) {
            String dirString = "";
            if(mc.player.getHorizontalFacing().getAxis() == Direction.Axis.X) {
                if(mc.player.getHorizontalFacing().getDirection() == Direction.AxisDirection.POSITIVE) {
                    dirString = "X+";
                } else if(mc.player.getHorizontalFacing().getDirection() == Direction.AxisDirection.NEGATIVE) {
                    dirString = "X-";
                }
            } else if(mc.player.getHorizontalFacing().getAxis() == Direction.Axis.Z) {
                if(mc.player.getHorizontalFacing().getDirection() == Direction.AxisDirection.POSITIVE) {
                    dirString = "Z+";
                } else if(mc.player.getHorizontalFacing().getDirection() == Direction.AxisDirection.NEGATIVE) {
                    dirString = "Z-";
                }
            }

            String nameString = "";
            if(mc.player.getHorizontalFacing().getName().equals("east")) nameString = "E";
            if(mc.player.getHorizontalFacing().getName().equals("west")) nameString = "W";
            if(mc.player.getHorizontalFacing().getName().equals("south")) nameString = "S";
            if(mc.player.getHorizontalFacing().getName().equals("north")) nameString = "N";

            float nameWidth = ImGui.calcTextSize(nameString).x;
            float dirWidth = ImGui.calcTextSize(dirString).x;

            if (textShadow.isEnabled()) {
                c = text.getColor().getFloatColor();
                ImVec2 oldPos;

                oldPos = ImGui.getCursorPos();
                ImGui.setCursorPosX(width.getFValue() * scale.getFValue() - 4 - dirWidth);
                if(direction.isEnabled()) UI.shadowText(dirString, 24, c[0], c[1], c[2], 1f);
                ImGui.setCursorPos(oldPos.x, oldPos.y);

                UI.shadowText(" X: " + MathHelper.round(mc.player.getPos().x, 1), 24, c[0], c[1], c[2], 1f);

                oldPos = ImGui.getCursorPos();
                ImGui.setCursorPosX(width.getFValue() * scale.getFValue() - 4 - nameWidth);
                if(direction.isEnabled()) UI.shadowText(nameString, 24, c[0], c[1], c[2], 1f);
                ImGui.setCursorPos(oldPos.x, oldPos.y);

                UI.shadowText(" Y: " + MathHelper.round(mc.player.getPos().y, 1), 24, c[0], c[1], c[2], 1f);
                UI.shadowText(" Z: " + MathHelper.round(mc.player.getPos().z, 1), 24, c[0], c[1], c[2], 1f);
                String biomeString =
                        mc.world.getBiome(new BlockPos((int) mc.player.getPos().x, (int) mc.player.getPos().y, (int) mc.player.getPos().z)).getKey().get().getValue().getPath();
                biomeString = biomeString.replaceAll("_", " ");
                biomeString = WordUtils.capitalize(biomeString);
                if (biome.isEnabled())
                    UI.shadowText(" Biome: " + biomeString, 24, c[0], c[1], c[2], 1f);
            } else {
                ImVec2 oldPos;
                oldPos = ImGui.getCursorPos();
                ImGui.setCursorPosX(width.getFValue() * scale.getFValue() - 4 - dirWidth);
                if(direction.isEnabled()) ImGui.text(dirString);
                ImGui.setCursorPos(oldPos.x, oldPos.y);

                ImGui.text(" X: " + MathHelper.round(mc.player.getPos().x, 1));

                oldPos = ImGui.getCursorPos();
                ImGui.setCursorPosX(width.getFValue() * scale.getFValue() - 4 - nameWidth);
                if(direction.isEnabled()) ImGui.text(nameString);
                ImGui.setCursorPos(oldPos.x, oldPos.y);

                ImGui.text(" Y: " + MathHelper.round(mc.player.getPos().y, 1));
                ImGui.text(" Z: " + MathHelper.round(mc.player.getPos().z, 1));
                String biomeString =
                        mc.world.getBiome(new BlockPos((int) mc.player.getPos().x, (int) mc.player.getPos().y, (int) mc.player.getPos().z)).getKey().get().getValue().getPath();
                biomeString = biomeString.replaceAll("_", " ");
                biomeString = WordUtils.capitalize(biomeString);
                if (biome.isEnabled())
                    ImGui.text(" Biome: " + biomeString);
            }
        } else {
            if (textShadow.isEnabled()) {
                ImVec2 oldPos;
                c = text.getColor().getFloatColor();

                oldPos = ImGui.getCursorPos();
                ImGui.setCursorPosX(width.getFValue() * scale.getFValue() - 4 - ImGui.calcTextSize("??").x);
                if(direction.isEnabled()) UI.shadowText("??", 24, c[0], c[1], c[2], 1f);
                ImGui.setCursorPos(oldPos.x, oldPos.y);

                UI.shadowText(" X: ???", 24, c[0], c[1], c[2], 1f);

                oldPos = ImGui.getCursorPos();
                ImGui.setCursorPosX(width.getFValue() * scale.getFValue() - 4 - ImGui.calcTextSize("?").x);
                if(direction.isEnabled()) UI.shadowText("?", 24, c[0], c[1], c[2], 1f);
                ImGui.setCursorPos(oldPos.x, oldPos.y);


                UI.shadowText(" Y: ???", 24, c[0], c[1], c[2], 1f);
                UI.shadowText(" Z: ???", 24, c[0], c[1], c[2], 1f);
                if (biome.isEnabled())
                    UI.shadowText(" Biome: ???", 24, c[0], c[1], c[2], 1f);
            } else {
                ImVec2 oldPos;
                oldPos = ImGui.getCursorPos();
                ImGui.setCursorPosX(width.getFValue() * scale.getFValue() - 4 - ImGui.calcTextSize("??").x);
                if(direction.isEnabled()) ImGui.text("??");
                ImGui.setCursorPos(oldPos.x, oldPos.y);
                ImGui.text(" X: ???");

                oldPos = ImGui.getCursorPos();
                ImGui.setCursorPosX(width.getFValue() * scale.getFValue() - 4 - ImGui.calcTextSize("?").x);
                if(direction.isEnabled()) ImGui.text("?");
                ImGui.setCursorPos(oldPos.x, oldPos.y);

                ImGui.text(" Y: ???");
                ImGui.text(" Z: ???");
                if (biome.isEnabled())
                    ImGui.text(" Biome: ???");
            }
        }

        ImGui.popStyleColor(3);
        ImGui.popFont();
        font.setScale(1f);
        ImGui.getStyle().setWindowRounding(8);
        this.position = ImGui.getWindowPos();
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
