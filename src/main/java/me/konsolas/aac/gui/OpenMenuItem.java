package me.konsolas.aac.gui;

import org.bukkit.entity.Player;

public class OpenMenuItem
extends CallbackMenuItem {
    private InventoryMenu menu;

    public OpenMenuItem() {
        super(null);
    }

    public OpenMenuItem(InventoryMenu inventoryMenu) {
        super(null);
        this.menu = inventoryMenu;
    }

    @Override
    public void onClick(Player player) {
        super.onClick(player);
        if (this.menu != null) {
            this.menu.open(player);
        }
    }

    public InventoryMenu getMenu() {
        return this.menu;
    }

    public OpenMenuItem setMenu(InventoryMenu inventoryMenu) {
        this.menu = inventoryMenu;
        return this;
    }
}

