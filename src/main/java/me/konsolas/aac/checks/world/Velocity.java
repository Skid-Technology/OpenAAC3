package me.konsolas.aac.checks.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import java.util.Locale;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.compat.CollisionQueryResult;
import me.konsolas.aac.compat.CollisionVectorAdjuster;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.compat.EnvironmentBlockChecks;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.util.HorizontalVector;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.checks.movement.MovementCheckListener;
import me.konsolas.aac.checks.movement.VelocityResponseState;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Velocity
extends Detector
implements MovementCheckListener {
    private final ExpiringMap<Player, VelocityResponseState> pendingResponses;
    private final ExpiringMap<Player, Double> recentHorizontalSpeeds;
    private final WeakHashMap lastAirVelocities;
    private int checkLength;
    private double maxTpDist;
    private boolean useVelocitySetbacks;
    private boolean checkDirection;
    private int minVl;
    private int vlNormal;
    private int vlNoPacket;

    public Velocity(AAC aAC) {        super(aAC);

        this.lastAirVelocities = new WeakHashMap();
        this.checkLength = 15;
        this.maxTpDist = 3.0;
        this.useVelocitySetbacks = true;
        this.checkDirection = true;
        this.minVl = 5;
        this.vlNormal = 2;
        this.vlNoPacket = 0;
        this.checkLength = this.plugin.getConfig().getInt("velocity.check_length", this.checkLength);
        this.maxTpDist = this.plugin.getConfig().getDouble("velocity.max_tp_dist", this.maxTpDist);
        this.useVelocitySetbacks = this.plugin.getConfig().getBoolean("velocity.use_velocity_setbacks", this.useVelocitySetbacks);
        this.checkDirection = this.plugin.getConfig().getBoolean("velocity.check_direction", this.checkDirection);
        this.minVl = this.plugin.getConfig().getInt("velocity.min_vl", this.minVl);
        this.vlNormal = this.plugin.getConfig().getInt("velocity.vl_normal", this.vlNormal);
        this.vlNoPacket = this.plugin.getConfig().getInt("velocity.vl_no_packet", this.vlNoPacket);
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 100L;
        ExpirationPolicy expirationPolicy = ExpirationPolicy.ACCESSED;
        this.recentHorizontalSpeeds = ExpiringMap.builder().expiration(ticks, timeUnit).expirationPolicy(expirationPolicy).build();
        TimeUnit timeUnit2 = TimeUnit.MILLISECONDS;
        long ticks2 = this.checkLength * 50;
        ExpirationPolicy expirationPolicy2 = ExpirationPolicy.CREATED;
        ExpirationListener<Player, VelocityResponseState> expirationListener = this::scheduleVelocityEvaluation;
        this.pendingResponses = ExpiringMap.builder().expiration(ticks2, timeUnit2).expirationPolicy(expirationPolicy2).expirationListener(expirationListener).build();
        VelocityPacketListener velocityPacketListener = new VelocityPacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.HELD_ITEM_SLOT, PacketType.Play.Client.POSITION, PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK, PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.BLOCK_PLACE, PacketType.Play.Client.ARM_ANIMATION});
        this.packetListeners.add(velocityPacketListener);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)velocityPacketListener);
    }

    private static Vector simulateKnockbackOffset(int i2, Vector vector, double d) {
        Vector vector2 = vector.clone();
        vector.setX(0).setY(0).setZ(0);
        for (int i = 0; i < i2; ++i) {
            vector.add(vector2);
            vector2.setX(vector2.getX() * 0.98);
            vector2.setZ(vector2.getZ() * 0.98);
            vector2.setY((vector2.getY() - 0.08) * 0.98);
            if (!(vector.length() > d)) continue;
            vector.subtract(vector2);
            break;
        }
        return vector2;
    }

    private void scheduleVelocitySetback(Player player, Vector vector, Location location, int i2) {
        Vector vector2 = Velocity.simulateKnockbackOffset(i2, vector, this.maxTpDist);
        Vector vector3 = vector;
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.applyVelocitySetback(player, location, vector3, vector2));
    }

    private void trackExpectedVelocity(Vector vector, PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2) {
        vector = vector.clone();
        if (playerMoveEvent.getPlayer().getVehicle() != null) {
            this.pendingResponses.remove(playerMoveEvent.getPlayer());
            return;
        }
        if (this.pendingResponses.containsKey(playerMoveEvent.getPlayer())) {
            double d;
            Object obj;
            VelocityResponseState velocityResponseState = (VelocityResponseState)this.pendingResponses.get(playerMoveEvent.getPlayer());
            Vector vector2 = VelocityResponseState.getExpectedVelocity(velocityResponseState).clone();
            if (groundCollisionState.isOnGround() || groundCollisionState2.isOnGround() || groundCollisionState.isAirStep() || groundCollisionState2.isAirStep()) {
                obj = new HorizontalVector((double)playerMoveEvent.getPlayer().getWalkSpeed() * 1.0);
                int value = -1;
                int value2 = -1;
                if (playerMoveEvent.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
                    for (PotionEffect potionEffect : playerMoveEvent.getPlayer().getActivePotionEffects()) {
                        if (potionEffect.getType().equals((Object)PotionEffectType.SPEED)) {
                            value = potionEffect.getAmplifier();
                        }
                        if (!potionEffect.getType().equals((Object)PotionEffectType.JUMP)) continue;
                        value2 = potionEffect.getAmplifier();
                    }
                }
                if (value > 0) {
                    obj = ((HorizontalVector)obj).applySpeedEffect(value);
                }
                if (groundCollisionState.isOnGround() && !groundCollisionState2.isOnGround()) {
                    Vector vector3;
                    double d2 = value2 + 1 > 0 ? (double)(value2 + 1) / 10.0 + 0.42 : 0.42;
                    Vector vector4 = vector3 = new Vector(vector.getX(), d2, vector.getZ());
                    Location location = playerMoveEvent.getPlayer().getLocation();
                    Player player = playerMoveEvent.getPlayer();
                    CollisionVectorAdjuster.adjustVectorForCollisions((Entity)player, location, vector4);
                    double d3 = 0.01;
                    double y = vector.getY();
                    double y2 = vector3.getY();
                    if (MathUtilities.approximatelyEquals(y2, y, d3)) {
                        vector2.setY(0.0);
                        vector.setY(0.0);
                    }
                    obj = ((HorizontalVector)obj).addJumpBoost();
                } else if (vector2.getY() == 0.0) {
                    obj = ((HorizontalVector)obj).addJumpBoost().increaseLength(HorizontalVector.ROUNDING_EPSILON.getLength());
                }
                obj = ((HorizontalVector)obj).maxLength(new HorizontalVector(this.recentHorizontalSpeeds.getOrDefault(playerMoveEvent.getPlayer(), 0.0)));
                d = ((HorizontalVector)obj).getX();
            } else {
                d = 0.1;
            }
            Vector vector5 = vector2;
            Location location = playerMoveEvent.getPlayer().getLocation();
            Player player = playerMoveEvent.getPlayer();
            obj = CollisionVectorAdjuster.adjustVectorForCollisions((Entity)player, location, vector5);
            if (((CollisionQueryResult)obj).getCollisions().size() > 0 && ((CollisionQueryResult)obj).getAdjustment() < 0.6) {
                vector2.setY(0.0);
                vector.setY(0.0);
            }
            double d4 = playerMoveEvent.getPlayer().getItemInHand() != null && playerMoveEvent.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) > 0 || VelocityResponseState.isResponded(velocityResponseState) || playerMoveEvent.getPlayer().hasPotionEffect(PotionEffectType.ABSORPTION) ? Math.min(vector.distance(vector2), vector.distance(vector2.clone().multiply(0.6))) : vector.distance(vector2);
            if ((d4 = Math.min(d4, vector.distance(VelocityResponseState.getExpectedVelocity(velocityResponseState)))) < d + 0.01) {
                this.decreaseViolationLevel(playerMoveEvent.getPlayer().getUniqueId(), 1);
                this.pendingResponses.remove(playerMoveEvent.getPlayer());
            }
            if (d4 < VelocityResponseState.getResponseDistance(velocityResponseState)) {
                VelocityResponseState.setObservedVelocity(velocityResponseState, vector);
                VelocityResponseState.setResponseDistance(velocityResponseState, d4);
                VelocityResponseState.setHorizontalResponse(velocityResponseState, d);
            }
        }
        this.recentHorizontalSpeeds.put(playerMoveEvent.getPlayer(), vector.length());
    }

    private void checkMidairDirectionChange(Vector vector, PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        long ticks2 = (ticks = 0x74D5D59FA342L ^ ticks) ^ 0x52271DEB48CAL;
        vector = vector.clone();
        Vector vector2 = (Vector)this.lastAirVelocities.get(playerMoveEvent.getPlayer());
        if (!(groundCollisionState.isOnGround() || groundCollisionState2.isOnGround() || groundCollisionState.isAirStep() || groundCollisionState2.isAirStep() || vector2 == null || this.pendingResponses.containsKey(playerMoveEvent.getPlayer()) || playerMoveEvent.getPlayer().getAllowFlight())) {
            AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(playerMoveEvent.getTo().toVector(), 0.6, 1.65).expand(new Vector(0.8, 0.1, 0.8));
            Player player = playerMoveEvent.getPlayer();
            if (CollisionAccess.getCollidingBoxes((Entity)player, axisAlignedBoundingBox).isEmpty()) {
                vector2.setY(vector2.getY() - 0.08);
                vector2.multiply(0.98);
                Vector vector3 = new Vector(vector2.getX() * 0.6, vector2.getY(), vector2.getZ() * 0.6);
                vector3.setY(vector2.getY());
                vector.setY(vector2.getY());
                double d = Math.min(vector2.distance(vector), vector3.distance(vector));
                if (d > 0.3) {
                    if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), this.vlNormal + (int)(d * 2.0), HackType.VELOCITY, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Velocity: changed direction midair (%.3f,%.3f,%.3f, expected %.3f,%.3f,%.3f)", vector.getX(), vector.getY(), vector.getZ(), vector2.getX(), vector2.getY(), vector2.getZ()), ticks2)) {
                        if (this.plugin.playerState.hasActiveVelocity(playerMoveEvent.getPlayer()) && this.useVelocitySetbacks) {
                            if (this.maxTpDist > 0.0 && this.getViolationLevel(playerMoveEvent.getPlayer()) > this.minVl) {
                                this.scheduleVelocitySetback(playerMoveEvent.getPlayer(), vector2.clone(), playerMoveEvent.getFrom().clone(), this.checkLength / 2);
                            }
                        } else if (this.maxTpDist > 0.0 && this.getViolationLevel(playerMoveEvent.getPlayer()) > this.minVl) {
                            boolean flag = false;
                            boolean flag2 = false;
                            double y = vector2.getY();
                            PlayerMoveEvent playerMoveEvent2 = playerMoveEvent;
                            Player player2 = playerMoveEvent.getPlayer();
                            this.plugin.setbackManager.applySetback(player2, playerMoveEvent2, y, flag2, flag);
                        }
                    }
                    this.lastAirVelocities.put(playerMoveEvent.getPlayer(), vector2);
                }
            }
        }
        this.lastAirVelocities.put(playerMoveEvent.getPlayer(), vector);
    }

    private boolean isExemptMovementState(Player player) {
        Player player2;
        return player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.isFlying() || AAC.serverVersion.isElytraGliding(player2 = player) || player.isDead();
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent playerChangedWorldEvent) {
        this.pendingResponses.remove(playerChangedWorldEvent.getPlayer());
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        return true;
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        Player player;
        long ticks2 = ticks ^ 0x460D8718C2C6L;
        if (this.plugin.setbackManager.getSetbackCheck(playerMoveEvent.getPlayer()) != null || playerMoveEvent.getPlayer().getVehicle() != null || EnvironmentBlockChecks.isPlayerInCobweb(new BlockPositionView(playerMoveEvent.getPlayer().getLocation())) || EnvironmentBlockChecks.isOnLadderOrVine(new BlockPositionView(playerMoveEvent.getPlayer().getLocation())) || this.isExemptMovementState(player = playerMoveEvent.getPlayer())) {
            this.pendingResponses.remove(playerMoveEvent.getPlayer());
            return;
        }
        AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(playerMoveEvent.getPlayer().getLocation().toVector(), 0.6, 1.65);
        Player player2 = playerMoveEvent.getPlayer();
        if (CollisionAccess.getCollidingBoxes((Entity)player2, axisAlignedBoundingBox).size() > 0) {
            return;
        }
        Vector vector = new Vector(playerMoveEvent.getTo().getX() - playerMoveEvent.getFrom().getX(), playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY(), playerMoveEvent.getTo().getZ() - playerMoveEvent.getFrom().getZ());
        if (this.checkDirection) {
            this.checkMidairDirectionChange(vector, playerMoveEvent, groundCollisionState, groundCollisionState2, blockPositionView, blockPositionView2, ticks2);
        }
        BlockPositionView blockPositionView3 = blockPositionView2;
        BlockPositionView blockPositionView4 = blockPositionView;
        GroundCollisionState groundCollisionState3 = groundCollisionState2;
        GroundCollisionState groundCollisionState4 = groundCollisionState;
        PlayerMoveEvent playerMoveEvent2 = playerMoveEvent;
        Vector vector2 = vector;
        this.trackExpectedVelocity(vector2, playerMoveEvent2, groundCollisionState4, groundCollisionState3, blockPositionView4, blockPositionView3);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        if (this.plugin.setbackManager.getSetbackCheck(playerTeleportEvent.getPlayer()) == null) {
            this.pendingResponses.remove(playerTeleportEvent.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        this.pendingResponses.remove(playerDeathEvent.getEntity());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        this.pendingResponses.remove(playerRespawnEvent.getPlayer());
    }

    private void applyVelocitySetback(Player player, Location location, Vector vector, Vector vector2) {
        Vector vector3 = vector;
        Location location2 = location;
        Player player2 = player;
        CollisionVectorAdjuster.adjustVectorForCollisions((Entity)player2, location2, vector3);
        this.plugin.setbackManager.setSetbackCheck(this, player);
        player.teleport(location.add(vector), PlayerTeleportEvent.TeleportCause.UNKNOWN);
        player.setVelocity(vector2);
        this.plugin.setbackManager.clearSetbackCheck(player);
    }

    private void scheduleVelocityEvaluation(Player player, VelocityResponseState velocityResponseState) {
        try {
            if (player == null || player.isDead()) {
                return;
            }
        }
        catch (NullPointerException nullPointerException) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.evaluateVelocityViolation(velocityResponseState, player));
    }

    private void evaluateVelocityViolation(VelocityResponseState velocityResponseState, Player player) {
        long ticks = 113218805911140L;
        if (VelocityResponseState.isAttackObserved(velocityResponseState)) {
            if (this.recordViolation(player.getUniqueId(), this.vlNormal, HackType.VELOCITY, String.format(Locale.UK, player.getName() + " failed Velocity: ignored server velocity (%.3f,%.3f,%.3f/%.3f, expected %.3f,%.3f,%.3f/%.3f)", VelocityResponseState.getObservedVelocity(velocityResponseState).getX(), VelocityResponseState.getObservedVelocity(velocityResponseState).getY(), VelocityResponseState.getObservedVelocity(velocityResponseState).getZ(), VelocityResponseState.getResponseDistance(velocityResponseState), VelocityResponseState.getExpectedVelocity(velocityResponseState).getX(), VelocityResponseState.getExpectedVelocity(velocityResponseState).getY(), VelocityResponseState.getExpectedVelocity(velocityResponseState).getZ(), VelocityResponseState.getHorizontalResponse(velocityResponseState)), ticks) && this.maxTpDist > 0.0 && this.getViolationLevel(player) > this.minVl) {
                if (this.useVelocitySetbacks) {
                    this.scheduleVelocitySetback(player, VelocityResponseState.getExpectedVelocity(velocityResponseState).clone(), VelocityResponseState.getOrigin(velocityResponseState).clone(), this.checkLength);
                } else {
                    this.plugin.setbackManager.setSetbackCheck(this, player);
                    player.teleport(VelocityResponseState.getOrigin(velocityResponseState), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                    player.setVelocity(VelocityResponseState.getExpectedVelocity(velocityResponseState));
                    this.plugin.setbackManager.clearSetbackCheck(player);
                }
            }
        } else if (this.recordViolation(player.getUniqueId(), this.vlNoPacket, HackType.VELOCITY, String.format(Locale.UK, player.getName() + " failed Velocity: didn't respond - frozen? (%.3f,%.3f,%.3f/%.3f, expected %.3f,%.3f,%.3f/%.3f)", VelocityResponseState.getObservedVelocity(velocityResponseState).getX(), VelocityResponseState.getObservedVelocity(velocityResponseState).getY(), VelocityResponseState.getObservedVelocity(velocityResponseState).getZ(), VelocityResponseState.getResponseDistance(velocityResponseState), VelocityResponseState.getExpectedVelocity(velocityResponseState).getX(), VelocityResponseState.getExpectedVelocity(velocityResponseState).getY(), VelocityResponseState.getExpectedVelocity(velocityResponseState).getZ(), VelocityResponseState.getHorizontalResponse(velocityResponseState)), ticks) && this.maxTpDist > 0.0 && this.getViolationLevel(player) > this.minVl) {
            if (this.useVelocitySetbacks) {
                this.scheduleVelocitySetback(player, VelocityResponseState.getExpectedVelocity(velocityResponseState).clone(), VelocityResponseState.getOrigin(velocityResponseState).clone(), this.checkLength);
            } else {
                this.plugin.setbackManager.setSetbackCheck(this, player);
                player.teleport(VelocityResponseState.getOrigin(velocityResponseState), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                player.setVelocity(VelocityResponseState.getExpectedVelocity(velocityResponseState));
                this.plugin.setbackManager.clearSetbackCheck(player);
            }
        }
    }

    static ExpiringMap<Player, VelocityResponseState> getPendingResponses(Velocity velocity) {
        return velocity.pendingResponses;
    }

    static boolean isExemptMovement(Velocity velocity, Player player) {
        Player player2 = player;
        return velocity.isExemptMovementState(player2);
    }
}

