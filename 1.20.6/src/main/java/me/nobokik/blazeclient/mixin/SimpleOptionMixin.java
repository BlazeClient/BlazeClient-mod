package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SimpleOption.DoubleSliderCallbacks.class)
public class SimpleOptionMixin {
    //@Inject(method = "validate(Ljava/lang/Double;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    //public void removeValidation(Double double_, CallbackInfoReturnable<Optional<Double>> cir) {
    //    if(Client.modManager().getMod(GeneralSettings.class).fullbright.isEnabled()) {
    //        if(double_ == 69420.0) {
    //            cir.setReturnValue(Optional.of(69420.0));
    //        }
    //    }
    //}
}
