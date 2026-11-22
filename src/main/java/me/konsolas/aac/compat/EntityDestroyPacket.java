package me.konsolas.aac.compat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class EntityDestroyPacket
extends PacketWrapper {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    public EntityDestroyPacket() {
        this(new PacketContainer(TYPE));
        this.handle.getModifier().writeDefaults();
    }

    public EntityDestroyPacket(PacketContainer packetContainer) {
        super(packetContainer, TYPE);
    }

    public int getEntityCount() {
        return ((int[])this.handle.getIntegerArrays().read(0)).length;
    }

    public int[] getEntityIds() {
        return (int[])this.handle.getIntegerArrays().read(0);
    }

    public void setEntityIds(int[] nArray) {
        this.handle.getIntegerArrays().write(0, nArray);
    }
}

