package me.konsolas.aac.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import me.konsolas.aac.AAC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ItemUseAccess {
    private static Method getHandleMethod;
    private static Method isUsingMethod1_8;
    private static Field activeItemField1_9;
    private static Field activeItemField1_10;
    private static Field activeItemField1_11;
    private static Object emptyItemStack;

    public static boolean isUsingItem(Player player) {
        Object obj;
        Object handle;
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called from the main thread");
        }
        if (emptyItemStack == null) {
            try {
                Class<?> clazz = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".ItemStack");
                obj = clazz.getField("a");
                emptyItemStack = ((Field)obj).get(null);
            }
            catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException reflectiveOperationException) {
                throw new RuntimeException("FAILED TO INITIALIZE 1.11 ITEM BINDINGS", reflectiveOperationException);
            }
        }
        try {
            if (getHandleMethod == null) {
                getHandleMethod = player.getClass().getDeclaredMethod("getHandle", new Class[0]);
                getHandleMethod.setAccessible(true);
            }
            handle = getHandleMethod.invoke(player, new Object[0]);
            if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
                if (isUsingMethod1_8 == null) {
                    isUsingMethod1_8 = handle.getClass().getSuperclass().getDeclaredMethod("bS", new Class[0]);
                    isUsingMethod1_8.setAccessible(true);
                }
                return (Boolean)isUsingMethod1_8.invoke(handle, new Object[0]);
            }
            if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_9) {
                if (activeItemField1_9 == null) {
                    activeItemField1_9 = handle.getClass().getSuperclass().getSuperclass().getDeclaredField("bn");
                    activeItemField1_9.setAccessible(true);
                }
                return (obj = activeItemField1_9.get(handle)) != null;
            }
            if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_10) {
                if (activeItemField1_10 == null) {
                    activeItemField1_10 = handle.getClass().getSuperclass().getSuperclass().getDeclaredField("bo");
                    activeItemField1_10.setAccessible(true);
                }
                return (obj = activeItemField1_10.get(handle)) != null;
            }
            if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_11) {
                if (activeItemField1_11 == null) {
                    activeItemField1_11 = handle.getClass().getSuperclass().getSuperclass().getDeclaredField("activeItem");
                    activeItemField1_11.setAccessible(true);
                }
                return !Objects.equals(obj = activeItemField1_11.get(handle), emptyItemStack);
            }
            if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_12) {
                if (activeItemField1_11 == null) {
                    activeItemField1_11 = handle.getClass().getSuperclass().getSuperclass().getDeclaredField("activeItem");
                    activeItemField1_11.setAccessible(true);
                }
                return !Objects.equals(obj = activeItemField1_11.get(handle), emptyItemStack);
            }
            throw new AssertionError();
        }
        catch (Exception exception) {
            Bukkit.getLogger().warning("[AAC] Failed to get item use state for " + player.getName() + " (Version " + AAC.serverVersion.getVersionPackage() + "):" + exception.getMessage());
            return false;
        }
    }
}

