package me.nobokik.blazeclient.api.event.events;

import me.nobokik.blazeclient.api.event.Cancellable;
import net.minecraft.entity.Entity;

@SuppressWarnings("all")
public class EntityRemoveEvent extends Cancellable {
    private static final EntityRemoveEvent INSTANCE = new EntityRemoveEvent();

    public Entity entity;

    public static EntityRemoveEvent get(Entity entity) {
        INSTANCE.setCancelled(false);
        INSTANCE.entity = entity;
        return INSTANCE;
    }
}
