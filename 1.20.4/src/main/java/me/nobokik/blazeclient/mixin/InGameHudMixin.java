package me.nobokik.blazeclient.mixin;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.HudRenderEvent;
import me.nobokik.blazeclient.api.util.SidebarEntry;
import me.nobokik.blazeclient.mod.mods.ArmorMod;
import me.nobokik.blazeclient.mod.mods.CrosshairMod;
import me.nobokik.blazeclient.mod.mods.PotionMod;
import me.nobokik.blazeclient.mod.mods.ScoreboardMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.stream.Collectors;

import static me.nobokik.blazeclient.Client.mc;

@Mixin(value = InGameHud.class, priority = 9999)
public class InGameHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Shadow
    private PlayerEntity getCameraPlayer() {
        return null;
    }

    @Shadow
    private void renderHotbarItem(DrawContext drawContext, int i, int j, float f, PlayerEntity playerEntity, ItemStack itemStack, int k) {

    }

    @Inject(
            method = "renderHotbar",
            at = @At("TAIL")
    )
    private void armorhud$injectArmorHUD(float f, DrawContext drawContext, CallbackInfo ci) {
        if (Client.modManager().getMod(ArmorMod.class).isEnabled()) this.armorhud$renderArmorHUD(f, drawContext);
    }

    private void armorhud$renderArmorHUD(float tickDelta, DrawContext drawContext) {
        ArmorMod mod = Client.modManager().getMod(ArmorMod.class);

        PlayerEntity player = this.getCameraPlayer();
        int xPos = (int) (mod.position.x / mc.options.getGuiScale().getValue());
        int yPos = (int) (mod.position.y / mc.options.getGuiScale().getValue());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if(mod.direction.getMode().equals("Vertical")) {
            for (int i = 0; i < 4; i++) {
                this.renderHotbarItem(drawContext, xPos + 3, yPos + 16 * i, tickDelta, player, player.getInventory().armor.get(3 - i), 1);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                this.renderHotbarItem(drawContext, xPos +  16 * i, yPos + 3, tickDelta, player, player.getInventory().armor.get(3 - i), 1);
            }
        }
        RenderSystem.disableBlend();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext drawContext, float f, CallbackInfo ci) {
        Client.EVENTBUS.post(HudRenderEvent.get(drawContext.getMatrices(), f));
    }

    //@Inject(method = "render", at = @At("RETURN"))
    //public void changeGamma(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
    //    if(Client.modManager().getMod(GeneralSettings.class).fullbright.isEnabled()) {
    //        mc.options.getGamma().setValue(69420.0);
    //    }else {
    //        mc.options.getGamma().setValue(1.0);
    //    }
    //}

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void renderStatusEffectOverlay(DrawContext drawContext, CallbackInfo ci) {
        if(Client.modManager().getMod(PotionMod.class).isEnabled() && Client.modManager().getMod(PotionMod.class).hideVanilla.isEnabled()) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(DrawContext drawContext, CallbackInfo ci) {
        if(!Client.modManager().getMod(CrosshairMod.class).isEnabled()) return;
        ci.cancel();
        if (!mc.options.getPerspective().isFirstPerson()) return;

        CrosshairMod mod = Client.modManager().getMod(CrosshairMod.class);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {
                if (mod.crosshair[row][col]) {
                    if (mc.targetedEntity instanceof LivingEntity) {
                        drawRectangle(drawContext,
                                mc.getWindow().getScaledWidth() / 2 - 5 + col,
                                mc.getWindow().getScaledHeight() / 2 - 5 + row,
                                1, 1, mod.targetColor.getColor().getRGB()
                        );
                    } else {
                        drawRectangle(drawContext,
                                mc.getWindow().getScaledWidth() / 2 - 5 + col,
                                mc.getWindow().getScaledHeight() / 2 - 5 + row,
                                1, 1, mod.color.getColor().getRGB()
                        );
                    }
                }
            }
        }
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();

        AttackIndicator indicator = mc.options.getAttackIndicator().getValue();
        if (indicator == AttackIndicator.CROSSHAIR) {
            float progress = mc.player.getAttackCooldownProgress(0.0F);

            // Whether a cross should be displayed under the indicator
            boolean targetingEntity = false;
            if (mc.targetedEntity != null && mc.targetedEntity instanceof LivingEntity
                    && progress >= 1.0F) {
                targetingEntity = mc.player.getAttackCooldownProgressPerTick() > 5.0F;
                targetingEntity &= mc.targetedEntity.isAlive();
            }

            int x = (int) ((mc.getWindow().getScaledWidth()) / 2 - 8);
            int y = (int) ((mc.getWindow().getScaledHeight()) / 2 - 7 + 16);

            //RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            if (targetingEntity) {
                drawContext.drawTexture(new Identifier("textures/gui/icons.png"), x, y, 68, 94, 16, 16);
            } else if (progress < 1.0F) {
                int k = (int) (progress * 17.0F);
                drawContext.drawTexture(new Identifier("textures/gui/icons.png"), x, y, 36, 94, 16, 4);
                drawContext.drawTexture(new Identifier("textures/gui/icons.png"), x, y, 52, 94, k, 4);
            }
            RenderSystem.defaultBlendFunc();
        }
        //matrices.pop();
    }
    private static void drawRectangle(DrawContext drawContext, int x, int y, int w, int h, int color) {
        drawContext.fill(x, y, x + w, y + h, color);
    }

    //@Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    //private void renderScoreboardSidebar(DrawContext drawContext, ScoreboardObjective scoreboardObjective, CallbackInfo ci) {
    //    if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && Client.modManager().getMod(ScoreboardMod.class).hideScoreboard.isEnabled()) {
    //        ci.cancel();
    //    }
    //}
    @Final
    @Shadow
    private static Comparator<ScoreboardEntry> SCOREBOARD_ENTRY_COMPARATOR;

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(DrawContext drawContext, ScoreboardObjective scoreboardObjective, CallbackInfo ci) {
        if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() &&
                Client.modManager().getMod(ScoreboardMod.class).hideScoreboard.isEnabled()) {
            ci.cancel();
        }
    }
    /*@Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(DrawContext drawContext, ScoreboardObjective scoreboardObjective, CallbackInfo ci) {
        if (Client.modManager().getMod(ScoreboardMod.class).isEnabled()) {
            ci.cancel();
            if(Client.modManager().getMod(ScoreboardMod.class).hideScoreboard.isEnabled()) return;
        }

        Scoreboard scoreboard = scoreboardObjective.getScoreboard();
        NumberFormat numberFormat = scoreboardObjective.getNumberFormatOr(StyledNumberFormat.RED);
        SidebarEntry[] ScoreboardEntrys = (SidebarEntry[])scoreboard.getScoreboardEntries(scoreboardObjective).stream().filter((scoreboardEntry) -> {
            return !scoreboardEntry.hidden();
        }).sorted(SCOREBOARD_ENTRY_COMPARATOR).limit(15L).map((scoreboardEntry) -> {
            Team team = scoreboard.getScoreHolderTeam(scoreboardEntry.comp_2127());
            Text text = scoreboardEntry.name();
            Text text2 = Team.decorateName(team, text);
            Text text3 = scoreboardEntry.formatted(numberFormat);
            int i = MinecraftClient.getInstance().textRenderer.getWidth(text3);
            return new SidebarEntry(text2, text3, i);
        }).toArray(SidebarEntry[]::new);
        Text text = scoreboardObjective.getDisplayName();
        int i = MinecraftClient.getInstance().textRenderer.getWidth(text);
        int j = i;
        int k = MinecraftClient.getInstance().textRenderer.getWidth(": ");
        SidebarEntry[] var10 = ScoreboardEntrys;
        int var11 = ScoreboardEntrys.length;

        for(int var12 = 0; var12 < var11; ++var12) {
            SidebarEntry ScoreboardEntry = var10[var12];
            j = Math.max(j, MinecraftClient.getInstance().textRenderer.getWidth(ScoreboardEntry.name()) + (ScoreboardEntry.scoreWidth() > 0 ? k + ScoreboardEntry.scoreWidth() : 0));
        }

        int finalJ = j;
        drawContext.draw(() -> {
            int length = ScoreboardEntrys.length;
            Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
            int l = length * 9;
            int m = this.scaledHeight / 2 + l / 3;
            int n = 1;
            int o = this.scaledWidth - finalJ - 3;
            int p = this.scaledWidth - 3 + 2;
            int q = MinecraftClient.getInstance().options.getTextBackgroundColor(0.3F);
            int r = MinecraftClient.getInstance().options.getTextBackgroundColor(0.4F);
            Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
            int s = m - length * 9;
            int var10001 = o - 2;
            Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
            drawContext.fill(var10001, s - 9 - 1, p, s - 1, r);
            drawContext.fill(o - 2, s - 1, p, m, q);
            TextRenderer var18 = MinecraftClient.getInstance().textRenderer;
            int var10003 = o + finalJ / 2 - i / 2;
            Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
            drawContext.drawText(var18, text, var10003, s - 9, -1, false);

            for(int t = 0; t < length; ++t) {
                SidebarEntry ScoreboardEntry = ScoreboardEntrys[t];
                var10001 = length - t;
                Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
                int u = m - var10001 * 9;
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, ScoreboardEntry.comp_2131, o, u, -1, false);
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, ScoreboardEntry.comp_2132, p - ScoreboardEntry.comp_2133, u, -1, false);
            }

        });
    }
*/
    //@Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", ordinal = 1))
    //private int scoreboard$drawHeadingText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
    //    if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && Client.modManager().getMod(ScoreboardMod.class).textShadow.isEnabled()) {
    //        return context.drawTextWithShadow(textRenderer, text, x, y, color);
    //    }
    //    return context.drawText(textRenderer, text, x, y, color, false);
    //}
