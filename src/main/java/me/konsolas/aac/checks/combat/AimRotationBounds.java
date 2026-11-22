package me.konsolas.aac.checks.combat;

import org.bukkit.Location;

public class AimRotationBounds {
    double minimumYaw;
    double maximumYaw;
    double minimumPitch;
    double maximumPitch;
    final KillauraSwitchModule module;

    public AimRotationBounds(KillauraSwitchModule killauraSwitchModule, Location location, Location location2) {
        this.module = killauraSwitchModule;
        this.minimumYaw = location2.getYaw() > location.getYaw() ? (double)location.getYaw() + 5.0 : (double)location2.getYaw() + 5.0;
        this.maximumYaw = location.getYaw() > location2.getYaw() ? (double)location.getYaw() - 5.0 : (double)location2.getYaw() - 5.0;
        this.minimumPitch = location2.getPitch() > location.getPitch() ? (double)location.getPitch() + 5.0 : (double)location2.getPitch() + 5.0;
        this.maximumPitch = location.getPitch() > location2.getPitch() ? (double)location.getPitch() - 5.0 : (double)location2.getPitch() - 5.0;
    }
}

