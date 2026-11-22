package me.konsolas.aac.util;

import java.util.Locale;
import org.bukkit.util.Vector;

public class MathUtilities {
    public static boolean approximatelyEquals(double d, double d2, double d3) {
        if (d3 == 0.0) {
            return d == d2;
        }
        return d + d3 > d2 && d - d3 < d2;
    }

    public static double multiplyPow(double d, double d2, int i2) {
        for (int i = 0; i < i2; ++i) {
            d *= d2;
        }
        return d;
    }

    public static double wrapDegrees(double d) {
        d %= 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        }
        if (d < -180.0) {
            d += 360.0;
        }
        return d;
    }

    public static int clampToInt(long ticks, int i2) {
        if (ticks < 0L) {
            return i2;
        }
        if (ticks > (long)i2) {
            return i2;
        }
        return (int)ticks;
    }

    public static double computeKnockbackScale(double d) {
        d += 1.0;
        double result = -41.3571 + 49.6634 * d - 5.92749 * Math.pow(d, 2.0) + 0.242424 * Math.pow(d, 3.0);
        return Math.min(result, 99.9);
    }

    public static String formatVector(Vector vector) {
        return String.format(Locale.UK, "%.3f,%.3f,%.3f", vector.getX(), vector.getY(), vector.getZ());
    }

    public static double angleDifferenceAbs(double d, double d2) {
        double d3 = Math.abs(d - d2) % 360.0;
        return d3 > 180.0 ? 360.0 - d3 : d3;
    }

    public static double angleDifferenceSigned(double d, double d2) {
        double d3 = (d - d2) % 360.0;
        return d3 > 180.0 ? 360.0 - d3 : d3;
    }

    public static String formatNumber(double d) {
        return String.format(Locale.UK, "%.3f", d);
    }

}
