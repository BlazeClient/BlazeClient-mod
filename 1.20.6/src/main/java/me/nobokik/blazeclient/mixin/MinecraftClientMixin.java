package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.api.event.events.AttackEvent;
import me.nobokik.blazeclient.api.event.events.BlockBreakEvent;
import me.nobokik.blazeclient.api.event.events.ItemUseEvent;
import me.nobokik.blazeclient.api.event.events.TickEvent;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.helpers.FPSHelper;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.nobokik.blazeclient.Client.mc;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private static int currentFps;

    //@Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    //private void getWindowTitle(CallbackInfoReturnable<String> cir) {
    //    cir.setReturnValue("Blaze Client " + SharedConstants.getGameVersion().getName());
    //}

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo ci) {
        Client.EVENTBUS.post(TickEvent.Pre.get());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onPostTick(CallbackInfo ci) {
        Client.EVENTBUS.post(TickEvent.Post.get());
        FPSHelper.setFPS(currentFps);
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onPreAttack(CallbackInfoReturnable<Boolean> cir) {
        if (Client.EVENTBUS.post(AttackEvent.Pre.get()).isCancelled()) cir.setReturnValue(false);
    }

    @Inject(method = "doAttack", at = @At("TAIL"), cancellable = true)
    private void onPostAttack(CallbackInfoReturnable<Boolean> cir) {
        if (Client.EVENTBUS.post(AttackEvent.Post.get()).isCancelled()) cir.setReturnValue(false);
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void onPreItemUse(CallbackInfo ci) {
        if (Client.EVENTBUS.post(ItemUseEvent.Pre.get()).isCancelled()) ci.cancel();
    }

    @Inject(method = "doItemUse", at = @At("TAIL"), cancellable = true)
    private void onPostItemUse(CallbackInfo ci) {
        if (Client.EVENTBUS.post(ItemUseEvent.Post.get()).isCancelled()) ci.cancel();
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onPreBlockBreak(boolean breaking, CallbackInfo ci) {
        if (Client.EVENTBUS.post(BlockBreakEvent.Pre.get()).isCancelled()) ci.cancel();
    }

    @Inject(method = "handleBlockBreaking", at = @At("TAIL"), cancellable = true)
    private void onPostBlockBreak(boolean breaking, CallbackInfo ci) {
        if (Client.EVENTBUS.post(BlockBreakEvent.Post.get()).isCancelled()) ci.cancel();
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void onStart(CallbackInfo ci) {
        new Client();
        Client.INSTANCE.init();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        Client.configManager().saveConfig();
    }
}
