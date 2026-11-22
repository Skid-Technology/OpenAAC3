package me.konsolas.aac.checks.movement;

import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.util.HorizontalVector;
import me.konsolas.aac.util.MathUtilities;

public class VerticalMovementPredictor {
    public static final VerticalMovementState DEFAULT_STATE = new VerticalMovementState();

    private static boolean isLessThanLimit(double d, double d2, double d3) {
        return d < d2 + d3;
    }

    private static boolean isGreaterThanMin(double d, double d2, double d3) {
        return d > d2 - d3;
    }

    private static boolean isApproximatelyEqual(double d, double d2, double d3) {
        double d4 = d3;
        double d5 = d2;
        double d6 = d;
        return MathUtilities.approximatelyEquals(d6, d5, d4);
    }

    public static double predictNextGravityMotion(double d) {
        d -= 0.08;
        if (Math.abs(d *= 0.98) < 0.005) {
            d = 0.0;
        }
        return d;
    }

    private static double predictLevitationMotion(double d, int i) {
        if (i > 127) {
            d = (d -= (double)(255 - i) * 0.005) < -((double)(255 - i) * 0.05) ? -((double)(255 - i) * 0.05) : d;
            d *= 0.98;
        } else {
            d = (d += (double)i * 0.005) > (double)i * 0.05 ? (double)i * 0.05 : d;
            d *= 0.98;
        }
        return d;
    }

    private static double scaleJumpBoost(double d) {
        return d * 1.5;
    }

