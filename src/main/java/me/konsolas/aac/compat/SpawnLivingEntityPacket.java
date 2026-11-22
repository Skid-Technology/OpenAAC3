package me.konsolas.aac.compat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.konsolas.aac.AAC;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class SpawnLivingEntityPacket
extends PacketWrapper {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING;
    private static PacketConstructor packetConstructor;

    public SpawnLivingEntityPacket() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public SpawnLivingEntityPacket(PacketContainer packetContainer) {
        super(packetContainer, TYPE);
    }

    public SpawnLivingEntityPacket(Entity entity) {
        super(SpawnLivingEntityPacket.createFromEntity(entity), TYPE);
    }

    private static PacketContainer createFromEntity(Entity entity) {
        if (packetConstructor == null) {
            packetConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, new Object[]{entity});
        }
        return packetConstructor.createPacket(new Object[]{entity});
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

    public EntityType getType() {
        return EntityType.fromId((int)((Integer)this.handle.getIntegers().read(1)));
    }

    public void setType(EntityType entityType) {
        this.handle.getIntegers().write(1, (int)entityType.getTypeId());
    }

    public double getX() {
        return AAC.serverVersion.getVersion() == ServerVersion.VER_1_8 ? (double)((Integer)this.handle.getIntegers().read(2)).intValue() / 32.0 : (Double)this.handle.getDoubles().read(0);
    }

    public void setX(double d) {
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            this.handle.getIntegers().write(2, ((int)Math.floor(d * 32.0)));
        } else {
            this.handle.getDoubles().write(0, d);
        }
    }

    public double getY() {
        return AAC.serverVersion.getVersion() == ServerVersion.VER_1_8 ? (double)((Integer)this.handle.getIntegers().read(3)).intValue() / 32.0 : (Double)this.handle.getDoubles().read(1);
    }

    public void setY(double d) {
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            this.handle.getIntegers().write(3, ((int)Math.floor(d * 32.0)));
        } else {
            this.handle.getDoubles().write(1, d);
        }
    }

    public double getZ() {
        return AAC.serverVersion.getVersion() == ServerVersion.VER_1_8 ? (double)((Integer)this.handle.getIntegers().read(4)).intValue() / 32.0 : (Double)this.handle.getDoubles().read(2);
    }

    public void setZ(double d) {
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            this.handle.getIntegers().write(4, ((int)Math.floor(d * 32.0)));
        } else {
            this.handle.getDoubles().write(2, d);
        }
    }

    public float getYaw() {
        return (float)((Byte)this.handle.getBytes().read(0)).byteValue() * 360.0f / 256.0f;
    }

    public void setYaw(float f) {
        this.handle.getBytes().write(0, ((byte)(f * 256.0f / 360.0f)));
    }

    public float getPitch() {
        return (float)((Byte)this.handle.getBytes().read(1)).byteValue() * 360.0f / 256.0f;
    }

    public void setPitch(float f) {
        this.handle.getBytes().write(1, ((byte)(f * 256.0f / 360.0f)));
    }

    public float getHeadPitch() {
        return (float)((Byte)this.handle.getBytes().read(2)).byteValue() * 360.0f / 256.0f;
    }

    public void setHeadPitch(float f) {
        this.handle.getBytes().write(2, ((byte)(f * 256.0f / 360.0f)));
    }

    public double getVelocityX() {
        return (double)((Integer)this.handle.getIntegers().read(5)).intValue() / 8000.0;
    }

    public void setVelocityX(double d) {
        this.handle.getIntegers().write(5, ((int)(d * 8000.0)));
    }

    public double getVelocityY() {
        return (double)((Integer)this.handle.getIntegers().read(6)).intValue() / 8000.0;
    }

    public void setVelocityY(double d) {
        this.handle.getIntegers().write(6, ((int)(d * 8000.0)));
    }

    public double getVelocityZ() {
        return (double)((Integer)this.handle.getIntegers().read(7)).intValue() / 8000.0;
    }

    public void setVelocityZ(double d) {
        this.handle.getIntegers().write(7, ((int)(d * 8000.0)));
    }

    public WrappedDataWatcher getMetadata() {
        return (WrappedDataWatcher)this.handle.getDataWatcherModifier().read(0);
    }

    public void setMetadata(WrappedDataWatcher wrappedDataWatcher) {
        this.handle.getDataWatcherModifier().write(0, wrappedDataWatcher);
    }
}

