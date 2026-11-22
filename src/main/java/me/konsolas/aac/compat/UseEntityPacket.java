package me.konsolas.aac.compat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.util.Vector;

public class UseEntityPacket
extends PacketWrapper {
    public static final PacketType TYPE = PacketType.Play.Client.USE_ENTITY;

    public UseEntityPacket() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public UseEntityPacket(PacketContainer packetContainer) {
        super(packetContainer, TYPE);
    }

    public int getTargetEntityId() {
        return (Integer)this.handle.getIntegers().read(0);
    }

    public void setTargetEntityId(int i) {
        this.handle.getIntegers().write(0, i);
    }

    public EnumWrappers.EntityUseAction getAction() {
        return (EnumWrappers.EntityUseAction)this.handle.getEntityUseActions().read(0);
    }

    public void setAction(EnumWrappers.EntityUseAction entityUseAction) {
        this.handle.getEntityUseActions().write(0, entityUseAction);
    }

    public Vector getTargetVector() {
        return (Vector)this.handle.getVectors().read(0);
    }

    public void setTargetVector(Vector vector) {
        this.handle.getVectors().write(0, vector);
    }
}

