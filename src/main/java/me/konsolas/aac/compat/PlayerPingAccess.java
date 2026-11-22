package me.konsolas.aac.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

public class PlayerPingAccess {
    private static Method getHandleMethod;
    private static Field pingField;

    public static int getPing(Player player, long ticks) {
        try {
            if (getHandleMethod == null) {
                getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                getHandleMethod.setAccessible(true);
            }
            Object handle = getHandleMethod.invoke(player);
            if (pingField == null) {
                pingField = handle.getClass().getDeclaredField("ping");
                pingField.setAccessible(true);
            }
            int ping = pingField.getInt(handle);
            return ping > 0 ? ping : 0;
        }
        catch (Exception exception) {
            return 1;
        }
    }
}
