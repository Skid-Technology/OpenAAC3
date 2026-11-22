package me.konsolas.aac.checks.movement;

import me.konsolas.aac.checks.world.VelocityPacketListener;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class VelocityResponseState {
    private final Vector expectedVelocity;
    private final Location origin;
    private double responseDistance;
    private Vector observedVelocity;
    private double horizontalResponse;
    private boolean responded;
    private boolean attackObserved = false;

    private VelocityResponseState(Location location, Vector vector) {
        this.origin = location;
        this.expectedVelocity = vector;
        this.responseDistance = 2.147483647E9;
        this.observedVelocity = new Vector(0, 0, 0);
    }

    public VelocityResponseState(Location location, Vector vector, VelocityPacketListener velocityPacketListener) {
        this(location, vector);
    }

    public static boolean setAttackObserved(VelocityResponseState velocityResponseState, boolean flag) {
        velocityResponseState.attackObserved = flag;
        return velocityResponseState.attackObserved;
    }

    public static boolean setResponded(VelocityResponseState velocityResponseState, boolean flag) {
        velocityResponseState.responded = flag;
        return velocityResponseState.responded;
    }

    public static Vector getExpectedVelocity(VelocityResponseState velocityResponseState) {
        return velocityResponseState.expectedVelocity;
    }

    public static boolean isResponded(VelocityResponseState velocityResponseState) {
        return velocityResponseState.responded;
    }

    public static double getResponseDistance(VelocityResponseState velocityResponseState) {
        return velocityResponseState.responseDistance;
    }

    public static Vector setObservedVelocity(VelocityResponseState velocityResponseState, Vector vector) {
        velocityResponseState.observedVelocity = vector;
        return velocityResponseState.observedVelocity;
    }

    public static double setResponseDistance(VelocityResponseState velocityResponseState, double d) {
        velocityResponseState.responseDistance = d;
        return velocityResponseState.responseDistance;
    }

    public static double setHorizontalResponse(VelocityResponseState velocityResponseState, double d) {
        velocityResponseState.horizontalResponse = d;
        return velocityResponseState.horizontalResponse;
    }

    public static boolean isAttackObserved(VelocityResponseState velocityResponseState) {
        return velocityResponseState.attackObserved;
    }

    public static Vector getObservedVelocity(VelocityResponseState velocityResponseState) {
        return velocityResponseState.observedVelocity;
    }

    public static double getHorizontalResponse(VelocityResponseState velocityResponseState) {
        return velocityResponseState.horizontalResponse;
    }

    public static Location getOrigin(VelocityResponseState velocityResponseState) {
        return velocityResponseState.origin;
    }
}

