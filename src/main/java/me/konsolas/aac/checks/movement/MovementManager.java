package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketListener;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.CleanupHandler;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class MovementManager
extends Detector
implements Listener,
CleanupHandler {
    public static final ServerTeleportCallContext TELEPORT_CONTEXT = new ServerTeleportCallContext();
    private final PacketListener packetListener;
    private final int minBalance;
    private final int cancelThreshold;
    private final int resetLevel;
    private final int balanceCap;
    private final boolean dealFall;
    private final double tpAcceptThreshold;
    private final Object balanceLock;
    private final Map<Player, Location> lastValidLocations;
    private final Map<Player, Deque<Location>> queuedLocations;
    private final Map<Player, Double> packetBalances;
    private final Map<Player, Long> lastPacketTimestamps;
    private final ConcurrentMap<Player, PendingTeleport> pendingTeleports;

    public MovementManager(AAC aAC) {        super(aAC);

        this.balanceLock = new Object();
        this.lastValidLocations = Collections.synchronizedMap(new WeakHashMap());
        this.queuedLocations = Collections.synchronizedMap(new WeakHashMap());
        this.packetBalances = Collections.synchronizedMap(new WeakHashMap());
        this.lastPacketTimestamps = Collections.synchronizedMap(new WeakHashMap());
        this.pendingTeleports = new ConcurrentHashMap();
        this.minBalance = aAC.getConfig().getInt("move.min_balance", -80);
        this.cancelThreshold = aAC.getConfig().getInt("move.cancel_threshold", 10);
        this.resetLevel = aAC.getConfig().getInt("move.reset_level", -1);
        this.balanceCap = aAC.getConfig().getInt("move.balance_cap", 15);
        this.dealFall = aAC.getConfig().getBoolean("move.deal_fall", true);
        this.tpAcceptThreshold = aAC.getConfig().getDouble("move.tp_accept_threshold", 0.0);
        Server server = aAC.getServer();
        this.scheduledTaskIds.add(server.getScheduler().scheduleSyncRepeatingTask((Plugin)aAC, this::decayMoveViolations, 1L, (long)aAC.getConfig().getInt("move.decay")));
        Server server2 = aAC.getServer();
        this.scheduledTaskIds.add(server2.getScheduler().scheduleSyncRepeatingTask((Plugin)aAC, this::flushStaleMoveQueues, 15L, 15L));
        this.packetListener = new MovementPacketListener(this, (Plugin)aAC, ListenerPriority.LOW, new PacketType[]{PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK, PacketType.Play.Server.POSITION});
        ProtocolLibrary.getProtocolManager().addPacketListener(this.packetListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processQueuedMoves(Player player, long ticks) {
        long ticks2 = (ticks = 0x269727F98245L ^ ticks) ^ 0x7F1E5BD63D22L;
        if (player.getVehicle() != null) {
            this.queuedLocations.remove(player);
            this.lastValidLocations.remove(player);
            return;
        }
        this.lastValidLocations.putIfAbsent(player, player.getLocation());
        this.packetBalances.putIfAbsent(player, Double.valueOf(this.minBalance));
        this.queuedLocations.putIfAbsent(player, new ConcurrentLinkedDeque());
        Deque deque = (Deque)this.queuedLocations.get(player);
        synchronized (deque) {
            Location location;
            if (this.plugin.logManager.isDebugEnabled()) {
                this.plugin.logManager.logPlayerDebug(player, "checking packets [size r:" + ((Deque)this.queuedLocations.get(player)).size() + "]");
            }
            if (((Deque)this.queuedLocations.get(player)).size() == 1) {
                this.packetBalances.put(player, Math.max((Double)this.packetBalances.get(player), (double)this.resetLevel));
            }
            Location location2 = (Location)this.lastValidLocations.get(player);
            while ((location = (Location)((Deque)this.queuedLocations.get(player)).poll()) != null) {
                if (location.getWorld() == null) {
                    location2 = location;
                    location.setWorld(player.getWorld());
                }
                PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(player, location2, location);
                Location location3 = playerMoveEvent.getTo().clone();
                if (this.plugin.logManager.isDebugEnabled()) {
                    Vector vector;
                    Vector vector2;
                    Vector vector3 = vector2 = playerMoveEvent.getFrom().toVector();
                    Vector vector4 = vector = playerMoveEvent.getTo().toVector();
                    this.plugin.logManager.logPlayerDebug(player, "move [from r:" + MathUtilities.formatVector(vector3) + " to r:" + MathUtilities.formatVector(vector4) + " eq u:" + playerMoveEvent.getFrom().equals((Object)playerMoveEvent.getTo()) + "]");
                }
                boolean flag = true;
                if (!playerMoveEvent.getTo().equals((Object)playerMoveEvent.getFrom())) {
                    if (!CollisionAccess.isValidLocation(playerMoveEvent.getTo()) || Math.abs(location2.getX() - playerMoveEvent.getTo().getX()) + Math.abs(location2.getY() - playerMoveEvent.getTo().getY()) + Math.abs(location2.getZ() - playerMoveEvent.getTo().getZ()) > 50.0) {
                        Vector vector;
                        Vector vector5 = vector = location2.toVector();
                        if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 100, HackType.MOVE, playerMoveEvent.getPlayer().getName() + " failed Move: (movement validation) sent unsafe movement " + MathUtilities.formatVector(vector5) + " -> " + location.toVector(), ticks2)) {
                            boolean flag2 = false;
                            boolean flag3 = false;
                            double y = player.getVelocity().getY();
                            PlayerMoveEvent playerMoveEvent2 = playerMoveEvent;
                            Player player2 = player;
                            this.plugin.setbackManager.applySetback(player2, playerMoveEvent2, y, flag3, flag2);
                            flag = this.dealFall;
                        }
                    } else {
                        this.plugin.checkManager.movementCallback.accept(playerMoveEvent);
                    }
                }
                if (!location3.equals((Object)playerMoveEvent.getTo())) {
                    double y2 = Math.min(player.getVelocity().getY(), -0.2);
                    float y3 = player.getFallDistance() + (float)Math.max(location2.getY() - location3.getY(), location2.getY() - playerMoveEvent.getTo().getY());
                    if (this.plugin.logManager.isDebugEnabled()) {
                        Vector vector;
                        Vector vector6;
                        Vector vector7;
                        Vector vector8 = vector7 = playerMoveEvent.getFrom().toVector();
                        Vector vector9 = vector6 = location3.toVector();
                        Vector vector10 = vector = playerMoveEvent.getTo().toVector();
                        this.plugin.logManager.logPlayerDebug(player, "setback [move from: " + MathUtilities.formatVector(vector8) + " to: " + MathUtilities.formatVector(vector9) + "] [setback to: " + MathUtilities.formatVector(vector10) + " velY: " + y2 + " fallD: " + y3 + "]");
                    }
                    this.plugin.setbackManager.setSetbackCheck(this, player);
                    player.teleport(playerMoveEvent.getTo(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                    player.setVelocity(new Vector(0.0, y2, 0.0));
                    if (flag) {
                        player.setFallDistance(y3);
                    }
                    this.plugin.setbackManager.clearSetbackCheck(player);
                    ((Deque)this.queuedLocations.get(playerMoveEvent.getPlayer())).clear();
                    break;
                }
                location2 = playerMoveEvent.getTo();
            }
            this.lastValidLocations.put(player, location2);
        }
    }

    private static Location extractLocationFromPacket(PacketContainer packetContainer, Location location, World world) {
        if (packetContainer.getType() == PacketType.Play.Client.FLYING) {
            return location.clone();
        }
        if (packetContainer.getType() == PacketType.Play.Client.POSITION_LOOK) {
            double d = (Double)packetContainer.getDoubles().read(0);
            double d2 = (Double)packetContainer.getDoubles().read(1);
            double d3 = (Double)packetContainer.getDoubles().read(2);
            float f = ((Float)packetContainer.getFloat().read(0)).floatValue();
            float f2 = ((Float)packetContainer.getFloat().read(1)).floatValue();
            return new Location(world, d, d2, d3, f, f2);
        }
        if (packetContainer.getType() == PacketType.Play.Client.POSITION) {
            double d4 = (Double)packetContainer.getDoubles().read(0);
            double d5 = (Double)packetContainer.getDoubles().read(1);
            double d6 = (Double)packetContainer.getDoubles().read(2);
            float yaw = location.getYaw();
            float pitch = location.getPitch();
            return new Location(world, d4, d5, d6, yaw, pitch);
        }
        if (packetContainer.getType() == PacketType.Play.Client.LOOK) {
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            float f3 = ((Float)packetContainer.getFloat().read(0)).floatValue();
            float f4 = ((Float)packetContainer.getFloat().read(1)).floatValue();
            return new Location(world, x, y, z, f3, f4);
        }
        throw new IllegalArgumentException("Can't accept packet of type " + packetContainer.getType());
    }

    private Location getLastValidLocation(Player player) {
        if (this.lastValidLocations.containsKey(player)) {
            return ((Location)this.lastValidLocations.get(player)).clone();
        }
        return player.getLocation();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        long ticks = 69757560353027L;
        if (playerMoveEvent.getPlayer().getVehicle() != null && playerMoveEvent.getFrom().getWorld().equals(playerMoveEvent.getTo().getWorld())) {
            this.plugin.checkManager.movementCallback.accept(playerMoveEvent);
            this.lastValidLocations.put(playerMoveEvent.getPlayer(), playerMoveEvent.getPlayer().getLocation());
            this.queuedLocations.remove(playerMoveEvent.getPlayer());
        } else {
            this.processQueuedMoves(playerMoveEvent.getPlayer(), ticks);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent playerChangedWorldEvent) {
        Queue queue;
        Queue queue2 = queue = (Queue)this.queuedLocations.computeIfAbsent(playerChangedWorldEvent.getPlayer(), MovementManager::createQueueOnWorldChange);
        synchronized (queue2) {
            queue.clear();
        }
        this.lastValidLocations.remove(playerChangedWorldEvent.getPlayer());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        if (playerTeleportEvent.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN || !TELEPORT_CONTEXT.isCalledFromBukkit() || playerTeleportEvent.getPlayer().getVehicle() != null) {
            Queue queue;
            Queue queue2 = queue = (Queue)this.queuedLocations.computeIfAbsent(playerTeleportEvent.getPlayer(), MovementManager::createQueueOnTeleport);
            synchronized (queue2) {
                queue.clear();
            }
            this.lastValidLocations.remove(playerTeleportEvent.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        this.lastValidLocations.remove(playerRespawnEvent.getPlayer());
        this.queuedLocations.putIfAbsent(playerRespawnEvent.getPlayer(), new ConcurrentLinkedDeque());
        ((Deque)this.queuedLocations.get(playerRespawnEvent.getPlayer())).clear();
        if (this.plugin.logManager.isDebugEnabled()) {
            Vector vector;
            Vector vector2 = vector = playerRespawnEvent.getRespawnLocation().toVector();
            this.plugin.logManager.logPlayerDebug(playerRespawnEvent.getPlayer(), "respawned -> " + MathUtilities.formatVector(vector2));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        this.queuedLocations.putIfAbsent(playerDeathEvent.getEntity(), new ConcurrentLinkedDeque());
        ((Deque)this.queuedLocations.get(playerDeathEvent.getEntity())).clear();
        if (this.plugin.logManager.isDebugEnabled()) {
            this.plugin.logManager.logPlayerDebug(playerDeathEvent.getEntity(), "died");
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        ProtocolLibrary.getProtocolManager().removePacketListener(this.packetListener);
        HandlerList.unregisterAll((Listener)this);
    }

    private static Deque createQueueOnTeleport(Player player) {
        return new ConcurrentLinkedDeque();
    }

    private static Deque createQueueOnWorldChange(Player player) {
        return new ConcurrentLinkedDeque();
    }

    private void flushStaleMoveQueues() {
        Bukkit.getOnlinePlayers().stream().filter(this::hasStaleMoveQueue).forEach(this::processPlayerMoveQueue);
    }

    private void processPlayerMoveQueue(Player player) {
        long ticks = 69757560353027L;
        this.queuedLocations.computeIfAbsent(player, MovementManager::createQueueOnFlush).add(player.getLocation());
        this.processQueuedMoves(player, ticks);
    }

    private static Deque createQueueOnFlush(Player player) {
        return new ConcurrentLinkedDeque();
    }

    private boolean hasStaleMoveQueue(Player player) {
        return System.nanoTime() - this.lastPacketTimestamps.getOrDefault(player, System.nanoTime()) > 500000000L;
    }

    private void decayMoveViolations() {
        Bukkit.getOnlinePlayers().forEach(this::decayPlayerMoveViolation);
    }

    private void decayPlayerMoveViolation(Player player) {
        this.decreaseViolationLevel(player.getUniqueId(), 1);
    }

    static Map<Player, Deque<Location>> getQueuedLocations(MovementManager movementManager) {
        return movementManager.queuedLocations;
    }

    static int getMinBalance(MovementManager movementManager) {
        return movementManager.minBalance;
    }

    static Map<Player, Double> getPacketBalances(MovementManager movementManager) {
        return movementManager.packetBalances;
    }

    static Location parsePacketLocation(PacketContainer packetContainer, Location location, World world) {
        World world2 = world;
        Location location2 = location;
        PacketContainer packetContainer2 = packetContainer;
        return MovementManager.extractLocationFromPacket(packetContainer2, location2, world2);
    }

    static Object getBalanceLock(MovementManager movementManager) {
        return movementManager.balanceLock;
    }

    static Map<Player, Long> getLastPacketTimestamps(MovementManager movementManager) {
        return movementManager.lastPacketTimestamps;
    }

    static int getBalanceCap(MovementManager movementManager) {
        return movementManager.balanceCap;
    }

    static ConcurrentMap<Player, PendingTeleport> getPendingTeleports(MovementManager movementManager) {
        return movementManager.pendingTeleports;
    }

    static Location getLastLocationForPlayer(MovementManager movementManager, Player player) {
        return movementManager.getLastValidLocation(player);
    }

    static int getCancelThreshold(MovementManager movementManager) {
        return movementManager.cancelThreshold;
    }

    static Map<Player, Location> getLastValidLocations(MovementManager movementManager) {
        return movementManager.lastValidLocations;
    }

    static int getResetLevel(MovementManager movementManager) {
        return movementManager.resetLevel;
    }

    static double getTpAcceptThreshold(MovementManager movementManager) {
        return movementManager.tpAcceptThreshold;
    }
}

