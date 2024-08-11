package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.mods.HitboxMod;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow public abstract void setRenderHitboxes(boolean renderHitboxes);

    @Inject(at = @At("HEAD"), method = "renderHitbox", cancellable = true)
    private static void renderHitbox(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, float f, float g, float h, float i, CallbackInfo ci) {
        if(Client.modManager().getMod(HitboxMod.class).isEnabled()) ci.cancel();
        else return;
        float[] color = Client.modManager().getMod(HitboxMod.class).boxColor.getColor().getFloatColorWAlpha();

        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrixStack, vertexConsumer, box, color[0], color[1], color[2], color[3]);
        if (entity instanceof EnderDragonEntity) {
            double d = -MathHelper.lerp((double) f, entity.lastRenderX, entity.getX());
            double e = -MathHelper.lerp((double) f, entity.lastRenderY, entity.getY());
            double j = -MathHelper.lerp((double) f, entity.lastRenderZ, entity.getZ());
            EnderDragonPart[] var11 = ((EnderDragonEntity)entity).getBodyParts();
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
                EnderDragonPart enderDragonPart = var11[var13];
                matrixStack.push();
                double k = d + MathHelper.lerp((double)f, enderDragonPart.lastRenderX, enderDragonPart.getX());
                double l = e + MathHelper.lerp((double)f, enderDragonPart.lastRenderY, enderDragonPart.getY());
                double m = j + MathHelper.lerp((double)f, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
                matrixStack.translate(k, l, m);
                WorldRenderer.drawBox(matrixStack, vertexConsumer, enderDragonPart.getBoundingBox().offset(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()), color[0], color[1], color[2], color[3]);
                matrixStack.pop();
            }
        }

        //Vec3d vec3d = entity.getRotationVec(tickDelta);
        //Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        //Matrix3f matrix3f = matrixStack.peek().getNormalMatrix();
        //vertexConsumer.vertex(matrix4f, 0.0F, entity.getStandingEyeHeight(), 0.0F).color(0, 0, 255, 255).normal(matrix3f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).next();
        //vertexConsumer.vertex(matrix4f, (float)(vec3d.x * 2.0), (float)((double)entity.getStandingEyeHeight() + vec3d.y * 2.0), (float)(vec3d.z * 2.0)).color(0, 0, 255, 255).normal(matrix3f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).next();
    }

    @Inject(at = @At("HEAD"), method = "render")
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        this.setRenderHitboxes(Client.modManager().isModEnabled(HitboxMod.class));
    }
}
