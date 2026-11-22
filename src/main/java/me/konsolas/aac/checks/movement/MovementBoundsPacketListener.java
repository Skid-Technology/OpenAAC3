package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Locale;
import me.konsolas.aac.checks.CheckManager;
import me.konsolas.aac.api.HackType;
import org.bukkit.plugin.Plugin;

public class MovementBoundsPacketListener
extends PacketAdapter {
    final CheckManager checkManager;

    public MovementBoundsPacketListener(CheckManager checkManager, Plugin plugin, ListenerPriority listenerPriority, PacketType[] packetTypeArray) {        super(plugin, listenerPriority, packetTypeArray);

        this.checkManager = checkManager;
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        long ticks = 113218805911140L;
        double d = (Double)packetEvent.getPacket().getDoubles().readSafely(0);
        double d2 = (Double)packetEvent.getPacket().getDoubles().readSafely(2);
        if (CheckManager.isOutOfBounds(this.checkManager, d, d2)) {
            try {
                this.checkManager.getDetector(HackType.SPEED).recordViolation(packetEvent.getPlayer().getUniqueId(), 100, HackType.SPEED, String.format(Locale.UK, packetEvent.getPlayer().getName() + " failed Speed: moved out of bounds (%.2f,%.2f)", d, d2), ticks);
            }
            catch (NullPointerException nullPointerException) {
                return;
            }
            PacketContainer packetContainer = packetEvent.getPacket().deepClone();
            packetContainer.getDoubles().writeSafely(0, packetEvent.getPlayer().getLocation().getX());
            packetContainer.getDoubles().writeSafely(1, packetEvent.getPlayer().getLocation().getY());
            packetContainer.getDoubles().writeSafely(2, packetEvent.getPlayer().getLocation().getZ());
            packetEvent.setCancelled(true);
            packetEvent.setPacket(packetContainer);
        }
    }
}

