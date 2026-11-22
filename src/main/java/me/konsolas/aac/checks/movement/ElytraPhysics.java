package me.konsolas.aac.checks.movement;

import me.konsolas.aac.util.MathUtilities;
import org.bukkit.util.Vector;

public class ElytraPhysics {
    public static Vector predictVelocity(Vector vector, float f, float f2) {
        double d;
        f = Math.min(Math.max(f, -90.0f), 90.0f);
        Vector vector2 = vector.clone();
        Vector vector3 = ElytraPhysics.direction(f, f2);
        float f3 = f * ((float)Math.PI / 180);
        double x = Math.sqrt(vector3.getX() * vector3.getX() + vector3.getZ() * vector3.getZ());
        double x2 = Math.sqrt(vector2.getX() * vector2.getX() + vector2.getZ() * vector2.getZ());
        double d2 = vector3.length();
        float f4 = (float)Math.cos(f3);
        f4 = (float)((double)f4 * (double)f4 * Math.min(1.0, d2 / 0.4));
        vector2.setY(vector2.getY() + -0.08 + (double)f4 * 0.06);
        if (vector2.getY() < 0.0 && x > Vector.getEpsilon()) {
            d = vector2.getY() * -0.1 * (double)f4;
            vector2.setY(vector2.getY() + d);
            vector2.setX(vector2.getX() + vector3.getX() * d / x);
            vector2.setZ(vector2.getZ() + vector3.getZ() * d / x);
        }
        if ((double)f3 < 0.0) {
            d = x2 * -Math.sin(f3) * 0.04;
            vector2.setY(vector2.getY() + d * 3.2);
            vector2.setX(vector2.getX() - vector3.getX() * d / x);
            vector2.setZ(vector2.getZ() - vector3.getZ() * d / x);
        }
        if (x > Vector.getEpsilon()) {
            vector2.setX(vector2.getX() + (vector3.getX() / x * x2 - vector2.getX()) * 0.1);
            vector2.setZ(vector2.getZ() + (vector3.getZ() / x * x2 - vector2.getZ()) * 0.1);
        }
        vector2.setX(vector2.getX() * 0.99);
        vector2.setY(vector2.getY() * 0.98);
        vector2.setZ(vector2.getZ() * 0.99);
        return vector2;
    }

    private static Vector direction(float f, float f2) {
        double d = Math.cos((double)(-f2 * ((float)Math.PI / 180)) - Math.PI);
        double d2 = Math.sin((double)(-f2 * ((float)Math.PI / 180)) - Math.PI);
        double d3 = -Math.cos(-f * ((float)Math.PI / 180));
        double d4 = Math.sin(-f * ((float)Math.PI / 180));
        return new Vector(d2 * d3, d4, d * d3);
    }

    private static float[] angles(Vector vector) {
        double d;
        double d2;
        float[] fArray = new float[2];
        double x = vector.getX();
        double z = vector.getZ();
        if (x == 0.0 && z == 0.0) {
            fArray[0] = vector.getY() > 0.0 ? -90.0f : 90.0f;
            return fArray;
        }
        double d3 = Math.atan2(-x, z);
        double d4 = d2 = Math.toDegrees((d3 + Math.PI * 2) % (Math.PI * 2));
        fArray[1] = (float)MathUtilities.wrapDegrees(d4);
        double d5 = x * x;
        double d6 = z * z;
        double d7 = Math.sqrt(d5 + d6);
        double y = d = Math.toDegrees(Math.atan(-vector.getY() / d7));
        fArray[0] = (float)MathUtilities.wrapDegrees(y);
        return fArray;
    }

    public static Vector predictFromObservedVelocity(Vector vector, Vector vector2) {
        Vector vector3 = vector2.clone();
        vector3.setX(vector3.getX() / 0.99);
        vector3.setY(vector3.getY() / 0.98);
        vector3.setZ(vector3.getZ() / 0.99);
        vector3.setY(vector3.getY() + 0.08);
        vector3.subtract(vector).normalize();
        Vector vector4 = vector3;
        float[] fArray = ElytraPhysics.angles(vector4);
        return ElytraPhysics.predictVelocity(vector, fArray[0], fArray[1]);
    }
}

