package me.konsolas.aac.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.konsolas.aac.AAC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityDimensions {
    private static final EntityDimensions defaultDimensions = new EntityDimensions(0.6, 20.0);
    private static final Map<EntityType, EntityDimensions> dimensionsByType = Collections.synchronizedMap(new HashMap<>());
    private static Field widthField;
    private static Field heightField;
    private final double width;
    private final double height;

    public EntityDimensions(double d, double d2) {
        this.width = d;
        this.height = d2;
    }

    public static EntityDimensions forEntity(Entity entity) {
        double d;
        double d2;
        Object obj;
        if (entity.getType() == EntityType.PLAYER) {
            return new EntityDimensions(0.6, 1.65);
        }
        if (dimensionsByType.containsKey(entity.getType())) {
            return dimensionsByType.get(entity.getType());
        }
        if (widthField == null || heightField == null) {
            try {
                Class<?> clazz = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".Entity");
                widthField = clazz.getDeclaredField("width");
                widthField.setAccessible(true);
                heightField = clazz.getDeclaredField("length");
                heightField.setAccessible(true);
            }
            catch (ClassNotFoundException | NoSuchFieldException reflectiveOperationException) {
                Bukkit.getLogger().warning("[AAC] Failed to pull entity hitboxes from NMS. Using default hitbox. (" + reflectiveOperationException.getMessage() + ")");
                return defaultDimensions;
            }
        }
        try {
            obj = entity.getClass().getMethod("getHandle", new Class[0]);
            ((Method)obj).setAccessible(true);
            Object obj2 = ((Method)obj).invoke(entity, new Object[0]);
            d2 = ((Float)widthField.get(obj2)).floatValue();
            d = ((Float)heightField.get(obj2)).floatValue();
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException reflectiveOperationException) {
            Bukkit.getLogger().warning("[AAC] Failed to pull entity hitboxes from NMS. Using default hitbox. (" + reflectiveOperationException.getMessage() + ")");
            return defaultDimensions;
        }
        EntityDimensions entityDimensions = new EntityDimensions(d2, d);
        dimensionsByType.put(entity.getType(), entityDimensions);
        return entityDimensions;
    }

    public EntityDimensions scale(double d) {
        return new EntityDimensions(this.width * d, this.height * d);
    }

    public EntityDimensions expand(double d, double d2) {
        return new EntityDimensions(this.width + d, this.height + d2);
    }

    public EntityDimensions max(EntityDimensions entityDimensions) {
        return new EntityDimensions(Math.max(this.width, entityDimensions.width), Math.max(this.height, entityDimensions.height));
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }
}

