package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
import me.konsolas.aac.api.HackType;
import org.bukkit.GameMode;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Fly
extends Detector
implements MovementCheckListener {
    private final ExpiringMap<UUID, Integer> recentViolationPlayers;
    private final ExpiringMap<Player, Double> expectedVelocityY;
    private final WeakHashMap<Player, Double> lastVerticalDeltas;
    private final WeakHashMap<Player, VerticalMovementState> lastVerticalStates;
    private double tolerance;
    private double eTolerance;
    private boolean teleport;
    private double vlMultiplier;
    private int minVl;
    private int velTicks;
    private boolean abilities;
    private boolean resetPitch;
    private int reduceDelay;
    private final Set levitatingPlayers;
    private final Set exemptNextMovePlayers;

    public Fly(AAC aAC) {        super(aAC);

        this.lastVerticalDeltas = new WeakHashMap();
        this.lastVerticalStates = new WeakHashMap();
        this.tolerance = 0.06;
        this.eTolerance = 0.2;
        this.teleport = true;
        this.vlMultiplier = 6.0;
        this.minVl = 12;
        this.velTicks = 12;
        this.abilities = true;
        this.resetPitch = true;
        this.reduceDelay = 20;
        this.levitatingPlayers = Collections.newSetFromMap(new WeakHashMap());
        this.exemptNextMovePlayers = Collections.newSetFromMap(new WeakHashMap());
        this.tolerance = this.plugin.getConfig().getDouble("fly.tolerance", this.tolerance);
        this.eTolerance = this.plugin.getConfig().getDouble("fly.e_tolerance", this.eTolerance);
        this.teleport = this.plugin.getConfig().getBoolean("fly.teleport", this.teleport);
        this.vlMultiplier = this.plugin.getConfig().getDouble("fly.vl_multiplier", this.vlMultiplier);
        this.minVl = this.plugin.getConfig().getInt("fly.min_vl", this.minVl);
        this.velTicks = this.plugin.getConfig().getInt("fly.vel_ticks", this.velTicks);
        this.abilities = this.plugin.getConfig().getBoolean("fly.abilities", this.abilities);
        this.resetPitch = this.plugin.getConfig().getBoolean("fly.reset_pitch", this.resetPitch);
        this.reduceDelay = this.plugin.getConfig().getInt("fly.reduce_delay", this.reduceDelay);
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = this.velTicks * 50;
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        this.expectedVelocityY = ExpiringMap.builder().expiration(ticks, timeUnit).expirationPolicy(expirationPolicy).build();
        ExpirationPolicy expirationPolicy2 = ExpirationPolicy.CREATED;
        TimeUnit timeUnit2 = TimeUnit.MILLISECONDS;
        long ticks2 = this.reduceDelay * 50;
        this.recentViolationPlayers = ExpiringMap.builder().expirationPolicy(expirationPolicy2).expiration(ticks2, timeUnit2).build();
        PacketAdapter packetAdapter = new FlyVelocityPacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.EXPLOSION});
        this.packetListeners.add(packetAdapter);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)packetAdapter);
        if (this.abilities) {
            packetAdapter = new FlyAbilitiesPacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Client.ABILITIES});
            this.packetListeners.add(packetAdapter);
            ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)packetAdapter);
        }
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        Player player;
        if (playerMoveEvent.isCancelled()) {
            return false;
        }
        if (!(playerMoveEvent.getPlayer().getGameMode() == GameMode.SPECTATOR || playerMoveEvent.getPlayer().getVehicle() != null || AAC.serverVersion.isElytraGliding(player = playerMoveEvent.getPlayer()) || playerMoveEvent.getPlayer().isFlying() || playerMoveEvent.getPlayer().isDead() || !(playerMoveEvent.getPlayer().getLocation().getY() > -1024.0) || playerMoveEvent.getPlayer().getAllowFlight() || playerMoveEvent.getPlayer().isSleeping())) {
            return true;
        }
        this.exemptNextMovePlayers.add(playerMoveEvent.getPlayer());
        this.lastVerticalDeltas.remove(playerMoveEvent.getPlayer());
        return false;
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        double d;
        double d2;
        double d3;
        long ticks2 = ticks ^ 0x60FF4F6C294EL;
        if (!this.recentViolationPlayers.containsKey(playerMoveEvent.getPlayer().getUniqueId())) {
            this.decreaseViolationLevel(playerMoveEvent.getPlayer().getUniqueId(), 1);
        }
        if (this.exemptNextMovePlayers.contains(playerMoveEvent.getPlayer())) {
            this.lastVerticalDeltas.remove(playerMoveEvent.getPlayer());
            this.exemptNextMovePlayers.remove(playerMoveEvent.getPlayer());
            return;
        }
        double y = playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY();
        double x = Math.sqrt(Math.pow(playerMoveEvent.getTo().getX() - playerMoveEvent.getFrom().getX(), 2.0) + Math.pow(playerMoveEvent.getTo().getZ() - playerMoveEvent.getFrom().getZ(), 2.0));
        if (y == 0.0 && x == 0.0 && this.lastVerticalStates.containsKey(playerMoveEvent.getPlayer()) && ((VerticalMovementState)this.lastVerticalStates.get((Object)playerMoveEvent.getPlayer())).timestamp > System.currentTimeMillis() - 250L) {
            return;
        }
        int value = 0;
        for (PotionEffect potionEffect : playerMoveEvent.getPlayer().getActivePotionEffects()) {
            if (!potionEffect.getType().equals((Object)PotionEffectType.JUMP)) continue;
            value = potionEffect.getAmplifier() + 1;
        }
        boolean flag = EnvironmentBlockChecks.isInLiquid(blockPositionView);
        boolean flag2 = EnvironmentBlockChecks.isInLiquid(blockPositionView2);
        if (!this.lastVerticalDeltas.containsKey(playerMoveEvent.getPlayer())) {
            this.lastVerticalDeltas.put(playerMoveEvent.getPlayer(), y);
            return;
        }
        int value2 = 0;
        boolean flag3 = this.levitatingPlayers.contains(playerMoveEvent.getPlayer());
        if (AAC.serverVersion.hasLevitation(playerMoveEvent.getPlayer())) {
            this.levitatingPlayers.add(playerMoveEvent.getPlayer());
            for (PotionEffect effect : playerMoveEvent.getPlayer().getActivePotionEffects()) {
                if (!effect.getType().equals((Object)PotionEffectType.LEVITATION)) continue;
                value2 = effect.getAmplifier() + 1;
            }
        } else {
            this.levitatingPlayers.remove(playerMoveEvent.getPlayer());
        }
        VerticalMovementState verticalMovementState = new VerticalMovementState();
        verticalMovementState.timestamp = System.currentTimeMillis();
        verticalMovementState.lastSafeLocation = playerMoveEvent.getFrom().clone();
        VerticalMovementState verticalMovementState3 = this.lastVerticalStates.get(playerMoveEvent.getPlayer()) == null ? new VerticalMovementState() : this.lastVerticalStates.get(playerMoveEvent.getPlayer());
        this.lastVerticalStates.put(playerMoveEvent.getPlayer(), verticalMovementState);
        boolean flag4 = false;
        for (Entity entity : playerMoveEvent.getPlayer().getNearbyEntities(1.5, 1.0, 1.5)) {
            if (!(entity instanceof Boat)) continue;
            flag4 = true;
        }
        if (!flag4 && EnvironmentBlockChecks.isOnWaterLily(blockPositionView)) {
            flag4 = true;
        }
        if (groundCollisionState.isCollided()) {
            groundCollisionState = groundCollisionState.isAirStep() ? GroundCollisionState.TRUE_AIRSTEP_COLLIDED : GroundCollisionState.TRUE_NORMAL_COLLIDED;
        }
        boolean flag5 = blockPositionView.getBlock().isLiquid();
        boolean flag6 = EnvironmentBlockChecks.isInFlowingLiquid(blockPositionView);
        boolean flag7 = EnvironmentBlockChecks.isInSnow(blockPositionView);
        boolean flag8 = EnvironmentBlockChecks.isNearPiston(blockPositionView);
        boolean flag9 = EnvironmentBlockChecks.isPlayerInCobweb(blockPositionView);
        boolean flag10 = EnvironmentBlockChecks.isOnLadderOrVine(blockPositionView);
        boolean flag11 = EnvironmentBlockChecks.isNearSnowColumn(blockPositionView) || EnvironmentBlockChecks.isNearSnowColumn(blockPositionView2);
        boolean flag12 = EnvironmentBlockChecks.isClimbingWithLowCeiling(blockPositionView, 0.8);
        boolean flag13 = EnvironmentBlockChecks.isClimbingWithLowCeiling(blockPositionView2, 0.3);
        VerticalMovementState verticalMovementState2 = verticalMovementState;
        Object obj = verticalMovementState3;
        int value3 = value2;
        int value4 = value;
        double d4 = this.tolerance;
        double d5 = x;
        boolean flag14 = flag10;
        boolean flag15 = flag8;
        boolean flag16 = flag9;
        boolean flag17 = flag11;
        boolean flag18 = flag2;
        boolean flag19 = flag;
        boolean flag20 = flag12;
        boolean flag21 = flag13;
        boolean flag22 = flag6;
        boolean flag23 = flag5;
        double y2 = playerMoveEvent.getPlayer().getVelocity().getY();
        boolean flag24 = flag4;
        boolean flag25 = flag7;
        boolean flag26 = EnvironmentBlockChecks.isAboveOrOnSlimeBlock(blockPositionView);
        GroundCollisionState groundCollisionState3 = groundCollisionState2;
        GroundCollisionState groundCollisionState4 = groundCollisionState;
        boolean flag27 = EnvironmentBlockChecks.isOnEdgeOfHalfBlock(blockPositionView);
        double d6 = (Double)this.lastVerticalDeltas.get(playerMoveEvent.getPlayer());
        double d7 = y;
        boolean flag28 = VerticalMovementPredictor.isValidVerticalMovement(d7, d6, flag27, groundCollisionState4, groundCollisionState3, flag26, flag25, flag24, y2, flag23, flag22, flag21, flag20, flag19, flag18, flag17, flag16, flag15, flag14, d5, d4, value4, value3, (VerticalMovementState)obj, verticalMovementState2);
        if (value2 == 0 || !flag3) {
            if (groundCollisionState.isOnGround() && groundCollisionState2.isOnGround() && y <= 0.6 && y > 0.0) {
                flag28 = true;
            }
        } else {
            flag28 = true;
        }
        if (EnvironmentBlockChecks.isNearShulkerBox(blockPositionView) && MathUtilities.approximatelyEquals(d3 = y, d2 = 0.05, d = this.tolerance)) {
            flag28 = true;
        }
        int value5 = 0;
        boolean flag29 = false;
        if (value2 == 0 && flag3 && Math.abs(y) < 1.0) {
            flag28 = true;
        }
        if (verticalMovementState3.lastSafeLocation != null && playerMoveEvent.getFrom().distanceSquared(playerMoveEvent.getTo()) < 5.0 && playerMoveEvent.getFrom().getWorld().equals(verticalMovementState3.lastSafeLocation.getWorld()) && playerMoveEvent.getFrom().distanceSquared(verticalMovementState3.lastSafeLocation) < 5.0 && verticalMovementState3.lastSafeLocation.getY() <= playerMoveEvent.getFrom().getY() && !groundCollisionState.isOnGround() && !flag28) {
            value5 = 5;
            flag29 = true;
        }
        if (!flag28 && this.expectedVelocityY.containsKey(playerMoveEvent.getPlayer())) {
            double d8 = (Double)this.expectedVelocityY.get(playerMoveEvent.getPlayer());
            double d9 = flag ? 0.2 : 0.0;
            if (y < d8 + this.tolerance + d9 || y < 0.0 && groundCollisionState2.isOnGround()) {
                if (y > d8 - this.tolerance) {
                    flag28 = true;
                } else if (Math.abs(y) > 1.0 && y > d8 * 0.5 - 0.1 - this.tolerance || y > d8 / 0.5 - 0.1 - this.tolerance) {
                    flag28 = true;
                }
            }
            this.expectedVelocityY.remove(playerMoveEvent.getPlayer());
        } else if (groundCollisionState.isOnGround()) {
            value5 = 5;
        }
        if (this.plugin.logManager.isDebugEnabled()) {
            double d10;
            double d11;
            double d12;
            double d13 = d12 = y;
            double d14 = d11 = verticalMovementState.lastDeltaY;
            double d15 = d10 = verticalMovementState.expectedDeltaY;
            this.plugin.logManager.logPlayerDebug(playerMoveEvent.getPlayer(), "(Fly) fullmove [dy r:" + MathUtilities.formatNumber(d13) + " u:" + MathUtilities.formatNumber(d14) + " e:" + MathUtilities.formatNumber(d15) + " fs u:" + verticalMovementState.fallStage + " uml u:" + flag29 + " g " + (Object)((Object)groundCollisionState) + (Object)((Object)groundCollisionState2) + "]");
        }
        if (!flag28) {
            int value6 = 100;
            long ticks3 = value5 + 5 + (int)(Math.abs(y - VerticalMovementPredictor.predictNextGravityMotion((Double)this.lastVerticalDeltas.get(playerMoveEvent.getPlayer()))) * this.vlMultiplier);
            int value7 = MathUtilities.clampToInt(ticks3, value6);
            if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), value7, HackType.FLY, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Fly: moved unexpectedly: %.4f (%.4f->%.4f / %s%s%d)", new Object[]{y, verticalMovementState3.lastDeltaY, verticalMovementState.expectedDeltaY, groundCollisionState, groundCollisionState2, verticalMovementState.fallStage}), ticks2) && this.teleport && (this.getViolationLevel(playerMoveEvent.getPlayer()) > this.minVl || Math.abs(y) > 1.0) && !playerMoveEvent.isCancelled()) {
                if (flag29) {
                    PlayerMoveEvent playerMoveEvent2 = new PlayerMoveEvent(playerMoveEvent.getPlayer(), verticalMovementState3.lastSafeLocation, verticalMovementState3.lastSafeLocation);
                    boolean flag30 = this.resetPitch;
                    boolean flag31 = false;
                    double y3 = Math.min(-0.1, playerMoveEvent.getPlayer().getVelocity().getY());
                    PlayerMoveEvent playerMoveEvent3 = playerMoveEvent2;
                    Player player = playerMoveEvent.getPlayer();
                    this.plugin.setbackManager.applySetback(player, playerMoveEvent3, y3, flag31, flag30);
                    playerMoveEvent.setTo(playerMoveEvent2.getTo());
                } else {
                    boolean flag32 = this.resetPitch;
                    boolean flag33 = true;
                    double y4 = playerMoveEvent.getPlayer().getVelocity().getY();
                    PlayerMoveEvent playerMoveEvent4 = playerMoveEvent;
                    Player player = playerMoveEvent.getPlayer();
                    this.plugin.setbackManager.applySetback(player, playerMoveEvent4, y4, flag33, flag32);
                }
            }
            ((VerticalMovementState)this.lastVerticalStates.get((Object)playerMoveEvent.getPlayer())).lastDeltaY = 0.0;
            this.lastVerticalDeltas.put(playerMoveEvent.getPlayer(), 0.0);
        } else {
            this.lastVerticalDeltas.put(playerMoveEvent.getPlayer(), y);
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent playerToggleFlightEvent) {
        this.lastVerticalDeltas.remove(playerToggleFlightEvent.getPlayer());
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent vehicleEnterEvent) {
        if (vehicleEnterEvent.getEntered() instanceof Player) {
            this.lastVerticalDeltas.remove(vehicleEnterEvent.getEntered());
        }
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent vehicleExitEvent) {
        if (vehicleExitEvent.getExited() instanceof Player) {
            this.lastVerticalDeltas.remove(vehicleExitEvent.getExited());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Iterator iterator = blockBreakEvent.getPlayer().getWorld().getNearbyEntities(blockBreakEvent.getBlock().getLocation(), 1.0, 3.0, 1.0).iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity)iterator.next();
            if (!(entity instanceof Player)) continue;
            this.lastVerticalStates.getOrDefault((Object)blockBreakEvent.getPlayer(), VerticalMovementPredictor.DEFAULT_STATE).brokeBlockNearby = true;
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Iterator iterator = blockPlaceEvent.getPlayer().getWorld().getNearbyEntities(blockPlaceEvent.getBlockPlaced().getLocation(), 1.0, 3.0, 1.0).iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity)iterator.next();
            if (!(entity instanceof Player)) continue;
            this.lastVerticalStates.getOrDefault((Object)blockPlaceEvent.getPlayer(), VerticalMovementPredictor.DEFAULT_STATE).placedBlockNearby = true;
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        this.lastVerticalDeltas.put(playerTeleportEvent.getPlayer(), 0.0);
        Optional.ofNullable(this.lastVerticalStates.get(playerTeleportEvent.getPlayer())).ifPresent(Fly::clearLastLocation);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        this.lastVerticalDeltas.remove(playerRespawnEvent.getPlayer());
    }

    @Override
    public boolean recordViolation(UUID uUID, int i, HackType hackType, String str, long ticks) {
        long ticks2 = ticks ^ 0L;
        this.recentViolationPlayers.put(uUID, 0);
        this.recentViolationPlayers.resetExpiration(uUID);
        return super.recordViolation(uUID, i, hackType, str, ticks2);
    }

    private static void clearLastLocation(VerticalMovementState verticalMovementState) {
        verticalMovementState.lastSafeLocation = null;
    }

    static ExpiringMap<Player, Double> getExpectedVelocityMap(Fly fly) {
        return fly.expectedVelocityY;
    }
}

