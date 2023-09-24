package me.nobokik.blazeclient.mod.setting;

import me.nobokik.blazeclient.mod.Mod;

public abstract class Setting {
	public String name;
	public Mod parent;

	public String getName() {
		return name;
	}
}