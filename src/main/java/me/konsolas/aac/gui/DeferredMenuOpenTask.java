package me.konsolas.aac.gui;

import org.bukkit.entity.Player;

public class DeferredMenuOpenTask
implements Runnable {
    final Player player;
    final DeferredOpenMenuItem menuItem;

    public DeferredMenuOpenTask(DeferredOpenMenuItem deferredOpenMenuItem, Player player) {
        this.menuItem = deferredOpenMenuItem;
        this.player = player;
    }

    @Override
    public void run() {
        if (this.player.isOnline() && DeferredOpenMenuItem.getMenu(this.menuItem) != null) {
            DeferredOpenMenuItem.getMenu(this.menuItem).open(this.player);
        }
    }
}

