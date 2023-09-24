package me.nobokik.blazeclient.mod.mods;

import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;

public class ScoreboardMod extends Mod {
    public final BooleanSetting hideScoreboard = new BooleanSetting("Hide Scoreboard", this, false);
    public final BooleanSetting background = new BooleanSetting("Background", this, true);
    public final BooleanSetting numbers = new BooleanSetting("Numbers", this, true);
    public final BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, false);
    public ScoreboardMod() {
        super("Scoreboard", "Change the scoreboard.", "\uF0CA");
    }
}
