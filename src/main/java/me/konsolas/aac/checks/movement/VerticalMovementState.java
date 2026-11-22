package me.konsolas.aac.checks.movement;

import org.bukkit.Location;

public class VerticalMovementState {
    public Location lastSafeLocation = null;
    public double expectedDeltaY = 0.0;
    public double lastDeltaY = 0.0;
    public boolean wasOnGround = false;
    public int fallStage = 0;
    public boolean placedBlockNearby;
    public boolean brokeBlockNearby;
    public long timestamp;

}

