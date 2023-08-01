package me.nobokik.blazeclient.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawableHelper.class)
public class DrawableHelperMixin {
    @Inject(at = @At("HEAD"), method = "drawTextWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", cancellable = true)
    private static void drawModifiedVersionString(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int color, CallbackInfo ci) {
        if(text.startsWith("Minecraft 1.19.4/Fabric (")) ci.cancel();
    }
}
