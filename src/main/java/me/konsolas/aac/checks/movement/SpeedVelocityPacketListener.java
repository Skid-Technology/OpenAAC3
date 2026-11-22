package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.konsolas.aac.compat.EntityVelocityPacket;
import me.konsolas.aac.util.HorizontalVector;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SpeedVelocityPacketListener
extends PacketAdapter {
    final Speed detector;

    public SpeedVelocityPacketListener(Speed speed, Plugin plugin, PacketType[] packetTypeArray) {        super(plugin, packetTypeArray);

        this.detector = speed;
    }

    public void onPacketSending(PacketEvent packetEvent) {
        PacketContainer packetContainer = packetEvent.getPacket();
        EntityVelocityPacket entityVelocityPacket = new EntityVelocityPacket(packetContainer);
        try {
            if (entityVelocityPacket.getEntityId() == packetEvent.getPlayer().getEntityId()) {
                HorizontalVector horizontalVector = new HorizontalVector(Math.abs(entityVelocityPacket.getVelocityX()), Math.abs(entityVelocityPacket.getVelocityZ()));
                Speed.getVelocityBoosts(this.detector).put(packetEvent.getPlayer(), horizontalVector);
                Player player = packetEvent.getPlayer();
                Speed.getVelocityBoosts(this.detector).resetExpiration(player);
            }
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            this.plugin.getLogger().warning("(Speed) Velocity packet was sent to a temporary player (" + packetEvent.getPlayer().getName() + "/" + packetEvent.getPlayer().getUniqueId() + ")");
        }
    }
}

