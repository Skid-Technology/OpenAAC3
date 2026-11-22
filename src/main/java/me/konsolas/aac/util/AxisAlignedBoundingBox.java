package me.konsolas.aac.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class AxisAlignedBoundingBox {
    private final Vector minimum;
    private final Vector maximum;

    public AxisAlignedBoundingBox(Vector vector, double d, double d2) {
        this.minimum = new Vector(vector.getX() - d / 2.0, vector.getY(), vector.getZ() - d / 2.0);
        this.maximum = new Vector(vector.getX() + d / 2.0, vector.getY() + d2, vector.getZ() + d / 2.0);
    }

    public AxisAlignedBoundingBox(Vector vector, EntityDimensions entityDimensions) {
        this(vector, entityDimensions.getWidth(), entityDimensions.getHeight());
    }

    public AxisAlignedBoundingBox(Location location) {
        this.minimum = new Vector(location.getX(), location.getY(), location.getZ());
        this.maximum = new Vector(location.getX() + 1.0, location.getY() + 1.0, location.getZ() + 1.0);
    }

    public AxisAlignedBoundingBox(double d, double d2, double d3, double d4, double d5, double d6) {
        this.minimum = new Vector(d, d2, d3);
        this.maximum = new Vector(d4, d5, d6);
    }

    public AxisAlignedBoundingBox(Vector vector, double d) {
        this(vector.getX() - d, vector.getY() - d, vector.getZ() - d, vector.getX() + d, vector.getY() + d, vector.getZ() + d);
    }

    public AxisAlignedBoundingBox expandTowards(Vector vector) {
        return new AxisAlignedBoundingBox(this.minimum.getX() + (vector.getX() < 0.0 ? vector.getX() : 0.0), this.minimum.getY() + (vector.getY() < 0.0 ? vector.getY() : 0.0), this.minimum.getZ() + (vector.getZ() < 0.0 ? vector.getZ() : 0.0), this.maximum.getX() + (vector.getX() > 0.0 ? vector.getX() : 0.0), this.maximum.getY() + (vector.getY() > 0.0 ? vector.getY() : 0.0), this.maximum.getZ() + (vector.getZ() > 0.0 ? vector.getZ() : 0.0));
    }

    public AxisAlignedBoundingBox expand(Vector vector) {
        return new AxisAlignedBoundingBox(this.minimum.getX() - vector.getX(), this.minimum.getY() - vector.getY(), this.minimum.getZ() - vector.getZ(), this.maximum.getX() + vector.getX(), this.maximum.getY() + vector.getY(), this.maximum.getZ() + vector.getZ());
    }

    public AxisAlignedBoundingBox offset(Vector vector) {
        return new AxisAlignedBoundingBox(this.minimum.getX() + vector.getX(), this.minimum.getY() + vector.getY(), this.minimum.getZ() + vector.getZ(), this.maximum.getX() + vector.getX(), this.maximum.getY() + vector.getY(), this.maximum.getZ() + vector.getZ());
    }

    public double calculateYOffset(AxisAlignedBoundingBox axisAlignedBoundingBox) {
        if (axisAlignedBoundingBox.maximum.getX() > this.minimum.getX() && axisAlignedBoundingBox.minimum.getX() < this.maximum.getX() && axisAlignedBoundingBox.maximum.getZ() > this.minimum.getZ() && axisAlignedBoundingBox.minimum.getZ() < this.maximum.getZ()) {
            if (axisAlignedBoundingBox.maximum.getY() <= this.minimum.getY()) {
                return this.minimum.getY() - axisAlignedBoundingBox.maximum.getY();
            }
            if (axisAlignedBoundingBox.minimum.getY() >= this.maximum.getY()) {
                return this.maximum.getY() - axisAlignedBoundingBox.minimum.getY();
            }
            return 0.0;
        }
        return 0.0;
    }

    public boolean intersectsRay(Vector vector, Vector vector2) {
        double[] dArray = this.computeRayBounds(vector, vector2);
        return Math.min(Math.min(Math.max(dArray[0], dArray[1]), Math.max(dArray[2], dArray[3])), Math.max(dArray[4], dArray[5])) >= 0.0 && Math.max(Math.max(Math.min(dArray[0], dArray[1]), Math.min(dArray[2], dArray[3])), Math.min(dArray[4], dArray[5])) <= Math.min(Math.min(Math.max(dArray[0], dArray[1]), Math.max(dArray[2], dArray[3])), Math.max(dArray[4], dArray[5]));
    }

    public double rayTraceDistance(Vector vector, Vector vector2) {
        double[] dArray = this.computeRayBounds(vector, vector2);
        double d = Math.max(Math.max(Math.min(dArray[0], dArray[1]), Math.min(dArray[2], dArray[3])), Math.min(dArray[4], dArray[5]));
        double d2 = Math.min(Math.min(Math.max(dArray[0], dArray[1]), Math.max(dArray[2], dArray[3])), Math.max(dArray[4], dArray[5]));
        if (d2 < 0.0 || d > d2) {
            return Double.NaN;
        }
        return d;
    }

    private double[] computeRayBounds(Vector vector, Vector vector2) {
        Vector vector3 = new Vector(1.0 / vector2.getX(), 1.0 / vector2.getY(), 1.0 / vector2.getZ());
        double[] dArray = new double[]{(this.minimum.getX() - vector.getX()) * vector3.getX(), (this.maximum.getX() - vector.getX()) * vector3.getX(), (this.minimum.getY() - vector.getY()) * vector3.getY(), (this.maximum.getY() - vector.getY()) * vector3.getY(), (this.minimum.getZ() - vector.getZ()) * vector3.getZ(), (this.maximum.getZ() - vector.getZ()) * vector3.getZ()};
        return dArray;
    }

    public AxisAlignedBoundingBox union(AxisAlignedBoundingBox axisAlignedBoundingBox) {
        if (axisAlignedBoundingBox == null) {
            return this;
        }
        return new AxisAlignedBoundingBox(Math.min(this.minimum.getX(), axisAlignedBoundingBox.minimum.getX()), Math.min(this.minimum.getY(), axisAlignedBoundingBox.minimum.getY()), Math.min(this.minimum.getZ(), axisAlignedBoundingBox.minimum.getZ()), Math.max(this.maximum.getX(), axisAlignedBoundingBox.maximum.getX()), Math.max(this.maximum.getY(), axisAlignedBoundingBox.maximum.getY()), Math.max(this.maximum.getZ(), axisAlignedBoundingBox.maximum.getZ()));
    }

    public boolean intersects(AxisAlignedBoundingBox axisAlignedBoundingBox) {
        return this.minimum.getX() < axisAlignedBoundingBox.maximum.getX() && this.maximum.getX() > axisAlignedBoundingBox.minimum.getX() && this.minimum.getY() < axisAlignedBoundingBox.maximum.getY() && this.maximum.getY() > axisAlignedBoundingBox.minimum.getY() && this.minimum.getZ() < axisAlignedBoundingBox.maximum.getZ() && this.maximum.getZ() > axisAlignedBoundingBox.minimum.getZ();
    }

    public boolean contains(Vector vector) {
        return this.minimum.getX() < vector.getX() && this.maximum.getX() > vector.getX() && this.minimum.getY() < vector.getY() && this.maximum.getY() > vector.getY() && this.minimum.getZ() < vector.getZ() && this.maximum.getZ() > vector.getZ();
    }

    public Vector getMinimum() {
        return this.minimum.clone();
    }

    public Vector getMaximum() {
        return this.maximum.clone();
    }

    public String toString() {
        Vector vector;
        Vector vector2;
        Vector vector3 = vector2 = this.minimum;
        Vector vector4 = vector = this.maximum;
        return "[{" + MathUtilities.formatVector(vector3) + "},{" + MathUtilities.formatVector(vector4) + "}]";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AxisAlignedBoundingBox)) {
            return false;
        }
        AxisAlignedBoundingBox axisAlignedBoundingBox = (AxisAlignedBoundingBox)obj;
        return this.minimum.equals((Object)axisAlignedBoundingBox.minimum) && this.maximum.equals((Object)axisAlignedBoundingBox.maximum);
    }

    public int hashCode() {
        return this.minimum.hashCode() ^ this.maximum.hashCode();
    }
}

