package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.mods.TimeChangerMod;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @ModifyArg(method = "setTimeOfDay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld$Properties;setTimeOfDay(J)V"))
    public long setTimeOfDay(long time) {
        if (Client.modManager().getMod(TimeChangerMod.class).isEnabled())
            return Client.modManager().getMod(TimeChangerMod.class).getTimeInt();
        else
            return time;
    }
}