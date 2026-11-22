package me.konsolas.aac.compat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import java.util.Set;

public class PlayerPositionPacket
extends PacketWrapper {
    public static final PacketType TYPE;
    private static final Class teleportFlagsClass;

    public PlayerPositionPacket() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public PlayerPositionPacket(PacketContainer packetContainer) {
        super(packetContainer, TYPE);
    }

    public double getX() {
        return (Double)this.handle.getDoubles().read(0);
    }

    public void setX(double d) {
        this.handle.getDoubles().write(0, d);
    }

    public double getY() {
        return (Double)this.handle.getDoubles().read(1);
    }

    public void setY(double d) {
        this.handle.getDoubles().write(1, d);
    }

    public double getZ() {
        return (Double)this.handle.getDoubles().read(2);
    }

    public void setZ(double d) {
        this.handle.getDoubles().write(2, d);
    }

    public float getYaw() {
        return ((Float)this.handle.getFloat().read(0)).floatValue();
    }

    public void setYaw(float f) {
        this.handle.getFloat().write(0, Float.valueOf(f));
    }

    public float getPitch() {
        return ((Float)this.handle.getFloat().read(1)).floatValue();
    }

    public void setPitch(float f) {
        this.handle.getFloat().write(1, Float.valueOf(f));
    }

    private StructureModifier getTeleportFlagsModifier() {
        return this.handle.getModifier().withType(Set.class, BukkitConverters.getSetConverter((Class)teleportFlagsClass, (EquivalentConverter)EnumWrappers.getGenericConverter(RelativePositionFlag.class)));
    }

    public Set getTeleportFlags() {
        return (Set)this.getTeleportFlagsModifier().read(0);
    }

    public void setTeleportFlags(Set set) {
        this.getTeleportFlagsModifier().write(0, set);
    }

    static {
        TYPE = PacketType.Play.Server.POSITION;
        teleportFlagsClass = MinecraftReflection.getMinecraftClass((String)"EnumPlayerTeleportFlags", (String[])new String[]{"PacketPlayOutPosition$EnumPlayerTeleportFlags"});
    }
}

