package me.nobokik.blazeclient.mixin;

import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoDrawer.class)
public class LogoDrawerMixin {
    @Inject(method = "draw*", at = @At("HEAD"), cancellable = true)
    public void cancelDraw(CallbackInfo ci) {
        ci.cancel();
    }
}
