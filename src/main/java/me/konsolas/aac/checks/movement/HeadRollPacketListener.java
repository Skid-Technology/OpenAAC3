package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.konsolas.aac.AAC;
import me.konsolas.aac.api.HackType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HeadRollPacketListener
extends PacketAdapter {
    final Derp detector;

    public HeadRollPacketListener(Derp derp, Plugin plugin, PacketType[] packetTypeArray) {        super(plugin, packetTypeArray);

        this.detector = derp;
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        long ticks = 113218805911140L;
        if (packetEvent.getPacketType() == PacketType.Play.Client.LOOK || packetEvent.getPacketType() == PacketType.Play.Client.POSITION_LOOK) {
            try {
                Player player = packetEvent.getPlayer();
                if (((AAC)this.plugin).checkManager.isPlayerExempt(player)) {
                    return;
                }
                float f = ((Float)packetEvent.getPacket().getFloat().read(1)).floatValue();
                if (f > 90.1f || f < -90.1f) {
                    packetEvent.setCancelled(true);
                    this.detector.recordViolation(packetEvent.getPlayer().getUniqueId(), 1, HackType.DERP, packetEvent.getPlayer().getName() + " failed HeadRoll: Moved with an illegal pitch (" + f + ")", ticks);
                }
            }
            catch (NullPointerException nullPointerException) {
            }
        }
    }
}

