package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.api.helpers.IndicatorHelper;
import me.nobokik.blazeclient.mod.mods.NametagsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity>  {
    @Shadow @Final protected EntityRenderDispatcher dispatcher;
    @Shadow public abstract TextRenderer getTextRenderer();

    /*
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        NametagsMod mod = Client.modManager().getMod(NametagsMod.class);
        if(!mod.isEnabled()) return;
        ci.cancel();

        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (!(d > 4096.0)) {
            boolean bl = !entity.isSneaky();
            float f = entity.getHeight() + 0.5F;
            int i = "deadmau5".equals(text.getString()) ? -10 : 0;
            matrices.push();
            matrices.translate(0.0F, f, 0.0F);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int j = (int)(g * 255.0F) << 24;
            TextRenderer textRenderer = this.getTextRenderer();
            float h = (float)(-textRenderer.getWidth(text) / 2);
            JColor empty = new JColor(0f,0f,0f, 0f);
            j = (int)(mod.opacity.getFValue() * 255.0F) << 24;
            if(mod.textShadow.isEnabled()) {
                j = (int)(mod.opacity.getFValue()/2 * 255.0F) << 24;
                textRenderer.draw(text, h, (float) i, 553648127, true, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light);
                if (bl) {
                    textRenderer.draw(text, h, (float) i, -1, true, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                }
                textRenderer.draw(text, h, (float) i, 553648127, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 0, light);
                if (entity instanceof AbstractClientPlayerEntity && text.getString().contains(entity.getName().getString()))
                    IndicatorHelper.addBadge(entity, matrices, vertexConsumers);

                if (bl) {
                    textRenderer.draw(text, h, (float) i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                }
            } else {
                textRenderer.draw(text, h, (float) i, 553648127, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light);
                if (entity instanceof AbstractClientPlayerEntity && text.getString().contains(entity.getName().getString()))
                    IndicatorHelper.addBadge(entity, matrices, vertexConsumers);

                if (bl) {
                    textRenderer.draw(text, h, (float) i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
                }
            }

            matrices.pop();
        }
    }
    */

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, CallbackInfo ci) {
        NametagsMod mod = Client.modManager().getMod(NametagsMod.class);
        if(!mod.isEnabled()) return;
        ci.cancel();

        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (!(d > 4096.0)) {
            Vec3d vec3d = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getYaw(f));
            if (vec3d != null) {
                boolean bl = !entity.isSneaky();
                int j = "deadmau5".equals(text.getString()) ? -10 : 0;
                matrixStack.push();
                matrixStack.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
                matrixStack.multiply(this.dispatcher.getRotation());
                matrixStack.scale(-0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
                float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
                int k = (int)(g * 255.0F) << 24;
                TextRenderer textRenderer = this.getTextRenderer();
                float h = (float)(-textRenderer.getWidth(text) / 2);

                textRenderer.draw(text, h, (float)j, 553648127, false, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, k, i);
                if (bl) {
                    textRenderer.draw(text, h, (float)j, -1, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, i);
                }

                matrixStack.pop();
            }
        }
    }

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", ordinal = 0))
    public void addBadges(T entity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayerEntity && text.getString().contains(entity.getName().getString()))
            IndicatorHelper.addBadge(entity, matrixStack, vertexConsumerProvider);
    }
}
