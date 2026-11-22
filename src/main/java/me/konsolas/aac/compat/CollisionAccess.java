package me.konsolas.aac.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.util.EntityDimensions;
import me.konsolas.aac.util.GroundCollisionState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class CollisionAccess {
    private static final Map blockBoxCache;
    private static Field minXField;
    private static Field minYField;
    private static Field minZField;
    private static Field maxXField;
    private static Field maxYField;
    private static Field maxZField;
    private static Method worldGetHandleMethod;
    private static Method worldGetCubesMethod;
    private static Method entityGetBoundingBoxMethod;
    private static Method aabbOffsetMethod;
    private static Constructor nmsAabbConstructor;
    private static final Vector GRAVITY_VECTOR;

    public static boolean isValidLocation(Location location) {
        if (location == null) {
            return false;
        }
        return !(!Double.isFinite(location.getX()) || !Double.isFinite(location.getY()) || !Double.isFinite(location.getZ()) || location.getY() > 32767.0 || location.getY() < -32767.0 || location.getX() >= 3.0E7 || location.getX() <= -3.0E7 || location.getZ() >= 3.0E7 || location.getZ() <= -3.0E7 || location.getX() >= 3.0E7 || location.getX() <= -3.0E7 || location.getZ() >= 3.0E7 || location.getZ() <= -3.0E7);
    }

    private static Object toNmsBoundingBox(AxisAlignedBoundingBox axisAlignedBoundingBox) {
        try {
            return nmsAabbConstructor.newInstance(axisAlignedBoundingBox.getMinimum().getX(), axisAlignedBoundingBox.getMinimum().getY(), axisAlignedBoundingBox.getMinimum().getZ(), axisAlignedBoundingBox.getMaximum().getX(), axisAlignedBoundingBox.getMaximum().getY(), axisAlignedBoundingBox.getMaximum().getZ());
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException reflectiveOperationException) {
            ReflectiveOperationException reflectiveOperationException2 = reflectiveOperationException;
            CollisionAccess.throwReflectionError(reflectiveOperationException2);
            return null;
        }
    }

    private static void throwReflectionError(Throwable throwable) {
        Bukkit.getLogger().info("[AAC] REFLECTION ERROR WHILST GROUND CHECKING: ");
        Bukkit.getLogger().info("[AAC] Translation: AAC is not compatible with your server version.");
        throw new IllegalStateException("REFLECTION ERROR", throwable);
    }

    public static boolean hasVerticalCollision(Player player, Vector vector, AxisAlignedBoundingBox axisAlignedBoundingBox, double d) {
        AxisAlignedBoundingBox axisAlignedBoundingBox2 = axisAlignedBoundingBox.expandTowards(vector);
        AxisAlignedBoundingBox axisAlignedBoundingBox3 = axisAlignedBoundingBox2;
        Player player2 = player;
        List<AxisAlignedBoundingBox> list = CollisionAccess.getCollidingBoxes((Entity)player2, axisAlignedBoundingBox3);
        if (list == null) {
            throw new IllegalStateException("collided AABB is null");
        }
        AxisAlignedBoundingBox axisAlignedBoundingBox4 = axisAlignedBoundingBox.offset(vector.add(new Vector(0.0, d, 0.0)));
        double d2 = 0.0;
        for (AxisAlignedBoundingBox axisAlignedBoundingBox5 : list) {
            d2 = Math.min(axisAlignedBoundingBox5.calculateYOffset(axisAlignedBoundingBox4), d2);
        }
        return d2 != 0.0;
    }

    public static List<AxisAlignedBoundingBox> getCollidingBoxes(Entity entity, AxisAlignedBoundingBox axisAlignedBoundingBox) {
        List<?> list;
        Object obj;
        Object obj2;
        Object obj3;
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called from the main thread");
        }
        if (!(CollisionAccess.isValidLocation(entity.getLocation()) && CollisionAccess.isValidLocation(axisAlignedBoundingBox.getMinimum().toLocation(entity.getWorld())) && CollisionAccess.isValidLocation(axisAlignedBoundingBox.getMaximum().toLocation(entity.getWorld())))) {
            return Collections.emptyList();
        }
        World world = entity.getLocation().getWorld();
        if (worldGetHandleMethod == null) {
            try {
                worldGetHandleMethod = world.getClass().getMethod("getHandle", new Class[0]);
                worldGetHandleMethod.setAccessible(true);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                NoSuchMethodException noSuchMethodException2 = noSuchMethodException;
                CollisionAccess.throwReflectionError(noSuchMethodException2);
                return Collections.emptyList();
            }
        }
        if (!blockBoxCache.containsKey(entity.getClass())) {
            try {
                obj3 = entity.getClass().getMethod("getHandle", new Class[0]);
                ((Method)obj3).setAccessible(true);
                blockBoxCache.put(entity.getClass(), obj3);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                Bukkit.getLogger().warning("[AAC] Received Player object without getHandle method");
                return Collections.emptyList();
            }
        }
        try {
            obj3 = worldGetHandleMethod.invoke(world, new Object[0]);
            obj2 = ((Method)blockBoxCache.get(entity.getClass())).invoke(entity, new Object[0]);
            AxisAlignedBoundingBox axisAlignedBoundingBox2 = axisAlignedBoundingBox;
            obj = CollisionAccess.toNmsBoundingBox(axisAlignedBoundingBox2);
        }
        catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            ReflectiveOperationException reflectiveOperationException2 = reflectiveOperationException;
            CollisionAccess.throwReflectionError(reflectiveOperationException2);
            return Collections.emptyList();
        }
        try {
            list = (List<?>)worldGetCubesMethod.invoke(obj3, obj2, obj);
        }
        catch (ClassCastException | IllegalAccessException | InvocationTargetException exception) {
            Exception exception2 = exception;
            CollisionAccess.throwReflectionError(exception2);
            return Collections.emptyList();
        }
        ArrayList<AxisAlignedBoundingBox> arrayList = new ArrayList<AxisAlignedBoundingBox>();
        try {
            for (Object e : list) {
                arrayList.add(new AxisAlignedBoundingBox(minXField.getDouble(e), minYField.getDouble(e), minZField.getDouble(e), maxXField.getDouble(e), maxYField.getDouble(e), maxZField.getDouble(e)));
            }
        }
        catch (IllegalAccessException illegalAccessException) {
            IllegalAccessException illegalAccessException2 = illegalAccessException;
            CollisionAccess.throwReflectionError(illegalAccessException2);
            return Collections.emptyList();
        }
        return arrayList;
    }

    public static GroundCollisionState checkGroundCollision(Entity entity, Location location, Location location2, Vector vector, GroundCollisionState groundCollisionState) {
        vector = vector.clone();
        location = location.clone();
        vector = vector.multiply(new Vector(2.0, vector.getY() < 0.0 ? 2.0 : 1.0, 2.0)).add(GRAVITY_VECTOR);
        Vector vector2 = vector.clone();
        Vector vector3 = vector;
        Location location3 = location;
        Entity entity2 = entity;
        CollisionQueryResult collisionQueryResult = CollisionVectorAdjuster.adjustVectorForCollisions(entity2, location3, vector3);
        boolean x = !collisionQueryResult.getCollisions().isEmpty() && collisionQueryResult.getAdjustment() < 0.6 + -GRAVITY_VECTOR.getY() && collisionQueryResult.getAdjustment() > 0.0 && (Math.abs(vector.getX()) < Math.abs(vector2.getX()) || Math.abs(vector.getZ()) < Math.abs(vector2.getZ()));
        boolean y = vector.getY() > vector2.getY() || groundCollisionState.isOnGround() && x;
        Entity entity3 = entity;
        AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(location2.toVector(), EntityDimensions.forEntity(entity3));
        boolean flag = collisionQueryResult.getCollisions().stream().anyMatch(arg_0 -> CollisionAccess.intersectsBoundingBox(axisAlignedBoundingBox, arg_0));
        return GroundCollisionState.of(y, x, flag);
    }

    public static GroundCollisionState getGroundCollisionState(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState) {
        if (!playerMoveEvent.getTo().getWorld().equals(playerMoveEvent.getFrom().getWorld())) {
            return GroundCollisionState.TRUE_NORMAL;
        }
        Player player = playerMoveEvent.getPlayer();
        Vector vector = playerMoveEvent.getTo().clone().subtract(playerMoveEvent.getFrom()).toVector();
        GroundCollisionState groundCollisionState2 = groundCollisionState;
        Vector vector2 = vector;
        Location location = playerMoveEvent.getTo();
        Location location2 = playerMoveEvent.getFrom();
        Player player2 = player;
        return CollisionAccess.checkGroundCollision((Entity)player2, location2, location, vector2, groundCollisionState2);
    }

    private static boolean intersectsBoundingBox(AxisAlignedBoundingBox axisAlignedBoundingBox, AxisAlignedBoundingBox axisAlignedBoundingBox2) {
        return axisAlignedBoundingBox2.intersects(axisAlignedBoundingBox);
    }

    static {
        blockBoxCache = new HashMap();
        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".Entity");
            Class<?> clazz2 = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".AxisAlignedBB");
            Class<?> clazz3 = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".World");
            minXField = clazz2.getDeclaredField("a");
            minYField = clazz2.getDeclaredField("b");
            minZField = clazz2.getDeclaredField("c");
            maxXField = clazz2.getDeclaredField("d");
            maxYField = clazz2.getDeclaredField("e");
            maxZField = clazz2.getDeclaredField("f");
            worldGetCubesMethod = clazz3.getDeclaredMethod("getCubes", clazz, clazz2);
            entityGetBoundingBoxMethod = clazz.getMethod("getBoundingBox", new Class[0]);
            aabbOffsetMethod = clazz2.getDeclaredMethod("a", Double.TYPE, Double.TYPE, Double.TYPE);
            CollisionAccess.nmsAabbConstructor = clazz2.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE);
            minXField.setAccessible(true);
            minYField.setAccessible(true);
            minZField.setAccessible(true);
            maxXField.setAccessible(true);
            maxYField.setAccessible(true);
            maxZField.setAccessible(true);
            worldGetCubesMethod.setAccessible(true);
            entityGetBoundingBoxMethod.setAccessible(true);
            aabbOffsetMethod.setAccessible(true);
            CollisionAccess.nmsAabbConstructor.setAccessible(true);
        }
        catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException reflectiveOperationException) {
            ReflectiveOperationException reflectiveOperationException2 = reflectiveOperationException;
            CollisionAccess.throwReflectionError(reflectiveOperationException2);
        }
        GRAVITY_VECTOR = new Vector(0.0, -0.08, 0.0);
    }
}

