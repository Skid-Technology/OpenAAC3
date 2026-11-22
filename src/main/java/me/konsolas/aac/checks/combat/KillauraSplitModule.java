package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.Iterator;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.util.EntityDimensions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class KillauraSplitModule
extends KillauraModule {
    private final PlayerSampleData normalPitchSamples = new PlayerSampleData(100);
    private final PlayerSampleData combatPitchSamples = new PlayerSampleData(100);

    public KillauraSplitModule(AAC aAC, Consumer<KillauraViolation> consumer) {
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
        if (playerMoveEvent.getFrom().getYaw() != playerMoveEvent.getTo().getYaw()) {
            double yaw = Math.hypot(playerMoveEvent.getFrom().getYaw(), playerMoveEvent.getTo().getYaw());
            Player player = playerMoveEvent.getPlayer();
            if (this.hasNearbyTarget(player)) {
                this.combatPitchSamples.addSample(playerMoveEvent.getPlayer(), yaw);
            } else {
                this.normalPitchSamples.addSample(playerMoveEvent.getPlayer(), yaw);
            }
            if (this.normalPitchSamples.hasFullSampleData(playerMoveEvent.getPlayer()) && this.combatPitchSamples.hasFullSampleData(playerMoveEvent.getPlayer())) {
                if (this.normalPitchSamples.getMean(playerMoveEvent.getPlayer()) > this.combatPitchSamples.getMean(playerMoveEvent.getPlayer())) {
                    this.addPlayerScore(playerMoveEvent.getPlayer(), 4.0);
                    if (this.getPlayerScore(playerMoveEvent.getPlayer()) > 5.0) {
                        this.reportViolation(playerMoveEvent.getPlayer(), "split", String.format("sharp changes in aiming speed (%.3f/%.3f)", this.normalPitchSamples.getMean(playerMoveEvent.getPlayer()), this.combatPitchSamples.getMean(playerMoveEvent.getPlayer())));
                    }
                    this.combatPitchSamples.clearSamples(playerMoveEvent.getPlayer());
                    this.normalPitchSamples.clearSamples(playerMoveEvent.getPlayer());
                } else {
                    this.setPlayerScore(playerMoveEvent.getPlayer(), this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.8);
                }
            }
        }
    }

    private boolean hasNearbyTarget(Player player) {
        Iterator iterator = player.getNearbyEntities(6.0, 6.0, 6.0).iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity)iterator.next();
            if (!(entity instanceof LivingEntity)) continue;
            Entity entity2 = entity;
            AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(entity.getLocation().toVector(), EntityDimensions.forEntity(entity2)).expand(new Vector(0.1, 0.1, 0.1));
            if (!axisAlignedBoundingBox.intersectsRay(player.getEyeLocation().toVector(), player.getLocation().getDirection())) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    }
}

