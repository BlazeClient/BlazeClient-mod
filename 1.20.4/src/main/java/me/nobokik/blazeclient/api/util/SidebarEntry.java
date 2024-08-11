package me.nobokik.blazeclient.api.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public record SidebarEntry(Text name, Text score, int scoreWidth) {
}
