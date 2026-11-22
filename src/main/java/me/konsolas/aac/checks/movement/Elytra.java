package me.konsolas.aac.checks.movement;

import java.util.Locale;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.compat.EnvironmentBlockChecks;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.compat.ServerVersion;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Elytra
extends Detector
implements MovementCheckListener {
    private final WeakHashMap lastMotionVectors;
    private final ExpiringMap<Player, Boolean> fireworkBoostCooldowns;
    private double tolerance;
    private boolean teleport;
    private int minVl;
    private double rocketTolerance;

    public Elytra(AAC aAC) {
        super(aAC);
        this.lastMotionVectors = new WeakHashMap();
        this.tolerance = 0.2;
        this.teleport = true;
        this.minVl = 12;
        this.rocketTolerance = 3.0;
        this.tolerance = this.plugin.getConfig().getDouble("elytra.tolerance", this.tolerance);
        this.rocketTolerance = this.plugin.getConfig().getDouble("elytra.rocket_tolerance", this.tolerance);
        this.teleport = this.plugin.getConfig().getBoolean("elytra.teleport", this.teleport);
        this.minVl = this.plugin.getConfig().getInt("elytra.min_vl", this.minVl);
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 3000L;
        this.fireworkBoostCooldowns = ExpiringMap.builder().expirationPolicy(expirationPolicy).expiration(ticks, timeUnit).build();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        Player player;
        if (playerInteractEvent.getItem() != null && playerInteractEvent.getItem().getType() == Material.FIREWORK && (AAC.serverVersion.getVersion() == ServerVersion.VER_1_11 || AAC.serverVersion.getVersion() == ServerVersion.VER_1_12) && AAC.serverVersion.isElytraGliding(player = playerInteractEvent.getPlayer()) && playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR) {
            this.fireworkBoostCooldowns.put(playerInteractEvent.getPlayer(), false);
            this.fireworkBoostCooldowns.resetExpiration(playerInteractEvent.getPlayer());
        }
    }

    @Override
    public void onTick(long ticks, long ticks2) {
        if (ticks % 50L == 0L) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.decreaseViolationLevel(player.getUniqueId(), 1);
            }
        }
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        long ticks2 = ticks ^ 0x60FF4F6C294EL;
        Vector vector = new Vector(playerMoveEvent.getTo().getX() - playerMoveEvent.getFrom().getX(), playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY(), playerMoveEvent.getTo().getZ() - playerMoveEvent.getFrom().getZ());
        Vector vector2 = (Vector)this.lastMotionVectors.get(playerMoveEvent.getPlayer());
        this.lastMotionVectors.put(playerMoveEvent.getPlayer(), vector);
        if (vector2 == null) {
            if ((Math.abs(vector.getX()) > 0.6 || Math.abs(vector.getZ()) > 0.6 || Math.abs(vector.getY()) > 0.6) && this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 5 + (int)(vector.length() * 5.0), HackType.FLY, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Elytra: Initiated flight too quickly: %.3f,%.3f,%.3f", vector.getX(), vector.getY(), vector.getZ()), ticks2) && this.teleport && this.getViolationLevel(playerMoveEvent.getPlayer()) > this.minVl && playerMoveEvent.getPlayer().getLocation().subtract(0.0, 1.0, 0.0).getBlock().isEmpty()) {
                playerMoveEvent.setTo(playerMoveEvent.getPlayer().getLocation().subtract(0.0, 1.0, 0.0));
            }
            return;
        }
        double x = Math.sqrt(vector.getX() * vector.getX() + vector.getZ() * vector.getZ());
        double y = vector.getY();
        double x2 = Math.sqrt(vector2.getX() * vector2.getX() + vector2.getZ() * vector2.getZ());
        double y2 = vector2.getY();
        boolean flag = this.fireworkBoostCooldowns.containsKey(playerMoveEvent.getPlayer());
        if (flag && Math.abs(x) < this.rocketTolerance && Math.abs(y) < this.rocketTolerance) {
            return;
        }
        if (EnvironmentBlockChecks.isInLiquid(blockPositionView2) || blockPositionView.getBlock().getType() == Material.WEB) {
            double y3 = Math.abs(vector.getY());
            double d = Jesus.isAboveLiquidSurface(blockPositionView) ? 0.42 : 0.2;
            if ((y3 > d || Math.abs(vector.getX()) > 0.5 || Math.abs(vector.getZ()) > 0.5) && x > x2 && this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 2 + (int)(vector.length() * 2.0), HackType.FLY, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Elytra: Moved too quickly in water %.3f,%.3f,%.3f", vector.getX(), vector.getY(), vector.getZ()), ticks2) && this.teleport && this.getViolationLevel(playerMoveEvent.getPlayer()) > this.minVl && playerMoveEvent.getPlayer().getLocation().subtract(0.0, 1.0, 0.0).getBlock().isEmpty()) {
                playerMoveEvent.setTo(playerMoveEvent.getPlayer().getLocation().subtract(0.0, 1.0, 0.0));
            }
            return;
        }
        double d2 = EntityPushCalculator.calculatePush(playerMoveEvent.getPlayer()).getLength();
        if (d2 > 0.0 && x < d2 + this.tolerance && y < y2 + d2) {
            return;
        }
        Vector vector3 = ElytraPhysics.predictVelocity(vector2, playerMoveEvent.getFrom().getPitch(), playerMoveEvent.getFrom().getYaw());
        Vector vector4 = ElytraPhysics.predictVelocity(vector2, playerMoveEvent.getTo().getPitch(), playerMoveEvent.getTo().getYaw());
        double x3 = vector.getX() * vector.getX() + vector.getZ() * vector.getZ() <= vector3.getX() * vector3.getX() + vector3.getZ() * vector3.getZ() + this.tolerance && (Math.abs(vector.getY()) <= Math.abs(vector3.getY() + this.tolerance) || vector.getY() < vector3.getY() && vector.getY() > vector3.getY() - this.tolerance - this.tolerance) ? 0.0 : Math.min(vector3.distance(vector), vector4.distance(vector));
        if (x3 > this.tolerance) {
            Vector vector5;
            Object[] objectArray = new Object[2];
            Vector vector6 = vector5 = vector;
            objectArray[0] = MathUtilities.formatVector(vector6);
            objectArray[1] = x3;
            if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), (int)(2.0 + x3 * 10.0), HackType.FLY, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Fly: (Elytra) unexpected movement: %s (%.3f)", objectArray), ticks2) && this.teleport && this.getViolationLevel(playerMoveEvent.getPlayer()) > this.minVl && playerMoveEvent.getPlayer().getLocation().subtract(0.0, 1.0, 0.0).getBlock().isEmpty()) {
                playerMoveEvent.setTo(playerMoveEvent.getPlayer().getLocation().subtract(0.0, 1.0, 0.0));
            }
        }
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        Player player;
        if (playerMoveEvent.isCancelled()) {
            return false;
        }
        return playerMoveEvent.getPlayer().getGameMode() != GameMode.SPECTATOR && playerMoveEvent.getPlayer().getVehicle() == null && !playerMoveEvent.getPlayer().isFlying() && !playerMoveEvent.getPlayer().isDead() && playerMoveEvent.getPlayer().getLocation().getY() > -1024.0 && !playerMoveEvent.getPlayer().getAllowFlight() && !playerMoveEvent.getPlayer().isSleeping() && AAC.serverVersion.isElytraGliding(player = playerMoveEvent.getPlayer());
    }
}

