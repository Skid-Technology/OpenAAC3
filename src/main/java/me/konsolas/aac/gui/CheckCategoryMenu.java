package me.konsolas.aac.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.SkullItemBuilder;
import me.konsolas.aac.api.AACAPIProvider;
import me.konsolas.aac.api.HackType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class CheckCategoryMenu {
    private static final Map CHECK_DESCRIPTIONS;

    public static InventoryMenu createMenu(String str, InventoryMenu inventoryMenu) {
        InventoryMenu inventoryMenu2 = new InventoryMenu(ChatColor.RED + "AAC " + ChatColor.DARK_GRAY + "GUI | " + WordUtils.capitalize((String)str) + " checks", 27);
        int value = 9;
        for (HackType hackType : HackType.values()) {
            if (!hackType.toString().startsWith(str)) continue;
            int value2 = value++;
            HackType hackType2 = hackType;
            InventoryMenu inventoryMenu3 = inventoryMenu2;
            CheckCategoryMenu.addCheckItem(inventoryMenu3, hackType2, value2);
        }
        if (inventoryMenu != null) {
            OpenMenuItem openMenuItem = new OpenMenuItem(inventoryMenu);
            openMenuItem.setIcon(new SkullItemBuilder(Material.BARRIER).withLore(new String[]{"\u00a78HOTKEY: 9"})).setDisplayName("Back to checks menu").setSlot(22).setHotbarKey(8);
            OpenMenuItem openMenuItem2 = openMenuItem;
            inventoryMenu2.addItem(openMenuItem2);
        }
        return inventoryMenu2;
    }

    private static void addCheckItem(InventoryMenu inventoryMenu, HackType hackType, int i) {
        MenuItem menuItem;
        AtomicBoolean atomicBoolean = new AtomicBoolean(AACAPIProvider.getAPI().isEnabled(hackType));
        Callback<MenuClickContext> callback = arg_0 -> CheckCategoryMenu.toggleCheckEnabled(atomicBoolean, hackType, i, inventoryMenu, (MenuClickContext)arg_0);
        int value = i - 9;
        AtomicBoolean atomicBoolean2 = atomicBoolean;
        HackType hackType2 = hackType;
        MenuItem menuItem2 = menuItem = new ContextClickMenuItem(callback).setIcon(new SkullItemBuilder(Material.STAINED_CLAY, 1, (short)(atomicBoolean.get() ? 5 : 14)).withDisplayName("\u00a77" + WordUtils.capitalize((String)hackType.getName())).withLore(CheckCategoryMenu.buildLore(hackType2, atomicBoolean2, value))).setHotbarKey(i - 9).setSlot(i);
        inventoryMenu.addItem(menuItem2);
    }

    private static List buildLore(HackType hackType, AtomicBoolean atomicBoolean, int i) {
        ArrayList<String> arrayList = new ArrayList<String>();
        String[] stringArray = (String[])CHECK_DESCRIPTIONS.get(hackType.toString());
        if (stringArray != null) {
            arrayList.add("\u00a78Description:");
            arrayList.add("");
            arrayList.addAll(Arrays.asList(stringArray));
            arrayList.add("");
        }
        arrayList.add("\u00a78Status:");
        arrayList.add("");
        arrayList.add("\u00a77Currently this check is " + (atomicBoolean.get() ? "\u00a72enabled" : "\u00a74disabled"));
        arrayList.add("\u00a78HOTKEY: " + (i + 1));
        return arrayList;
    }

    private static void toggleCheckEnabled(AtomicBoolean atomicBoolean, HackType hackType, int i, InventoryMenu inventoryMenu, MenuClickContext menuClickContext) {
        if (!menuClickContext.getPlayer().hasPermission("AAC.manage")) {
            menuClickContext.getPlayer().sendMessage(AAC.messagePrefix + "No permission. Requires AAC.manage");
            return;
        }
        if (atomicBoolean.get()) {
            AACAPIProvider.getAPI().disableCheck(hackType);
            atomicBoolean.set(false);
        } else {
            AACAPIProvider.getAPI().enableCheck(hackType);
            atomicBoolean.set(true);
        }
        int value = i - 9;
        AtomicBoolean atomicBoolean2 = atomicBoolean;
        HackType hackType2 = hackType;
        menuClickContext.getItem().setIcon(new SkullItemBuilder(Material.STAINED_CLAY, 1, (short)(atomicBoolean.get() ? 5 : 14)).withDisplayName("\u00a77" + WordUtils.capitalize((String)hackType.getName())).withLore(CheckCategoryMenu.buildLore(hackType2, atomicBoolean2, value)));
        inventoryMenu.rebuildInventory();
        inventoryMenu.open(menuClickContext.getPlayer());
    }

    static {
        CHECK_DESCRIPTIONS = new CheckDescriptions();
    }
}

