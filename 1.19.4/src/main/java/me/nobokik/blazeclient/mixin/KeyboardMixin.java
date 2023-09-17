package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.KeyPressEvent;
import me.nobokik.blazeclient.menu.FirstMenu;
import me.nobokik.blazeclient.menu.ModMenu;
import me.nobokik.blazeclient.menu.ModSettings;
import me.nobokik.blazeclient.menu.SideMenu;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.nobokik.blazeclient.gui.ImguiLoader.imGuiGlfw;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        imGuiGlfw.keyCallback(window, key, scancode, action, 0);
        Client.EVENTBUS.post(KeyPressEvent.get(key, scancode, action, window));
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS && (FirstMenu.getInstance().isVisible || ModMenu.getInstance().isVisible || ModSettings.getInstance().isVisible)) {
            SideMenu.getInstance().selectedWindow = "Mods";
            ModSettings.getInstance().isVisible = false;
            ModMenu.getInstance().isVisible = true;
            FirstMenu.toggle(false);
            ModMenu.toggle(false);
            ModSettings.toggle(false);
            SideMenu.toggle(false);
            SideMenu.toggle(false);
            ci.cancel();
        }
        if(action != GLFW.GLFW_RELEASE & (FirstMenu.getInstance().isVisible || ModMenu.getInstance().isVisible || ModSettings.getInstance().isVisible)) ci.cancel();
    }
}
