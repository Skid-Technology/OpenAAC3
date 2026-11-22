package me.konsolas.aac.compat;

import java.util.Iterator;
import java.util.List;
import me.konsolas.aac.util.MathUtilities;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NearbyTargetLookup {

    public static LivingEntity findClosestLivingEntity(Player player) {
        Iterator iterator;
        double d = Double.MAX_VALUE;
        LivingEntity livingEntity = null;
        double d2 = 6.0;
        double d3 = 6.0;
        double d4 = 6.0;
        Player player2 = player;
        List list = player2.getNearbyEntities(d4, d3, d2);
        Iterator iterator2 = list.iterator();
        while ((iterator = iterator2).hasNext()) {
            Iterator iterator3 = iterator2;
            Entity entity = (Entity)iterator3.next();
            if (!(entity instanceof LivingEntity)) continue;
            try {
                Entity entity2 = entity;
                Player player3 = player;
                Location location = player3.getLocation();
                Location location2 = entity2.getLocation();
                double d5 = location2.distanceSquared(location);
                if (!(d5 < d)) continue;
                d = d5;
                livingEntity = (LivingEntity)entity;
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
        }
        return livingEntity;
    }

    public static double getYawDifferenceToTarget(Player player, Entity entity) {
        double d;
        Player player2 = player;
        Location location = player2.getLocation();
        double yaw = location.getYaw();
        Player player3 = player;
        Entity entity2 = entity;
        Location location2 = entity2.getLocation();
        Location location3 = player3.getLocation();
        Location location4 = location2;
        Location location5 = location3;
        double d2 = NearbyTargetLookup.getLookYawBetween(location5, location4);
        double d3 = d = yaw - d2;
        double d4 = MathUtilities.wrapDegrees(d3);
        return Math.abs(d4);
    }

    private static double getLookYawBetween(Location location, Location location2) {
        Location location3 = location2;
        Location location4 = location;
        double x = location3.getX() - location4.getX();
        Location location5 = location2;
        Location location6 = location;
        double z = location5.getZ() - location6.getZ();
        double d = x;
        double d2 = z;
        return (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
    }


}

