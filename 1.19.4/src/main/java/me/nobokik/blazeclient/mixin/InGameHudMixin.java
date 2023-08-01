package me.nobokik.blazeclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.HudRenderEvent;
import me.nobokik.blazeclient.mod.mods.ArmorMod;
import me.nobokik.blazeclient.mod.mods.CrosshairMod;
import me.nobokik.blazeclient.mod.mods.PotionMod;
import me.nobokik.blazeclient.mod.mods.ScoreboardMod;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.nobokik.blazeclient.Client.mc;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Shadow
    private PlayerEntity getCameraPlayer() {
        return null;
    }

    @Shadow
    private void renderHotbarItem(MatrixStack matrixStack, int i, int j, float f, PlayerEntity playerEntity, ItemStack itemStack, int k) {

    }

    @Inject(
            method = "renderHotbar",
            at = @At("TAIL")
    )
    private void armorhud$injectArmorHUD(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (Client.modManager().getMod(ArmorMod.class).isEnabled()) this.armorhud$renderArmorHUD(tickDelta, matrices);
    }

    private void armorhud$renderArmorHUD(float tickDelta, MatrixStack matrices) {
        ArmorMod mod = Client.modManager().getMod(ArmorMod.class);

        PlayerEntity player = this.getCameraPlayer();
        int xPos = (int) (mod.position.x / mc.options.getGuiScale().getValue());
        int yPos = (int) (mod.position.y / mc.options.getGuiScale().getValue());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if(mod.direction.getMode().equals("Vertical")) {
            for (int i = 0; i < 4; i++) {
                this.renderHotbarItem(matrices, xPos + 3, yPos + 16 * i, tickDelta, player, player.getInventory().armor.get(3 - i), 1);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                this.renderHotbarItem(matrices, xPos +  16 * i, yPos + 3, tickDelta, player, player.getInventory().armor.get(3 - i), 1);
            }
        }
        RenderSystem.disableBlend();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        Client.EVENTBUS.post(HudRenderEvent.get(matrices, tickDelta));
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
    public void renderStatusEffectOverlay(MatrixStack matrices, CallbackInfo ci) {
        if(Client.modManager().getMod(PotionMod.class).isEnabled() && Client.modManager().getMod(PotionMod.class).hideVanilla.isEnabled()) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
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
                        drawRectangle(matrices,
                                mc.getWindow().getScaledWidth() / 2 - 5 + col,
                                mc.getWindow().getScaledHeight() / 2 - 5 + row,
                                1, 1, mod.targetColor.getColor().getRGB()
                        );
                    } else {
                        drawRectangle(matrices,
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

            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            if (targetingEntity) {
                DrawableHelper.drawTexture(matrices, x, y, 68, 94, 16, 16);
            } else if (progress < 1.0F) {
                int k = (int) (progress * 17.0F);
                DrawableHelper.drawTexture(matrices, x, y, 36, 94, 16, 4);
                DrawableHelper.drawTexture(matrices, x, y, 52, 94, k, 4);
            }
            RenderSystem.defaultBlendFunc();
        }
        //matrices.pop();
    }
    private static void drawRectangle(MatrixStack matrices, int x, int y, int w, int h, int color) {
        DrawableHelper.fill(matrices, x, y, x + w, y + h, color);
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && Client.modManager().getMod(ScoreboardMod.class).hideScoreboard.isEnabled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", ordinal = 1))
    private int scoreboard$drawHeadingText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && Client.modManager().getMod(ScoreboardMod.class).textShadow.isEnabled()) {
            return textRenderer.drawWithShadow(matrices, text, x, y, color);
        }
        return textRenderer.draw(matrices, text, x, y, color);
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", ordinal = 0))
    private int scoreboard$drawText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && Client.modManager().getMod(ScoreboardMod.class).textShadow.isEnabled()) {
            return textRenderer.drawWithShadow(matrices, text, x, y, color);
        }
        return textRenderer.draw(matrices, text, x, y, color);
    }

    @ModifyArg(
            method = "renderScoreboardSidebar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"
            ),
            index = 1
    )
    public String removeNumbers(String text) {
        if (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && !Client.modManager().getMod(ScoreboardMod.class).numbers.isEnabled()) return "";
        else return text;
    }

    @Redirect(method = "renderScoreboardSidebar", slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 0))
    private int scoreboard$modifyScoreWidth(TextRenderer textRenderer, String score, MatrixStack matrices, ScoreboardObjective objective) {
        return (Client.modManager().getMod(ScoreboardMod.class).isEnabled() && !Client.modManager().getMod(ScoreboardMod.class).numbers.isEnabled()) ? 0 : textRenderer.getWidth(score);
    }


    @Redirect(
            method = "renderScoreboardSidebar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I",
                    ordinal = 0
            )
    )
    public int scoreboard$setBackgroundOpacity(GameOptions instance, float fallbackOpacity) {
        if(!Client.modManager().getMod(ScoreboardMod.class).isEnabled()) return mc.options.getTextBackgroundColor(fallbackOpacity);
        return (Client.modManager().getMod(ScoreboardMod.class).background.isEnabled()) ? mc.options.getTextBackgroundColor(fallbackOpacity) : mc.options.getTextBackgroundColor(0F);
    }

    @Redirect(
            method = "renderScoreboardSidebar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I",
                    ordinal = 1
            )
    )
    public int scoreboard$setBackgroundOpacity2(GameOptions instance, float fallbackOpacity) {
        if(!Client.modManager().getMod(ScoreboardMod.class).isEnabled()) return mc.options.getTextBackgroundColor(fallbackOpacity);
        return (Client.modManager().getMod(ScoreboardMod.class).background.isEnabled()) ? mc.options.getTextBackgroundColor(fallbackOpacity) : mc.options.getTextBackgroundColor(0F);
    }
}
