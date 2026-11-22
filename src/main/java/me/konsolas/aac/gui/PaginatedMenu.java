package me.konsolas.aac.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.konsolas.aac.util.SkullItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PaginatedMenu
extends InventoryMenu {
    private final int pageSize;
    private final int pageCount;
    private final List allItems;
    private final MenuItem nextItem;
    private final MenuItem previousItem;
    private List pages;
    private final InventoryMenu mainMenu;

    public PaginatedMenu(String str, int i, int i2, List list, MenuItem menuItem, MenuItem menuItem2, InventoryMenu inventoryMenu) {
        super(str, i);
        this.pageSize = i2;
        this.allItems = list;
        this.nextItem = menuItem;
        this.previousItem = menuItem2;
        this.pageCount = list.size() / i2 + 1;
        this.mainMenu = inventoryMenu;
        this.buildPages();
    }

    private void buildPages() {
        MenuItem menuItem;
        InventoryMenu inventoryMenu;
        int i;
        Iterator iterator = this.allItems.iterator();
        this.pages = new ArrayList();
        int value = 0;
        for (i = 0; i < this.pageCount; ++i) {
            int value2 = this.getSize();
            inventoryMenu = new InventoryMenu(value2);
            inventoryMenu.setTitle(this.getTitle());
            while (iterator.hasNext() && value < this.pageSize) {
                menuItem = (MenuItem)iterator.next();
                menuItem.setSlot(value);
                MenuItem menuItem2 = menuItem;
                inventoryMenu.addItem(menuItem2);
                ++value;
            }
            this.pages.add(inventoryMenu);
            value = 0;
        }
        for (i = 0; i < this.pages.size(); ++i) {
            inventoryMenu = (InventoryMenu)this.pages.get(i);
            if (i != 0) {
                menuItem = new DeferredOpenMenuItem((InventoryMenu)this.pages.get(i - 1));
                menuItem.setDisplayName(this.previousItem.getDisplayName()).setIcon(this.previousItem.buildIcon()).setSlot(this.previousItem.getSlot()).setHotbarKey(0);
                MenuItem menuItem3 = menuItem;
                inventoryMenu.addItem(menuItem3);
            }
            if (this.mainMenu != null) {
                menuItem = new OpenMenuItem(this.mainMenu);
                menuItem.setIcon(new SkullItemBuilder(Material.BARRIER).withLore(new String[]{"\u00a78HOTKEY: 9"})).setDisplayName("Back to main menu").setSlot(this.pageSize + 4);
                menuItem.setHotbarKey(8);
                MenuItem menuItem4 = menuItem;
                inventoryMenu.addItem(menuItem4);
            }
            if (i + 1 >= this.pageCount) continue;
            menuItem = new DeferredOpenMenuItem((InventoryMenu)this.pages.get(i + 1));
            menuItem.setDisplayName(this.nextItem.getDisplayName()).setIcon(this.nextItem.buildIcon()).setSlot(this.nextItem.getSlot()).setHotbarKey(1);
            MenuItem menuItem5 = menuItem;
            inventoryMenu.addItem(menuItem5);
        }
    }

    @Override
    public InventoryMenu open(Player player) {
        super.open(player);
        return ((InventoryMenu)this.pages.get(0)).open(player);
    }
}

