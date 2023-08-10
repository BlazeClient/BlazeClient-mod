package me.nobokik.blazeclient.api.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import club.minnced.discord.rpc.DiscordUser;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.util.Timer;
import java.util.TimerTask;

import static me.nobokik.blazeclient.Client.LOGGER;
import static me.nobokik.blazeclient.Client.mc;

public class DiscordClient {
    public static final DiscordRPC LIB = DiscordRPC.INSTANCE;
    public static final DiscordEventHandlers HANDLERS = new DiscordEventHandlers();
    public static Long STARTED_TIME_GAME;
    public static String APPLICATION_ID = "1139268729578987601";
    public static final String STEAM_ID = "";
    public static final Boolean AUTO_REGISTER = true;
    public static boolean CONNECTED_DISCORD = false;
    public static DiscordUser USER;
    private static final Timer TIMER = new Timer();
    private static String lastException;

    public static void init() {
        STARTED_TIME_GAME = System.currentTimeMillis() / 1000;
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            LIB.Discord_Shutdown();
        });
        HANDLERS.ready = (user) -> {
            LOGGER.debug("The mod has been connected to Discord!");
            USER = user;
            CONNECTED_DISCORD = true;
        };
        HANDLERS.disconnected = (err, reason) -> {
            LOGGER.debug("The mod has been pulled from Discord");
            LOGGER.error("Reason: "+reason);

            CONNECTED_DISCORD = false;
        };
        LIB.Discord_Initialize(APPLICATION_ID, HANDLERS, AUTO_REGISTER, STEAM_ID);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                LIB.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler").start();
        start();
    }

    private static void start(){
        TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if(CONNECTED_DISCORD) updatePresence();
                    if(lastException != null) lastException = null;
                } catch(Exception ex){
                    if(lastException == null || !lastException.equals(ex.getMessage())){
                        ex.printStackTrace();
                        DiscordRichPresence presence = new DiscordRichPresence();
                        presence.details = "Error";
                        presence.state = "Check logs!";
                        LIB.Discord_UpdatePresence(presence);
                        lastException = ex.getMessage();
                    }
                }
            }
        }, 2500, 2500);
    }


    private static void updatePresence(){
        if(Client.modManager().getMod(GeneralSettings.class).enableDiscordRPC.isEnabled()){
            if(mc.world == null || mc.player == null){
                if(Game.getGameState() == 1) {
                    DiscordRichPresence presence = new DiscordRichPresence();
                    presence.state = "Loading game";
                    DiscordClient.updateDiscordPresence(presence);
                }
                else if(Game.getGameState() == 2) {
                    DiscordRichPresence presence = new DiscordRichPresence();
                    presence.state = "Connecting to a server";
                    DiscordClient.updateDiscordPresence(presence);
                }
                else if(Game.getGameState() == 3) {
                    DiscordRichPresence presence = new DiscordRichPresence();
                    presence.state = "Disconnected from a server";
                    DiscordClient.updateDiscordPresence(presence);
                }
                else {
                    DiscordRichPresence presence = new DiscordRichPresence();
                    presence.state = "In the main menu";
                    DiscordClient.updateDiscordPresence(presence);
                }
            } else {
                if(mc.isInSingleplayer()) {
                    DiscordRichPresence presence = new DiscordRichPresence();
                    presence.state = "Singleplayer";
                    DiscordClient.updateDiscordPresence(presence);
                }
                else if(mc.getCurrentServerEntry() != null) {
                    DiscordRichPresence presence = new DiscordRichPresence();
                    presence.state = "Multiplayer";
                    if(Client.modManager().getMod(GeneralSettings.class).showAddress.isEnabled()) {
                        presence.details = "Multiplayer";
                        presence.state = mc.getCurrentServerEntry().address;
                    } else {
                        presence.state = "Multiplayer";
                    }
                    DiscordClient.updateDiscordPresence(presence);
                }
                else {
                    DiscordRichPresence presence = new DiscordRichPresence();
                    presence.state = "In the main menu";
                    DiscordClient.updateDiscordPresence(presence);
                }
            }
        } else {
            updateDiscordPresence(null);
        }
    }
    public static void updateDiscordPresence(DiscordRichPresence presence){
        presence.largeImageKey = "logo";
        presence.startTimestamp = STARTED_TIME_GAME;
        if(CONNECTED_DISCORD) LIB.Discord_UpdatePresence(presence);
    }
}