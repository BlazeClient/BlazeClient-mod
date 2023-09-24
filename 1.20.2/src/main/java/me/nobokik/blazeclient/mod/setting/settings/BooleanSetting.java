package me.nobokik.blazeclient.mod.setting.settings;

import imgui.ImGui;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.RenderableSetting;
import me.nobokik.blazeclient.mod.setting.Setting;

public class BooleanSetting extends Setting implements RenderableSetting {
	public boolean enabled;
	  
	public BooleanSetting(String name, Mod parent, boolean enabled) {
	    this.name = name;
	    this.parent = parent;
	    this.enabled = enabled;
		if (parent != null) parent.addSettings(this);
	}
	  
	public boolean isEnabled() {
	    return this.enabled;
	}
	  
	public void setEnabled(boolean enabled) {
	    this.enabled = enabled;
	}
	
	public void toggle() {
	    this.enabled = !this.enabled;
	}

	@Override
	public void render() {
		ImGui.pushID(parent.getName()+"/"+this.getName());

		ImGui.text(this.name);
		ImGui.sameLine();
		if (ImGui.checkbox("", this.enabled)) {
			toggle();
		}

		ImGui.popID();
	}
}
