package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraAccelerationModule
extends KillauraModule {
    private final Map<Player, AimRotationDelta> lastRotationDeltas = new WeakHashMap<>();
    private final Map<Player, Long> lastAttackTimes = new WeakHashMap<>();
    private final PlayerSampleData yawSamples = new PlayerSampleData(100);
    private final PlayerSampleData pitchSamples = new PlayerSampleData(100);
    private final Map<Player, Integer> rotationStepCounts = new WeakHashMap<>();

    public KillauraAccelerationModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        Location location = playerMoveEvent.getTo();
        Location location2 = playerMoveEvent.getFrom();
        KillauraAccelerationModule killauraAccelerationModule = this;
        AimRotationDelta aimRotationDelta = new AimRotationDelta(killauraAccelerationModule, location2, location);
        if (!this.lastRotationDeltas.containsKey(playerMoveEvent.getPlayer())) {
            this.lastRotationDeltas.put(playerMoveEvent.getPlayer(), aimRotationDelta);
            return;
        }
        if (System.currentTimeMillis() - this.lastAttackTimes.getOrDefault(playerMoveEvent.getPlayer(), 0L) < 550L) {
            double d;
            DoubleSummaryStatistics doubleSummaryStatistics;
            AimRotationDelta aimRotationDelta2 = aimRotationDelta;
            AimRotationDelta aimRotationDelta3 = (AimRotationDelta)this.lastRotationDeltas.get(playerMoveEvent.getPlayer());
            KillauraAccelerationModule killauraAccelerationModule2 = this;
            AimRotationDelta aimRotationDelta4 = new AimRotationDelta(killauraAccelerationModule2, aimRotationDelta3, aimRotationDelta2);
            this.yawSamples.addSample(playerMoveEvent.getPlayer(), AimRotationDelta.getYawDelta(aimRotationDelta4));
            this.pitchSamples.addSample(playerMoveEvent.getPlayer(), AimRotationDelta.getPitchDelta(aimRotationDelta4));
            this.lastRotationDeltas.put(playerMoveEvent.getPlayer(), aimRotationDelta);
            int value = 0;
            if (this.pitchSamples.hasFullSampleData(playerMoveEvent.getPlayer())) {
                doubleSummaryStatistics = this.pitchSamples.getSampleData(playerMoveEvent.getPlayer()).getSamples().stream().mapToDouble(Math::abs).summaryStatistics();
                d = this.pitchSamples.getSampleData(playerMoveEvent.getPlayer()).getSamples().stream().mapToDouble(Math::abs).sorted().skip(this.pitchSamples.getSampleCapacity() / 2).findFirst().orElseThrow(AssertionError::new);
                if (doubleSummaryStatistics.getMax() > 25.0) {
                    value += 20;
                }
                if (d > 0.8 && doubleSummaryStatistics.getAverage() > d + 1.5) {
                    value += 10;
                }
            }
            if (this.yawSamples.hasFullSampleData(playerMoveEvent.getPlayer())) {
                doubleSummaryStatistics = this.yawSamples.getSampleData(playerMoveEvent.getPlayer()).getSamples().stream().mapToDouble(Math::abs).summaryStatistics();
                d = this.yawSamples.getSampleData(playerMoveEvent.getPlayer()).getSamples().stream().mapToDouble(Math::abs).sorted().skip(this.yawSamples.getSampleCapacity() / 2).findFirst().orElseThrow(AssertionError::new);
                if (doubleSummaryStatistics.getMax() > 45.0) {
                    value += 10;
                }
                if (d > 0.8 && doubleSummaryStatistics.getAverage() > d * 2.0) {
                    value += 35;
                }
            }
            int value2 = value;
            this.rotationStepCounts.compute(playerMoveEvent.getPlayer(), (arg_0, arg_1) -> KillauraAccelerationModule.incrementRotationStepCount(value2, arg_0, arg_1));
        } else {
            this.lastRotationDeltas.remove(playerMoveEvent.getPlayer());
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        block2: {
            Player player;
            block4: {
                block3: {
                    if (!(entityDamageByEntityEvent.getDamager() instanceof Player) || !(entityDamageByEntityEvent.getEntity() instanceof LivingEntity)) break block2;
                    if (((Player)entityDamageByEntityEvent.getDamager()).getAllowFlight()) {
                        return;
                    }
                    player = (Player)entityDamageByEntityEvent.getDamager();
                    if (System.currentTimeMillis() - this.lastAttackTimes.getOrDefault(player, 0L) >= 550L) break block3;
                    if (this.rotationStepCounts.getOrDefault(player, 0) <= 100) break block4;
                    this.addPlayerScore(player, (double)((Integer)this.rotationStepCounts.get(player)).intValue() / 10.0);
                    if (!(this.getPlayerScore(player) > 5.0)) break block4;
                    this.reportViolation(player, "accel", "abnormal aiming patterns");
                    this.setPlayerScore(player, this.getPlayerScore(player) * 0.6);
                    break block4;
                }
                this.setPlayerScore(player, this.getPlayerScore(player) * 0.95);
                for (int i = 0; i < 75; ++i) {
                    this.yawSamples.getSampleData(player).getSamples().pollLast();
                    this.pitchSamples.getSampleData(player).getSamples().pollLast();
                }
            }
            this.rotationStepCounts.remove(player);
            this.lastAttackTimes.put((Player)entityDamageByEntityEvent.getDamager(), System.currentTimeMillis());
        }
    }

    private static Integer incrementRotationStepCount(int i2, Player player, Integer intVal) {
        return intVal == null ? i2 : intVal + i2;
    }
}

