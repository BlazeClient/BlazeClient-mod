package me.nobokik.blazeclient.api.discord;

import net.minecraft.client.gui.screen.*;

import static me.nobokik.blazeclient.Client.mc;

public class Game {
    private static Screen lastScreen;
    private static boolean update = false;
    private static int gameState = 0;
    public static boolean isUpdate() {
        return update;
    }

    public static void setUpdated(boolean updated) {
        update = updated;
    }

    public static int getGameState() {
        return gameState;
    }

    public static void setGameState(int newGameState) {
        gameState = newGameState;
    }
    public static void tick() {
        if (lastScreen != mc.currentScreen) {
            if (lastScreen != null) screenChange(lastScreen);
            lastScreen = mc.currentScreen;
        }
    }

    private static void screenTick() {
        if (mc.currentScreen instanceof LevelLoadingScreen) {
            setGameState(1);
        } else if (mc.currentScreen instanceof ProgressScreen || mc.currentScreen instanceof ConnectScreen || mc.currentScreen instanceof DownloadingTerrainScreen) {
            setGameState(2);
        } else  if (mc.currentScreen instanceof DisconnectedScreen) {
            setGameState(3);
        } else {
            setGameState(0);
        }
        setUpdated(true);
    }
    private static void screenTickFastLoad() {
        if (mc.currentScreen instanceof LevelLoadingScreen) {
            setGameState(1);
        } else if (mc.currentScreen instanceof ProgressScreen || mc.currentScreen instanceof ConnectScreen || mc.currentScreen instanceof DownloadingTerrainScreen) {
            setGameState(2);
        } else  if (mc.currentScreen instanceof DisconnectedScreen) {
            setGameState(3);
        } else {
            setGameState(0);
        }
        setUpdated(true);
    }

    private static void screenChange(Screen lastScreen) {
        if (isUpdate() && (lastScreen instanceof DisconnectedScreen || lastScreen instanceof LevelLoadingScreen || lastScreen instanceof ProgressScreen || lastScreen instanceof ConnectScreen || lastScreen instanceof ConfirmScreen || lastScreen instanceof DownloadingTerrainScreen)) {
            setUpdated(false);
        }
    }
}