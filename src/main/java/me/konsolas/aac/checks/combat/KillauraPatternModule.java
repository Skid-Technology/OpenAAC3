package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.compat.NearbyTargetLookup;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class KillauraPatternModule
extends KillauraModule {
    private static final int[] NETWORK_TOPOLOGY = new int[]{2, 2, 1};
    private final Map<Player, TrainingSet> trainingSets = new WeakHashMap<>();
    private final Map<Player, Long> lastAttackTimes = new WeakHashMap<>();
    private final TargetRotationTracker targetRotationTracker = new TargetRotationTracker(false);
    private final Map<Player, Integer> validHitCounts = new WeakHashMap<>();
    private final Map<Player, Map.Entry<NeuralNetwork, TrainingSet>> trainedModels = new WeakHashMap<>();

    public KillauraPatternModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
    }

    private static double evaluateMeanError(NeuralNetwork neuralNetwork, TrainingSet trainingSet) {
        double d = 0.0;
        Iterator iterator = trainingSet.iterator();
        while (iterator.hasNext()) {
            TrainingSample trainingSample = (TrainingSample)iterator.next();
            double[] dArray = trainingSample.getInputs();
            double d2 = trainingSample.getExpectedOutputs()[0];
            double d3 = neuralNetwork.evaluate(dArray)[0];
            double d4 = Math.abs(d2 - d3);
            d += d4;
        }
        d /= (double)trainingSet.size();
        return d *= 180.0;
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        double d;
        long ticks;
        Player player;
        Player player2;
        Player player3 = player2 = (player = playerMoveEvent.getPlayer());
        LivingEntity livingEntity = NearbyTargetLookup.findClosestLivingEntity(player3);
        if (livingEntity == null) {
            this.trainingSets.remove(player);
            this.trainedModels.remove(player);
            this.validHitCounts.remove(player);
            return;
        }
        long ticks2 = ticks = this.lastAttackTimes.containsKey(player) ? (Long)this.lastAttackTimes.get(player) : 0L;
        if (System.currentTimeMillis() - ticks > 1250L) {
            this.trainingSets.remove(player);
            this.trainedModels.remove(player);
            this.validHitCounts.remove(player);
            return;
        }
        LivingEntity livingEntity2 = livingEntity;
        Player player4 = player;
        LivingEntity livingEntity3 = livingEntity2;
        Player player5 = player4;
        double d2 = NearbyTargetLookup.getYawDifferenceToTarget(player5, (Entity)livingEntity3) / 180.0;
        double d3 = player.getLocation().distance(livingEntity.getLocation()) / 6.0;
        double yaw = d = (double)(playerMoveEvent.getTo().getYaw() - playerMoveEvent.getFrom().getYaw());
        double d4 = Math.abs(MathUtilities.wrapDegrees(yaw));
        double d5 = d4 / 180.0;
        if (!this.trainingSets.containsKey(player) && !this.trainedModels.containsKey(player)) {
            this.trainingSets.put(player, new TrainingSet());
            this.validHitCounts.remove(player);
        }
        if (this.trainingSets.containsKey(player)) {
            TrainingSet trainingSet = (TrainingSet)this.trainingSets.get(player);
            trainingSet.addSample(new TrainingSample(new double[]{d2, d3}, new double[]{d5}));
            if (trainingSet.size() == 5) {
                Server server = Bukkit.getServer();
                server.getScheduler().runTaskAsynchronously((Plugin)this.getPlugin(), () -> this.trainPatternNetwork(trainingSet, player));
            }
        } else if (this.trainedModels.containsKey(player)) {
            Map.Entry entry = (Map.Entry)this.trainedModels.get(player);
            ((TrainingSet)entry.getValue()).addSample(new TrainingSample(new double[]{d2, d3}, new double[]{d5}));
            if (((TrainingSet)entry.getValue()).size() == 20) {
                this.trainedModels.remove(player);
                if (this.validHitCounts.getOrDefault(player, 0) < 1) {
                    return;
                }
                double d6 = KillauraPatternModule.evaluateMeanError((NeuralNetwork)entry.getKey(), (TrainingSet)entry.getValue());
                if (d6 <= 2.5) {
                    this.addPlayerScore(player, 1.0 + (2.5 - d6));
                    if (this.getPlayerScore(player) > 3.0) {
                        this.reportViolation(player, "pattern", "computer-like aiming patterns (d=" + d6 + ")");
                        this.setPlayerScore(player, this.getPlayerScore(player) - 1.0);
                    }
                } else {
                    this.setPlayerScore(player, this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.95);
                }
            }
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER) {
            if (((Player)entityDamageByEntityEvent.getDamager()).getAllowFlight()) {
                return;
            }
            if (entityDamageByEntityEvent.getEntity() instanceof LivingEntity) {
                this.lastAttackTimes.put((Player)entityDamageByEntityEvent.getDamager(), System.currentTimeMillis());
                if (this.targetRotationTracker.hasTargetChangedAndMoved((Player)entityDamageByEntityEvent.getDamager(), (LivingEntity)entityDamageByEntityEvent.getEntity())) {
                    this.validHitCounts.merge((Player)entityDamageByEntityEvent.getDamager(), 1, Integer::sum);
                }
            }
        }
    }

    private void trainPatternNetwork(TrainingSet trainingSet, Player player) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(2, NETWORK_TOPOLOGY);
        for (int i = 0; i < 10000; ++i) {
            for (int j = 0; j < trainingSet.size(); ++j) {
                TrainingSample trainingSample = trainingSet.getRandomSample();
                neuralNetwork.train(trainingSample.getInputs(), trainingSample.getExpectedOutputs(), 0.01, 0.1);
            }
        }
        this.trainedModels.put(player, new AbstractMap.SimpleImmutableEntry<NeuralNetwork, TrainingSet>(neuralNetwork, new TrainingSet()));
        this.trainingSets.remove(player);
    }
}

