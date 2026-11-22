package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraSwitchModule
extends KillauraModule {
    private final Map previousRotationBounds = new WeakHashMap();
    private final ExpiringMap recentTargets;
    private final TargetRotationTracker targetRotationTracker = new TargetRotationTracker();

    public KillauraSwitchModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 500L;
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        this.recentTargets = ExpiringMap.builder().expiration(ticks, timeUnit).expirationPolicy(expirationPolicy).build();
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        AimRotationBounds aimRotationBounds = (AimRotationBounds)this.previousRotationBounds.get(playerMoveEvent.getPlayer());
        Entity entity = (Entity)this.recentTargets.get(playerMoveEvent.getPlayer());
        if (aimRotationBounds != null && entity != null && playerMoveEvent.getFrom().getWorld().equals(entity.getWorld()) && playerMoveEvent.getFrom().distance(entity.getLocation()) < 6.0) {
            if (playerMoveEvent.getTo().getYaw() != playerMoveEvent.getFrom().getYaw() && aimRotationBounds.minimumYaw < (double)playerMoveEvent.getTo().getYaw() && (double)playerMoveEvent.getTo().getYaw() < aimRotationBounds.maximumYaw) {
                this.reportViolation(playerMoveEvent.getPlayer(), "switch", "aimbot-like yaw patterns");
            } else if (playerMoveEvent.getTo().getPitch() != playerMoveEvent.getFrom().getPitch() && aimRotationBounds.minimumPitch < (double)playerMoveEvent.getTo().getPitch() && (double)playerMoveEvent.getTo().getPitch() < aimRotationBounds.maximumPitch) {
                this.reportViolation(playerMoveEvent.getPlayer(), "switch", "aimbot-like pitch patterns");
            } else {
                this.setPlayerScore(playerMoveEvent.getPlayer(), this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.975);
            }
        }
        this.previousRotationBounds.put(playerMoveEvent.getPlayer(), new AimRotationBounds(this, playerMoveEvent.getFrom(), playerMoveEvent.getTo()));
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager() instanceof Player && entityDamageByEntityEvent.getEntity() instanceof LivingEntity && this.targetRotationTracker.hasTargetChangedAndMoved((Player)entityDamageByEntityEvent.getDamager(), (LivingEntity)entityDamageByEntityEvent.getEntity())) {
            this.recentTargets.put((Player)entityDamageByEntityEvent.getDamager(), entityDamageByEntityEvent.getEntity());
        }
    }
}

