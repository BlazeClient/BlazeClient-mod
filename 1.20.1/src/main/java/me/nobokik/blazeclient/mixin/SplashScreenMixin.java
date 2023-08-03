package me.nobokik.blazeclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.menu.MainMenuButtons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.RenderLayer;
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
import java.util.function.Consumer;
import java.util.function.IntSupplier;

import static net.minecraft.util.math.ColorHelper.Abgr.withAlpha;

@Mixin(SplashOverlay.class)
public abstract class SplashScreenMixin extends Overlay {
    @Shadow
    private long reloadCompleteTime;

    @Shadow @Final @Mutable
    static Identifier LOGO;

    @Shadow @Final @Mutable
    private static IntSupplier BRAND_ARGB;

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

    @Shadow @Final
    private Consumer<Optional<Throwable>> exceptionHandler;


    /**
     * @author a
     * @reason a
     */
    @Overwrite
    public void render(DrawContext drawContext, int i, int j, float f) {
        //LOGO =  new Identifier("blaze-client","icon.png");
        Identifier BG = new Identifier("blaze-client","waves.png");
        BRAND_ARGB = () -> ColorHelper.Argb.getArgb(255, 30, 30, 46);
        int k = drawContext.getScaledWindowWidth();
        int l = drawContext.getScaledWindowHeight();
        long m = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = m;
        }

        if (this.reloadCompleteTime > 1) {
            this.client.setOverlay(null);
            MainMenuButtons.reloadComplete = true;
        }

        float g = this.reloadCompleteTime > -1L ? (float)(m - this.reloadCompleteTime) / 1000.0F : -1.0F;
        float h = this.reloadStartTime > -1L ? (float)(m - this.reloadStartTime) / 500.0F : -1.0F;
        float o;
        int n;
        if (g >= 1.0F) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(drawContext, 0, 0, f);
            }

            n = MathHelper.ceil((1.0F - MathHelper.clamp(g - 1.0F, 0.0F, 1.0F)) * 255.0F);
            drawContext.fill(RenderLayer.getGuiOverlay(), 0, 0, k, l, withAlpha(BRAND_ARGB.getAsInt(), n));
            o = 1.0F - MathHelper.clamp(g - 1.0F, 0.0F, 1.0F);
        } else if (this.reloading) {
            if (this.client.currentScreen != null && h < 1.0F) {
                this.client.currentScreen.render(drawContext, i, j, f);
            }

            n = MathHelper.ceil(MathHelper.clamp((double)h, 0.15, 1.0) * 255.0);
            drawContext.fill(RenderLayer.getGuiOverlay(), 0, 0, k, l, withAlpha(BRAND_ARGB.getAsInt(), n));
            o = MathHelper.clamp(h, 0.0F, 1.0F);
        } else {
            n = BRAND_ARGB.getAsInt();
            float p = (float)(n >> 16 & 255) / 255.0F;
            float q = (float)(n >> 8 & 255) / 255.0F;
            float r = (float)(n & 255) / 255.0F;
            GlStateManager._clearColor(p, q, r, 1.0F);
            GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            o = 1.0F;
        }

        n = (int)((double)drawContext.getScaledWindowWidth() * 0.5);
        int s = (int)((double)drawContext.getScaledWindowHeight() * 0.5);
        double d = Math.min((double)drawContext.getScaledWindowWidth() * 0.75, (double)drawContext.getScaledWindowHeight()) * 0.25;
        int t = (int)(d * 0.5);
        double e = d * 4.0;
        int u = (int)(e * 0.5);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
        drawContext.setShaderColor(1.0F, 1.0F, 1.0F, o);
        drawContext.drawTexture(LOGO, n - u, s - t, u, (int)d, -0.0625F, 0.0F, 120, 60, 120, 120);
        drawContext.drawTexture(LOGO, n, s - t, u, (int)d, 0.0625F, 60.0F, 120, 60, 120, 120);
        drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        int v = (int)((double)drawContext.getScaledWindowHeight() * 0.8325);
        float w = this.reload.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95F + w * 0.050000012F, 0.0F, 1.0F);
        if (g < 1.0F) {
            this.renderProgressBar(drawContext, k / 2 - u, v - 5, k / 2 + u, v + 5, 1.0F - MathHelper.clamp(g, 0.0F, 1.0F));
        }

        if (g >= 2.0F) {
            this.client.setOverlay((Overlay)null);
        }

        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || h >= 2.0F)) {
            try {
                this.reload.throwException();
                this.exceptionHandler.accept(Optional.empty());
            } catch (Throwable var23) {
                this.exceptionHandler.accept(Optional.of(var23));
            }

            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, drawContext.getScaledWindowWidth(), drawContext.getScaledWindowHeight());
            }
        }
    }

    private void renderProgressBar(DrawContext drawContext, int i, int j, int k, int l, float f) {
        int m = MathHelper.ceil((float)(k - i - 2) * this.progress);
        int n = Math.round(f * 255.0F);
        int o = ColorHelper.Argb.getArgb(n, 255, 255, 255);
        drawContext.fill(i + 2, j + 2, i + m, l - 2, o);
        drawContext.fill(i + 1, j, k - 1, j + 1, o);
        drawContext.fill(i + 1, l, k - 1, l - 1, o);
        drawContext.fill(i, j, i + 1, l, o);
        drawContext.fill(k, j, k - 1, l, o);
    }
}