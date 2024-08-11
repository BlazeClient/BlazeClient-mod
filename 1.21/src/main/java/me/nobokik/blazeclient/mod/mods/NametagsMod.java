package me.nobokik.blazeclient.mod.mods;

import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.NumberSetting;

public class NametagsMod extends Mod {
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, false);
    public final NumberSetting opacity = new NumberSetting("Opacity", this, 0.25, 0, 1, 0.01);

    public NametagsMod() {
        super("Nametags", "Change nametags.", "\uF02C");
    }
}
