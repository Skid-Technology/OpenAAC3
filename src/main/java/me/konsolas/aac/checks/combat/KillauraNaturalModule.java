package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.util.MathUtilities;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class KillauraNaturalModule
extends KillauraModule {
    private final WeakHashMap<Player, NaturalAimState> aimStates = new WeakHashMap<>();
    private final ExpiringMap<UUID, Boolean> recentCombatPlayers;
    private final TargetRotationTracker targetRotationTracker = new TargetRotationTracker();

    public KillauraNaturalModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 750L;
        this.recentCombatPlayers = ExpiringMap.builder().expirationPolicy(expirationPolicy).expiration(ticks, timeUnit).build();
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        if (!this.recentCombatPlayers.containsKey(playerMoveEvent.getPlayer().getUniqueId())) {
            this.aimStates.remove(playerMoveEvent.getPlayer());
        }
        this.aimStates.putIfAbsent(playerMoveEvent.getPlayer(), new NaturalAimState(this, null));
        NaturalAimState naturalAimState = (NaturalAimState)this.aimStates.get(playerMoveEvent.getPlayer());
        Vector vector = playerMoveEvent.getFrom().getDirection().normalize();
        LivingEntity livingEntity = this.findTargetInView(playerMoveEvent.getPlayer(), vector);
        if (livingEntity != null) {
            boolean flag;
            double d;
            double d2 = d = this.vectorToYaw(this.vectorBetween(playerMoveEvent.getPlayer().getLocation(), livingEntity.getLocation()));
            double yaw = playerMoveEvent.getFrom().getYaw();
            double d3 = d2;
            double d4 = yaw;
            double d5 = MathUtilities.angleDifferenceAbs(d4, d3);
            double d6 = d;
            double yaw2 = playerMoveEvent.getTo().getYaw();
            double d7 = d6;
            double d8 = yaw2;
            double d9 = MathUtilities.angleDifferenceAbs(d8, d7);
            double yaw3 = playerMoveEvent.getFrom().getYaw();
            double yaw4 = playerMoveEvent.getTo().getYaw();
            double d10 = yaw3;
            double d11 = yaw4;
            double pitch = playerMoveEvent.getFrom().getPitch();
            double pitch2 = playerMoveEvent.getTo().getPitch();
            double d12 = pitch;
            double d13 = pitch2;
            double d14 = Math.sqrt(Math.pow(MathUtilities.angleDifferenceAbs(d11, d10), 2.0) + Math.pow(MathUtilities.angleDifferenceAbs(d13, d12), 2.0));
            boolean flag2 = flag = d5 > d9;
            if (naturalAimState.switchedOffTarget) {
                if (flag) {
                    if (naturalAimState.hitSampleCount <= 5 && naturalAimState.hitDeltaAvg < (double)naturalAimState.hitSampleCount / 2.0) {
                        this.setPlayerScore(playerMoveEvent.getPlayer(), this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.8 - 1.0);
                    } else if (naturalAimState.hitSampleCount > 10 && naturalAimState.missedSampleCount < naturalAimState.hitSampleCount - 6 && naturalAimState.hitDeltaAvg > 1.8 && naturalAimState.streakCount > 0) {
                        this.addPlayerScore(playerMoveEvent.getPlayer(), 2 + (naturalAimState.hitSampleCount - naturalAimState.missedSampleCount));
                    } else if (d5 < 17.5 && naturalAimState.hitSampleCount > 6 && naturalAimState.streakCount > 0) {
                        this.addPlayerScore(playerMoveEvent.getPlayer(), (double)(naturalAimState.hitSampleCount * naturalAimState.hitSampleCount) / 4.0);
                    } else {
                        this.setPlayerScore(playerMoveEvent.getPlayer(), this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.9 - 0.5);
                    }
                    if (this.getPlayerScore(playerMoveEvent.getPlayer()) > 20.0) {
                        this.reportViolation(playerMoveEvent.getPlayer(), "natural", "unnatural aiming patterns (" + naturalAimState + ")");
                        this.setPlayerScore(playerMoveEvent.getPlayer(), this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.5);
                    }
                    this.aimStates.put(playerMoveEvent.getPlayer(), new NaturalAimState(this, null));
                } else {
                    naturalAimState.missedDeltaAvg = naturalAimState.missedDeltaAvg != 0.0 ? (naturalAimState.missedDeltaAvg + d14) / 2.0 : d14;
                    ++naturalAimState.missedSampleCount;
                }
            } else if (flag) {
                naturalAimState.hitDeltaAvg = naturalAimState.hitDeltaAvg != 0.0 ? (naturalAimState.hitDeltaAvg + d14) / 2.0 : d14;
                ++naturalAimState.hitSampleCount;
            } else {
                naturalAimState.switchedOffTarget = true;
            }
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager() instanceof Player && entityDamageByEntityEvent.getEntity() instanceof LivingEntity) {
            Player player = (Player)entityDamageByEntityEvent.getDamager();
            this.recentCombatPlayers.put(player.getUniqueId(), Boolean.TRUE);
            this.recentCombatPlayers.resetExpiration(player.getUniqueId());
            if (this.targetRotationTracker.hasTargetChangedAndMoved(player, (LivingEntity)entityDamageByEntityEvent.getEntity())) {
                Optional.ofNullable(this.aimStates.get(player)).ifPresent(KillauraNaturalModule::markAttackSampled);
            }
        }
    }

    private LivingEntity findTargetInView(Player player, Vector vector) {
        LivingEntity livingEntity = null;
        double d = Double.MAX_VALUE;
        for (Entity entity : player.getNearbyEntities(5.0, 5.0, 5.0)) {
            if (!(entity instanceof LivingEntity)) continue;
            LivingEntity livingEntity2 = (LivingEntity)entity;
            if (livingEntity == null) {
                livingEntity = (LivingEntity)entity;
                continue;
            }
            double d2 = this.vectorBetween(player.getLocation(), livingEntity2.getLocation()).distanceSquared(vector);
            if (!(d2 < d)) continue;
            d = d2;
            livingEntity = livingEntity2;
        }
        return livingEntity;
    }

    private Vector vectorBetween(Location location, Location location2) {
        return location2.toVector().subtract(location.toVector());
    }

    private double vectorToYaw(Vector vector) {
        return -Math.toDegrees(Math.atan2(vector.getX(), vector.getZ()));
    }

    private static void markAttackSampled(NaturalAimState naturalAimState) {
        ++naturalAimState.streakCount;
    }
}

