package me.nobokik.blazeclient.api.event.events;

import me.nobokik.blazeclient.api.event.Cancellable;

@SuppressWarnings("all")
public class ItemUseEvent extends Cancellable {

    public static class Pre extends ItemUseEvent {
        private static final Pre INSTANCE = new Pre();

        public static Pre get() {
            return INSTANCE;
        }
    }

    public static class Post extends ItemUseEvent {
        private static final Post INSTANCE = new Post();

        public static Post get() {
            return INSTANCE;
        }
    }
	
}
