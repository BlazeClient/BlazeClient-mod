package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.MouseButtonEvent;
import me.nobokik.blazeclient.api.event.events.MouseMoveEvent;
import me.nobokik.blazeclient.api.event.events.MouseUpdateEvent;
import me.nobokik.blazeclient.api.helpers.KeystrokeHelper;
import me.nobokik.blazeclient.menu.FirstMenu;
import me.nobokik.blazeclient.menu.ModMenu;
import me.nobokik.blazeclient.menu.ModSettings;
import me.nobokik.blazeclient.menu.SideMenu;
import me.nobokik.blazeclient.mod.mods.ZoomMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.nobokik.blazeclient.Client.mc;
import static me.nobokik.blazeclient.api.helpers.CPSHelper.*;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void onMouseMove(long window, double mouseX, double mouseY, CallbackInfo ci) {
        if (window == mc.getWindow().getHandle())
            Client.EVENTBUS.post(MouseMoveEvent.get(mouseX, mouseY));
    }

    @Inject(method = "updateMouse", at = @At("HEAD"))
    private void onMouseUpdate(CallbackInfo ci) {
        Client.EVENTBUS.post(MouseUpdateEvent.get());
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        Client.EVENTBUS.post(MouseButtonEvent.get(button, action));

        if (ModMenu.getInstance().isVisible || ModSettings.getInstance().isVisible || FirstMenu.getInstance().isVisible || SideMenu.getInstance().isVisible) {
            ci.cancel();
            return;
        }


        switch (button) {
            case 0 -> {
                KeystrokeHelper.getHelper(GLFW.GLFW_MOUSE_BUTTON_LEFT).setPressed(action == 1);
                KeystrokeHelper.getHelper(GLFW.GLFW_MOUSE_BUTTON_LEFT).setPressTime(System.currentTimeMillis());
            }
            case 1 -> {
                KeystrokeHelper.getHelper(GLFW.GLFW_MOUSE_BUTTON_RIGHT).setPressed(action == 1);
                KeystrokeHelper.getHelper(GLFW.GLFW_MOUSE_BUTTON_RIGHT).setPressTime(System.currentTimeMillis());
            }
        }

        if (mc.currentScreen != null) return;
        long time = System.currentTimeMillis();

        if (action != 1) return;

        if (button == 0) leftClicks.add(time);
        else if (button == 1) rightClicks.add(time);

        removeOldClicks(time);
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        double scrollY = vertical * 30;

        if (ModSettings.getInstance().isVisible) {
            ModSettings.getInstance().scrollY -= scrollY;
        } else if (ModMenu.getInstance().isVisible) {
            ModMenu.getInstance().scrollY -= scrollY;
        }

        if (ModMenu.getInstance().isVisible || ModSettings.getInstance().isVisible || FirstMenu.getInstance().isVisible || SideMenu.getInstance().isVisible) {
            ci.cancel();
        }
        Client.modManager().getMod(ZoomMod.class).scroll += vertical;
        if(Client.modManager().getMod(ZoomMod.class).zoomEnabled)
            ci.cancel();
    }
}
