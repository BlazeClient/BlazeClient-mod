package me.nobokik.blazeclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.api.helpers.IndicatorHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    private GameProfile profile;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"))
    public int moveName(TextRenderer instance, StringVisitable text) {
        if (profile != null && IndicatorHelper.isUsingClient(profile.getId()))
            return instance.getWidth(text) + 10;
        return instance.getWidth(text);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    public int moveName2(DrawContext instance, TextRenderer textRenderer, Text text, int i, int j, int k) {
        if (profile != null && IndicatorHelper.isUsingClient(profile.getId())) {
            RenderSystem.setShaderTexture(0, IndicatorHelper.badgeIcon);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            instance.drawTexture(IndicatorHelper.badgeIcon, i, j, 8, 8, 0, 0, 8, 8, 8, 8);

            i += 9;
        }
        profile = null;
        return instance.drawTextWithShadow(textRenderer, text, i, j, k);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
    public PlayerListEntry getPlayer(PlayerListEntry playerEntry) {
        profile = playerEntry.getProfile();
        return playerEntry;
    }
}