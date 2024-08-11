package me.nobokik.blazeclient.api.event.events;

import me.nobokik.blazeclient.api.event.Cancellable;
import net.minecraft.entity.Entity;

@SuppressWarnings("all")
public class AttackEntityEvent extends Cancellable {
    private static final AttackEntityEvent INSTANCE = new AttackEntityEvent();

    public Entity target;

    public static AttackEntityEvent get(Entity target) {
        INSTANCE.setCancelled(false);
        INSTANCE.target = target;
        return INSTANCE;
    }
}
