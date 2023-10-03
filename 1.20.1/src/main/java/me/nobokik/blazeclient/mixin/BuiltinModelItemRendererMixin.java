package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShieldEntityModel;getHandle()Lnet/minecraft/client/model/ModelPart;"))
    public void render(ItemStack itemStack, ModelTransformationMode mode, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        if(!Client.modManager().getMod(GeneralSettings.class).lowShield.isEnabled()) return;
        if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && (mode.equals(ModelTransformationMode.FIRST_PERSON_LEFT_HAND) || mode.equals(ModelTransformationMode.FIRST_PERSON_RIGHT_HAND))) {
            matrixStack.translate(0, 0.2F, 0);
        }
    }
}