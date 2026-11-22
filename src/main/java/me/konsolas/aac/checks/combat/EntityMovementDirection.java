package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketContainer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.ServerVersion;
import org.bukkit.util.Vector;

public class EntityMovementDirection {
    private final Vector direction;

    public EntityMovementDirection(PacketContainer packetContainer) {
        double d;
        double d2;
        double d3;
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            d3 = ((Byte)packetContainer.getBytes().read(0)).byteValue();
            d2 = ((Byte)packetContainer.getBytes().read(1)).byteValue();
            d = ((Byte)packetContainer.getBytes().read(2)).byteValue();
        } else {
            d3 = ((Integer)packetContainer.getIntegers().read(1)).intValue();
            d2 = ((Integer)packetContainer.getIntegers().read(2)).intValue();
            d = ((Integer)packetContainer.getIntegers().read(3)).intValue();
        }
        this.direction = new Vector(d3, d2, d).normalize();
    }

    public Vector getDirection() {
        return this.direction;
    }

    static {
        if (null != null) {
        }
    }
}

