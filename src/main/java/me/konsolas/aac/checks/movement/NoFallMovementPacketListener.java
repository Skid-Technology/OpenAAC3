package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.konsolas.aac.AAC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NoFallMovementPacketListener
extends PacketAdapter {
    final NoFall detector;

    public NoFallMovementPacketListener(NoFall noFall, Plugin plugin, PacketType[] packetTypeArray) {        super(plugin, packetTypeArray);

        this.detector = noFall;
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        if (packetEvent.isCancelled()) {
            return;
        }
        try {
            if (packetEvent.getPlayer().getGameMode() == GameMode.CREATIVE || packetEvent.getPlayer().getGameMode() == GameMode.SPECTATOR || packetEvent.getPlayer().getAllowFlight() || packetEvent.getPlayer().getVehicle() != null) {
                return;
            }
            Player player = packetEvent.getPlayer();
            if (((AAC)this.plugin).checkManager.isPlayerExempt(player)) {
                return;
            }
            PacketContainer packetContainer = packetEvent.getPacket();
            Location location = packetEvent.getPlayer().getLocation();
            if (packetEvent.getPacketType() == PacketType.Play.Client.POSITION_LOOK || packetEvent.getPacketType() == PacketType.Play.Client.POSITION) {
                location = new Location(packetEvent.getPlayer().getWorld(), ((Double)packetContainer.getDoubles().read(0)).doubleValue(), ((Double)packetContainer.getDoubles().read(1)).doubleValue(), ((Double)packetContainer.getDoubles().read(2)).doubleValue());
            }
            NoFall.getPendingFallChecks(this.detector).add(new FallDamageContext(this.detector, location, (Boolean)packetContainer.getBooleans().read(0), packetEvent.getPlayer().getLocation().getY(), packetEvent.getPlayer().getVelocity().getY(), packetEvent.getPlayer(), (Double)NoFall.getHighestAirYMap(this.detector).get(packetEvent.getPlayer())));
            if (NoFall.getGroundedPlayers(this.detector).contains(packetEvent.getPlayer())) {
                packetContainer.getBooleans().write(0, true);
            }
        }
        catch (NullPointerException nullPointerException) {
        }
    }
}

