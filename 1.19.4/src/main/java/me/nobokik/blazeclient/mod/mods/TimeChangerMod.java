package me.nobokik.blazeclient.mod.mods;

import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.ModeSetting;

public class TimeChangerMod extends Mod {
    public final ModeSetting time = new ModeSetting("Time", this, "Day", "Day", "Night");

    public TimeChangerMod() {
        super("Time Changer", "Change time.", "\uF185");
    }

    public int getTimeInt() {
        if(this.isEnabled()) {
            if (time.is("Day")) return 0;
            else return 24000;
        }
        return -1;
    }
}
