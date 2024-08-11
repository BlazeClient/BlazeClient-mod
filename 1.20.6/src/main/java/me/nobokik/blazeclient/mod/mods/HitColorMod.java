package me.nobokik.blazeclient.mod.mods;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.OverlayReloadListener;
import me.nobokik.blazeclient.api.event.events.TickEvent;
import me.nobokik.blazeclient.api.event.orbit.EventHandler;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.ColorSetting;
import me.nobokik.blazeclient.mod.setting.settings.NumberSetting;

public class HitColorMod extends Mod {
    public final ColorSetting hitColor = new ColorSetting("Hit Color", this, new JColor(0.7f, 0f, 0f, 0.75f), true);
    public HitColorMod() {
        super("Hit Color", "Change the hit color.", "\uF53F");
    }
}
