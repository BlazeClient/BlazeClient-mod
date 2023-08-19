package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.multiplayer.*;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;


@Mixin(value = MultiplayerServerListWidget.ServerEntry.class, priority = 0)
public class ServerEntryMixin {

    @Shadow
    private @Final ServerInfo server;
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I", ordinal = 0))
    public int changeText(TextRenderer instance, MatrixStack matrixStack, String string, float f, float g, int i) {
        if(Client.starServers.contains(server.address.toLowerCase())) instance.drawWithShadow(matrixStack, "★", f-50, g+14, 16776960);
        return instance.draw(matrixStack, string, f, g, i);
    }

}