//
    //@Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", ordinal = 0))
    //private int scoreboard$drawText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
    //    if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && Client.modManager().getMod(ScoreboardMod.class).textShadow.isEnabled()) {
    //        return context.drawTextWithShadow(textRenderer, text, x, y, color);
    //    }
    //    return context.drawText(textRenderer, text, x, y, color, false);
    //}
//
    //@ModifyArg(
    //        method = "renderScoreboardSidebar",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"
    //        ),
    //        index = 1
    //)
    //public String removeNumbers(String text) {
    //    if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && !Client.modManager().getMod(ScoreboardMod.class).numbers.isEnabled()) return "";
    //    else return text;
    //}
//
    //@Redirect(method = "renderScoreboardSidebar", slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 0))
    //private int scoreboard$modifyScoreWidth(TextRenderer textRenderer, String score, DrawContext context, ScoreboardObjective objective) {
    //    return (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && !Client.modManager().getMod(ScoreboardMod.class).numbers.isEnabled()) ? 0 : textRenderer.getWidth(score);
    //}
//
//
    //@Redirect(
    //        method = "renderScoreboardSidebar",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I",
    //                ordinal = 0
    //        )
    //)
    //public int scoreboard$setBackgroundOpacity(GameOptions instance, float fallbackOpacity) {
    //    if(!Client.modManager().getMod(ScoreboardMod.class).isEnabled()) return mc.options.getTextBackgroundColor(fallbackOpacity);
    //    return (Client.modManager().getMod(ScoreboardMod.class).background.isEnabled()) ? mc.options.getTextBackgroundColor(fallbackOpacity) : mc.options.getTextBackgroundColor(0F);
    //}
//
    //@Redirect(
    //        method = "renderScoreboardSidebar",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I",
    //                ordinal = 1
    //        )
    //)
    //public int scoreboard$setBackgroundOpacity2(GameOptions instance, float fallbackOpacity) {
    //    if(!Client.modManager().getMod(ScoreboardMod.class).isEnabled()) return mc.options.getTextBackgroundColor(fallbackOpacity);
    //    return (Client.modManager().getMod(ScoreboardMod.class).background.isEnabled()) ? mc.options.getTextBackgroundColor(fallbackOpacity) : mc.options.getTextBackgroundColor(0F);
    //}
}
