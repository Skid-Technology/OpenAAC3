package me.konsolas.aac.checks.combat;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.api.HackType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class Fastbow
extends Detector {
    private final WeakHashMap drawStartTimes;
    private final ExpiringMap<Player, Boolean> bowCooldowns;
    private long lenienceMs;
    private long blockTicks;
    private int minVl;

    public Fastbow(AAC aAC) {        super(aAC);

        this.drawStartTimes = new WeakHashMap();
        this.lenienceMs = 130L;
        this.blockTicks = 100L;
        this.minVl = 5;
        this.lenienceMs = this.plugin.getConfig().getLong("fastbow.lenience_ms", this.lenienceMs);
        this.blockTicks = this.plugin.getConfig().getLong("fastbow.block_ticks", this.blockTicks);
        this.minVl = this.plugin.getConfig().getInt("fastbow.min_vl", this.minVl);
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 50L * this.blockTicks;
        ExpirationListener<Player, Boolean> expirationListener = this::notifyBowCooldownExpired;
        this.bowCooldowns = ExpiringMap.builder().expirationPolicy(expirationPolicy).expiration(ticks, timeUnit).expirationListener(expirationListener).build();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.hasItem() && playerInteractEvent.getItem().getType() == Material.BOW) {
            long now = System.currentTimeMillis();
            this.drawStartTimes.put(playerInteractEvent.getPlayer(), now);
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent entityShootBowEvent) {
        long ticks = 113218805911140L;
        if (entityShootBowEvent.getEntity() instanceof Player) {
            Player player = (Player)entityShootBowEvent.getEntity();
            if (this.plugin.checkManager.isPlayerExempt(player)) {
                return;
            }
            if (this.bowCooldowns.containsKey(entityShootBowEvent.getEntity())) {
                entityShootBowEvent.setCancelled(true);
                return;
            }
            double d = entityShootBowEvent.getForce();
            long ticks2 = this.getExpectedDrawMs(d) - this.lenienceMs;
            if (this.drawStartTimes.containsKey(entityShootBowEvent.getEntity())) {
                long now = System.currentTimeMillis() - (Long)this.drawStartTimes.get(entityShootBowEvent.getEntity());
                if (now >= ticks2) {
                    this.drawStartTimes.put((Player)entityShootBowEvent.getEntity(), System.currentTimeMillis());
                    this.decreaseViolationLevel(entityShootBowEvent.getEntity().getUniqueId(), 1);
                } else if (ticks2 > 150L && this.recordViolation(entityShootBowEvent.getEntity().getUniqueId(), (int)(1L + Math.abs((now - ticks2) / 90L)), HackType.FASTBOW, entityShootBowEvent.getEntity().getName() + " failed Fastbow: drew back bow too quickly (" + now + "/" + ticks2 + " ms)", ticks) && this.blockTicks > 0L && this.getViolationLevel((Player)entityShootBowEvent.getEntity()) > this.minVl) {
                    entityShootBowEvent.setCancelled(true);
                    this.bowCooldowns.put((Player)entityShootBowEvent.getEntity(), false);
                    entityShootBowEvent.getEntity().sendMessage(AAC.messagePrefix + this.plugin.messages.getMessage("too_many_shots"));
                }
            }
        }
    }

    private long getExpectedDrawMs(double d) {
        return (long)(100.0 + 950.0 * d);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onFastbowPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        super.onPlayerJoin(playerJoinEvent);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onFastbowPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        super.onPlayerQuit(playerQuitEvent);
    }

    private void notifyBowCooldownExpired(Player player, Boolean boolValue) {
        player.sendMessage(AAC.messagePrefix + this.plugin.messages.getMessage("allow_bow"));
    }
}

