package me.konsolas.aac.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.konsolas.aac.AAC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class InventoryMenu {
    private static MenuClickListener clickListener;
    private static final HashMap openMenus;
    private boolean menuState;
    private final boolean persistent;
    private String title;
    private int size;
    private final ArrayList<MenuItem> items;
    private Inventory inventory;
    private InventoryMenu parentMenu;
    private boolean cancelUnmatchedClicks;
    private boolean autoRefresh;
    private Callback<Void> closeCallback;
    private final Map hotbarSlots;

    public InventoryMenu() {
        this("Inventory");
    }

    public InventoryMenu(int i2) {
        this("Inventory", i2);
    }

    public InventoryMenu(String str) {
        this(str, 9);
    }

    public InventoryMenu(String str, int i2) {
        this(str, i2, false);
    }

    public InventoryMenu(String str, int i2, boolean flag) {
        this(str, i2, flag, (Plugin)AAC.getInstance());
    }

    public InventoryMenu(String str, int i2, boolean flag, Plugin plugin) {
        this.menuState = true;
        this.items = new ArrayList();
        this.parentMenu = null;
        this.cancelUnmatchedClicks = true;
        this.autoRefresh = true;
        this.hotbarSlots = new HashMap();
        this.title = str;
        this.size = i2;
        this.persistent = flag;
        this.rebuildInventory();
        if (clickListener == null) {
            clickListener = new MenuClickListener(this, null);
            Plugin plugin2 = plugin;
            MenuClickListener menuClickListener = clickListener;
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents((Listener)menuClickListener, plugin2);
        }
    }

    public InventoryMenu addItem(MenuItem menuItem) {
        this.items.add(menuItem);
        if (this.autoRefresh) {
            this.rebuildInventory();
        }
        return this;
    }

    public InventoryMenu removeItem(MenuItem menuItem) {
        this.items.remove(menuItem);
        if (this.autoRefresh) {
            this.rebuildInventory();
        }
        return this;
    }

    public InventoryMenu rebuildInventory() {
        this.inventory = Bukkit.createInventory(null, (int)this.size, (String)this.title);
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            MenuItem menuItem = (MenuItem)iterator.next();
            if (menuItem.getSlot() >= this.size) {
                System.err.println("Cannot create inventory: size: " + this.size + " position: " + menuItem.getSlot());
                continue;
            }
            if (menuItem.getHotbarKey() > -1) {
                this.hotbarSlots.put(menuItem.getHotbarKey(), menuItem.getSlot());
            }
            this.inventory.setItem(menuItem.getSlot(), menuItem.buildIcon());
        }
        return this;
    }

    public InventoryMenu addIcons(List list) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = (ItemStack)iterator.next();
            int value = -1;
            for (int i = 0; i < this.inventory.getSize(); ++i) {
                if (this.inventory.getItem(i) != null && this.inventory.getItem(i).getType() != Material.AIR) continue;
                value = i;
                break;
            }
            MenuItem menuItem = new StaticMenuItem().setIcon(itemStack).setSlot(value);
            this.addItem(menuItem);
        }
        return this;
    }

    public InventoryMenu addIcons(ItemStack[] itemStackArray) {
        return this.addIcons(Arrays.asList(itemStackArray));
    }

    public InventoryMenu addItems(MenuItem[] menuItemArray) {
        MenuItem[] menuItemArray2 = menuItemArray;
        int value = menuItemArray2.length;
        for (int i = 0; i < value; ++i) {
            MenuItem menuItem = menuItemArray2[i];
            int value2 = -1;
            for (int j = 0; j < this.inventory.getSize(); ++j) {
                if (this.inventory.getItem(j) != null && this.inventory.getItem(j).getType() != Material.AIR) continue;
                value2 = j;
                break;
            }
            MenuItem menuItem2 = menuItem.setSlot(value2);
            this.addItem(menuItem2);
        }
        return this;
    }

    public InventoryMenu open(Player player) {
        openMenus.put(player.getUniqueId(), this);
        player.openInventory(this.inventory);
        return this;
    }

    public InventoryMenu setCloseCallback(Callback<Void> callback) {
        this.closeCallback = callback;
        return this;
    }

    private MenuItem getHotbarItem(int i2) {
        Integer intVal = (Integer)this.hotbarSlots.get(i2);
        if (intVal != null) {
            for (MenuItem menuItem : new ArrayList<>(this.getItems())) {
                if (menuItem.getSlot() != intVal.intValue()) continue;
                return menuItem;
            }
        }
        return null;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)AAC.getInstance(), (Runnable)new DeferredMenuCloseTask(this, inventoryCloseEvent), 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        openMenus.remove(playerQuitEvent.getPlayer().getUniqueId());
    }

    public boolean isMenuState() {
        return this.menuState;
    }

    public void setMenuState(boolean flag) {
        this.menuState = flag;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int i2) {
        this.size = i2;
    }

    public InventoryMenu getParentMenu() {
        return this.parentMenu;
    }

    public void setParentMenu(InventoryMenu inventoryMenu) {
        this.parentMenu = inventoryMenu;
    }

    public boolean isCancelUnmatchedClicks() {
        return this.cancelUnmatchedClicks;
    }

    public void setCancelUnmatchedClicks(boolean flag) {
        this.cancelUnmatchedClicks = flag;
    }

    public boolean isAutoRefresh() {
        return this.autoRefresh;
    }

    public void setAutoRefresh(boolean flag) {
        this.autoRefresh = flag;
    }

    public static HashMap getOpenMenus() {
        return openMenus;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public ArrayList<MenuItem> getItems() {
        return this.items;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Callback<Void> getCloseCallback() {
        return this.closeCallback;
    }

    public Map getHotbarSlots() {
        return this.hotbarSlots;
    }

    public static void resetClickListener() {
        clickListener = null;
    }

    static HashMap getOpenMenusMap() {
        return openMenus;
    }

    static MenuItem findMenuItemAtSlot(InventoryMenu inventoryMenu, int i2) {
        return inventoryMenu.getHotbarItem(i2);
    }

    static {
        openMenus = new HashMap();
    }
}

