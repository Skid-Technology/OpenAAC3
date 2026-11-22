package me.konsolas.aac.gui;

import me.konsolas.aac.AAC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ContextClickMenuItem
extends MenuItem {
    private Callback<MenuClickContext> callback = new EmptyContextCallback(this);

    public ContextClickMenuItem(Callback<MenuClickContext> callback) {
        if (callback != null) {
            this.callback = callback;
        }
    }

    @Override
    public void onClick(Player player) {
        ContextClickMenuItem contextClickMenuItem = this;
        Bukkit.getScheduler().runTask((Plugin)AAC.getInstance(), (Runnable)new DeferredContextClickTask(this, player, contextClickMenuItem));
    }

    public Callback<MenuClickContext> getCallback() {
        return this.callback;
    }

    public ContextClickMenuItem setCallback(Callback<MenuClickContext> callback) {
        this.callback = callback;
        return this;
    }

    static Callback<MenuClickContext> getCallback(ContextClickMenuItem contextClickMenuItem) {
        return contextClickMenuItem.callback;
    }
}

