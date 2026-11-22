package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Deque;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.compat.PlayerPositionPacket;
import me.konsolas.aac.compat.RelativePositionFlag;
import me.konsolas.aac.api.HackType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class MovementPacketListener
extends PacketAdapter {
    final MovementManager movementManager;

    public MovementPacketListener(MovementManager movementManager, Plugin plugin, ListenerPriority listenerPriority, PacketType[] packetTypeArray) {        super(plugin, listenerPriority, packetTypeArray);

        this.movementManager = movementManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onPacketReceiving(PacketEvent packetEvent) {
        Location location;
        long ticks = 113218805911140L;
        try {
            if (packetEvent.isCancelled()) {
                return;
            }
            packetEvent.getPlayer().getUniqueId();
        }
        catch (NullPointerException nullPointerException) {
            return;
        }
        Deque<Location> deque = MovementManager.getQueuedLocations(this.movementManager).computeIfAbsent(packetEvent.getPlayer(), MovementPacketListener::createPositionQueue);
        MovementManager.getPacketBalances(this.movementManager).putIfAbsent(packetEvent.getPlayer(), Double.valueOf(MovementManager.getMinBalance(this.movementManager)));
        double d = (Double)MovementManager.getPacketBalances(this.movementManager).get(packetEvent.getPlayer());
        Deque<Location> deque2 = deque;
        synchronized (deque2) {
            location = Optional.<Location>ofNullable(deque.peekLast()).orElseGet(() -> this.fallbackPlayerLocation(packetEvent));
        }
        World world = packetEvent.getPlayer().getWorld();
        Location location2 = location;
        PacketContainer packetContainer = packetEvent.getPacket();
        Location targetLoc = MovementManager.parsePacketLocation(packetContainer, location2, world);
        if (!CollisionAccess.isValidLocation(targetLoc) || Math.abs(location.getX() - targetLoc.getX()) + Math.abs(location.getY() - targetLoc.getY()) + Math.abs(location.getZ() - targetLoc.getZ()) > 50.0) {
            packetEvent.setCancelled(true);
        }
        Object obj = MovementManager.getBalanceLock(this.movementManager);
        synchronized (obj) {
            long now = System.nanoTime();
            MovementManager.getLastPacketTimestamps(this.movementManager).computeIfAbsent(packetEvent.getPlayer(), arg_0 -> MovementPacketListener.initialPacketTimestamp(now, arg_0));
            long ticks2 = now - (Long)MovementManager.getLastPacketTimestamps(this.movementManager).get(packetEvent.getPlayer());
            MovementManager.getLastPacketTimestamps(this.movementManager).put(packetEvent.getPlayer(), now);
            double d2 = (double)ticks2 / 5.0E7;
            MovementManager.getPacketBalances(this.movementManager).put(packetEvent.getPlayer(), Math.max(Math.min((Double)MovementManager.getPacketBalances(this.movementManager).get(packetEvent.getPlayer()) + 1.0 - d2, (double)MovementManager.getBalanceCap(this.movementManager)), (double)MovementManager.getMinBalance(this.movementManager)));
        }
        if (MovementManager.getPendingTeleports(this.movementManager).containsKey(packetEvent.getPlayer())) {
            Location deque3 = targetLoc;
            Location location3 = MovementManager.getLastLocationForPlayer(this.movementManager, packetEvent.getPlayer());
            if (!((PendingTeleport)MovementManager.getPendingTeleports(this.movementManager).get(packetEvent.getPlayer())).matches(location3, deque3)) {
                if (((AAC)this.plugin).logManager.isDebugEnabled()) {
                    Vector vector;
                    Vector vector2 = vector = targetLoc.toVector();
                    ((AAC)this.plugin).logManager.logPlayerDebug(packetEvent.getPlayer(), "waiting for tp reply: " + MathUtilities.formatVector(vector2) + ", for " + MovementManager.getPendingTeleports(this.movementManager).get(packetEvent.getPlayer()));
                }
                packetEvent.setCancelled(true);
                return;
            }
        }
        if ((obj = (PendingTeleport)MovementManager.getPendingTeleports(this.movementManager).remove(packetEvent.getPlayer())) != null) {
            if (((AAC)this.plugin).logManager.isDebugEnabled()) {
                Vector vector;
                Vector vector3 = vector = targetLoc.toVector();
                ((AAC)this.plugin).logManager.logPlayerDebug(packetEvent.getPlayer(), "answered tp: " + MathUtilities.formatVector(vector3) + ", for " + obj);
            }
            Deque deque4 = deque;
            synchronized (deque4) {
                Location location4 = targetLoc.clone();
                location4.setWorld(null);
                deque.add(location4);
            }
        }
        if (d < (double)MovementManager.getCancelThreshold(this.movementManager)) {
            obj = deque;
            synchronized (obj) {
                deque.add(targetLoc);
            }
        } else {
            MovementManager.getPacketBalances(this.movementManager).put(packetEvent.getPlayer(), (Double)MovementManager.getPacketBalances(this.movementManager).get(packetEvent.getPlayer()) - 1.0);
            if (packetEvent.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                packetEvent.setCancelled(true);
                this.movementManager.recordViolation(packetEvent.getPlayer().getUniqueId(), 1, HackType.MOVE, String.format(Locale.UK, packetEvent.getPlayer().getName() + " failed Move: (movement validation) too many packets (balance %.2f)", d), ticks);
            }
        }
    }

    public void onPacketSending(PacketEvent packetEvent) {
        PacketContainer packetContainer = packetEvent.getPacket();
        PlayerPositionPacket playerPositionPacket = new PlayerPositionPacket(packetContainer);
        Vector vector = new Vector(playerPositionPacket.getX(), playerPositionPacket.getY(), playerPositionPacket.getZ());
        Set set = playerPositionPacket.getTeleportFlags();
        PendingTeleport pendingTeleport = new PendingTeleport(this.movementManager, set, vector);
        MovementManager.getPacketBalances(this.movementManager).compute(packetEvent.getPlayer(), this::adjustBalanceOnTeleport);
        if (playerPositionPacket.getX() == 0.0 && playerPositionPacket.getY() == 0.0 && playerPositionPacket.getZ() == 0.0 && set.contains((Object)RelativePositionFlag.X) && set.contains((Object)RelativePositionFlag.Y) && set.contains((Object)RelativePositionFlag.Z)) {
            return;
        }
        if (!MovementManager.getLastValidLocations(this.movementManager).containsKey(packetEvent.getPlayer())) {
            if (((AAC)this.plugin).logManager.isDebugEnabled()) {
                ((AAC)this.plugin).logManager.logPlayerDebug(packetEvent.getPlayer(), "important tp: ? -> " + pendingTeleport.toString());
            }
            MovementManager.getPendingTeleports(this.movementManager).put(packetEvent.getPlayer(), pendingTeleport);
            MovementManager.getQueuedLocations(this.movementManager).putIfAbsent(packetEvent.getPlayer(), new ConcurrentLinkedDeque());
            ((Deque)MovementManager.getQueuedLocations(this.movementManager).get(packetEvent.getPlayer())).clear();
        } else if (((AAC)this.plugin).logManager.isDebugEnabled()) {
            Vector vector2;
            Vector vector3 = vector2 = ((Location)MovementManager.getLastValidLocations(this.movementManager).get(packetEvent.getPlayer())).toVector();
            ((AAC)this.plugin).logManager.logPlayerDebug(packetEvent.getPlayer(), "(bukkit) tp: " + MathUtilities.formatVector(vector3) + " -> " + pendingTeleport.toString());
        }
    }

    private Double adjustBalanceOnTeleport(Player player, Double doubleVal) {
        return doubleVal == null ? (double)(MovementManager.getResetLevel(this.movementManager) - 1) : doubleVal - 1.0;
    }

    private static Long initialPacketTimestamp(long ticks, Player player) {
        return ticks - 50000000L;
    }

    private Location fallbackPlayerLocation(PacketEvent packetEvent) {
        return MovementManager.getLastValidLocations(this.movementManager).getOrDefault(packetEvent.getPlayer(), packetEvent.getPlayer().getLocation());
    }

    private static Deque createPositionQueue(Player player) {
        return new ConcurrentLinkedDeque();
    }
}

