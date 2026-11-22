package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.MathUtilities;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraYawModule
extends KillauraModule {
    private final PlayerSampleData yawSamples = new PlayerSampleData(8);
    private final Map lastMoveYaws = new HashMap();
    private final Map lastAttackTimes = new HashMap();

    public KillauraYawModule(AAC aAC, Consumer<KillauraViolation> consumer) {
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
        double d;
        double yaw = d = (double)(playerMoveEvent.getTo().getYaw() - playerMoveEvent.getFrom().getYaw());
        double d2 = Math.abs(MathUtilities.wrapDegrees(yaw));
        this.lastMoveYaws.put(playerMoveEvent.getPlayer().getUniqueId(), d2);
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER && entityDamageByEntityEvent.getEntity() instanceof LivingEntity && entityDamageByEntityEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            Player player = (Player)entityDamageByEntityEvent.getDamager();
            if (player.getAllowFlight()) {
                return;
            }
            this.lastAttackTimes.put(entityDamageByEntityEvent.getDamager().getUniqueId(), System.currentTimeMillis());
            if (!this.lastMoveYaws.containsKey(player.getUniqueId())) {
                return;
            }
            double d = (Double)this.lastMoveYaws.get(player.getUniqueId());
            this.yawSamples.addSample(player, d);
            if (this.yawSamples.hasFullSampleData(player)) {
                double d2 = 0.0;
                Iterator iterator = this.yawSamples.getSampleData(player).getSamples().iterator();
                while (iterator.hasNext()) {
                    double d3 = (Double)iterator.next();
                    if (!(d3 >= 27.0)) continue;
                    d2 += d3;
                }
                if (d2 > 60.0) {
                    this.addPlayerScore(player, d2 / 10.0);
                    if (this.getPlayerScore(player) > 10.0) {
                        this.reportViolation(player, "yaw", "snapping to attack targets (t=" + d2 + ")");
                    }
                } else {
                    this.setPlayerScore(player, this.getPlayerScore(player) * 0.75);
                }
                this.yawSamples.clearSamples(player);
            }
        }
    }
}

