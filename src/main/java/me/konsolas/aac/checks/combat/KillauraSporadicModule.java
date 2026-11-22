package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.NearbyTargetLookup;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraSporadicModule
extends KillauraModule {
    private final PlayerSampleData angleSamples = new PlayerSampleData(35);
    private final Map lastAttackTimes = new HashMap();
    private final Map lastTargetIds = new HashMap();

    public KillauraSporadicModule(AAC aAC, Consumer<KillauraViolation> consumer) {
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
        LivingEntity livingEntity;
        long ticks;
        Player player;
        Player player2 = player = playerMoveEvent.getPlayer();
        LivingEntity livingEntity2 = NearbyTargetLookup.findClosestLivingEntity(player2);
        long ticks2 = ticks = this.lastAttackTimes.containsKey(playerMoveEvent.getPlayer().getUniqueId()) ? (Long)this.lastAttackTimes.get(playerMoveEvent.getPlayer().getUniqueId()) : 0L;
        if (livingEntity2 == null || System.currentTimeMillis() - ticks > 520L) {
            this.lastAttackTimes.remove(playerMoveEvent.getPlayer().getUniqueId());
            this.angleSamples.clearSamples(playerMoveEvent.getPlayer());
            return;
        }
        LivingEntity livingEntity3 = livingEntity2;
        Player player3 = playerMoveEvent.getPlayer();
        Player player4 = player3;
        double d = NearbyTargetLookup.getYawDifferenceToTarget(player4, (Entity)(livingEntity = livingEntity3));
        if (d > 20.0) {
            this.angleSamples.addSample(playerMoveEvent.getPlayer(), d);
            if (this.angleSamples.hasFullSampleData(playerMoveEvent.getPlayer())) {
                this.addPlayerScore(playerMoveEvent.getPlayer(), this.angleSamples.getMean(playerMoveEvent.getPlayer()) / 10.0 + 3.0);
                if (this.getPlayerScore(playerMoveEvent.getPlayer()) > 3.0) {
                    this.reportViolation(playerMoveEvent.getPlayer(), "sporadic", "sporadic aiming patterns (y=" + this.angleSamples.getMean(playerMoveEvent.getPlayer()) + ")");
                }
                this.angleSamples.clearSamples(playerMoveEvent.getPlayer());
            }
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER) {
            if (((Player)entityDamageByEntityEvent.getDamager()).getAllowFlight()) {
                return;
            }
            if (this.lastTargetIds.containsKey(entityDamageByEntityEvent.getDamager().getUniqueId()) && ((Integer)this.lastTargetIds.get(entityDamageByEntityEvent.getDamager().getUniqueId())).intValue() != entityDamageByEntityEvent.getEntity().getEntityId()) {
                this.lastAttackTimes.remove(entityDamageByEntityEvent.getDamager().getUniqueId());
                this.lastTargetIds.remove(entityDamageByEntityEvent.getDamager().getUniqueId());
                return;
            }
            this.lastAttackTimes.put(entityDamageByEntityEvent.getDamager().getUniqueId(), System.currentTimeMillis());
            this.lastTargetIds.put(entityDamageByEntityEvent.getDamager().getUniqueId(), entityDamageByEntityEvent.getEntity().getEntityId());
            this.setPlayerScore((Player)entityDamageByEntityEvent.getDamager(), this.getPlayerScore((Player)entityDamageByEntityEvent.getDamager()) * 0.95);
        }
    }
}

