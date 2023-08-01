package me.nobokik.blazeclient.api.hook;

import net.minecraft.client.gui.hud.ChatHudLine;

import java.util.List;

public interface IChatHudExt {
    List<ChatHudLine> getMessages();
    void refreshMessages();
}