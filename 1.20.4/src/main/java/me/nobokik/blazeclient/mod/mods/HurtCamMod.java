package me.nobokik.blazeclient.mod.mods;

import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.NumberSetting;

public class HurtCamMod extends Mod {
    public final BooleanSetting disableHurtcam = new BooleanSetting("Disable hurtcam", this, false);
    public final BooleanSetting oldHurtcam = new BooleanSetting("Old Hurtcam", this, false);
    public final NumberSetting scale = new NumberSetting("Scale", this, 1, 0, 2, 0.1);
    public HurtCamMod() {
        super("Hurt Cam", "Change the hurtcam.", "\uF030");
    }
}
