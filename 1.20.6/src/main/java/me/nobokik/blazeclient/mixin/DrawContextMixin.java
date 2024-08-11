package me.nobokik.blazeclient.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Shadow
    public int drawText(TextRenderer textRenderer, @Nullable String string, int i, int j, int k, boolean bl) {
        return 0;
    }

    @Inject(at = @At("HEAD"), method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I", cancellable = true)
    public void drawTextWithShadow(TextRenderer textRenderer, String string, int i, int j, int k, CallbackInfoReturnable<Integer> cir) {
        if(string.startsWith("Minecraft 1.20.6")) cir.setReturnValue(drawText(textRenderer, "", i, j, k, true));
    }
}
