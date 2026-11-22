package me.konsolas.aac.gui;

import org.bukkit.entity.Player;

public class MenuClickContext {
    private final Player player;
    private final ContextClickMenuItem item;

    public MenuClickContext(Player player, ContextClickMenuItem contextClickMenuItem) {
        this.player = player;
        this.item = contextClickMenuItem;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ContextClickMenuItem getItem() {
        return this.item;
    }

    static {
        if (null == null) {
            // empty if block
        }
    }
}

