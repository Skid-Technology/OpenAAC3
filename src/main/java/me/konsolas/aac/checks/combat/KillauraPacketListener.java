package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

public class KillauraPacketListener
extends PacketAdapter {
    final Killaura detector;

    public KillauraPacketListener(Killaura killaura, Plugin plugin, PacketType[] packetTypeArray) {
        super(plugin, packetTypeArray);
        this.detector = killaura;
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        Killaura.getModules(this.detector).values().forEach(module -> module.onPacket(packetEvent));
    }

    public void onPacketSending(PacketEvent packetEvent) {
        Killaura.getModules(this.detector).values().forEach(module -> module.onPacket(packetEvent));
    }
}

