package me.konsolas.aac.checks.world;

import java.util.WeakHashMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class FastPlace
extends Detector {
    private static final ItemStack BONE_MEAL_ITEM = new ItemStack(Material.INK_SACK, 1, (short)15);
    private int maxPlacePerSecond;
    private boolean cancelPlace;
    private int cancelVl;
    private final WeakHashMap placeCounts;

    public FastPlace(AAC aAC) {        super(aAC);

        this.maxPlacePerSecond = 10;
        this.cancelPlace = true;
        this.cancelVl = 2;
        this.placeCounts = new WeakHashMap();
        this.maxPlacePerSecond = this.plugin.getConfig().getInt("fastplace.max_place_per_second", this.maxPlacePerSecond);
        this.cancelPlace = this.plugin.getConfig().getBoolean("fastplace.cancel_place", this.cancelPlace);
        this.cancelVl = this.plugin.getConfig().getInt("fastplace.cancel_vl", this.cancelVl);
        Server server = this.plugin.getServer();
        this.scheduledTaskIds.add(server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::resetPlaceCounts, 20L, 20L));
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player;
        long ticks = 113218805911140L;
        if (blockPlaceEvent.getPlayer().getGameMode() == GameMode.CREATIVE || this.plugin.checkManager.isPlayerExempt(player = blockPlaceEvent.getPlayer())) {
            return;
        }
        for (ItemStack itemStack : AAC.serverVersion.getHeldItems(blockPlaceEvent.getPlayer())) {
            if (itemStack != null && itemStack.getType() != Material.FLINT_AND_STEEL && !itemStack.isSimilar(BONE_MEAL_ITEM)) continue;
            return;
        }
        if (!this.placeCounts.containsKey(blockPlaceEvent.getPlayer())) {
            this.placeCounts.put(blockPlaceEvent.getPlayer(), 1);
        } else {
            this.placeCounts.put(blockPlaceEvent.getPlayer(), (Integer)this.placeCounts.get(blockPlaceEvent.getPlayer()) + 1);
        }
        if ((Integer)this.placeCounts.get(blockPlaceEvent.getPlayer()) > this.maxPlacePerSecond && this.recordViolation(blockPlaceEvent.getPlayer().getUniqueId(), (Integer)this.placeCounts.get(blockPlaceEvent.getPlayer()) - this.maxPlacePerSecond, HackType.FASTBREAK, blockPlaceEvent.getPlayer().getName() + " failed FastPlace: placed " + this.placeCounts.get(blockPlaceEvent.getPlayer()) + " blocks in 1 second", ticks) && this.cancelPlace && this.getViolationLevel(blockPlaceEvent.getPlayer()) > this.cancelVl) {
            blockPlaceEvent.setCancelled(true);
        }
    }

    private void resetPlaceCounts() {
        this.placeCounts.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.decreaseViolationLevel(player.getUniqueId(), 1);
        }
    }
}

