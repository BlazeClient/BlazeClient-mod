package me.nobokik.blazeclient.api.event.events;

@SuppressWarnings("all")
public class PlayerTickEvent {

    private static final PlayerTickEvent INSTANCE = new PlayerTickEvent();

    public static PlayerTickEvent get() {
        return INSTANCE;
    }

}
