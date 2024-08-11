package me.nobokik.blazeclient.mod.setting.settings;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImInt;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.setting.RenderableSetting;
import me.nobokik.blazeclient.mod.setting.Setting;

import java.util.Arrays;

public class ModeSetting extends Setting implements RenderableSetting {
	public int index;
	  
	public String[] modes;
	  
	public ModeSetting(String name, Mod parent, String defaultMode, String... modes) {
	    this.name = name;
	    this.parent = parent;
	    this.modes = modes;
	    this.index = Arrays.stream(this.modes).toList().indexOf(defaultMode);
		parent.addSettings(this);
	}
	  
	public String getMode() {
	    return this.modes[this.index];
	}
	  
	public void setMode(String mode) {
		this.index = Arrays.stream(this.modes).toList().indexOf(mode);
	}
	  
	public boolean is(String mode) {
	    return (this.index == Arrays.stream(this.modes).toList().indexOf(mode));
	}
	  
	public void cycle() {
	    if (this.index < this.modes.length - 1) {
	      	this.index++;
	    } else {
	      	this.index = 0;
	    }
	}

	@Override
	public void render() {
		//ImGui.pushID(parent.getName()+"/"+this.getName());

		//ImGui.text(this.name);

		//ImInt currentItem = new ImInt(this.index);

		//ImGui.pushItemWidth(170f);
		//ImGui.combo("", currentItem, modes);
		//ImGui.popItemWidth();

		//this.index = currentItem.get();

		//ImGui.popID();
		ImGui.text(this.name);
		ImGui.sameLine();
		int i = 0;
		for(String s : modes) {
			ImGui.sameLine();
			if(index != i) {
				ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, 1f);
				ImGui.pushStyleColor(ImGuiCol.Button, 0.05f, 0.05f, 0.11f, 0.65f);
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.05f, 0.05f, 0.11f, 0.8f);
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.05f, 0.05f, 0.11f, 0.9f);
			} else {
				float[] c = JColor.getGuiColor().getFloatColor();
				float[] c1 = JColor.getGuiColor().jBrighter().getFloatColor();
				float[] c2 = JColor.getGuiColor().jDarker().getFloatColor();
				ImGui.pushStyleColor(ImGuiCol.Text, 0.80f, 0.84f, 0.96f, 1f);
				ImGui.pushStyleColor(ImGuiCol.Button, c[0], c[1], c[2], 0.65f);
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, c1[0], c1[1], c1[2], 0.8f);
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, c2[0], c2[1], c2[2], 0.9f);
			}
			if(ImGui.button(s)) {
				this.index = i;
			}
			ImGui.popStyleColor(4);
			i++;
		}

	}
}