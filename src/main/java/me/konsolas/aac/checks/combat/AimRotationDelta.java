package me.konsolas.aac.checks.combat;

import me.konsolas.aac.util.MathUtilities;
import org.bukkit.Location;

public class AimRotationDelta {
    private final float pitchDelta;
    private final float yawDelta;
    final KillauraAccelerationModule module;

    public AimRotationDelta(KillauraAccelerationModule killauraAccelerationModule, Location location, Location location2) {
        double d;
        this.module = killauraAccelerationModule;
        double yaw = d = (double)(location2.getYaw() - location.getYaw());
        this.yawDelta = (float)MathUtilities.wrapDegrees(yaw);
        this.pitchDelta = location2.getPitch() - location.getPitch();
    }

    public AimRotationDelta(KillauraAccelerationModule killauraAccelerationModule, AimRotationDelta aimRotationDelta, AimRotationDelta aimRotationDelta2) {
        double d;
        this.module = killauraAccelerationModule;
        double d2 = d = (double)(aimRotationDelta2.yawDelta - aimRotationDelta.yawDelta);
        this.yawDelta = (float)MathUtilities.wrapDegrees(d2);
        this.pitchDelta = aimRotationDelta2.pitchDelta - aimRotationDelta.pitchDelta;
    }

    public String toString() {
        return "{" + this.pitchDelta + ", " + this.yawDelta + "}";
    }

    static float getYawDelta(AimRotationDelta aimRotationDelta) {
        return aimRotationDelta.yawDelta;
    }

    static float getPitchDelta(AimRotationDelta aimRotationDelta) {
        return aimRotationDelta.pitchDelta;
    }
}

