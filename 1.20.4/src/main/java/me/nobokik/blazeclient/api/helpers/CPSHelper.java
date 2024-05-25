package me.nobokik.blazeclient.api.helpers;

import me.nobokik.blazeclient.api.event.events.MouseButtonEvent;
import me.nobokik.blazeclient.api.event.events.MouseUpdateEvent;
import me.nobokik.blazeclient.api.event.orbit.EventHandler;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static me.nobokik.blazeclient.Client.mc;

public class CPSHelper {

    public static final List<Long> leftClicks = new ArrayList<>();
    public static final List<Long> rightClicks = new ArrayList<>();

    //@EventHandler
    //public void onClick(MouseButtonEvent event) {
    //    if (mc.currentScreen != null) return;
    //    long time = System.currentTimeMillis();
//
    //    if (event.action != 1) return;
//
    //    if (event.button == 0) leftClicks.add(time);
    //    else if (event.button == 1) rightClicks.add(time);
//
    //    removeOldClicks(time);
    //}

    public static int getCPS(int mouseButton) {
        removeOldClicks(System.currentTimeMillis());
        return mouseButton == 0 ? leftClicks.size() : rightClicks.size();
    }

    public static void removeOldClicks(long currentTime) {
        leftClicks.removeIf(e -> e + 1000 < currentTime);
        rightClicks.removeIf(e -> e + 1000 < currentTime);
    }
}