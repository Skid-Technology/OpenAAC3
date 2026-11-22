package me.konsolas.aac.gui;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MenuClickListener
implements Listener {
    final InventoryMenu menu;

    private MenuClickListener(InventoryMenu inventoryMenu) {
        this.menu = inventoryMenu;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        if (!(inventoryClickEvent.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)inventoryClickEvent.getWhoClicked();
        if (InventoryMenu.getOpenMenusMap().containsKey(player.getUniqueId())) {
            InventoryMenu inventoryMenu = (InventoryMenu)InventoryMenu.getOpenMenusMap().get(player.getUniqueId());
            if (inventoryClickEvent.getInventory().equals(inventoryMenu.getInventory())) {
                boolean flag = false;
                if (inventoryClickEvent.getClick() == ClickType.NUMBER_KEY) {
                    MenuItem menuItem = InventoryMenu.findMenuItemAtSlot(inventoryMenu, inventoryClickEvent.getHotbarButton());
                    if (menuItem != null) {
                        flag = true;
                        menuItem.onClick(player);
                        if (menuItem.shouldCancelClick()) {
                            inventoryClickEvent.setCancelled(true);
                        }
                    }
                    if (!flag && inventoryMenu.isCancelUnmatchedClicks()) {
                        inventoryClickEvent.setCancelled(true);
                    }
                    return;
                }
                ItemStack itemStack = inventoryClickEvent.getCurrentItem();
                if (itemStack == null) {
                    return;
                }
                for (MenuItem menuItem : new ArrayList<>(inventoryMenu.getItems())) {
                    if (menuItem.getSlot() != inventoryClickEvent.getSlot()) continue;
                    flag = true;
                    menuItem.onClick(player);
                    if (!menuItem.shouldCancelClick()) continue;
                    inventoryClickEvent.setCancelled(true);
                }
                if (!flag && inventoryMenu.isCancelUnmatchedClicks()) {
                    inventoryClickEvent.setCancelled(true);
                }
            }
        }
    }

    public MenuClickListener(InventoryMenu inventoryMenu, DeferredMenuCloseTask deferredMenuCloseTask) {
        this(inventoryMenu);
    }
}

