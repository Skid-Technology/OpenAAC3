package me.konsolas.aac.checks.movement;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.compat.CollisionVectorAdjuster;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.GroundCollisionState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class SetbackManager
implements Listener,
MovementCheckListener {
    private final Map<Player, Location> safeLocations = Collections.synchronizedMap(new WeakHashMap<>());
    private final Map<Player, Detector> setbackChecks = Collections.synchronizedMap(new WeakHashMap<>());
    private final AAC plugin;

    public SetbackManager(AAC aAC) {
        this.plugin = aAC;
    }

    public void setSetbackCheck(Detector detector, Player player) {
        this.setbackChecks.put(player, detector);
    }

    public void clearSetbackCheck(Player player) {
        this.setbackChecks.remove(player);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        if (this.setbackChecks.containsKey(playerTeleportEvent.getPlayer()) && playerTeleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            if (this.setbackChecks.get(playerTeleportEvent.getPlayer()) instanceof Speed || this.setbackChecks.get(playerTeleportEvent.getPlayer()) instanceof Fly) {
                this.safeLocations.remove(playerTeleportEvent.getPlayer());
            }
        } else {
            if (playerTeleportEvent.getTo().equals((Object)playerTeleportEvent.getFrom()) && playerTeleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                return;
            }
            this.safeLocations.put(playerTeleportEvent.getPlayer(), playerTeleportEvent.getTo().clone());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        this.safeLocations.remove(playerRespawnEvent.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void resetSafeLocationOnWorldChange(PlayerChangedWorldEvent playerChangedWorldEvent) {
        this.safeLocations.remove(playerChangedWorldEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent playerToggleFlightEvent) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.updateSafeLocationDelayed(playerToggleFlightEvent), 40L);
    }

    public Detector getSetbackCheck(Player player) {
        return (Detector)this.setbackChecks.get(player);
    }

    private void recordSafeLocation(PlayerMoveEvent playerMoveEvent, BlockPositionView blockPositionView) {
        if (CollisionVectorAdjuster.isClearColumn(blockPositionView)) {
            if (this.safeLocations.containsKey(playerMoveEvent.getPlayer())) {
                if (playerMoveEvent.getFrom().getWorld() != ((Location)this.safeLocations.get(playerMoveEvent.getPlayer())).getWorld() || playerMoveEvent.getFrom().distanceSquared((Location)this.safeLocations.get(playerMoveEvent.getPlayer())) > 3.0 || playerMoveEvent.getFrom().getY() - ((Location)this.safeLocations.get(playerMoveEvent.getPlayer())).getY() > 0.2) {
                    this.safeLocations.put(playerMoveEvent.getPlayer(), playerMoveEvent.getFrom().clone());
                } else if (CollisionVectorAdjuster.isPassableColumn(blockPositionView)) {
                    this.safeLocations.put(playerMoveEvent.getPlayer(), playerMoveEvent.getFrom().clone());
                }
            } else {
                this.safeLocations.put(playerMoveEvent.getPlayer(), playerMoveEvent.getFrom().clone());
            }
        }
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        if (groundCollisionState.isOnGround() && groundCollisionState2.isOnGround()) {
            this.recordSafeLocation(playerMoveEvent, blockPositionView);
        }
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        if (playerMoveEvent.getPlayer().isFlying()) {
            this.safeLocations.put(playerMoveEvent.getPlayer(), playerMoveEvent.getPlayer().getLocation());
        }
        return true;
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent worldUnloadEvent) {
        this.safeLocations.values().removeIf(arg_0 -> SetbackManager.matchesWorld(worldUnloadEvent, arg_0));
    }

    @EventHandler
    public void resetChecksOnWorldChange(PlayerChangedWorldEvent playerChangedWorldEvent) {
        this.safeLocations.remove(playerChangedWorldEvent.getPlayer());
        this.setbackChecks.remove(playerChangedWorldEvent.getPlayer());
    }

    public void applySetback(Player player, PlayerMoveEvent playerMoveEvent, double d, boolean flag, boolean flag2) {
        Location location;
        double d2;
        Location location2;
        if (d > -0.1) {
            d = -0.1;
        }
        if ((location2 = this.safeLocations.getOrDefault(player, playerMoveEvent.getFrom())) != null && flag && location2.getWorld().equals(player.getWorld())) {
            double x = Math.abs(playerMoveEvent.getFrom().getX() - location2.getX());
            double z = Math.abs(playerMoveEvent.getFrom().getZ() - location2.getZ());
            d2 = Math.sqrt(Math.pow(x, 2.0) + Math.pow(z, 2.0));
            if (d2 < 3.0 && playerMoveEvent.getFrom().getY() > location2.getY()) {
                playerMoveEvent.setTo(location2);
                return;
            }
        }
        Location location3 = playerMoveEvent.getFrom();
        AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(location3.toVector(), 0.6, 1.65);
        Player player2 = player;
        AxisAlignedBoundingBox axisAlignedBoundingBox2 = axisAlignedBoundingBox = axisAlignedBoundingBox.expandTowards(new Vector(0.0, d, 0.0));
        List<AxisAlignedBoundingBox> list = CollisionAccess.getCollidingBoxes((Entity)player2, axisAlignedBoundingBox2);
        if (list.isEmpty()) {
            location = location3.clone().add(0.0, d, 0.0);
        } else {
            d2 = axisAlignedBoundingBox.getMinimum().getY();
            for (AxisAlignedBoundingBox axisAlignedBoundingBox3 : list) {
                if (!(axisAlignedBoundingBox3.getMaximum().getY() > d2) || !(axisAlignedBoundingBox3.getMaximum().getY() <= location3.getY() + 0.6)) continue;
                d2 = axisAlignedBoundingBox3.getMaximum().getY();
            }
            if (d2 == axisAlignedBoundingBox.getMinimum().getY()) {
                location = location3;
            } else {
                location = location3;
                location.setY(d2);
            }
        }
        if (flag2) {
            location.setPitch(0.0f);
        }
        playerMoveEvent.setTo(location);
    }

    private static boolean matchesWorld(WorldUnloadEvent worldUnloadEvent, Location location) {
        return location.getWorld().equals(worldUnloadEvent.getWorld());
    }

    private void updateSafeLocationDelayed(PlayerToggleFlightEvent playerToggleFlightEvent) {
        this.safeLocations.put(playerToggleFlightEvent.getPlayer(), playerToggleFlightEvent.getPlayer().getLocation());
    }
}

