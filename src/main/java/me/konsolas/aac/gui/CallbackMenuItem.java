package me.konsolas.aac.gui;

import me.konsolas.aac.AAC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CallbackMenuItem
extends MenuItem {
    private final Plugin plugin;
    private Callback<Player> callback;

    public CallbackMenuItem(Callback<Player> callback) {
        this(callback, (Plugin)AAC.getInstance());
    }

    public CallbackMenuItem(Callback<Player> callback, Plugin plugin) {
        this.callback = new EmptyPlayerCallback(this);
        this.plugin = plugin;
        if (callback != null) {
            this.callback = callback;
        }
    }

    @Override
    public void onClick(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, (Runnable)new DeferredPlayerClickTask(this, player));
    }

    public Callback<Player> getCallback() {
        return this.callback;
    }

    public CallbackMenuItem setCallback(Callback<Player> callback) {
        this.callback = callback;
        return this;
    }

    static Callback<Player> getCallback(CallbackMenuItem callbackMenuItem) {
        return callbackMenuItem.callback;
    }
}

