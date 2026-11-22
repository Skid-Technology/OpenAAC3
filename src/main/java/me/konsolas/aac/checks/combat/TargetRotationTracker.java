package me.konsolas.aac.checks.combat;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TargetRotationTracker {
    private final Map<UUID, Float> previousYaw = new HashMap<>();
    private final Map<UUID, Vector> previousTargetPosition = new HashMap<>();
    private final Map<UUID, Integer> previousTargetId = new HashMap<>();
    private final boolean requireTargetDistance;

    public TargetRotationTracker(boolean flag) {
        this.requireTargetDistance = flag;
    }

    public TargetRotationTracker() {
        this(true);
    }

    public boolean hasTargetChangedAndMoved(Player player, LivingEntity livingEntity) {
        this.previousYaw.putIfAbsent(player.getUniqueId(), Float.valueOf(player.getLocation().getYaw()));
        this.previousTargetPosition.putIfAbsent(player.getUniqueId(), livingEntity.getLocation().toVector());
        this.previousTargetId.putIfAbsent(player.getUniqueId(), livingEntity.getEntityId());
        double yaw = player.getLocation().getYaw() - ((Float)this.previousYaw.get(player.getUniqueId())).floatValue();
        if (Math.abs(yaw) < 70.0) {
            return false;
        }
        if (((Vector)this.previousTargetPosition.get(player.getUniqueId())).distance(livingEntity.getLocation().toVector()) < 0.6) {
            return false;
        }
        if (livingEntity.getEntityId() != ((Integer)this.previousTargetId.get(player.getUniqueId())).intValue() && (!this.requireTargetDistance || livingEntity.getLocation().toVector().distance((Vector)this.previousTargetPosition.get(player.getUniqueId())) < 2.5)) {
            this.previousTargetId.put(player.getUniqueId(), livingEntity.getEntityId());
            this.previousTargetPosition.put(player.getUniqueId(), livingEntity.getLocation().toVector());
            this.previousYaw.put(player.getUniqueId(), Float.valueOf(player.getLocation().getYaw()));
            return false;
        }
        this.previousTargetId.put(player.getUniqueId(), livingEntity.getEntityId());
        this.previousTargetPosition.put(player.getUniqueId(), livingEntity.getLocation().toVector());
        this.previousYaw.put(player.getUniqueId(), Float.valueOf(player.getLocation().getYaw()));
        return true;
    }
}

