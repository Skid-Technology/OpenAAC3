package me.konsolas.aac.gui;

import java.util.concurrent.atomic.AtomicBoolean;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.SkullItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;

public class MainMenu {
    public static void open(Player player) {
        InventoryMenu inventoryMenu = new InventoryMenu(ChatColor.RED + "AAC " + ChatColor.DARK_GRAY + "GUI", 27);
        InventoryMenu inventoryMenu2 = new InventoryMenu(ChatColor.RED + "AAC " + ChatColor.DARK_GRAY + "GUI | Checks", 27);
        InventoryMenu inventoryMenu3 = inventoryMenu2;
        String str = "combat";
        MenuItem menuItem = new DeferredOpenMenuItem(CheckCategoryMenu.createMenu(str, inventoryMenu3)).setIcon(new SkullItemBuilder(Material.GOLD_SWORD).withDisplayName("\u00a77Combat checks").withLore(new String[]{"\u00a78Category contains:", "", "\u00a7eHitbox\u00a77: Checks if a player is aiming at the target hitbox", "\u00a7eCriticals\u00a77: Checks for criticals whilst on the ground", "\u00a7eFightSpeed\u00a77: Checks for fast clicking / autoclicker", "\u00a7eFastUse\u00a77: Checks for consuming items too quickly", "\u00a7eRegen\u00a77: Checks regeneration speed", "\u00a7eKillaura\u00a77: Checks for basic killauras", "\u00a7eFastBow\u00a77: Checks for bow pullback speed", "\u00a7eKnockback\u00a77: Checks if the player moves being attacked", "", "\u00a78HOTKEY: 1"}).withFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES})).setSlot(10).setHotbarKey(0);
        inventoryMenu2.addItem(menuItem);
        InventoryMenu inventoryMenu4 = inventoryMenu2;
        String str2 = "movement";
        MenuItem menuItem2 = new DeferredOpenMenuItem(CheckCategoryMenu.createMenu(str2, inventoryMenu4)).setIcon(new SkullItemBuilder(Material.IRON_BOOTS).withDisplayName("\u00a77Movement checks").withLore(new String[]{"\u00a78Category contains:", "", "\u00a7eSpeed\u00a77: Checks if the player moves too fast", "\u00a7ePhase\u00a77: Checks if the player moves through a block", "\u00a7eFly\u00a77: Checks if the player tries to fly", "\u00a7eElytra\u00a77: Checks if the player moves incorrectly with an elytra", "\u00a7eVehicles\u00a77: Checks if the player moves incorrectly with an vehicle", "\u00a7eNoFall\u00a77: Checks if the player tries to avoid fall damage", "\u00a7eJesus\u00a77: Checks if the player tries to walk on water", "\u00a7eDerp\u00a77: Checks for impossible head rotations", "", "\u00a78HOTKEY: 2"}).withFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES})).setSlot(12).setHotbarKey(1);
        inventoryMenu2.addItem(menuItem2);
        InventoryMenu inventoryMenu5 = inventoryMenu2;
        String str3 = "world";
        MenuItem menuItem3 = new DeferredOpenMenuItem(CheckCategoryMenu.createMenu(str3, inventoryMenu5)).setIcon(new SkullItemBuilder(Material.DIRT).withDisplayName("\u00a77World checks").withLore(new String[]{"\u00a78Category contains:", "", "\u00a7eFastBreak\u00a77: Checks block break speed", "\u00a7eFastPlace\u00a77: Checks block place speed", "\u00a7eNuker\u00a77: Checks all gamemodes for simultaneous block breaking", "\u00a7eNoSwing\u00a77: Checks that the player swings his item when attacking", "\u00a7eInteract\u00a77: Checks for impossible interactions (like hitting through walls)", "\u00a7eVelocity\u00a77: Checks if the player moves correctly after velocity", "", "\u00a78HOTKEY: 3"}).withFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES})).setSlot(14).setHotbarKey(2);
        inventoryMenu2.addItem(menuItem3);
        InventoryMenu inventoryMenu6 = inventoryMenu2;
        String str4 = "chat";
        MenuItem menuItem4 = new DeferredOpenMenuItem(CheckCategoryMenu.createMenu(str4, inventoryMenu6)).setIcon(new SkullItemBuilder(Material.BOOKSHELF).withDisplayName("\u00a77Chat checks").withLore(new String[]{"\u00a78Category contains:", "", "\u00a7eSpam\u00a77: Checks if the player is trying to spam the chat", "", "\u00a78HOTKEY: 4"}).withFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES})).setSlot(16).setHotbarKey(3);
        inventoryMenu2.addItem(menuItem4);
        MenuItem menuItem5 = new OpenMenuItem(inventoryMenu).setIcon(new SkullItemBuilder(Material.BARRIER).withLore(new String[]{"\u00a78HOTKEY: 9"})).setDisplayName("Back to main menu").setSlot(22).setHotbarKey(8);
        inventoryMenu2.addItem(menuItem5);
        MenuItem menuItem6 = new CallbackMenuItem(MainMenu::reloadPluginConfig).setIcon(player.hasPermission("AAC.manage") ? new SkullItemBuilder(Material.COMMAND).withDisplayName("\u00a77Reload AAC").withLore(new String[]{ChatColor.GRAY + "Completly reload AAC checks", "", "\u00a78HOTKEY: 1"}) : SkullItemBuilder.NO_PERMISSION_ICON.copyBuilder().withDisplayName("\u00a77Reload AAC")).setSlot(10).setHotbarKey(0);
        inventoryMenu.addItem(menuItem6);
        MenuItem menuItem7 = new DeferredOpenMenuItem(inventoryMenu2).setIcon(new SkullItemBuilder(Material.WATCH).withDisplayName("\u00a77Control checks").withLore(new String[]{ChatColor.DARK_RED + "Disable " + ChatColor.GRAY + "or " + ChatColor.DARK_GREEN + "enable " + ChatColor.GRAY + "AAC checks", "", "\u00a78HOTKEY: 2"})).setSlot(12).setHotbarKey(1);
        inventoryMenu.addItem(menuItem7);
        InventoryMenu inventoryMenu7 = inventoryMenu;
        InventoryMenu inventoryMenu8 = ViolationMenu.createMenu(inventoryMenu7);
        MenuItem menuItem8 = new DeferredOpenMenuItem(inventoryMenu8).setIcon(new SkullItemBuilder(Material.PAPER).withDisplayName("\u00a77Violation log").withLore(new String[]{ChatColor.GRAY + "See which " + ChatColor.YELLOW + "players" + ChatColor.GRAY + " are getting " + ChatColor.DARK_RED + "violations", "", "\u00a78HOTKEY: 3"})).setSlot(14).setHotbarKey(2);
        inventoryMenu.addItem(menuItem8);
        AtomicBoolean atomicBoolean = new AtomicBoolean(AAC.getInstance().logManager.verboseSubscribers.contains(player.getUniqueId()) && player.hasPermission("AAC.verbose"));
        MenuItem menuItem9 = new StaticMenuItem().setIcon(new SkullItemBuilder(Material.INK_SACK, 1, (short)(atomicBoolean.get() ? 10 : 8)).withDisplayName(atomicBoolean.get() ? ChatColor.GREEN + "Verbose Enabled" : ChatColor.GRAY + "Verbose Disabled")).setSlot(25);
        MenuItem menuItem10 = new CallbackMenuItem(arg_0 -> MainMenu.toggleVerboseOutput(atomicBoolean, menuItem9, inventoryMenu, arg_0)).setIcon(player.hasPermission("AAC.verbose") ? new SkullItemBuilder(Material.BOOK_AND_QUILL).withDisplayName("\u00a77Toggle verbose output").withLore(new String[]{ChatColor.DARK_RED + "Disable " + ChatColor.GRAY + "or " + ChatColor.DARK_GREEN + "enable " + ChatColor.GRAY + "AAC verbose", "", "\u00a78HOTKEY: 4"}) : SkullItemBuilder.NO_PERMISSION_ICON.copyBuilder().withDisplayName("\u00a77Toggle verbose output")).setSlot(16).setHotbarKey(3);
        inventoryMenu.addItem(menuItem10);
        MenuItem menuItem11 = menuItem9;
        inventoryMenu.addItem(menuItem11);
        inventoryMenu.open(player);
    }

    private static void toggleVerboseOutput(AtomicBoolean atomicBoolean, MenuItem menuItem, InventoryMenu inventoryMenu, Player player) {
        if (!player.hasPermission("AAC.verbose")) {
            player.sendMessage(AAC.messagePrefix + "No permission. Requires AAC.verbose");
            return;
        }
        if (atomicBoolean.get()) {
            AAC.getInstance().logManager.verboseSubscribers.add(player.getUniqueId());
            player.sendMessage(AAC.messagePrefix + "You will now receive verbose output");
            atomicBoolean.set(false);
            menuItem.setIcon(new SkullItemBuilder(Material.INK_SACK, 1, (short)8).withDisplayName(ChatColor.GRAY + "Verbose Disabled"));
            inventoryMenu.rebuildInventory();
            inventoryMenu.open(player);
        } else {
            AAC.getInstance().logManager.verboseSubscribers.remove(player.getUniqueId());
            player.sendMessage(AAC.messagePrefix + "You will no longer receive verbose output");
            atomicBoolean.set(true);
            menuItem.setIcon(new SkullItemBuilder(Material.INK_SACK, 1, (short)10).withDisplayName(ChatColor.GREEN + "Verbose Enabled"));
            inventoryMenu.rebuildInventory();
            inventoryMenu.open(player);
        }
    }

    private static void reloadPluginConfig(Player player) {
        if (!player.hasPermission("AAC.manage")) {
            player.sendMessage(AAC.messagePrefix + "No permission. Requires AAC.manage");
            return;
        }
        player.closeInventory();
        player.sendMessage(AAC.messagePrefix + "Reload request registered");
        String str = player.getName() + " requested reload";
        AAC.getInstance().logManager.logViolation(str);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)AAC.getInstance(), MainMenu::reopenMainMenu, 2L);
    }

    private static void reopenMainMenu() {
        AAC.getInstance().reloadPlugin();
    }
}

