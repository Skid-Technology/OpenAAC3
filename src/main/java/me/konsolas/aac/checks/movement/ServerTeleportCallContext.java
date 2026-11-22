package me.konsolas.aac.checks.movement;

public class ServerTeleportCallContext
extends SecurityManager {
    public boolean isCalledFromBukkit() {
        Class<?>[] classArray = this.getClassContext();
        int value = classArray.length;
        for (int i = 0; i < value; ++i) {
            Class<?> clazz = classArray[i];
            if (!(clazz.getName().startsWith("net.minecraft.server") ? clazz.getSimpleName().equals("EntityPlayer") || clazz.getSimpleName().equals("World") || clazz.getSimpleName().equals("LoginListener") || clazz.getSimpleName().equals("CommandTp") : clazz.getName().startsWith("org.bukkit.craftbukkit") && clazz.getSimpleName().equals("CraftPlayer"))) continue;
            return false;
        }
        return true;
    }
}

