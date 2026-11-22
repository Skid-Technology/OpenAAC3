package me.konsolas.aac.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class AttackContext {
    private final Location attackerOrigin;
    private final Vector defenderVelocity;
    private final double initialDistance;
    private double lastDistance;
    private double maxDistance;
    private int awayTickCount;
    private boolean movingAway;
    private boolean approachedDefender;
    private boolean inWater;
    private int taskId;

    public AttackContext(Entity entity, Entity entity2, boolean flag) {
        if (!entity.getWorld().equals(entity2.getWorld())) {
            throw new IllegalStateException("Attacker and defender are in different worlds");
        }
        this.attackerOrigin = entity.getLocation();
        this.initialDistance = Math.sqrt(Math.pow(entity.getLocation().getX() - entity2.getLocation().getX(), 2.0) + Math.pow(entity.getLocation().getZ() - entity2.getLocation().getZ(), 2.0));
        this.defenderVelocity = entity2.getVelocity();
        this.maxDistance = this.initialDistance;
        this.awayTickCount = 0;
        this.movingAway = false;
        this.inWater = flag;
    }

    public Location getAttackerOrigin() {
        return this.attackerOrigin;
    }

    public double getMaxDistance() {
        return this.maxDistance;
    }

    public void setTaskId(int i) {
        this.taskId = i;
    }

    public void cancelTask() {
        Server server = Bukkit.getServer();
        server.getScheduler().cancelTask(this.taskId);
    }

    public void updateDistance(Location location) {
        double x = Math.sqrt(Math.pow(this.attackerOrigin.getX() - location.getX(), 2.0) + Math.pow(this.attackerOrigin.getZ() - location.getZ(), 2.0));
        if (x > this.maxDistance) {
            this.maxDistance = x;
        }
        if (x < this.lastDistance && x < 1.2) {
            this.approachedDefender = true;
        }
        boolean flag = x < this.lastDistance;
        this.updateDirection(flag);
        this.lastDistance = x;
    }

    private void updateDirection(boolean flag) {
        if (this.approachedDefender) {
            return;
        }
        if (flag) {
            if (this.movingAway) {
                this.approachedDefender = true;
            }
        } else if (this.movingAway) {
            ++this.awayTickCount;
        } else {
            this.movingAway = true;
        }
    }

    public int getAwayTickCount() {
        return this.awayTickCount;
    }

    public double getInitialDistance() {
        return this.initialDistance;
    }

    public Vector getDefenderVelocity() {
        return this.defenderVelocity;
    }

    public boolean isInWater() {
        return this.inWater;
    }
}

