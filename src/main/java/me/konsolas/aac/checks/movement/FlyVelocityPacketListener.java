package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.EntityVelocityPacket;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FlyVelocityPacketListener
extends PacketAdapter {
    final Fly detector;

    public FlyVelocityPacketListener(Fly fly, Plugin plugin, PacketType[] packetTypeArray) {        super(plugin, packetTypeArray);

        this.detector = fly;
    }

    public void onPacketSending(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            PacketContainer packetContainer = packetEvent.getPacket();
            EntityVelocityPacket entityVelocityPacket = new EntityVelocityPacket(packetContainer);
            try {
                if (entityVelocityPacket.getEntityId() == packetEvent.getPlayer().getEntityId() && ((AAC)this.plugin).setbackManager.getSetbackCheck(packetEvent.getPlayer()) == null) {
                    Fly.getExpectedVelocityMap(this.detector).remove(packetEvent.getPlayer());
                    Fly.getExpectedVelocityMap(this.detector).put(packetEvent.getPlayer(), entityVelocityPacket.getVelocityY());
                    Player player = packetEvent.getPlayer();
                    Fly.getExpectedVelocityMap(this.detector).resetExpiration(player);
                }
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                this.plugin.getLogger().warning("(Fly) Velocity packet was sent to a temporary player (" + packetEvent.getPlayer().getName() + "/" + packetEvent.getPlayer().getUniqueId() + ")");
            }
        }
    }
}

