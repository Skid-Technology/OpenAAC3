package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.konsolas.aac.AAC;
import me.konsolas.aac.api.HackType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SpeedItemUsePacketListener
extends PacketAdapter {
    final Speed detector;

    public SpeedItemUsePacketListener(Speed speed, Plugin plugin, PacketType[] packetTypeArray) {        super(plugin, packetTypeArray);

        this.detector = speed;
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        long ticks = 113218805911140L;
        AAC aAC = (AAC)this.plugin;
        Player player = packetEvent.getPlayer();
        if (aAC.checkManager.isPlayerExempt(player)) {
            return;
        }
        if (!AAC.serverVersion.hasConsumableOrWeapon(AAC.serverVersion.getHeldItems(packetEvent.getPlayer()))) {
            return;
        }
        if (!Speed.getItemUseToggleCounts(this.detector).containsKey(packetEvent.getPlayer())) {
            Speed.getItemUseToggleCounts(this.detector).put(packetEvent.getPlayer(), 1);
        } else {
            Speed.getItemUseToggleCounts(this.detector).put(packetEvent.getPlayer(), (Integer)Speed.getItemUseToggleCounts(this.detector).get(packetEvent.getPlayer()) + 1);
            if ((Integer)Speed.getItemUseToggleCounts(this.detector).get(packetEvent.getPlayer()) > Speed.getMaxUseToggles(this.detector)) {
                this.detector.recordViolation(packetEvent.getPlayer().getUniqueId(), 50, HackType.SPEED, packetEvent.getPlayer().getName() + " failed Speed: tried to toggle use too quickly (" + Speed.getItemUseToggleCounts(this.detector).get(packetEvent.getPlayer()) + " toggles/second)", ticks);
            }
        }
    }
}

