package me.nobokik.blazeclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.helpers.IndicatorHelper;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static me.nobokik.blazeclient.Client.mc;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    private GameProfile profile;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"))
    public int moveName(TextRenderer instance, StringVisitable text) {
        if (profile != null && IndicatorHelper.isUsingClient(profile.getId()))
            return instance.getWidth(text) + 10;
        return instance.getWidth(text);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    public int moveName2(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color) {
        if (profile != null && IndicatorHelper.isUsingClient(profile.getId())) {
            RenderSystem.setShaderTexture(0, IndicatorHelper.badgeIcon);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            DrawableHelper.drawTexture(matrices, (int) x, (int) y, 8, 8, 0, 0, 8, 8, 8, 8);

            x += 9;
        }
        profile = null;
        return instance.drawWithShadow(matrices, text, x, y, color);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
    public PlayerListEntry getPlayer(PlayerListEntry playerEntry) {
        profile = playerEntry.getProfile();
        return playerEntry;
    }

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    public void renderDetailedLatency(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        if (Client.modManager().getMod(GeneralSettings.class).numericalPing.isEnabled()) {
            ci.cancel();

            String pingString = Integer.toString(entry.getLatency());
            pingString = shiftPing(pingString);
            if(Client.modManager().getMod(GeneralSettings.class).msPing.isEnabled()) pingString = pingString + "ms";

            matrices.push();
            matrices.translate(0, 0, 100);

            mc.textRenderer.drawWithShadow(matrices, pingString,
                    x + width - mc.textRenderer.getWidth(pingString) - 1, y - (Client.modManager().getMod(GeneralSettings.class).smallPing.isEnabled() ? 2 : 0),
                    getPingColour(entry.getLatency()));

            matrices.pop();
        }
    }

    public String shiftPing(String string) {
        if (Client.modManager().getMod(GeneralSettings.class).smallPing.isEnabled()) {
            // based on numeric ping
            char[] characters = new char[string.length()];

            for (int index = 0; index < string.length(); index++) {
                characters[index] = string.charAt(index);

                if (Character.isDigit(characters[index])) {
                    characters[index] += 8272;
                }
            }

            return String.valueOf(characters);
        }

        return string;
    }

    private static int getPingColour(int latency) {
        int level;

        if (latency == -2) {
            level = latency;
        } else if (latency < 0) {
            level = 5;
        } else if (latency < 50) {
            level = 0;
        } else if (latency < 100) {
            level = 1;
        } else if (latency < 150) {
            level = 2;
        } else if (latency < 200) {
            level = 3;
        } else {
            level = 4;
        }

        Color colour = switch (level) {
            case 1 -> new Color(0xFFFF00);
            case 2 -> new Color(0xFF9600);
            case 3 -> new Color(0xFF6400);
            case 4, 5 -> new Color(0xFF0000);
            default -> new Color(0x00FF00);
        };

        return colour.getRGB();
    }
}