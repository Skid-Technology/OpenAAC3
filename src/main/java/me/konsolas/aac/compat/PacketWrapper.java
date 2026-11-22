package me.konsolas.aac.compat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;

public abstract class PacketWrapper {
    protected PacketContainer handle;

    protected PacketWrapper(PacketContainer packetContainer, PacketType packetType) {
        if (packetContainer == null) {
            throw new IllegalArgumentException("Packet handle cannot be NULL.");
        }
        if (!Objects.equal((Object)packetContainer.getType(), (Object)packetType)) {
            throw new IllegalArgumentException(packetContainer.getHandle() + " is not a packet of type " + packetType);
        }
        this.handle = packetContainer;
    }

    public PacketContainer getHandle() {
        return this.handle;
    }

    public void sendPacket(Player player) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, this.getHandle());
        }
        catch (InvocationTargetException invocationTargetException) {
            throw new RuntimeException("Cannot send packet.", invocationTargetException);
        }
    }

    public void receivePacket(Player player) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(player, this.getHandle());
        }
        catch (Exception exception) {
            throw new RuntimeException("Cannot recieve packet.", exception);
        }
    }
}

