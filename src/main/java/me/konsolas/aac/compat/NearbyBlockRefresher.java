package me.konsolas.aac.compat;

import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class NearbyBlockRefresher {
    public static void refreshNearbyBlocks(Player player) {
        int x = (int)player.getLocation().getX();
        int y = (int)player.getLocation().getY();
        int z = (int)player.getLocation().getZ();
        for (int i = x - 5; i <= x + 5; ++i) {
            for (int j = y - 5; j <= y + 5; ++j) {
                for (int k = z - 5; k <= z + 5; ++k) {
                    Block block = player.getWorld().getBlockAt(i, j, k);
                    if (block.getState() instanceof InventoryHolder || block.getState() instanceof Sign || block.getState() instanceof Banner) continue;
                    int value = block.getTypeId();
                    byte by = block.getData();
                    block.setType(Material.AIR, false);
                    block.setTypeIdAndData(value, by, false);
                }
            }
        }
    }
}

