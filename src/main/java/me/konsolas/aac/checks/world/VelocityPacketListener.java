package me.konsolas.aac.checks.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.compat.CollisionVectorAdjuster;
import me.konsolas.aac.compat.EntityVelocityPacket;
import me.konsolas.aac.compat.EnvironmentBlockChecks;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.checks.movement.VelocityResponseState;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class VelocityPacketListener
extends PacketAdapter {
    final Velocity detector;

    public VelocityPacketListener(Velocity velocity, Plugin plugin, PacketType[] packetTypeArray) {        super(plugin, packetTypeArray);

        this.detector = velocity;
    }

    public void onPacketSending(PacketEvent packetEvent) {
        Player player;
        if (packetEvent.isCancelled() || ((AAC)this.plugin).checkManager.isPlayerExempt(player = packetEvent.getPlayer())) {
            return;
        }
        PacketContainer packetContainer = packetEvent.getPacket();
        EntityVelocityPacket entityVelocityPacket = new EntityVelocityPacket(packetContainer);
        try {
            packetEvent.getPlayer().getEntityId();
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            this.plugin.getLogger().warning("(Velocity) Velocity packet was sent to a temporary player (" + packetEvent.getPlayer().getName() + "/" + packetEvent.getPlayer().getUniqueId() + ")");
            return;
        }
        if (entityVelocityPacket.getEntityId() == packetEvent.getPlayer().getEntityId()) {
            if (Velocity.getPendingResponses(this.detector).containsKey(packetEvent.getPlayer())) {
                return;
            }
            Vector vector = entityVelocityPacket.getVelocity();
            Velocity.getPendingResponses(this.detector).put(packetEvent.getPlayer(), new VelocityResponseState(packetEvent.getPlayer().getLocation(), vector, null));
            if (((AAC)this.plugin).logManager.isDebugEnabled()) {
                Vector vector2;
                Vector vector3 = vector2 = vector;
                ((AAC)this.plugin).logManager.logPlayerDebug(packetEvent.getPlayer(), "Server velocity: " + MathUtilities.formatVector(vector3));
            }
            Server server = Bukkit.getServer();
            Player eventPlayer = packetEvent.getPlayer();
            server.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                if (((AAC)this.plugin).setbackManager.getSetbackCheck(eventPlayer) != null || eventPlayer.getVehicle() != null || EnvironmentBlockChecks.isPlayerInCobweb(new BlockPositionView(eventPlayer.getLocation())) || EnvironmentBlockChecks.isOnLadderOrVine(new BlockPositionView(eventPlayer.getLocation())) || Velocity.isExemptMovement(this.detector, eventPlayer)) {
                    Velocity.getPendingResponses(this.detector).remove(eventPlayer);
                    return;
                }
                if (Velocity.getPendingResponses(this.detector).containsKey(eventPlayer)) {
                    Vector expectedVel = VelocityResponseState.getExpectedVelocity((VelocityResponseState)Velocity.getPendingResponses(this.detector).get(eventPlayer)).clone();
                    CollisionVectorAdjuster.adjustVectorForCollisions((Entity)eventPlayer, eventPlayer.getLocation(), expectedVel);
                    if (expectedVel.length() == 0.0) {
                        Velocity.getPendingResponses(this.detector).remove(eventPlayer);
                    }
                }
            });
        }
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        try {
            if (Velocity.getPendingResponses(this.detector).containsKey(packetEvent.getPlayer())) {
                VelocityResponseState.setAttackObserved((VelocityResponseState)Velocity.getPendingResponses(this.detector).get(packetEvent.getPlayer()), true);
            }
            if (packetEvent.getPacketType() == PacketType.Play.Client.USE_ENTITY && packetEvent.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK && Velocity.getPendingResponses(this.detector).containsKey(packetEvent.getPlayer())) {
                VelocityResponseState.setResponded((VelocityResponseState)Velocity.getPendingResponses(this.detector).get(packetEvent.getPlayer()), true);
            }
        }
        catch (NullPointerException nullPointerException) {}
    }
}

