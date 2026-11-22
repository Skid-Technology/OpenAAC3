package me.konsolas.aac.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class DeferredMenuCloseTask
implements Runnable {
    final InventoryCloseEvent event;
    final InventoryMenu menu;

    public DeferredMenuCloseTask(InventoryMenu inventoryMenu, InventoryCloseEvent inventoryCloseEvent) {
        this.menu = inventoryMenu;
        this.event = inventoryCloseEvent;
    }

    @Override
    public void run() {
        if (this.event.getPlayer() instanceof Player) {
            Player player = (Player)this.event.getPlayer();
            InventoryMenu inventoryMenu = (InventoryMenu)InventoryMenu.getOpenMenusMap().get(player.getUniqueId());
            if (player.getOpenInventory().getType() == InventoryType.CRAFTING && inventoryMenu != null && !inventoryMenu.isPersistent()) {
                if (inventoryMenu.getCloseCallback() != null) {
                    inventoryMenu.getCloseCallback().call(null);
                }
                InventoryMenu.getOpenMenusMap().remove(player.getUniqueId());
            }
        }
    }
}

