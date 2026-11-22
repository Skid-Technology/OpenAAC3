package me.konsolas.aac.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.konsolas.aac.AAC;
import me.konsolas.aac.punishment.DefaultViolationWeights;
import me.konsolas.aac.punishment.DescendingValueMapSorter;
import me.konsolas.aac.punishment.PlayerViolationSummary;
import me.konsolas.aac.util.SkullItemBuilder;
import me.konsolas.aac.api.HackType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class ViolationMenu {
    private static final Map<HackType, Double> DEFAULT_WEIGHTS = Collections.unmodifiableMap(new DefaultViolationWeights(HackType.class));

    public static InventoryMenu createMenu(InventoryMenu inventoryMenu) {
        ArrayList<PlayerViolationSummary> arrayList = new ArrayList<PlayerViolationSummary>();
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            int value = 0;
            Map<String, Integer> map = new HashMap();
            for (HackType hackType : HackType.values()) {
                if (!AAC.getInstance().checkManager.isCheckEnabled(hackType)) continue;
                int value2 = AAC.getInstance().checkManager.getDetector(hackType).getViolationLevel(player2);
                value2 = (int)((Double)DEFAULT_WEIGHTS.get((Object)hackType) * (double)value2);
                if (value2 <= 0) continue;
                value += value2;
                map.put(WordUtils.capitalize((String)hackType.getName()), value2);
            }
            map = DescendingValueMapSorter.sortDescending(map);
            ArrayList arrayList2 = new ArrayList();
            if (map.size() > 0) {
                arrayList2.add("\u00a78Violation breakdown");
                arrayList2.add("");
                for (Map.Entry entry : map.entrySet()) {
                    arrayList2.add("\u00a77" + (String)entry.getKey() + ": \u00a7c" + entry.getValue() + " VL");
                }
                arrayList2.add("");
                arrayList2.add("\u00a7rClick to switch to \u00a7cSPECTATOR");
                arrayList2.add("\u00a7rand \u00a7cteleport silently\u00a7r to the player");
            }
            arrayList.add(new PlayerViolationSummary(player2, value, arrayList2, null));
        }
        arrayList.sort(ViolationMenu::compareViolationTotals);
        ArrayList arrayList3 = new ArrayList();
        for (PlayerViolationSummary playerViolationSummary : arrayList) {
            String str = playerViolationSummary.getPlayer().getName();
            arrayList3.add(new CallbackMenuItem(arg_0 -> ViolationMenu.teleportToSuspect(playerViolationSummary, arg_0)).setIcon(new SkullItemBuilder(Material.SKULL_ITEM, 1, (short)3).withSkullOwner(str).withDisplayName("\u00a7e" + playerViolationSummary.getPlayer().getName() + " \u00a77[\u00a7c" + playerViolationSummary.getViolationTotal() + " VL\u00a77]").withFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES}).withLore(playerViolationSummary.getViolations())));
        }
        String str2 = "MHF_ArrowLeft";
        MenuItem backItem = new StaticMenuItem().setIcon(new SkullItemBuilder(Material.SKULL_ITEM, 1, (short)3).withSkullOwner(str2).withLore(new String[]{"\u00a78HOTKEY: 1"})).setDisplayName("Back").setSlot(18);
        String str3 = "MHF_ArrowRight";
        MenuItem menuItem = new StaticMenuItem().setIcon(new SkullItemBuilder(Material.SKULL_ITEM, 1, (short)3).withSkullOwner(str3).withLore(new String[]{"\u00a78HOTKEY: 2"})).setDisplayName("Next").setSlot(26);
        InventoryMenu inventoryMenu2 = inventoryMenu;
        MenuItem menuItem2 = menuItem;
        MenuItem menuItem3 = backItem;
        ArrayList arrayList4 = arrayList3;
        int value3 = 18;
        int value4 = 27;
        String str4 = ChatColor.RED + "AAC " + ChatColor.DARK_GRAY + "GUI | Violations";
        return new PaginatedMenu(str4, value4, value3, arrayList4, menuItem3, menuItem2, inventoryMenu2);
    }

    private static void teleportToSuspect(PlayerViolationSummary playerViolationSummary, Player player) {
        if (player.getGameMode() != GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(AAC.messagePrefix + "Updated gamemode");
        }
        player.teleport(playerViolationSummary.getPlayer().getLocation().add(0.0, 1.0, 0.0));
        player.sendMessage(AAC.messagePrefix + "Teleported to " + playerViolationSummary.getPlayer().getName());
    }

    private static int compareViolationTotals(PlayerViolationSummary playerViolationSummary, PlayerViolationSummary playerViolationSummary2) {
        return Integer.compare(playerViolationSummary2.getViolationTotal(), playerViolationSummary.getViolationTotal());
    }
}

