package me.konsolas.aac.gui;

import org.bukkit.entity.Player;

public class DeferredPlayerClickTask
implements Runnable {
    final Player player;
    final CallbackMenuItem menuItem;

    public DeferredPlayerClickTask(CallbackMenuItem callbackMenuItem, Player player) {
        this.menuItem = callbackMenuItem;
        this.player = player;
    }

    @Override
    public void run() {
        if (this.player.isOnline()) {
            CallbackMenuItem.getCallback(this.menuItem).call(this.player);
        }
    }
}

