package me.nobokik.blazeclient.mod.setting.settings;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.gui.ImguiLoader;
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
		//ImGui.pushID(parent.getName()+"/"+this.getName());
		//ImGui.text(this.name);
		//ImGui.sameLine();
		//if (ImGui.checkbox("", this.enabled)) {
		//	toggle();
		//}
		//ImGui.popID();




		//float[] color = JColor.getGuiColor().getFloatColor();
		//ImGui.pushID(parent.getName()+"/"+this.getName());
		//ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
		//ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
		//ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
		//ImGui.pushStyleColor(ImGuiCol.Text, color[0], color[1], color[2], 0f);
		//ImGui.pushFont(ImguiLoader.getFontAwesome48());
		//ImVec2 pos = ImGui.getCursorPos().clone();
		//ImGui.setCursorPos(pos.x, pos.y-6);
		//ImGui.text(this.enabled ? "\uF205" : "\uF204");
		//ImGui.popStyleColor(1);
		//if(ImGui.isItemHovered()) {
		//	ImGui.pushStyleColor(ImGuiCol.Text, color[0], color[1], color[2], 0.7f);
		//	if(ImGui.isMouseClicked(0)) toggle();
		//} else {
		//	ImGui.pushStyleColor(ImGuiCol.Text, color[0], color[1], color[2], 1f);
		//}
		//ImGui.setCursorPos(pos.x, pos.y-6);
		//ImGui.text(this.enabled ? "\uF205" : "\uF204");
		//ImGui.popFont();
		//ImGui.popStyleColor(4);
		//ImGui.sameLine();
		//ImGui.setCursorPosX(ImGui.getCursorPosX()+4);
		//ImGui.setCursorPosY(ImGui.getCursorPosY()+6);
		//ImGui.text(this.name);
		//ImGui.popID();

		float[] color = JColor.getGuiColor().getFloatColor();
		ImGui.pushID(parent.getName()+"/"+this.getName());
		ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
		ImVec2 pos = ImGui.getCursorPos().clone();
		if(ImGui.button("", 37f, 32f)) {
			toggle();
		}
		ImVec2 pos2 = ImGui.getCursorPos().clone();
		if(ImGui.isItemHovered()) {
			ImGui.pushStyleColor(ImGuiCol.Text, color[0], color[1], color[2], 0.7f);
		} else {
			ImGui.pushStyleColor(ImGuiCol.Text, color[0], color[1], color[2], 1f);
		}
		ImGui.pushFont(ImguiLoader.getFontAwesome32());
		ImGui.setCursorPos(pos.x, pos.y);
		ImGui.text(this.enabled ? "\uF205" : "\uF204");
		ImGui.popFont();
		ImGui.popStyleColor(4);
		ImGui.sameLine();
		ImGui.setCursorPosY(ImGui.getCursorPosY()-4);
		ImGui.text(this.name);
		ImGui.setCursorPosY(ImGui.getCursorPosY()+4);
		ImGui.popID();
	}
}
