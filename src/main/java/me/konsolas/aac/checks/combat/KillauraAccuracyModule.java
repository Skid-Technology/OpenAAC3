package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class KillauraAccuracyModule
extends KillauraModule {
    private final ExpiringMap<UUID, AccuracyState> accuracyStates;
    private final TargetRotationTracker targetRotationTracker = new TargetRotationTracker();

    public KillauraAccuracyModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 500L;
        ExpirationListener<UUID, AccuracyState> expirationListener = this::evaluateAccuracyWindow;
        this.accuracyStates = ExpiringMap.builder().expirationPolicy(expirationPolicy).expiration(ticks, timeUnit).expirationListener(expirationListener).build();
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketType.Play.Client.ARM_ANIMATION) {
            Optional.ofNullable(this.accuracyStates.get(packetEvent.getPlayer().getUniqueId())).ifPresent(KillauraAccuracyModule::incrementAttemptCount);
        } else if (this.isAttackPacket(packetEvent.getPacket())) {
            Server server = Bukkit.getServer();
            server.getScheduler().scheduleSyncDelayedTask((Plugin)this.getPlugin(), () -> this.checkAimDeviationViolation(packetEvent));
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    }

    private void checkAimDeviationViolation(PacketEvent packetEvent) {
        if (packetEvent.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
            LivingEntity livingEntity = null;
            int value = 0;
            try {
                for (Entity entity : packetEvent.getPlayer().getNearbyEntities(7.0, 7.0, 7.0)) {
                    if (entity.getEntityId() == ((Integer)packetEvent.getPacket().getIntegers().read(0)).intValue()) {
                        if (entity instanceof LivingEntity) {
                            livingEntity = (LivingEntity)entity;
                        }
                        break;
                    }
                    ++value;
                }
            }
            catch (NullPointerException nullPointerException) {
                return;
            }
            if (value > 5) {
                return;
            }
            if (livingEntity != null) {
                AccuracyState accuracyState = (AccuracyState)this.accuracyStates.get(packetEvent.getPlayer().getUniqueId());
                if (accuracyState != null) {
                    ++accuracyState.checkCount;
                    if (this.targetRotationTracker.hasTargetChangedAndMoved(packetEvent.getPlayer(), livingEntity)) {
                        ++accuracyState.accurateHitCount;
                    }
                    if (accuracyState.checkCount < 20) {
                        this.accuracyStates.resetExpiration(packetEvent.getPlayer().getUniqueId());
                    }
                } else {
                    this.accuracyStates.put(packetEvent.getPlayer().getUniqueId(), new AccuracyState(this, null));
                }
            }
        }
    }

    private static void incrementAttemptCount(AccuracyState accuracyState) {
        ++accuracyState.swingCount;
    }

    private void evaluateAccuracyWindow(UUID uUID, AccuracyState accuracyState) {
        Player player = Bukkit.getPlayer((UUID)uUID);
        if (player == null) {
            return;
        }
        if (accuracyState.accurateHitCount < accuracyState.checkCount / 10 + 1) {
            this.setPlayerScore(player, this.getPlayerScore(player) * 0.95);
            return;
        }
        if (accuracyState.checkCount < 10) {
            this.setPlayerScore(player, this.getPlayerScore(player) * 0.975);
            return;
        }
        double d = (double)accuracyState.checkCount / (double)accuracyState.swingCount;
        if (Double.isNaN(d)) {
            this.addPlayerScore(player, 5 * accuracyState.checkCount);
            return;
        }
        if (d >= 1.0) {
            this.addPlayerScore(player, 1.0 * (double)accuracyState.checkCount);
        } else if (d > 0.9) {
            this.addPlayerScore(player, d * (double)accuracyState.checkCount);
        } else {
            this.setPlayerScore(player, this.getPlayerScore(player) * 0.9);
        }
        if (this.getPlayerScore(player) > 5.0) {
            this.reportViolation(player, "accuracy", "attacking with high accuracy (r=" + String.format("%.3f", d) + "/" + accuracyState.checkCount + ")");
            this.setPlayerScore(player, this.getPlayerScore(player) * 0.6);
        }
    }
}

