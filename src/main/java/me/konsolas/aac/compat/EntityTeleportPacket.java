package me.konsolas.aac.compat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.konsolas.aac.AAC;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class EntityTeleportPacket
extends PacketWrapper {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_TELEPORT;

    public EntityTeleportPacket() {
        this(new PacketContainer(TYPE));
        this.handle.getModifier().writeDefaults();
    }

    public EntityTeleportPacket(PacketContainer packetContainer) {
        super(packetContainer, TYPE);
    }

    public int getEntityId() {
        return (Integer)this.handle.getIntegers().read(0);
    }

    public void setEntityId(int i) {
        this.handle.getIntegers().write(0, i);
    }

    public Entity getEntity(World world) {
        return (Entity)this.handle.getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent packetEvent) {
        return this.getEntity(packetEvent.getPlayer().getWorld());
    }

    public double getX() {
        return AAC.serverVersion.getVersion() == ServerVersion.VER_1_8 ? (double)((Integer)this.handle.getIntegers().read(1)).intValue() / 32.0 : (Double)this.handle.getDoubles().read(0);
    }

    public void setX(double d) {
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            this.handle.getIntegers().write(1, (int)Math.floor(d * 32.0));
        } else {
            this.handle.getDoubles().write(0, d);
        }
    }

    public double getY() {
        return AAC.serverVersion.getVersion() == ServerVersion.VER_1_8 ? (double)((Integer)this.handle.getIntegers().read(2)).intValue() / 32.0 : (Double)this.handle.getDoubles().read(1);
    }

    public void setY(double d) {
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            this.handle.getIntegers().write(2, (int)Math.floor(d * 32.0));
        } else {
            this.handle.getDoubles().write(1, d);
        }
    }

    public double getZ() {
        return AAC.serverVersion.getVersion() == ServerVersion.VER_1_8 ? (double)((Integer)this.handle.getIntegers().read(3)).intValue() / 32.0 : (Double)this.handle.getDoubles().read(2);
    }

    public void setZ(double d) {
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            this.handle.getIntegers().write(3, (int)Math.floor(d * 32.0));
        } else {
            this.handle.getDoubles().write(2, d);
        }
    }

    public float getYaw() {
        return (float)((Byte)this.handle.getBytes().read(0)).byteValue() * 360.0f / 256.0f;
    }

    public void setYaw(float f) {
        this.handle.getBytes().write(0, (byte)(f * 256.0f / 360.0f));
    }

    public float getPitch() {
        return (float)((Byte)this.handle.getBytes().read(1)).byteValue() * 360.0f / 256.0f;
    }

    public void setPitch(float f) {
        this.handle.getBytes().write(1, (byte)(f * 256.0f / 360.0f));
    }
}

