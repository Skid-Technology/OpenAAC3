package me.konsolas.aac.checks.movement;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.WeakHashMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.compat.EnvironmentBlockChecks;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.compat.ServerVersion;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class Vehicles
extends Detector
implements MovementCheckListener {
    private final WeakHashMap lastVerticalDeltas;
    private final Set nonGroundPlayers;
    private double tolerance;
    private boolean teleport;
    private int minVl;
    private double vlMultiplier;

    public Vehicles(AAC aAC) {        super(aAC);

        this.lastVerticalDeltas = new WeakHashMap();
        this.nonGroundPlayers = Collections.newSetFromMap(new WeakHashMap());
        this.tolerance = 0.015;
        this.teleport = true;
        this.minVl = 12;
        this.vlMultiplier = 6.0;
        this.tolerance = this.plugin.getConfig().getDouble("vehicles.tolerance", this.tolerance);
        this.teleport = this.plugin.getConfig().getBoolean("vehicles.teleport", this.teleport);
        this.minVl = this.plugin.getConfig().getInt("vehicles.min_vl", this.minVl);
        this.vlMultiplier = this.plugin.getConfig().getDouble("vehicles.vl_multiplier", this.vlMultiplier);
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            this.plugin.getLogger().info("Disabling check movement.Vehicles as server is 1.8.8");
            boolean flag = false;
            HackType hackType = HackType.VEHICLES;
            this.plugin.checkManager.disableCheck(hackType, flag);
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
        Player player = playerMoveEvent.getPlayer();
        double y = playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY();
        if (!this.lastVerticalDeltas.containsKey(playerMoveEvent.getPlayer())) {
            this.lastVerticalDeltas.put(playerMoveEvent.getPlayer(), 0.0);
        }
        EntityType entityType = player.getVehicle().getType();
        boolean flag = !this.nonGroundPlayers.contains(playerMoveEvent.getPlayer());
        boolean flag2 = EnvironmentBlockChecks.isVehicleOnGround(player.getVehicle());
        if (flag2) {
            this.nonGroundPlayers.remove(playerMoveEvent.getPlayer());
        } else {
            this.nonGroundPlayers.add(playerMoveEvent.getPlayer());
        }
        double d = (Double)this.lastVerticalDeltas.get(player);
        double x = Math.sqrt(Math.pow(playerMoveEvent.getTo().getX() - playerMoveEvent.getFrom().getX(), 2.0) + Math.pow(playerMoveEvent.getTo().getZ() - playerMoveEvent.getFrom().getZ(), 2.0));
        switch (VehicleTypeSwitch.ENTITY_ORDINALS[entityType.ordinal()]) {
            case 1: {
                if (this.isBoatAirborne((Boat)player.getVehicle()) && y > 0.0) {
                    int value = 100;
                    long ticks3 = 6 + (int)(Math.abs(y) * this.vlMultiplier);
                    int value2 = MathUtilities.clampToInt(ticks3, value);
                    if (!this.recordViolation(player.getUniqueId(), value2, HackType.FLY, String.format(Locale.UK, player.getName() + " failed Vehicles: (BOAT) moved unexpectedly: %.3f (ABOVE WATER)", y), ticks2) || !this.teleport || this.getViolationLevel(player) <= this.minVl && !(Math.abs(y) > 1.0)) break;
                    this.plugin.setbackManager.setSetbackCheck(this, player);
                    player.teleport(playerMoveEvent.getFrom());
                    this.lastVerticalDeltas.put(player, player.getVelocity().getY());
                    break;
                }
                double d2 = VerticalMovementPredictor.predictNextGravityMotion(d);
                if (!(Math.abs(y) > Math.abs(d2) + this.tolerance) && (flag2 || !(y > d2 + this.tolerance))) break;
                int value3 = 100;
                long ticks4 = 6 + (int)(Math.abs(y) * this.vlMultiplier);
                int value4 = MathUtilities.clampToInt(ticks4, value3);
                if (!this.recordViolation(player.getUniqueId(), value4, HackType.FLY, String.format(Locale.UK, player.getName() + " failed Vehicles: (BOAT) moved unexpectedly: %.3f (%.3f)", y, d2), ticks2) || !this.teleport || this.getViolationLevel(player) <= this.minVl && !(Math.abs(y) > 1.0)) break;
                this.plugin.setbackManager.setSetbackCheck(this, player);
                player.teleport(playerMoveEvent.getFrom());
                this.lastVerticalDeltas.put(player, player.getVelocity().getY());
                break;
            }
            case 2: {
                if (flag) {
                    if (!(y > 2.0)) break;
                    int value5 = 100;
                    long ticks5 = 5 + (int)(Math.abs(y) * this.vlMultiplier);
                    int value6 = MathUtilities.clampToInt(ticks5, value5);
                    if (!this.recordViolation(player.getUniqueId(), value6, HackType.FLY, String.format(Locale.UK, player.getName() + " failed Vehicles: (HORSE) jumped unexpectedly: %.3f (JUMP)", y), ticks2) || !this.teleport || this.getViolationLevel(player) <= this.minVl && !(Math.abs(y) > 1.0)) break;
                    this.plugin.setbackManager.setSetbackCheck(this, player);
                    player.teleport(playerMoveEvent.getFrom());
                    this.lastVerticalDeltas.put(player, player.getVelocity().getY());
                    break;
                }
                double d3 = VerticalMovementPredictor.predictNextGravityMotion(d);
                if (!(Math.abs(y) > Math.abs(d3) + this.tolerance) && (flag2 || !(y > d3 + this.tolerance))) break;
                int value7 = 100;
                long ticks6 = (int)(Math.abs(y) * this.vlMultiplier);
                int value8 = MathUtilities.clampToInt(ticks6, value7);
                if (!this.recordViolation(player.getUniqueId(), value8, HackType.FLY, String.format(Locale.UK, player.getName() + " failed Vehicles: (HORSE) moved unexpectedly: %.3f (%.3f)", y, d), ticks2) || !this.teleport || this.getViolationLevel(player) <= this.minVl && !(Math.abs(y) > 1.0)) break;
                this.plugin.setbackManager.setSetbackCheck(this, player);
                player.teleport(playerMoveEvent.getFrom());
                this.lastVerticalDeltas.put(player, player.getVelocity().getY());
                break;
            }
            case 3: {
                if (flag) {
                    if (y > 1.0) {
                        int value9 = 100;
                        long ticks7 = 5 + (int)(Math.abs(y) * this.vlMultiplier);
                        int value10 = MathUtilities.clampToInt(ticks7, value9);
                        if (this.recordViolation(player.getUniqueId(), value10, HackType.FLY, String.format(Locale.UK, player.getName() + " failed Vehicles: (PIG) moved unexpectedly: %.3f (JUMP)", y), ticks2) && this.teleport && (this.getViolationLevel(player) > this.minVl || Math.abs(y) > 1.0)) {
                            this.plugin.setbackManager.setSetbackCheck(this, player);
                            player.teleport(playerMoveEvent.getFrom());
                            this.lastVerticalDeltas.put(player, player.getVelocity().getY());
                        }
                    }
                } else {
                    double d4 = VerticalMovementPredictor.predictNextGravityMotion(d);
                    if (Math.abs(y) > Math.abs(d4) + this.tolerance || !flag2 && y > d4 + this.tolerance) {
                        int value11 = 100;
                        long ticks8 = 5 + (int)(Math.abs(y) * this.vlMultiplier);
                        int value12 = MathUtilities.clampToInt(ticks8, value11);
                        if (this.recordViolation(player.getUniqueId(), value12, HackType.FLY, String.format(Locale.UK, player.getName() + " failed Vehicles: (PIG) moved unexpectedly: %.3f (%.3f)", y, d4), ticks2) && this.teleport && (this.getViolationLevel(player) > this.minVl || Math.abs(y) > 1.0)) {
                            this.plugin.setbackManager.setSetbackCheck(this, player);
                            player.teleport(playerMoveEvent.getFrom());
                            this.lastVerticalDeltas.put(player, player.getVelocity().getY());
                        }
                    }
                }
                if (!(x > 0.412) || !this.recordViolation(player.getUniqueId(), 5 + (int)(Math.abs(x) * this.vlMultiplier), HackType.FLY, String.format(Locale.UK, player.getName() + " failed Vehicles: (PIG) moved too quickly (%.3f)", x), ticks2) || !this.teleport || this.getViolationLevel(player) <= this.minVl && !(Math.abs(y) > 1.0)) break;
                this.plugin.setbackManager.setSetbackCheck(this, player);
                player.teleport(playerMoveEvent.getFrom());
                this.lastVerticalDeltas.put(player, player.getVelocity().getY());
            }
        }
        this.lastVerticalDeltas.put(player, y);
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        Player player;
        if (playerMoveEvent.isCancelled()) {
            return false;
        }
        return playerMoveEvent.getPlayer().getGameMode() != GameMode.SPECTATOR && !AAC.serverVersion.isElytraGliding(player = playerMoveEvent.getPlayer()) && !playerMoveEvent.getPlayer().isFlying() && !playerMoveEvent.getPlayer().isDead() && playerMoveEvent.getPlayer().getLocation().getY() > -1024.0 && !playerMoveEvent.getPlayer().getAllowFlight() && !playerMoveEvent.getPlayer().isSleeping() && playerMoveEvent.getPlayer().getVehicle() != null;
    }

    private boolean isBoatAirborne(Boat boat) {
        Material material = boat.getLocation().getBlock().getType();
        return material != Material.STATIONARY_WATER && material != Material.WATER && !boat.isOnGround();
    }
}

