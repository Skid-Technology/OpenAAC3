package me.konsolas.aac.checks.movement;

import java.util.Iterator;
import me.konsolas.aac.util.HorizontalVector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityPushCalculator {
    public static HorizontalVector calculatePush(Player player) {
        HorizontalVector horizontalVector = HorizontalVector.ZERO;
        Iterator iterator = player.getNearbyEntities(2.0, 1.8, 2.0).iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity)iterator.next();
            if (entity.equals(player.getVehicle())) continue;
            double x = player.getLocation().getX() - entity.getLocation().getX();
            double z = player.getLocation().getZ() - entity.getLocation().getZ();
            double d = Math.max(Math.abs(x), Math.abs(z));
            if (!(d > 0.01)) continue;
            d = Math.sqrt(d);
            x /= d;
            z /= d;
            double d2 = Math.min(1.0, 1.0 / d);
            x *= d2;
            z *= d2;
            x *= 0.05;
            z *= 0.05;
            horizontalVector = horizontalVector.add(new HorizontalVector(x *= 1.0, z *= 1.0));
        }
        return horizontalVector;
    }
}

