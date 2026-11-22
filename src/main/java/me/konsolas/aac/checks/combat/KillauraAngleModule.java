package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.MathUtilities;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class KillauraAngleModule
extends KillauraModule {
    private final PlayerSampleData angleSamples = new PlayerSampleData(4);

    public KillauraAngleModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
    }

    private static Vector directionToTarget(Location location, Location location2) {
        double x = location2.getX() - location.getX();
        double y = location2.getY() - location.getY();
        double z = location2.getZ() - location.getZ();
        double d = Math.sqrt(x * x + z * z);
        float f = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(-(Math.atan2(y, d) * 180.0 / Math.PI));
        return new Vector(f, f2, 0.0f);
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getEntity().getNearbyEntities(1.0, 3.0, 1.0).size() <= 2 && entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER && entityDamageByEntityEvent.getEntity() instanceof LivingEntity && entityDamageByEntityEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            double d;
            double d2;
            Player player = (Player)entityDamageByEntityEvent.getDamager();
            if (player.getAllowFlight()) {
                return;
            }
            LivingEntity livingEntity = (LivingEntity)entityDamageByEntityEvent.getEntity();
            Location location = livingEntity.getLocation().add(0.0, livingEntity.getEyeHeight(), 0.0);
            Location location2 = player.getLocation().add(0.0, player.getEyeHeight(), 0.0);
            Vector vector = new Vector(location2.getYaw(), location2.getPitch(), 0.0f);
            Vector vector2 = KillauraAngleModule.directionToTarget(location2, location);
            double x = d2 = vector.getX() - vector2.getX();
            double d3 = MathUtilities.wrapDegrees(x);
            double y = d = vector.getY() - vector2.getY();
            double d4 = MathUtilities.wrapDegrees(y);
            double x2 = Math.sqrt(Math.pow(player.getLocation().getX() - livingEntity.getLocation().getX(), 2.0) + Math.pow(player.getLocation().getZ() - livingEntity.getLocation().getZ(), 2.0));
            double d5 = location2.distance(location);
            double y2 = Math.abs(d3 * x2 * d5) + Math.abs(d4 * Math.abs(location.getY() - location2.getY()) * d5);
            this.angleSamples.addSample(player, y2);
            if (this.angleSamples.hasFullSampleData(player) && this.angleSamples.getMean(player) / 20.0 > 5.0) {
                this.reportViolation(player, "angle", "not aiming on target (s=" + this.angleSamples.getMean(player) + ")");
                this.angleSamples.getSampleData(player).clear();
            }
        }
    }
}

