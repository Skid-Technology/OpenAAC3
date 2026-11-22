package me.konsolas.aac.checks.movement;

import java.util.HashMap;
import java.util.UUID;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.CleanupHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

public class PlayerEventStateTracker
implements Listener,
CleanupHandler {
    private final AAC plugin;
    private final HashSet recentLandings = new HashSet();
    private final HashSet recentTakeoffs = new HashSet();
    private final Map lastArmSwingTimes = new HashMap();
    private final Map lastRegenerationTimes = new HashMap();
    private final Map lastRegenerationReasons = new HashMap();
    private final Map lastInteractionTimes = new HashMap();
    private final ConcurrentMap activeVelocities = new ConcurrentHashMap(32, 0.9f, 1);
    private final ConcurrentMap lastVelocityTimes = new ConcurrentHashMap(32, 0.9f, 1);
    private final Map<UUID, Location> previousLocations = new HashMap<>();

    public PlayerEventStateTracker(AAC aAC) {
        this.plugin = aAC;
    }

    public void updateMovementState(PlayerMoveEvent playerMoveEvent, boolean flag, boolean flag2) {
        if (flag && !flag2) {
            this.recentTakeoffs.add(playerMoveEvent.getPlayer().getUniqueId());
            Server server = Bukkit.getServer();
            server.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.clearTakeoffState(playerMoveEvent), 3L);
        } else if (!flag && flag2) {
            this.recentLandings.add(playerMoveEvent.getPlayer().getUniqueId());
            Server server = Bukkit.getServer();
            server.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.clearLadderState(playerMoveEvent), 3L);
        }
        if (this.activeVelocities.containsKey(playerMoveEvent.getPlayer().getUniqueId()) && System.currentTimeMillis() - this.getLastVelocityTime(playerMoveEvent.getPlayer()) > 90L) {
            if (flag2 && flag) {
                this.activeVelocities.remove(playerMoveEvent.getPlayer().getUniqueId());
            } else if (playerMoveEvent.getPlayer().getVelocity().getY() == 0.0 && playerMoveEvent.getPlayer().getVelocity().getX() == 0.0 && playerMoveEvent.getPlayer().getVelocity().getZ() == 0.0) {
                this.activeVelocities.remove(playerMoveEvent.getPlayer().getUniqueId());
            }
        }
        this.previousLocations.put(playerMoveEvent.getPlayer().getUniqueId(), playerMoveEvent.getFrom());
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent playerVelocityEvent) {
        if (this.plugin.setbackManager.getSetbackCheck(playerVelocityEvent.getPlayer()) == null) {
            this.activeVelocities.put(playerVelocityEvent.getPlayer().getUniqueId(), playerVelocityEvent.getVelocity());
            this.lastVelocityTimes.put(playerVelocityEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent playerAnimationEvent) {
        if (playerAnimationEvent.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            this.lastArmSwingTimes.put(playerAnimationEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityRegainHealth(EntityRegainHealthEvent entityRegainHealthEvent) {
        if (entityRegainHealthEvent.getEntity().getType() == EntityType.PLAYER) {
            this.lastRegenerationTimes.put(entityRegainHealthEvent.getEntity().getUniqueId(), System.currentTimeMillis());
            this.lastRegenerationReasons.put(entityRegainHealthEvent.getEntity().getUniqueId(), entityRegainHealthEvent.getRegainReason());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        this.lastInteractionTimes.put(playerInteractEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent worldUnloadEvent) {
        Set<UUID> hashSet = new HashSet<>();
        for (Map.Entry<UUID, Location> entry : this.previousLocations.entrySet()) {
            if (!((Location)entry.getValue()).getWorld().equals(worldUnloadEvent.getWorld())) continue;
            hashSet.add(entry.getKey());
        }
        for (UUID uUID : hashSet) {
            this.previousLocations.remove(uUID);
        }
    }

    public synchronized boolean hasActiveVelocity(Player player) {
        return this.activeVelocities.containsKey(player.getUniqueId());
    }

    public synchronized long getLastVelocityTime(Player player) {
        return this.lastVelocityTimes.get(player.getUniqueId()) != null ? (Long)this.lastVelocityTimes.get(player.getUniqueId()) : 0L;
    }

    public long getLastArmSwingTime(Player player) {
        try {
            return (Long)this.lastArmSwingTimes.get(player.getUniqueId());
        }
        catch (NullPointerException nullPointerException) {
            return 0L;
        }
    }

    public long getLastRegenerationTime(Player player) {
        try {
            return (Long)this.lastRegenerationTimes.get(player.getUniqueId());
        }
        catch (NullPointerException nullPointerException) {
            return 0L;
        }
    }

    public EntityRegainHealthEvent.RegainReason getLastRegenerationReason(Player player) {
        try {
            return (EntityRegainHealthEvent.RegainReason)this.lastRegenerationReasons.get(player.getUniqueId());
        }
        catch (NullPointerException nullPointerException) {
            return EntityRegainHealthEvent.RegainReason.CUSTOM;
        }
    }

    public long getLastInteractionTime(Player player) {
        try {
            return (Long)this.lastInteractionTimes.get(player.getUniqueId());
        }
        catch (NullPointerException nullPointerException) {
            return 0L;
        }
    }

    @Override
    public void cleanup() {
    }

    private void clearLadderState(PlayerMoveEvent playerMoveEvent) {
        this.recentLandings.remove(playerMoveEvent.getPlayer().getUniqueId());
    }

    private void clearTakeoffState(PlayerMoveEvent playerMoveEvent) {
        this.recentTakeoffs.remove(playerMoveEvent.getPlayer().getUniqueId());
    }
}

