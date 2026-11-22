package me.konsolas.aac.checks.world;

import java.util.Iterator;
import java.util.WeakHashMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public final class Nuker
extends Detector {
    private int maxBreaksPerSecond;
    private int vlDivisor;
    private final WeakHashMap breakCounts;
    private final WeakHashMap lastBrokenBlockLocations;

    public Nuker(AAC aAC) {        super(aAC);

        this.maxBreaksPerSecond = 40;
        this.vlDivisor = 41;
        this.breakCounts = new WeakHashMap();
        this.lastBrokenBlockLocations = new WeakHashMap();
        this.maxBreaksPerSecond = this.plugin.getConfig().getInt("nuker.max_breaks_per_second", this.maxBreaksPerSecond);
        this.vlDivisor = this.plugin.getConfig().getInt("nuker.vl_divisor", this.vlDivisor);
        Server server = Bukkit.getServer();
        this.scheduledTaskIds.add(server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::resetBreakCounts, 6L, 20L));
        Server server2 = Bukkit.getServer();
        this.scheduledTaskIds.add(server2.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::decayNukerViolations, 6L, 50L));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        long ticks = 113218805911140L;
        Player player = blockBreakEvent.getPlayer();
        if (this.plugin.checkManager.isPlayerExempt(player)) {
            return;
        }
        if (this.lastBrokenBlockLocations.containsKey(blockBreakEvent.getPlayer()) && ((Vector)this.lastBrokenBlockLocations.get(blockBreakEvent.getPlayer())).equals((Object)blockBreakEvent.getBlock().getLocation().toVector())) {
            return;
        }
        if (!this.breakCounts.containsKey(blockBreakEvent.getPlayer())) {
            this.breakCounts.put(blockBreakEvent.getPlayer(), 1);
        } else {
            this.breakCounts.put(blockBreakEvent.getPlayer(), (Integer)this.breakCounts.get(blockBreakEvent.getPlayer()) + 1);
            if ((Integer)this.breakCounts.get(blockBreakEvent.getPlayer()) > this.maxBreaksPerSecond) {
                if ((Integer)this.breakCounts.get(blockBreakEvent.getPlayer()) % this.vlDivisor == 0 && this.recordViolation(blockBreakEvent.getPlayer().getUniqueId(), 1, HackType.NUKER, blockBreakEvent.getPlayer().getName() + " failed Nuker: broke too many blocks (" + this.breakCounts.get(blockBreakEvent.getPlayer()) + " blocks/second)", ticks)) {
                    blockBreakEvent.setCancelled(true);
                }
                return;
            }
        }
        this.lastBrokenBlockLocations.put(blockBreakEvent.getPlayer(), blockBreakEvent.getBlock().getLocation().toVector());
    }

    private void decayNukerViolations() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.decreaseViolationLevel(player.getUniqueId(), 1);
        }
    }

    private void resetBreakCounts() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.breakCounts.put(player, 0);
        }
    }
}

