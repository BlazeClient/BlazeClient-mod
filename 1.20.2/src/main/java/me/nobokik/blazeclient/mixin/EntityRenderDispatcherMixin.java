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
    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        if(Client.modManager().getMod(HitboxMod.class).isEnabled()) ci.cancel();
        else return;
        float[] color = Client.modManager().getMod(HitboxMod.class).boxColor.getColor().getFloatColorWAlpha();

        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertices, box, color[0], color[1], color[2], color[3]);
        if (entity instanceof EnderDragonEntity) {
            double d = -MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
            double e = -MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
            double f = -MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
            EnderDragonPart[] var11 = ((EnderDragonEntity)entity).getBodyParts();
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
                EnderDragonPart enderDragonPart = var11[var13];
                matrices.push();
                double g = d + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderX, enderDragonPart.getX());
                double h = e + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderY, enderDragonPart.getY());
                double i = f + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
                matrices.translate(g, h, i);
                WorldRenderer.drawBox(matrices, vertices, enderDragonPart.getBoundingBox().offset(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()), color[0], color[1], color[2], color[3]);
                matrices.pop();
            }
        }

        //Vec3d vec3d = entity.getRotationVec(tickDelta);
        //Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        //Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        //vertices.vertex(matrix4f, 0.0F, entity.getStandingEyeHeight(), 0.0F).color(0, 0, 255, 255).normal(matrix3f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).next();
        //vertices.vertex(matrix4f, (float)(vec3d.x * 2.0), (float)((double)entity.getStandingEyeHeight() + vec3d.y * 2.0), (float)(vec3d.z * 2.0)).color(0, 0, 255, 255).normal(matrix3f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).next();
    }

    @Inject(at = @At("HEAD"), method = "render")
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        this.setRenderHitboxes(Client.modManager().isModEnabled(HitboxMod.class));
    }
}
