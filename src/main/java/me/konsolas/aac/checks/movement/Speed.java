package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
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
import me.konsolas.aac.util.HorizontalVector;
import me.konsolas.aac.compat.ItemUseAccess;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class Speed
extends Detector
implements MovementCheckListener {
    private final WeakHashMap sneakToggleCounts;
    private final WeakHashMap itemUseToggleCounts;
    private final ExpiringMap<Player, HorizontalVector> velocityBoosts;
    private final WeakHashMap lastMoveVectors;
    private final WeakHashMap lastAllowedSpeeds;
    private final Set airbornePlayers;
    private final WeakHashMap offGroundTicks;
    private final ExpiringMap<Player, Boolean> cobwebBoostUsed;
    private final ExpiringMap<Player, Boolean> iceBoostPlayers;
    private final ExpiringMap<UUID, Integer> vlDecayCooldowns;
    private final ExpiringMap<Player, Integer> cachedSpeedLevels;
    private final ExpiringMap<Player, Boolean> itemUseGracePlayers;
    private int maxSneakToggles;
    private int maxUseToggles;
    private int velTicks;
    private double threshold;
    private double vlMultiplier;
    private int minVl;
    private int reduceDelay;
    private double itemScalar;
    private double sneakScalar;
    private double soulSandScalar;
    private boolean checkSprintDirection;
    private boolean checkSlownessEffect;
    private boolean checkCobwebs;
    private boolean strictVelocityChecking;

    public Speed(AAC aAC) {        super(aAC);

        this.sneakToggleCounts = new WeakHashMap();
        this.itemUseToggleCounts = new WeakHashMap();
        this.lastMoveVectors = new WeakHashMap();
        this.lastAllowedSpeeds = new WeakHashMap();
        this.airbornePlayers = Collections.newSetFromMap(new WeakHashMap());
        this.offGroundTicks = new WeakHashMap();
        this.maxSneakToggles = 15;
        this.maxUseToggles = 35;
        this.velTicks = 12;
        this.threshold = 0.01;
        this.vlMultiplier = 5.0;
        this.minVl = 20;
        this.reduceDelay = 20;
        this.itemScalar = 0.5;
        this.sneakScalar = 0.63;
        this.soulSandScalar = 0.7;
        this.checkSprintDirection = true;
        this.checkSlownessEffect = true;
        this.checkCobwebs = true;
        this.strictVelocityChecking = true;
        this.maxSneakToggles = this.plugin.getConfig().getInt("speed.max_sneak_toggles", this.maxSneakToggles);
        this.maxUseToggles = this.plugin.getConfig().getInt("speed.max_use_toggles", this.maxUseToggles);
        this.velTicks = this.plugin.getConfig().getInt("speed.vel_ticks", this.velTicks);
        this.threshold = this.plugin.getConfig().getDouble("speed.threshold", this.threshold);
        this.vlMultiplier = this.plugin.getConfig().getDouble("speed.vl_multiplier", this.vlMultiplier);
        this.minVl = this.plugin.getConfig().getInt("speed.min_vl", this.minVl);
        this.reduceDelay = this.plugin.getConfig().getInt("speed.reduce_delay", this.reduceDelay);
        this.itemScalar = this.plugin.getConfig().getDouble("speed.item_scalar", this.itemScalar);
        this.sneakScalar = this.plugin.getConfig().getDouble("speed.sneak_scalar", this.sneakScalar);
        this.soulSandScalar = this.plugin.getConfig().getDouble("speed.soul_sand_scalar", this.soulSandScalar);
        this.checkSprintDirection = this.plugin.getConfig().getBoolean("speed.check_sprint_direction", this.checkSprintDirection);
        this.checkSlownessEffect = this.plugin.getConfig().getBoolean("speed.check_slowness_effect", this.checkSlownessEffect);
        this.checkCobwebs = this.plugin.getConfig().getBoolean("speed.check_cobwebs", this.checkCobwebs);
        this.strictVelocityChecking = this.plugin.getConfig().getBoolean("speed.strict_velocity_checking", this.strictVelocityChecking);
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 200L;
        this.cobwebBoostUsed = ExpiringMap.builder().expirationPolicy(expirationPolicy).expiration(ticks, timeUnit).build();
        ExpirationPolicy expirationPolicy2 = ExpirationPolicy.CREATED;
        TimeUnit timeUnit2 = TimeUnit.MILLISECONDS;
        long ticks2 = this.reduceDelay * 50;
        this.vlDecayCooldowns = ExpiringMap.builder().expirationPolicy(expirationPolicy2).expiration(ticks2, timeUnit2).build();
        ExpirationPolicy expirationPolicy3 = ExpirationPolicy.CREATED;
        TimeUnit timeUnit3 = TimeUnit.MILLISECONDS;
        long ticks3 = 100L;
        this.iceBoostPlayers = ExpiringMap.builder().expirationPolicy(expirationPolicy3).expiration(ticks3, timeUnit3).build();
        ExpirationPolicy expirationPolicy4 = ExpirationPolicy.CREATED;
        TimeUnit timeUnit4 = TimeUnit.MILLISECONDS;
        long ticks4 = 100L;
        this.cachedSpeedLevels = ExpiringMap.builder().expirationPolicy(expirationPolicy4).expiration(ticks4, timeUnit4).build();
        ExpirationPolicy expirationPolicy5 = ExpirationPolicy.CREATED;
        TimeUnit timeUnit5 = TimeUnit.MILLISECONDS;
        long ticks5 = 200L;
        this.itemUseGracePlayers = ExpiringMap.builder().expirationPolicy(expirationPolicy5).expiration(ticks5, timeUnit5).build();
        Server server = Bukkit.getServer();
        this.scheduledTaskIds.add(server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::decaySpeedViolations, 20L, 53L));
        Server server2 = Bukkit.getServer();
        this.scheduledTaskIds.add(server2.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::resetToggleCounters, 6L, 20L));
        PacketAdapter packetAdapter = new SpeedItemUsePacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Client.BLOCK_PLACE, PacketType.Play.Client.BLOCK_DIG});
        this.packetListeners.add(packetAdapter);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)packetAdapter);
        TimeUnit timeUnit6 = TimeUnit.MILLISECONDS;
        long ticks6 = this.velTicks * 50;
        ExpirationPolicy expirationPolicy6 = ExpirationPolicy.CREATED;
        this.velocityBoosts = ExpiringMap.builder().expiration(ticks6, timeUnit6).expirationPolicy(expirationPolicy6).build();
        packetAdapter = new SpeedVelocityPacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Server.ENTITY_VELOCITY});
        this.packetListeners.add(packetAdapter);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)packetAdapter);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent playerFishEvent) {
        if (playerFishEvent.getCaught() instanceof Player && !playerFishEvent.getPlayer().equals(playerFishEvent.getCaught())) {
            Player player = (Player)playerFishEvent.getCaught();
            this.velocityBoosts.put(player, HorizontalVector.ONE);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent playerToggleSneakEvent) {
        long ticks = 113218805911140L;
        Player player = playerToggleSneakEvent.getPlayer();
        if (this.plugin.checkManager.isPlayerExempt(player)) {
            return;
        }
        if (!this.sneakToggleCounts.containsKey(playerToggleSneakEvent.getPlayer())) {
            this.sneakToggleCounts.put(playerToggleSneakEvent.getPlayer(), 1);
        } else {
            this.sneakToggleCounts.put(playerToggleSneakEvent.getPlayer(), (Integer)this.sneakToggleCounts.get(playerToggleSneakEvent.getPlayer()) + 1);
            if ((Integer)this.sneakToggleCounts.get(playerToggleSneakEvent.getPlayer()) > this.maxSneakToggles) {
                this.recordViolation(playerToggleSneakEvent.getPlayer().getUniqueId(), 50, HackType.SPEED, playerToggleSneakEvent.getPlayer().getName() + " failed Speed: tried to toggle sneak too quickly (" + this.sneakToggleCounts.get(playerToggleSneakEvent.getPlayer()) + " toggles/second)", ticks);
                return;
            }
        }
        if (this.plugin.logManager.isDebugEnabled()) {
            this.plugin.logManager.logPlayerDebug(playerToggleSneakEvent.getPlayer(), "(Speed) PlayerToggleSneakEvent sneaks[m:" + this.maxSneakToggles + ",r:" + this.sneakToggleCounts.get(playerToggleSneakEvent.getPlayer()) + "]");
        }
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        if (playerMoveEvent.getPlayer().isDead() || playerMoveEvent.getPlayer().isFlying() || playerMoveEvent.getPlayer().getGameMode().equals((Object)GameMode.SPECTATOR) || playerMoveEvent.getPlayer().getAllowFlight()) {
            this.lastMoveVectors.put(playerMoveEvent.getPlayer(), new HorizontalVector(Math.abs(playerMoveEvent.getTo().getX() - playerMoveEvent.getFrom().getX()) * 1.2, Math.abs(playerMoveEvent.getTo().getZ() - playerMoveEvent.getFrom().getZ()) * 1.2));
            return false;
        }
        if (playerMoveEvent.getPlayer().getVehicle() != null) {
            this.lastMoveVectors.remove(playerMoveEvent.getPlayer());
            return false;
        }
        return true;
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        int i;
        HorizontalVector horizontalVector;
        long ticks2 = ticks ^ 0x60FF4F6C294EL;
        double x = playerMoveEvent.getTo().getX() - playerMoveEvent.getFrom().getX();
        double z = playerMoveEvent.getTo().getZ() - playerMoveEvent.getFrom().getZ();
        if (Math.abs(x) + Math.abs(z) < 0.001) {
            return;
        }
        HorizontalVector horizontalVector2 = new HorizontalVector(Math.abs(x), Math.abs(z));
        Player player = playerMoveEvent.getPlayer();
        if (AAC.serverVersion.isElytraGliding(player)) {
            this.lastMoveVectors.put(playerMoveEvent.getPlayer(), horizontalVector2.multiply(2.0));
            this.iceBoostPlayers.put(playerMoveEvent.getPlayer(), false);
            return;
        }
        int value = -1;
        int value2 = -1;
        int value3 = -1;
        if (playerMoveEvent.getPlayer().hasPotionEffect(PotionEffectType.SPEED) || playerMoveEvent.getPlayer().hasPotionEffect(PotionEffectType.SLOW)) {
            for (PotionEffect potionEffect : playerMoveEvent.getPlayer().getActivePotionEffects()) {
                if (potionEffect.getType().equals((Object)PotionEffectType.SPEED)) {
                    value = potionEffect.getAmplifier();
                    continue;
                }
                if (potionEffect.getType().equals((Object)PotionEffectType.SLOW)) {
                    value2 = potionEffect.getAmplifier();
                    continue;
                }
                if (!potionEffect.getType().equals((Object)PotionEffectType.JUMP)) continue;
                value3 = potionEffect.getAmplifier();
            }
        }
        if (groundCollisionState.isCollided()) {
            groundCollisionState = groundCollisionState.isAirStep() ? GroundCollisionState.TRUE_AIRSTEP_COLLIDED : GroundCollisionState.TRUE_NORMAL_COLLIDED;
        }
        double d = playerMoveEvent.getPlayer().getWalkSpeed();
        Block block = playerMoveEvent.getPlayer().getLocation().getBlock();
        boolean flag = blockPositionView.getMaterialAtNegOffset(0.0, 0.1, 0.0) == Material.PACKED_ICE || blockPositionView.getMaterialAtNegOffset(0.0, 0.1, 0.0) == Material.ICE || blockPositionView.getMaterialAtNegOffset(0.0, 0.9, 0.0) == Material.PACKED_ICE || blockPositionView.getMaterialAtNegOffset(0.0, 0.9, 0.0) == Material.ICE;
        boolean flag2 = EnvironmentBlockChecks.isNearSlabOrStair(blockPositionView2);
        double y = playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY();
        HorizontalVector horizontalVector3 = new HorizontalVector(d, d);
        Player player2 = playerMoveEvent.getPlayer();
        if (!this.lastMoveVectors.containsKey(player2)) {
            this.lastMoveVectors.put(player2, horizontalVector2);
            return;
        }
        HorizontalVector horizontalVector4 = (HorizontalVector)this.lastMoveVectors.get(player2);
        int value4 = 4;
        if (this.checkCobwebs && EnvironmentBlockChecks.isInCobweb(blockPositionView)) {
            horizontalVector = HorizontalVector.BASE_AIR;
            double y2 = Math.abs(playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY());
            double d2 = 0.05;
            if (blockPositionView.getMaterialAtNegOffset(0.0, 0.1, 0.0) != Material.WEB) {
                d2 = 0.15;
                if (!this.cobwebBoostUsed.containsKey(player2) && horizontalVector2.getLength() > horizontalVector.getLength()) {
                    horizontalVector = horizontalVector.multiply(3.0);
                    this.cobwebBoostUsed.put(player2, false);
                }
            }
            if (!flag2 && (y2 < -d2 || y2 > d2)) {
                int value5 = 100;
                long ticks3 = (int)(Math.abs(horizontalVector2.getLength() - horizontalVector.getLength()) * this.vlMultiplier + 20.0);
                int value6 = MathUtilities.clampToInt(ticks3, value5);
                if (this.recordViolation(player2.getUniqueId(), value6, HackType.SPEED, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Speed: (WEB/VERTICAL) moved too quickly: %.3f blocks/tick (%.3f)", y2, 0.05), ticks2) && (this.getViolationLevel(playerMoveEvent.getPlayer()) > this.minVl || value6 > this.minVl)) {
                    playerMoveEvent.setTo(playerMoveEvent.getFrom());
                }
                return;
            }
        } else if (EnvironmentBlockChecks.isInLiquid(blockPositionView)) {
            int value7 = playerMoveEvent.getPlayer().getInventory().getBoots() != null ? playerMoveEvent.getPlayer().getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER) : 0;
            boolean blToWater = EnvironmentBlockChecks.isInLiquid(blockPositionView2);
            boolean flag3 = EnvironmentBlockChecks.isNotSurroundedByLiquid(blockPositionView2);
            boolean flag4 = EnvironmentBlockChecks.isNotSurroundedByLiquid(blockPositionView.offset(0.0, 0.5, 0.0));
            boolean flag5 = EnvironmentBlockChecks.isNotSurroundedByLiquid(blockPositionView2.offset(0.0, 0.5, 0.0));
            this.airbornePlayers.remove(player2);
            double d3 = 0.92;
            double d4 = 0.02;
            double d5 = value7;
            d5 *= 1.1;
            if (d5 > 3.0) {
                d5 = 3.0;
            }
            if (!groundCollisionState.isOnGround()) {
                d5 *= 0.6;
            }
            if (d5 > 0.0) {
                d4 += 0.08 * d5 / 3.0;
            }
            if (!blToWater) {
                d3 = 1.1;
            }
            horizontalVector = horizontalVector4.multiply(d3).maxLength((HorizontalVector)this.velocityBoosts.get(player2)).increaseLength(d4);
            if (EnvironmentBlockChecks.isInFlowingLiquid(blockPositionView)) {
                horizontalVector = horizontalVector.increaseLength(0.1);
            }
            if (flag3) {
                horizontalVector = horizontalVector.increaseLength(0.25);
            }
            if (flag4 && !flag5) {
                horizontalVector = horizontalVector.increaseLength(0.025);
            }
            horizontalVector = horizontalVector.maxLength(new HorizontalVector(0.05, 0.05)).increaseLength((double)value7 * 0.01);
            if (value != -1) {
                horizontalVector = horizontalVector.applySpeedEffect(value);
                this.cachedSpeedLevels.put(player2, value);
                this.cachedSpeedLevels.resetExpiration(player2);
            } else if (this.cachedSpeedLevels.containsKey(player2)) {
                horizontalVector = horizontalVector.applySpeedEffect((Integer)this.cachedSpeedLevels.get(player2));
            }
        } else if (groundCollisionState.isOnGround()) {
            boolean flag6 = EnvironmentBlockChecks.isStandingOnSoulSand(blockPositionView);
            boolean blOnIceOrSlab = EnvironmentBlockChecks.isNearSnowColumn(blockPositionView);
            boolean flag7 = EnvironmentBlockChecks.isInSnow(blockPositionView);
            Player player4 = playerMoveEvent.getPlayer();
            boolean flag8 = ItemUseAccess.isUsingItem(player4);
            boolean flag9 = playerMoveEvent.getPlayer().isSneaking();
            horizontalVector = horizontalVector3;
            horizontalVector = horizontalVector.increaseLength(0.01);
            if (value != -1) {
                horizontalVector = horizontalVector.applySpeedEffect(value);
                this.cachedSpeedLevels.put(player2, value);
                this.cachedSpeedLevels.resetExpiration(player2);
            } else {
                Integer intVal = (Integer)this.cachedSpeedLevels.get(player2);
                if (intVal != null) {
                    horizontalVector = horizontalVector.applySpeedEffect(intVal);
                }
            }
            if (this.checkSlownessEffect && value2 != -1) {
                horizontalVector = horizontalVector.applySlownessEffect(value2).maxLength(new HorizontalVector(0.05, 0.05));
            }
            horizontalVector = horizontalVector.clampNonNegative();
            if (flag8) {
                horizontalVector = horizontalVector.multiply(this.itemScalar);
                if (horizontalVector2.getLength() > horizontalVector.getLength() && horizontalVector2.getLength() < d && !this.itemUseGracePlayers.containsKey(player2)) {
                    horizontalVector = horizontalVector2;
                    this.itemUseGracePlayers.put(player2, false);
                }
            } else if (flag9) {
                horizontalVector = horizontalVector.multiply(this.sneakScalar);
            } else if (flag6) {
                horizontalVector = horizontalVector.multiply(this.soulSandScalar);
            }
            boolean flag10 = false;
            if (this.checkSprintDirection) {
                double d6;
                double d7;
                Location location = playerMoveEvent.getPlayer().getLocation();
                location.setDirection(new Vector(x, 0.0, z).normalize());
                double yaw = d7 = (double)location.getYaw();
                double d8 = MathUtilities.wrapDegrees(yaw);
                double yaw2 = d6 = d8 - (double)playerMoveEvent.getPlayer().getLocation().getYaw();
                if (Math.abs(MathUtilities.wrapDegrees(yaw2)) >= 65.0) {
                    flag10 = true;
                }
            }
            if (flag10 || playerMoveEvent.getPlayer().getFoodLevel() <= 6 || playerMoveEvent.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS)) {
                horizontalVector = horizontalVector.applySlowMovement();
            }
            if (flag) {
                this.iceBoostPlayers.put(player2, false);
                this.iceBoostPlayers.resetExpiration(player2);
            } else if (this.iceBoostPlayers.containsKey(player2) && horizontalVector.getLength() < 0.6) {
                horizontalVector = horizontalVector.applyJumpMovementModifiers(value, value2, playerMoveEvent.getPlayer().getWalkSpeed());
            }
            if (groundCollisionState2.isOnGround()) {
                if (flag) {
                    if (horizontalVector4.getLength() > horizontalVector.getLength()) {
                        horizontalVector = horizontalVector4;
                    }
                } else if (horizontalVector4.decayGroundFriction().getLength() > horizontalVector.getLength()) {
                    horizontalVector = horizontalVector4.decayGroundFriction();
                }
            } else if (horizontalVector4.decayAirFriction().getLength() > horizontalVector.getLength()) {
                horizontalVector = horizontalVector4.decayAirFriction();
            }
            if (!flag9 && !flag8 && !flag && this.airbornePlayers.contains(player2) && y >= 0.0) {
                horizontalVector = horizontalVector.maxLength(horizontalVector4).clampMinWalkSpeed().applyJumpMovementModifiers(value, value2, d);
                this.airbornePlayers.remove(player2);
            }
            if (!(groundCollisionState2.isOnGround() || flag8 || flag9)) {
                if (this.plugin.logManager.isDebugEnabled()) {
                    this.plugin.logManager.logPlayerDebug(playerMoveEvent.getPlayer(), "(Speed) takeoff [dy r:" + y + "]");
                }
                if (!groundCollisionState.isAirStep() && !groundCollisionState2.isAirStep()) {
                    value4 += 20;
                }
                horizontalVector = horizontalVector.addJumpBoost();
                if (groundCollisionState2.isAirStep()) {
                    horizontalVector = horizontalVector.applyJumpMovementModifiers(value, value2, d);
                }
                if (flag) {
                    horizontalVector = horizontalVector.maxLength(new HorizontalVector(0.42));
                }
                if (!(flag || blOnIceOrSlab || groundCollisionState2.isAirStep() || groundCollisionState.isAirStep())) {
                    this.offGroundTicks.put(player2, 0);
                }
            } else if (value3 != -1 && (value3 < 0 || value3 > 127 && value3 < 253)) {
                horizontalVector = horizontalVector.addJumpBoost();
            } else if (groundCollisionState2.isOnGround() && (groundCollisionState.isAirStep() || groundCollisionState2.isAirStep()) || flag7 || flag2 && y > 0.105) {
                if (groundCollisionState2.isAirStep()) {
                    horizontalVector = horizontalVector.addJumpBoost();
                }
                if (groundCollisionState.isAirStep()) {
                    this.airbornePlayers.add(player2);
                }
            }
            if (this.velocityBoosts.containsKey(player2)) {
                if (this.strictVelocityChecking) {
                    if (horizontalVector2.getLength() > horizontalVector.getLength() + this.threshold && horizontalVector.getLength() < this.velocityBoosts.getOrDefault(player2, horizontalVector).addLength(horizontalVector).getLength()) {
                        horizontalVector = this.velocityBoosts.getOrDefault(player2, horizontalVector).addJumpBoost().addLength(horizontalVector);
                        this.velocityBoosts.remove(player2);
                        this.offGroundTicks.remove(player2);
                    }
                } else if (horizontalVector2.getLength() > horizontalVector.getLength() + this.threshold) {
                    horizontalVector = ((HorizontalVector)this.velocityBoosts.get(player2)).addJumpBoost().addLength(horizontalVector).boostSprint().maxLength(horizontalVector);
                    this.offGroundTicks.remove(player2);
                }
            }
            if ((block.getType() == Material.PISTON_BASE || block.getType() == Material.PISTON_STICKY_BASE || block.getType() == Material.PISTON_MOVING_PIECE || block.getType() == Material.PISTON_EXTENSION) && horizontalVector.getLength() < HorizontalVector.ONE.getLength()) {
                horizontalVector = HorizontalVector.ONE;
            }
        } else {
            boolean flag11 = EnvironmentBlockChecks.isNearStairs(blockPositionView2);
            if (!(groundCollisionState2.isOnGround() || groundCollisionState.isOnGround() || groundCollisionState2.isAirStep() || groundCollisionState.isAirStep())) {
                value4 += 10;
            }
            if (this.offGroundTicks.containsKey(player2)) {
                this.offGroundTicks.put(player2, (Integer)this.offGroundTicks.get(playerMoveEvent.getPlayer()) + 1);
                if ((Integer)this.offGroundTicks.get(player2) == 1) {
                    horizontalVector = !flag && !flag2 && horizontalVector4.getLength() > horizontalVector3.getLength() + this.threshold ? horizontalVector4.applyAirDrag().clampMinWalkSpeed() : horizontalVector4.decayAirFriction().clampMinWalkSpeed();
                    this.offGroundTicks.remove(player2);
                } else {
                    horizontalVector = (HorizontalVector)this.lastAllowedSpeeds.get(player2);
                }
            } else {
                horizontalVector = horizontalVector4.decayAirFriction().clampMinWalkSpeed();
            }
            if (this.velocityBoosts.containsKey(player2)) {
                if (this.strictVelocityChecking) {
                    if (horizontalVector2.getLength() > horizontalVector.getLength() + this.threshold && horizontalVector.getLength() < this.velocityBoosts.getOrDefault(player2, horizontalVector).addLength(horizontalVector).getLength()) {
                        horizontalVector = this.velocityBoosts.getOrDefault(player2, horizontalVector).addLength(horizontalVector);
                        this.velocityBoosts.remove(player2);
                    }
                } else if (horizontalVector2.getLength() > horizontalVector.getLength() + this.threshold) {
                    horizontalVector = ((HorizontalVector)this.velocityBoosts.get(player2)).addLength(horizontalVector).boostSprint().maxLength(horizontalVector);
                }
            }
            if (flag11) {
                if (horizontalVector.getLength() < horizontalVector3.getLength()) {
                    horizontalVector = horizontalVector3;
                }
                horizontalVector.addJumpBoost().clampMinWalkSpeed();
            }
            this.airbornePlayers.add(playerMoveEvent.getPlayer());
        }
        if (value2 > 1) {
            horizontalVector = horizontalVector.increaseLength(0.01);
        }
        if (groundCollisionState.isOnGround() && groundCollisionState2.isOnGround() && !groundCollisionState.isAirStep() && !groundCollisionState2.isAirStep() && !flag2) {
            value4 += 10;
        }
        HorizontalVector horizontalVector5 = EntityPushCalculator.calculatePush(playerMoveEvent.getPlayer());
        horizontalVector = horizontalVector.increaseLength(horizontalVector5.getLength());
        this.lastAllowedSpeeds.put(player2, horizontalVector);
        if (this.plugin.logManager.isDebugEnabled()) {
            double d9;
            double d10;
            double d11;
            double d12 = d11 = y;
            double d13 = d10 = horizontalVector2.getLength();
            double d14 = d9 = horizontalVector.getLength();
            this.plugin.logManager.logPlayerDebug(playerMoveEvent.getPlayer(), "(Speed) fullmove [dy r:" + MathUtilities.formatNumber(d12) + " dxz r: " + MathUtilities.formatNumber(d13) + " l:" + MathUtilities.formatNumber(d14) + " g " + (Object)((Object)groundCollisionState) + (Object)((Object)groundCollisionState2) + "]");
        }
        if (horizontalVector.getLength() + this.threshold < horizontalVector2.getLength()) {
            int value8 = 100;
            long ticks4 = (int)(Math.abs(Math.pow(horizontalVector2.getLength() - horizontalVector.getLength(), 2.0)) * this.vlMultiplier) + value4;
            i = MathUtilities.clampToInt(ticks4, value8);
            if (this.recordViolation(player2.getUniqueId(), i, HackType.SPEED, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Speed: moved too quickly: %.3f blocks/tick (%.3f/%.3f) g:" + (Object)((Object)groundCollisionState) + (Object)((Object)groundCollisionState2), horizontalVector2.getLength(), horizontalVector.getLength(), y), ticks2) && (this.getViolationLevel(playerMoveEvent.getPlayer()) > this.minVl || i > this.minVl)) {
                boolean flag12 = false;
                boolean flag13 = false;
                double y3 = playerMoveEvent.getPlayer().getVelocity().getY();
                PlayerMoveEvent playerMoveEvent2 = playerMoveEvent;
                Player player7 = playerMoveEvent.getPlayer();
                this.plugin.setbackManager.applySetback(player7, playerMoveEvent2, y3, flag13, flag12);
            }
            this.lastMoveVectors.put(playerMoveEvent.getPlayer(), HorizontalVector.ZERO);
        } else {
            if (!this.vlDecayCooldowns.containsKey(player2.getUniqueId())) {
                this.decreaseViolationLevel(player2.getUniqueId(), 1);
            }
            this.lastMoveVectors.put(playerMoveEvent.getPlayer(), horizontalVector2);
        }
    }

    @Override
    public boolean recordViolation(UUID uUID, int i, HackType hackType, String str, long ticks) {
        long ticks2 = ticks ^ 0L;
        this.vlDecayCooldowns.put(uUID, 0);
        this.vlDecayCooldowns.resetExpiration(uUID);
        return super.recordViolation(uUID, i, hackType, str, ticks2);
    }

    private void resetToggleCounters() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.sneakToggleCounts.put(player, 0);
            this.itemUseToggleCounts.put(player, 0);
            this.decreaseViolationLevel(player.getUniqueId(), 1);
        }
    }

    private void decaySpeedViolations() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.decreaseViolationLevel(player.getUniqueId(), 1);
        }
    }

    static WeakHashMap getItemUseToggleCounts(Speed speed) {
        return speed.itemUseToggleCounts;
    }

    static int getMaxUseToggles(Speed speed) {
        return speed.maxUseToggles;
    }

    static ExpiringMap<Player, HorizontalVector> getVelocityBoosts(Speed speed) {
        return speed.velocityBoosts;
    }
}

