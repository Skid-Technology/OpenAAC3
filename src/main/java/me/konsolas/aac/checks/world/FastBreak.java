package me.konsolas.aac.checks.world;

import java.util.WeakHashMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.BlockBreakSpeedAccess;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.api.HackType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class FastBreak
extends Detector {
    private int letTicks;
    private boolean cancelBreak;
    private boolean checkDelay;
    private int cancelVl;
    private final WeakHashMap breakStartTimes;
    private final WeakHashMap targetBlockLocations;
    private final WeakHashMap lastBlockBreakTimes;

    public FastBreak(AAC aAC) {        super(aAC);

        this.letTicks = 2;
        this.cancelBreak = true;
        this.checkDelay = true;
        this.cancelVl = 1;
        this.breakStartTimes = new WeakHashMap();
        this.targetBlockLocations = new WeakHashMap();
        this.lastBlockBreakTimes = new WeakHashMap();
        this.letTicks = this.plugin.getConfig().getInt("fastbreak.let_ticks", this.letTicks);
        this.cancelBreak = this.plugin.getConfig().getBoolean("fastbreak.cancel_break", this.cancelBreak);
        this.checkDelay = this.plugin.getConfig().getBoolean("fastbreak.check_delay", this.checkDelay);
        this.cancelVl = this.plugin.getConfig().getInt("fastbreak.cancel_vl", this.cancelVl);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        long ticks = 113218805911140L;
        if (playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK) {
            long ticks2;
            long ticks3;
            if (playerInteractEvent.getClickedBlock().getLocation().toVector().equals(this.targetBlockLocations.get(playerInteractEvent.getPlayer()))) {
                ticks3 = System.currentTimeMillis();
                long ticks4 = ticks3 - (ticks2 = ((Long)this.breakStartTimes.get(playerInteractEvent.getPlayer())).longValue()) / 50L;
                if (ticks4 >= BlockBreakSpeedAccess.getBreakTicks(playerInteractEvent.getClickedBlock(), playerInteractEvent.getPlayer())) {
                    this.breakStartTimes.put(playerInteractEvent.getPlayer(), System.currentTimeMillis());
                }
            } else {
                this.targetBlockLocations.put(playerInteractEvent.getPlayer(), playerInteractEvent.getClickedBlock().getLocation().toVector());
                this.breakStartTimes.put(playerInteractEvent.getPlayer(), System.currentTimeMillis());
            }
            if (this.checkDelay && this.lastBlockBreakTimes.containsKey(playerInteractEvent.getPlayer()) && BlockBreakSpeedAccess.getHasteLevel(playerInteractEvent.getPlayer()) == 0) {
                ticks3 = (Long)this.lastBlockBreakTimes.get(playerInteractEvent.getPlayer());
                ticks2 = System.currentTimeMillis();
                if (ticks2 - ticks3 < 100L && playerInteractEvent.getClickedBlock() != null && BlockBreakSpeedAccess.getBreakTicks(playerInteractEvent.getClickedBlock(), playerInteractEvent.getPlayer()) > 1L && this.recordViolation(playerInteractEvent.getPlayer().getUniqueId(), 2, HackType.FASTBREAK, playerInteractEvent.getPlayer().getName() + " failed FastBreak: interacted too quickly after breaking a block", ticks) && this.cancelBreak && this.getViolationLevel(playerInteractEvent.getPlayer()) > this.cancelVl) {
                    playerInteractEvent.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player;
        long ticks = 113218805911140L;
        if (blockBreakEvent.getPlayer().getGameMode() == GameMode.CREATIVE || this.plugin.checkManager.isPlayerExempt(player = blockBreakEvent.getPlayer())) {
            return;
        }
        if (!blockBreakEvent.getBlock().getLocation().toVector().equals(this.targetBlockLocations.get(blockBreakEvent.getPlayer())) && this.recordViolation(blockBreakEvent.getPlayer().getUniqueId(), 1, HackType.FASTBREAK, blockBreakEvent.getPlayer().getName() + " failed FastBreak: broke the wrong block", ticks) && this.cancelBreak) {
            blockBreakEvent.setCancelled(true);
        }
        if (this.breakStartTimes.containsKey(blockBreakEvent.getPlayer())) {
            long now = System.currentTimeMillis();
            long ticks2 = (Long)this.breakStartTimes.get(blockBreakEvent.getPlayer());
            long ticks3 = (now - ticks2) / 50L;
            long ticks4 = BlockBreakSpeedAccess.getBreakTicks(blockBreakEvent.getBlock(), blockBreakEvent.getPlayer());
            if (ticks4 == 0L) {
                return;
            }
            if (ticks3 + (long)this.letTicks < ticks4) {
                if (this.recordViolation(blockBreakEvent.getPlayer().getUniqueId(), 1, HackType.FASTBREAK, blockBreakEvent.getPlayer().getName() + " failed FastBreak: tried to break blocks too quickly (" + ticks3 + ", expected " + ticks4 + ")", ticks) && this.cancelBreak && this.getViolationLevel(blockBreakEvent.getPlayer()) > this.cancelVl) {
                    blockBreakEvent.setCancelled(true);
                }
            } else {
                this.decreaseViolationLevel(blockBreakEvent.getPlayer().getUniqueId(), 1);
            }
        }
        this.lastBlockBreakTimes.put(blockBreakEvent.getPlayer(), System.currentTimeMillis());
    }
}