    public static boolean isValidVerticalMovement(double d, double d2, boolean flag, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, boolean flag2, boolean flag3, boolean flag4, double d3, boolean flag5, boolean flag6, boolean flag7, boolean flag8, boolean flag9, boolean flag10, boolean flag11, boolean flag12, boolean flag13, boolean flag14, double d4, double d5, int i, int i2, VerticalMovementState verticalMovementState, VerticalMovementState verticalMovementState2) {
        double d6;
        double d7;
        double d8;
        double d9;
        double d10;
        double d11;
        double d12;
        block40: {
            block39: {
                double d13 = verticalMovementState.lastDeltaY;
                verticalMovementState2.lastDeltaY = d;
                d12 = VerticalMovementPredictor.predictNextGravityMotion(d13);
                double d14 = Math.max(i > 0 ? (double)i / 10.0 + 0.42 : 0.42, flag4 ? 0.65 : 0.0);
                if (i2 > 0) {
                    if (groundCollisionState.isOnGround() && d > 0.0 && d < 1.0) {
                        verticalMovementState2.fallStage = 0;
                        verticalMovementState2.expectedDeltaY = 1.0;
                        return true;
                    }
                    double d15 = VerticalMovementPredictor.predictLevitationMotion(d13, i2);
                    verticalMovementState2.fallStage = 1;
                    verticalMovementState2.expectedDeltaY = d15;
                    double d16 = d5;
                    double d17 = d15;
                    double d18 = d;
                    return VerticalMovementPredictor.isApproximatelyEqual(d18, d17, d16);
                }
                if (d13 < -0.1 && verticalMovementState.placedBlockNearby && d > 0.2) {
                    groundCollisionState = GroundCollisionState.TRUE_NORMAL;
                }
                if (verticalMovementState.brokeBlockNearby && d <= 0.0) {
                    return true;
                }
                if (flag6) {
                    d5 += 0.2;
                }
                if (flag11) {
                    if (d13 <= 0.0 && d > 0.0) {
                        double d19;
                        verticalMovementState2.fallStage = 3;
                        verticalMovementState2.expectedDeltaY = d19 = Math.max(Math.max(VerticalMovementPredictor.scaleJumpBoost(d3), d14), Math.max(VerticalMovementPredictor.scaleJumpBoost(-d13), 1.51));
                        return VerticalMovementPredictor.isLessThanLimit(d, d19, d5);
                    }
                    if (d < -d5 && d < d13 - 0.05 && VerticalMovementPredictor.isGreaterThanMin(d, -0.5, d5)) {
                        verticalMovementState2.fallStage = 4;
                        return true;
                    }
                    if (groundCollisionState.isOnGround() && Math.abs(d) < 0.1) {
                        verticalMovementState2.fallStage = 5;
                        return true;
                    }
                    if (d > 0.0 && d < 0.3 && VerticalMovementPredictor.isLessThanLimit(d, d12 * 1.2, d5)) {
                        verticalMovementState2.fallStage = 6;
                        return true;
                    }
                }
                if (flag3) {
                    if (d > d14) {
                        verticalMovementState2.fallStage = 7;
                        verticalMovementState2.lastDeltaY = d14;
                    } else {
                        verticalMovementState2.fallStage = 8;
                    }
                    return VerticalMovementPredictor.isLessThanLimit(d, d14 + 0.2, d5);
                }
                if (flag13) {
                    verticalMovementState2.fallStage = 9;
                    return VerticalMovementPredictor.isLessThanLimit(d, 1.125, d5);
                }
                if (flag12) {
                    verticalMovementState2.fallStage = 10;
                    return groundCollisionState.isOnGround() ? VerticalMovementPredictor.isLessThanLimit(d, 0.6, 0.0) : VerticalMovementPredictor.isLessThanLimit(d, 0.0, d5);
                }
                if (flag14) {
                    boolean flag15 = VerticalMovementPredictor.isLessThanLimit(d, Math.max(d12, 0.155), d5) && VerticalMovementPredictor.isGreaterThanMin(d, Math.min(d12, -0.2), d5);
                    verticalMovementState2.fallStage = 11;
                    if (!groundCollisionState.isOnGround()) {
                        return flag15;
                    }
                    if (flag15) {
                        return true;
                    }
                }
                if (groundCollisionState.isOnGround() || flag4) {
                    verticalMovementState2.expectedDeltaY = d14;
                    if (!(groundCollisionState2.isOnGround() || flag4 || flag7 || flag9)) {
                        double d20;
                        double d21;
                        double d22;
                        double d23;
                        double d24;
                        double d25;
                        double d26;
                        double d27;
                        double d28;
                        double d29 = d;
                        double d30 = d14;
                        double d31 = d5;
                        if (VerticalMovementPredictor.isApproximatelyEqual(d29, d30, d31)) {
                            verticalMovementState2.fallStage = 12;
                            return true;
                        }
                        if (VerticalMovementPredictor.isLessThanLimit(d, d14, d5) && VerticalMovementPredictor.isGreaterThanMin(d, d14 - 0.02, d5) && d4 <= 0.2) {
                            verticalMovementState2.lastDeltaY = 0.42;
                            verticalMovementState2.fallStage = 13;
                            return true;
                        }
                        if (d4 <= 0.1 && groundCollisionState.isAirStep() && groundCollisionState2.isAirStep()) {
                            verticalMovementState2.fallStage = 14;
                            double d32 = d;
                            double d33 = 0.333;
                            double d34 = d5;
                            if (VerticalMovementPredictor.isApproximatelyEqual(d32, d33, d34)) {
                                return true;
                            }
                        }
                        verticalMovementState2.fallStage = 15;
                        double d35 = d;
                        double d36 = 0.0;
                        double d37 = d5;
                        return VerticalMovementPredictor.isApproximatelyEqual(d35, d36, d37) || VerticalMovementPredictor.isApproximatelyEqual(d28 = d, d27 = -0.08, d26 = d5) || VerticalMovementPredictor.isApproximatelyEqual(d25 = d, d24 = -0.16, d23 = d5 + HorizontalVector.ROUNDING_EPSILON.getLength()) || VerticalMovementPredictor.isApproximatelyEqual(d22 = d, d21 = d12, d20 = d5) || flag8 && VerticalMovementPredictor.isLessThanLimit(d, d14, d5);
                    }
                    if (Math.abs(d + d13) < HorizontalVector.ROUNDING_EPSILON.getLength()) {
                        verticalMovementState2.fallStage = 16;
                        verticalMovementState2.lastDeltaY = 0.42;
                    } else {
                        verticalMovementState2.fallStage = 17;
                    }
                    return VerticalMovementPredictor.isLessThanLimit(d, d14, d5) && VerticalMovementPredictor.isGreaterThanMin(d, Math.min(d12, -0.2), d5);
                }
                if (flag9) {
                    double d38;
                    double d39;
                    double d40;
                    double d41;
                    double d42;
                    double d43;
                    double d44;
                    double d45;
                    double d46;
                    double d47;
                    double d48;
                    double d49;
                    double d50;
                    double d51;
                    double d52;
                    verticalMovementState2.wasOnGround = true;
                    if (!flag10 && !flag6) {
                        verticalMovementState2.fallStage = 18;
                        verticalMovementState2.expectedDeltaY = flag2 ? Math.max(d12, d14) : Math.max(flag5 ? 0.0 : 0.2, d12);
                        return VerticalMovementPredictor.isLessThanLimit(d, verticalMovementState2.expectedDeltaY, d5);
                    }
                    verticalMovementState2.fallStage = 19;
                    verticalMovementState2.expectedDeltaY = Math.max(d12, 0.2);
                    if (flag2) {
                        verticalMovementState2.fallStage = 20;
                        verticalMovementState2.expectedDeltaY = Math.max(Math.max(d14, d14 + d2), 0.1);
                        return VerticalMovementPredictor.isLessThanLimit(d, verticalMovementState2.expectedDeltaY, d5) && VerticalMovementPredictor.isGreaterThanMin(d, Math.min(d12, -0.6), d5);
                    }
                    if (groundCollisionState2.isOnGround() || d4 < 0.05 || flag8 || flag7) {
                        verticalMovementState2.fallStage = 22;
                        return VerticalMovementPredictor.isLessThanLimit(d, Math.max(d13 * 0.8 + 0.02, 0.34), d5) && VerticalMovementPredictor.isGreaterThanMin(d, Math.min(d13 * 0.8 - 0.8, -0.16), d5);
                    }
                    verticalMovementState2.fallStage = 23;
                    double d53 = d;
                    double d54 = d13 * 0.8 + 0.02;
                    double d55 = d5;
                    return VerticalMovementPredictor.isApproximatelyEqual(d53, d54, d55) || VerticalMovementPredictor.isApproximatelyEqual(d52 = d, d51 = d13 * 0.8 - 0.02, d50 = d5) || VerticalMovementPredictor.isApproximatelyEqual(d49 = d, d48 = d12, d47 = d5) || VerticalMovementPredictor.isApproximatelyEqual(d46 = d, d45 = d12 * 0.8, d44 = d5) || VerticalMovementPredictor.isApproximatelyEqual(d43 = d, d42 = d12 + 0.04, d41 = d5) || VerticalMovementPredictor.isApproximatelyEqual(d40 = d, d39 = -0.0376, d38 = d5);
                }
                if (verticalMovementState.wasOnGround) {
                    d5 += 0.3;
                }
                if (groundCollisionState2.isOnGround()) {
                    verticalMovementState2.expectedDeltaY = d12;
                    if (flag8 || flag7) {
                        verticalMovementState2.fallStage = 24;
                        return VerticalMovementPredictor.isGreaterThanMin(d, Math.min(VerticalMovementPredictor.predictNextGravityMotion(d12), -0.2), d5) && VerticalMovementPredictor.isLessThanLimit(d, Math.max(d12, d12 < -0.1 ? 0.4 : 0.0), d5);
                    }
                    verticalMovementState2.fallStage = 25;
                    return VerticalMovementPredictor.isGreaterThanMin(d, Math.min(VerticalMovementPredictor.predictNextGravityMotion(d12), 0.0), d5) && VerticalMovementPredictor.isLessThanLimit(d, Math.max(d12, d12 < -0.1 ? 0.4 : 0.0), d5);
                }
                verticalMovementState2.expectedDeltaY = d12;
                double d56 = d;
                double d57 = d12;
                double d58 = d5;
                if (VerticalMovementPredictor.isApproximatelyEqual(d56, d57, d58)) break block39;
                double d59 = d5;
                double d60 = d;
                double d61 = VerticalMovementPredictor.predictNextGravityMotion(d2);
                if (!VerticalMovementPredictor.isApproximatelyEqual(d60, d61, d59)) break block40;
            }
            verticalMovementState2.fallStage = 26;
            return true;
        }
        if (flag8) {
            verticalMovementState2.fallStage = 27;
            verticalMovementState2.expectedDeltaY = d12;
            return VerticalMovementPredictor.isLessThanLimit(d, Math.max(d12, -0.2), d5) && VerticalMovementPredictor.isGreaterThanMin(d, -0.5, d5);
        }
        double d62 = d12;
        double d63 = 0.0;
        double d64 = 0.05 + d5;
        if (MathUtilities.approximatelyEquals(d62, d63, d64)) {
            verticalMovementState2.fallStage = 29;
            verticalMovementState2.lastDeltaY = verticalMovementState2.expectedDeltaY = VerticalMovementPredictor.predictNextGravityMotion(verticalMovementState2.expectedDeltaY);
            double d65 = d5;
            double d66 = verticalMovementState2.expectedDeltaY + d12;
            double d67 = d;
            return VerticalMovementPredictor.isApproximatelyEqual(d67, d66, d65);
        }
        verticalMovementState2.fallStage = 30;
        if (flag && MathUtilities.approximatelyEquals(d11 = d, d10 = 0.404, d9 = d5) && MathUtilities.approximatelyEquals(d8 = d12, d7 = -0.2305, d6 = d5)) {
            verticalMovementState2.fallStage = 31;
            verticalMovementState2.lastDeltaY = 0.42;
            return true;
        }
        return false;
    }
}

