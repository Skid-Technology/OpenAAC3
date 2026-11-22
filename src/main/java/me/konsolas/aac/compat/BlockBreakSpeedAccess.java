package me.konsolas.aac.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import me.konsolas.aac.AAC;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockBreakSpeedAccess {
    private static final Method craftPlayerGetHandle;
    private static final Method craftWorldGetHandle;
    private static final Constructor blockPositionConstructor;
    private static final Method worldGetTypeMethod;
    private static Method iBlockDataGetBlock;
    private static Method blockGetDamageMethod;
    private static Method iBlockDataGetDamageMethod;

    public static long getBreakTicks(Block block, Player player) {
        return (long)(1.0f / BlockBreakSpeedAccess.getBlockDamage(block, player));
    }

    private static float getBlockDamage(Block block, Player player) {
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            try {
                Object x = blockPositionConstructor.newInstance(block.getX(), block.getY(), block.getZ());
                Object obj = craftWorldGetHandle.invoke(block.getWorld(), new Object[0]);
                Object obj2 = worldGetTypeMethod.invoke(obj, x);
                Object obj3 = iBlockDataGetBlock.invoke(obj2, new Object[0]);
                return ((Float)blockGetDamageMethod.invoke(obj3, craftPlayerGetHandle.invoke(player, new Object[0]), obj, x)).floatValue();
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException reflectiveOperationException) {
                throw new RuntimeException(reflectiveOperationException);
            }
        }
        try {
            Object x2 = blockPositionConstructor.newInstance(block.getX(), block.getY(), block.getZ());
            Object obj4 = craftWorldGetHandle.invoke(block.getWorld(), new Object[0]);
            Object obj5 = worldGetTypeMethod.invoke(obj4, x2);
            return ((Float)iBlockDataGetDamageMethod.invoke(obj5, craftPlayerGetHandle.invoke(player, new Object[0]), obj4, x2)).floatValue();
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException reflectiveOperationException) {
            throw new RuntimeException(reflectiveOperationException);
        }
    }

    public static int getHasteLevel(Player player) {
        Iterator iterator = player.getActivePotionEffects().iterator();
        while (iterator.hasNext()) {
            PotionEffect potionEffect = (PotionEffect)iterator.next();
            if (!potionEffect.getType().equals((Object)PotionEffectType.FAST_DIGGING)) continue;
            return potionEffect.getAmplifier() + 1;
        }
        return 0;
    }

    static {
        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".BlockPosition");
            Class<?> clazz2 = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".World");
            craftWorldGetHandle = Class.forName("org.bukkit.craftbukkit." + AAC.serverVersion.getVersionPackage() + ".CraftWorld").getMethod("getHandle", new Class[0]);
            craftPlayerGetHandle = Class.forName("org.bukkit.craftbukkit." + AAC.serverVersion.getVersionPackage() + ".entity.CraftPlayer").getMethod("getHandle", new Class[0]);
            blockPositionConstructor = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
            worldGetTypeMethod = clazz2.getMethod("getType", clazz);
            if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
                iBlockDataGetBlock = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".IBlockData").getMethod("getBlock", new Class[0]);
                blockGetDamageMethod = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".Block").getMethod("getDamage", Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".EntityHuman"), clazz2, clazz);
            } else {
                iBlockDataGetDamageMethod = Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".IBlockData").getMethod("a", Class.forName("net.minecraft.server." + AAC.serverVersion.getVersionPackage() + ".EntityHuman"), clazz2, clazz);
            }
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}

