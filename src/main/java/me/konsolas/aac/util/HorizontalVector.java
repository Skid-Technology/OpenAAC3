package me.konsolas.aac.util;

public class HorizontalVector {
    public static final HorizontalVector ROUNDING_EPSILON = new HorizontalVector(Math.sqrt(0.001953125));
    public static final HorizontalVector ONE = new HorizontalVector(1.0, 1.0);
    public static final HorizontalVector ZERO = new HorizontalVector(0.0, 0.0);
    public static final HorizontalVector BASE_WALK = new HorizontalVector(0.21, 0.21);
    public static final HorizontalVector BASE_AIR = new HorizontalVector(0.1, 0.1);
    private final double x;
    private final double z;
    private final double length;

    public HorizontalVector(double d) {
        this(d, d);
    }

    public HorizontalVector(double d, double d2) {
        this.x = d;
        this.z = d2;
        this.length = Math.sqrt(d * d + d2 * d2);
    }

    public double getX() {
        return this.x;
    }

    public double getZ() {
        return this.z;
    }

    public HorizontalVector decayAirFriction() {
        return new HorizontalVector(this.x * 0.98, this.z * 0.98);
    }

    public HorizontalVector clampMinWalkSpeed() {
        return this.maxLength(BASE_WALK);
    }

    public HorizontalVector addJumpBoost() {
        return this.increaseLength(0.2);
    }

    public HorizontalVector applySpeedEffect(int i) {
        return this.multiply(1.0 + 0.2 * (double)(i + 1));
    }

    public HorizontalVector applySlownessEffect(int i) {
        return this.multiply(Math.max(1.0 - 0.1 * (double)(i + 1), 0.0));
    }

    public HorizontalVector boostSprint() {
        return this.multiply(1.2);
    }

    public HorizontalVector addLength(HorizontalVector horizontalVector) {
        return this.increaseLength(horizontalVector.getLength());
    }

    public HorizontalVector scaleKnockback() {
        return this.multiply(1.2);
    }

    public HorizontalVector decayGroundFriction() {
        return this.multiply(0.98);
    }

    public HorizontalVector applyJumpMovementModifiers(int i, int i2, double d) {
        double d2 = 0.0;
        if (d > 0.21 && d < 0.35) {
            d2 = 0.06;
        }
        return this.increaseLength(0.1 + (double)(i + 1) * 0.025 + d2);
    }

    public HorizontalVector applyAirDrag() {
        return this.multiply(0.6);
    }

    public HorizontalVector applyPotionDrag() {
        return this.multiply(0.8);
    }

    public HorizontalVector clampNonNegative() {
        return new HorizontalVector(Math.max(this.x, 0.0), Math.max(this.z, 0.0));
    }

    public HorizontalVector multiply(double d) {
        return new HorizontalVector(this.x * d, this.z * d);
    }

    public HorizontalVector add(HorizontalVector horizontalVector) {
        return new HorizontalVector(this.x + horizontalVector.x, this.z + horizontalVector.z);
    }

    public HorizontalVector increaseLength(double d) {
        if (this.getLength() == 0.0) {
            return new HorizontalVector(Math.sqrt(d * d / 2.0));
        }
        return this.multiply((this.getLength() + d) / this.getLength());
    }

    public HorizontalVector applySlowMovement() {
        return this.multiply(0.8);
    }

    public HorizontalVector maxLength(HorizontalVector horizontalVector) {
        if (horizontalVector == null || this.getLength() > horizontalVector.getLength()) {
            return this;
        }
        return horizontalVector;
    }

    public double getLength() {
        return this.length;
    }
}

