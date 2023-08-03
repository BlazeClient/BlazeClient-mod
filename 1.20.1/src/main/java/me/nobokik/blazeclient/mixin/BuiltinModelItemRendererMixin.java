package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.nobokik.blazeclient.Client.mc;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShieldEntityModel;getHandle()Lnet/minecraft/client/model/ModelPart;"))
    public void lowShield(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if (Client.modManager().getMod(GeneralSettings.class).lowShield.isEnabled()
                &&  mc.options.getPerspective().isFirstPerson()
                && (mode.equals(ModelTransformationMode.FIRST_PERSON_LEFT_HAND)
                || mode.equals(ModelTransformationMode.FIRST_PERSON_RIGHT_HAND))) {
            matrices.translate(0, 0.2F, 0);
        }
    }
}
