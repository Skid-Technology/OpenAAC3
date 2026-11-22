package me.konsolas.aac.gui;

import me.konsolas.aac.AAC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DeferredOpenMenuItem
extends CallbackMenuItem {
    private InventoryMenu menu;

    public DeferredOpenMenuItem(InventoryMenu inventoryMenu) {
        super(null);
        this.menu = inventoryMenu;
        this.menu.setMenuState(false);
    }

    @Override
    public void onClick(Player player) {
        super.onClick(player);
        Bukkit.getScheduler().runTask((Plugin)AAC.getInstance(), (Runnable)new DeferredMenuOpenTask(this, player));
    }

    public InventoryMenu getMenu() {
        return this.menu;
    }

    public DeferredOpenMenuItem setMenu(InventoryMenu inventoryMenu) {
        this.menu = inventoryMenu;
        return this;
    }

    static InventoryMenu getMenu(DeferredOpenMenuItem deferredOpenMenuItem) {
        return deferredOpenMenuItem.menu;
    }
}

