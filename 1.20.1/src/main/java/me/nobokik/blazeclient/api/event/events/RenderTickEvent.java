package me.nobokik.blazeclient.api.event.events;

@SuppressWarnings("all")
public class RenderTickEvent {

    private static final RenderTickEvent INSTANCE = new RenderTickEvent();

    public static RenderTickEvent get() {
        return INSTANCE;
    }

}
