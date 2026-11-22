package me.konsolas.aac.compat;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.util.EnumMap;
import me.konsolas.aac.AAC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class InvisibleEntityMetadata {
    private static final EnumMap metadataCache = new EnumMap(EntityType.class);

    public static WrappedDataWatcher getInvisibleMetadata(EntityType entityType, World world) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called from the main thread");
        }
        if (entityType == EntityType.PLAYER) {
            if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
                WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
                wrappedDataWatcher.setObject(0, (Object)32);
                return wrappedDataWatcher;
            }
            WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (Object)32);
            return wrappedDataWatcher;
        }
        if (metadataCache.get(entityType) != null) {
            return (WrappedDataWatcher)metadataCache.get(entityType);
        }
        Entity entity = world.spawnEntity(new Location(world, 0.0, 256.0, 0.0), entityType);
        WrappedDataWatcher wrappedDataWatcher = WrappedDataWatcher.getEntityWatcher((Entity)entity).deepClone();
        wrappedDataWatcher.setObject(0, (Object)((byte)((Byte)wrappedDataWatcher.getObject(0) | 0x20)), false);
        metadataCache.put(entityType, wrappedDataWatcher.deepClone());
        entity.remove();
        return wrappedDataWatcher;
    }
}

