package me.nobokik.blazeclient.mod.mods;

import me.nobokik.blazeclient.api.event.events.TickEvent;
import me.nobokik.blazeclient.api.event.orbit.EventHandler;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.mixin.EntityRenderDispatcherAccessor;
import me.nobokik.blazeclient.mixin.EntityRenderDispatcherMixin;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.ColorSetting;
import me.nobokik.blazeclient.mod.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class HitboxMod extends Mod {
    public final ColorSetting boxColor = new ColorSetting("Box Color", this, new JColor(1f,1f,1f), true);

    public HitboxMod() {
        super("Hitbox", "Change the hitbox.", "\uF0C8");
    }
}
