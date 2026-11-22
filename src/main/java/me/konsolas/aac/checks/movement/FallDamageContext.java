package me.konsolas.aac.checks.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FallDamageContext {
    private final Location location;
    private final boolean onGround;
    private final double currentY;
    private final double verticalVelocity;
    private final Player player;
    private final Double lastGroundY;
    final NoFall detector;

    public FallDamageContext(NoFall noFall, Location location, boolean flag, double d, double d2, Player player, Double doubleVal) {
        this.detector = noFall;
        this.location = location;
        this.onGround = flag;
        this.currentY = d;
        this.verticalVelocity = d2;
        this.player = player;
        this.lastGroundY = doubleVal;
    }

    static Location getLocation(FallDamageContext fallDamageContext) {
        return fallDamageContext.location;
    }

    static Player getPlayer(FallDamageContext fallDamageContext) {
        return fallDamageContext.player;
    }

    static boolean isOnGround(FallDamageContext fallDamageContext) {
        return fallDamageContext.onGround;
    }

    static double getVerticalVelocity(FallDamageContext fallDamageContext) {
        return fallDamageContext.verticalVelocity;
    }

    static double getCurrentY(FallDamageContext fallDamageContext) {
        return fallDamageContext.currentY;
    }

    static Double getLastGroundY(FallDamageContext fallDamageContext) {
        return fallDamageContext.lastGroundY;
    }
}

