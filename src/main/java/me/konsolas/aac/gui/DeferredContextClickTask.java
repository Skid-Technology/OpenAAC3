package me.konsolas.aac.gui;

import org.bukkit.entity.Player;

public class DeferredContextClickTask
implements Runnable {
    final Player player;
    final ContextClickMenuItem item;
    final ContextClickMenuItem menuItem;

    public DeferredContextClickTask(ContextClickMenuItem contextClickMenuItem, Player player, ContextClickMenuItem contextClickMenuItem2) {
        this.menuItem = contextClickMenuItem;
        this.player = player;
        this.item = contextClickMenuItem2;
    }

    @Override
    public void run() {
        if (this.player.isOnline()) {
            ContextClickMenuItem.getCallback(this.menuItem).call(new MenuClickContext(this.player, this.item));
        }
    }
}

