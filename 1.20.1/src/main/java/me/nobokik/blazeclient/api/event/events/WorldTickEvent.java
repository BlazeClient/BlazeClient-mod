package me.nobokik.blazeclient.api.event.events;

@SuppressWarnings("all")
public class WorldTickEvent {

    private static final WorldTickEvent INSTANCE = new WorldTickEvent();

    public static WorldTickEvent get() {
        return INSTANCE;
    }

}
