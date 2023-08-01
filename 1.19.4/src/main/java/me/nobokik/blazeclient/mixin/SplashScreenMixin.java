package me.nobokik.blazeclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.menu.MainMenuButtons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.util.Optional;
import java.util.OptionalInt;

import static net.minecraft.util.math.ColorHelper.Abgr.withAlpha;

@Mixin(SplashOverlay.class)
public abstract class SplashScreenMixin extends Overlay {
    @Shadow
    private long reloadCompleteTime;

    @Shadow @Final @Mutable
    static Identifier LOGO;

    @Shadow @Final @Mutable
    static int MOJANG_RED;

    @Shadow @Final
    private MinecraftClient client;

    @Shadow @Final
    private boolean reloading;

    @Shadow
    private long reloadStartTime;

    @Shadow @Final
    private ResourceReload reload;
    @Shadow
    private float progress;


    /**
     * @author a
     * @reason a
     */
    @Overwrite
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //LOGO =  new Identifier("blaze-client","icon.png");
        Identifier BG = new Identifier("blaze-client","waves.png");
        MOJANG_RED = ColorHelper.Argb.getArgb(255, 30, 30, 46);
        if (this.reloadCompleteTime > 1) {
            this.client.setOverlay(null);
            MainMenuButtons.reloadComplete = true;
        }

        int i = this.client.getWindow().getScaledWidth();
        int j = this.client.getWindow().getScaledHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = l;
        }

        float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0F : -1.0F;
        float g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0F : -1.0F;
        float h;
        int k;
        if (f >= 1.0F) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(matrices, 0, 0, delta);
            }

            k = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            fill(matrices, 0, 0, i, j, withAlpha(MOJANG_RED, k));
            h = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.reloading) {
            if (this.client.currentScreen != null && g < 1.0F) {
                this.client.currentScreen.render(matrices, mouseX, mouseY, delta);
            }

            k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);
            fill(matrices, 0, 0, i, j, withAlpha(MOJANG_RED, k));
            h = MathHelper.clamp(g, 0.0F, 1.0F);
        } else {
            k = MOJANG_RED;
            float m = (float)(k >> 16 & 255) / 255.0F;
            float n = (float)(k >> 8 & 255) / 255.0F;
            float o = (float)(k & 255) / 255.0F;
            GlStateManager._clearColor(m, n, o, 1.0F);
            GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            h = 1.0F;
        }

        k = (int)((double)this.client.getWindow().getScaledWidth() * 0.5);
        int p = (int)((double)this.client.getWindow().getScaledHeight() * 0.5);
        double d = Math.min((double)this.client.getWindow().getScaledWidth() * 0.75, (double)this.client.getWindow().getScaledHeight()) * 0.25;
        int q = (int)(d * 0.5);
        double e = d * 4.0;
        int r = (int)(e * 0.5);
        RenderSystem.setShaderTexture(0, LOGO);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, h);
        drawTexture(matrices, k - r, p - q, r, (int)d, -0.0625F, 0.0F, 120, 60, 120, 120);
        drawTexture(matrices, k, p - q, r, (int)d, 0.0625F, 60.0F, 120, 60, 120, 120);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        int s = (int)((double)this.client.getWindow().getScaledHeight() * 0.8325);
        float t = this.reload.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95F + t * 0.050000012F, 0.0F, 1.0F);
        if (f < 1.0F) {
            this.renderProgressBar(matrices, i / 2 - r, s - 5, i / 2 + r, s + 5, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
        }

        if (f >= 2.0F) {
            this.client.setOverlay((Overlay)null);
        }

        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0F)) {
            //try {
            //    this.reload.throwException();
            //    this.exceptionHandler.accept(Optional.empty());
            //} catch (Throwable var23) {
            //    this.exceptionHandler.accept(Optional.of(var23));
            //}
            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
            }
        }
    }
    private void renderProgressBar(MatrixStack matrices, int minX, int minY, int maxX, int maxY, float opacity) {
        int i = MathHelper.ceil((float)(maxX - minX - 2) * this.progress);
        int j = Math.round(opacity * 255.0F);
        int k = ColorHelper.Argb.getArgb(j, 255, 255, 255);
        fill(matrices, minX + 2, minY + 2, minX + i, maxY - 2, k);
        fill(matrices, minX + 1, minY, maxX - 1, minY + 1, k);
        fill(matrices, minX + 1, maxY, maxX - 1, maxY - 1, k);
        fill(matrices, minX, minY, minX + 1, maxY, k);
        fill(matrices, maxX, minY, maxX - 1, maxY, k);
    }
}