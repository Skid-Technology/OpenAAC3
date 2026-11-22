package me.konsolas.aac.api;

import me.konsolas.aac.api.AACAPI;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AACAPILoadedEvent
extends Event {
    private final AACAPI api;

    public AACAPILoadedEvent(AACAPI aACAPI) {
        this.api = aACAPI;
    }

    public static HandlerList getHandlerList() {
        return null;
    }

    public HandlerList getHandlers() {
        return null;
    }

    public AACAPI getAPI() {
        return this.api;
    }

    static {
        new HandlerList();
    }
}

