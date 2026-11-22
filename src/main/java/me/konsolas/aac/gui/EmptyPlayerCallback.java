package me.konsolas.aac.gui;

import org.bukkit.entity.Player;

public class EmptyPlayerCallback
implements Callback<Player> {
    final CallbackMenuItem menuItem;

    public EmptyPlayerCallback(CallbackMenuItem callbackMenuItem) {
        this.menuItem = callbackMenuItem;
    }

    public void noop(Player player) {
    }

    @Override
    public void call(Player player) {
        this.noop(player);
    }
}
