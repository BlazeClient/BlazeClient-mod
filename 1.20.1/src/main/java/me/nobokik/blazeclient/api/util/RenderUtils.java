package me.nobokik.blazeclient.api.util;

import me.nobokik.blazeclient.menu.FirstMenu;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import static me.nobokik.blazeclient.Client.mc;
import static me.nobokik.blazeclient.Client.modManager;
import static me.nobokik.blazeclient.api.util.RenderUtils.isRenderable;

public class RenderUtils {
    public static boolean isRenderable() {
        if(mc.player == null && !FirstMenu.getInstance().isVisible) return false;
        if (mc.currentScreen instanceof ChatScreen && !modManager().getMod(GeneralSettings.class).showInChat.isEnabled())
            return false;
        if (mc.currentScreen instanceof InventoryScreen && !modManager().getMod(GeneralSettings.class).showInInventory.isEnabled())
            return false;
        if (mc.currentScreen != null &&
                !(mc.currentScreen instanceof InventoryScreen) &&
                !(mc.currentScreen instanceof ChatScreen) &&
                !FirstMenu.getInstance().isVisible
        )
            return false;
        if (mc.options.hudHidden)
            return false;

        return true;
    }
}
