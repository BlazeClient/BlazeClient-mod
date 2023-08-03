package me.nobokik.blazeclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.api.helpers.IndicatorHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
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
}