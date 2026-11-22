package me.konsolas.aac.checks.combat;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RecentCombatTracker
implements Listener {
    private final ExpiringMap<UUID, UUID> recentCombat;
    private final ExpiringMap<UUID, Boolean> recentDamage;

    public RecentCombatTracker(JavaPlugin javaPlugin) {
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 2000L;
        this.recentCombat = ExpiringMap.builder().expirationPolicy(expirationPolicy).expiration(ticks, timeUnit).build();
        ExpirationPolicy expirationPolicy2 = ExpirationPolicy.CREATED;
        TimeUnit timeUnit2 = TimeUnit.MILLISECONDS;
        long ticks2 = 3000L;
        this.recentDamage = ExpiringMap.builder().expirationPolicy(expirationPolicy2).expiration(ticks2, timeUnit2).build();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER) {
            this.recentCombat.put(entityDamageByEntityEvent.getDamager().getUniqueId(), entityDamageByEntityEvent.getEntity().getUniqueId());
            this.recentCombat.resetExpiration(entityDamageByEntityEvent.getDamager().getUniqueId());
        }
        if (entityDamageByEntityEvent.getEntity().getType() == EntityType.PLAYER) {
            this.recentCombat.put(entityDamageByEntityEvent.getEntity().getUniqueId(), entityDamageByEntityEvent.getDamager().getUniqueId());
            this.recentCombat.resetExpiration(entityDamageByEntityEvent.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public synchronized void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity().getType() == EntityType.PLAYER) {
            this.recentDamage.put(entityDamageEvent.getEntity().getUniqueId(), true);
            this.recentDamage.resetExpiration(entityDamageEvent.getEntity().getUniqueId());
        }
    }

    public boolean hasRecentCombat(Player player) {
        return this.recentCombat.containsKey(player.getUniqueId());
    }

    public synchronized boolean hasRecentDamage(Player player) {
        return this.recentDamage.containsKey(player.getUniqueId());
    }
}

