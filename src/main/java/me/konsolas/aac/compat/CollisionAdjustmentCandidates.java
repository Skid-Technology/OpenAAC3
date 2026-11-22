package me.konsolas.aac.compat;

import java.util.ArrayList;
import java.util.List;

public class CollisionAdjustmentCandidates {
    private final List candidates = new ArrayList();
    private double bestDistance = Double.MAX_VALUE;

    public CollisionAdjustmentCandidates() {
    }

    static List getCandidates(CollisionAdjustmentCandidates collisionAdjustmentCandidates) {
        return collisionAdjustmentCandidates.candidates;
    }

    static double setBestDistance(CollisionAdjustmentCandidates collisionAdjustmentCandidates, double d) {
        collisionAdjustmentCandidates.bestDistance = d;
        return collisionAdjustmentCandidates.bestDistance;
    }

    static double getBestDistance(CollisionAdjustmentCandidates collisionAdjustmentCandidates) {
        return collisionAdjustmentCandidates.bestDistance;
    }
}

