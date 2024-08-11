package me.nobokik.blazeclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.RenderTickEvent;
import me.nobokik.blazeclient.gui.ImguiLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.obfuscate.DontObfuscate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Inject(at = @At("HEAD"), method = "flipFrame", remap = false)
    private static void runTickTail(CallbackInfo ci) {
        Client.EVENTBUS.post(RenderTickEvent.get());
        MinecraftClient.getInstance().getProfiler().push("ImGui Render");
        ImguiLoader.onFrameRender();
        MinecraftClient.getInstance().getProfiler().pop();
    }
}
