package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.konsolas.aac.AAC;
import me.konsolas.aac.api.HackType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FlyAbilitiesPacketListener
extends PacketAdapter {
    final Fly detector;

    public FlyAbilitiesPacketListener(Fly fly, Plugin plugin, PacketType[] packetTypeArray) {        super(plugin, packetTypeArray);

        this.detector = fly;
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        Player player;
        long ticks = 113218805911140L;
        if (!packetEvent.getPlayer().getAllowFlight() && packetEvent.getPlayer().getGameMode() != GameMode.CREATIVE && packetEvent.getPlayer().getGameMode() != GameMode.SPECTATOR && !((AAC)this.plugin).checkManager.isPlayerExempt(player = packetEvent.getPlayer())) {
            this.detector.recordViolation(packetEvent.getPlayer().getUniqueId(), 1000, HackType.FLY, packetEvent.getPlayer().getName() + " failed Fly: sent invalid ability packets", ticks);
        }
    }
}

