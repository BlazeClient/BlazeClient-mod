package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.mods.FreelookMod;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    public void interceptMovement(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (Client.modManager().getMod(FreelookMod.class).consumeRotation(cursorDeltaX, cursorDeltaY)) {
            ci.cancel();
        }
    }
}
