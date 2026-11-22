package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.compat.EnvironmentBlockChecks;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public final class NoFall
extends Detector
implements MovementCheckListener {
    private double requiredVelocity;
    private boolean dealDamage;
    private boolean checkReduceAttempts;
    private final Map<Player, Double> highestAirY;
    private final Set<Player> groundedPlayers;
    private final Queue<FallDamageContext> pendingFallChecks;

    public NoFall(AAC aAC) {        super(aAC);

        this.requiredVelocity = -0.2;
        this.dealDamage = true;
        this.checkReduceAttempts = true;
        this.highestAirY = Collections.synchronizedMap(new WeakHashMap());
        this.groundedPlayers = Collections.newSetFromMap(new WeakHashMap());
        this.pendingFallChecks = new ConcurrentLinkedQueue();
        this.requiredVelocity = this.plugin.getConfig().getDouble("nofall.required_velocity", this.requiredVelocity);
        this.dealDamage = this.plugin.getConfig().getBoolean("nofall.deal_damage", this.dealDamage);
        this.checkReduceAttempts = this.plugin.getConfig().getBoolean("nofall.check_reduce_attempts", this.checkReduceAttempts);
        NoFallMovementPacketListener noFallMovementPacketListener = new NoFallMovementPacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.FLYING, PacketType.Play.Client.LOOK});
        this.packetListeners.add(noFallMovementPacketListener);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)noFallMovementPacketListener);
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        return playerMoveEvent.getPlayer().getGameMode() != GameMode.CREATIVE && playerMoveEvent.getPlayer().getGameMode() != GameMode.SPECTATOR && !playerMoveEvent.getPlayer().getAllowFlight() && playerMoveEvent.getPlayer().getVehicle() == null;
    }

    @Override
    public void onTick(long ticks, long ticks2) {
        long ticks3 = ticks2 ^ 0x4DEAE20E7407L;
        while (!this.pendingFallChecks.isEmpty()) {
            double d;
            FallDamageContext fallDamageContext = (FallDamageContext)this.pendingFallChecks.remove();
            BlockPositionView blockPositionView = new BlockPositionView(FallDamageContext.getLocation(fallDamageContext));
            if (!CollisionAccess.isValidLocation(new Location(blockPositionView.getWorld(), blockPositionView.getX(), blockPositionView.getY(), blockPositionView.getZ()))) continue;
            boolean flag = CollisionAccess.getGroundCollisionState(new PlayerMoveEvent(FallDamageContext.getPlayer(fallDamageContext), FallDamageContext.getLocation(fallDamageContext), FallDamageContext.getLocation(fallDamageContext)), GroundCollisionState.FALSE_NORMAL).isOnGround() || EnvironmentBlockChecks.isNearSlabOrStair(blockPositionView);
            boolean flag2 = FallDamageContext.isOnGround(fallDamageContext);
            double d2 = FallDamageContext.getVerticalVelocity(fallDamageContext);
            double d3 = FallDamageContext.getCurrentY(fallDamageContext);
            if (flag || FallDamageContext.getLastGroundY(fallDamageContext) == null || !((d = FallDamageContext.getLastGroundY(fallDamageContext).doubleValue()) - d3 > 3.0) || !(d2 <= this.requiredVelocity) || !flag2 || !this.recordViolation(FallDamageContext.getPlayer(fallDamageContext).getUniqueId(), 1, HackType.NOFALL, FallDamageContext.getPlayer(fallDamageContext).getName() + " failed NoFall: invalid midair ground packet", ticks3) || !this.dealDamage) continue;
            FallDamageContext.getPlayer(fallDamageContext).setFallDistance((float)(FallDamageContext.getLastGroundY(fallDamageContext) - FallDamageContext.getPlayer(fallDamageContext).getLocation().getY()));
        }
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        long ticks2 = ticks ^ 0x60FF4F6C294EL;
        double y = playerMoveEvent.getTo().getY();
        double y2 = y - playerMoveEvent.getFrom().getY();
        if (groundCollisionState2.isOnGround()) {
            if (this.plugin.logManager.isDebugEnabled()) {
                this.plugin.logManager.logPlayerDebug(playerMoveEvent.getPlayer(), "(NoFall) " + playerMoveEvent.getPlayer().getName() + " landed");
            }
            this.groundedPlayers.add(playerMoveEvent.getPlayer());
            double d = this.highestAirY.getOrDefault(playerMoveEvent.getPlayer(), 0.0) - y;
            if (d >= 3.5 && (double)playerMoveEvent.getPlayer().getFallDistance() - y2 < d - 1.0) {
                if ((double)playerMoveEvent.getPlayer().getFallDistance() <= 3.5 && d > 3.5 + (double)(playerMoveEvent.getPlayer().getFallDistance() * 2.0f)) {
                    if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 2, HackType.NOFALL, String.format(playerMoveEvent.getPlayer().getName() + " failed NoFall: tried to avoid fall damage (%.2f/%.2f blocks)", Float.valueOf(playerMoveEvent.getPlayer().getFallDistance()), d), ticks2) && this.dealDamage) {
                        playerMoveEvent.getPlayer().setFallDistance((float)d);
                    }
                } else if (this.checkReduceAttempts && this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 1, HackType.NOFALL, String.format(playerMoveEvent.getPlayer().getName() + " failed NoFall: tried to reduce fall damage (%.2f/%.2f blocks)", Float.valueOf(playerMoveEvent.getPlayer().getFallDistance()), d), ticks2) && this.dealDamage) {
                    playerMoveEvent.getPlayer().setFallDistance((float)d);
                }
            }
        } else {
            this.groundedPlayers.remove(playerMoveEvent.getPlayer());
        }
        if (!(groundCollisionState.isOnGround() || groundCollisionState2.isOnGround() || EnvironmentBlockChecks.isOnLadderOrVine(blockPositionView) || EnvironmentBlockChecks.isOnLadderOrVine(blockPositionView2) || EnvironmentBlockChecks.isInLiquid(blockPositionView) || EnvironmentBlockChecks.isInLiquid(blockPositionView2))) {
            if (this.highestAirY.containsKey(playerMoveEvent.getPlayer())) {
                if (y > (Double)this.highestAirY.get(playerMoveEvent.getPlayer())) {
                    this.highestAirY.put(playerMoveEvent.getPlayer(), y);
                }
            } else {
                this.highestAirY.put(playerMoveEvent.getPlayer(), y);
            }
        } else {
            this.highestAirY.remove(playerMoveEvent.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            BlockPositionView blockPositionView = new BlockPositionView(player.getLocation());
            try {
                if (!blockPositionView.getBlockAtNegOffset(0.0, 0.2, 0.3).equals(blockBreakEvent.getBlock()) && !blockPositionView.getBlockAtNegOffset(0.3, 0.2, 0.0).equals(blockBreakEvent.getBlock()) && !blockPositionView.getBlockAtNegOffset(0.0, 0.2, -0.3).equals(blockBreakEvent.getBlock()) && !blockPositionView.getBlockAtNegOffset(-0.3, 0.2, 0.0).equals(blockBreakEvent.getBlock()) && !blockPositionView.getBlockAtNegOffset(-0.3, 0.2, -0.3).equals(blockBreakEvent.getBlock()) && !blockPositionView.getBlockAtNegOffset(-0.3, 0.2, 0.3).equals(blockBreakEvent.getBlock()) && !blockPositionView.getBlockAtNegOffset(0.3, 0.2, -0.3).equals(blockBreakEvent.getBlock()) && !blockPositionView.getBlockAtNegOffset(0.3, 0.2, 0.3).equals(blockBreakEvent.getBlock())) continue;
                this.highestAirY.put(player, player.getLocation().getY());
            }
            catch (NullPointerException nullPointerException) {}
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        if (!(playerTeleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN && MovementManager.TELEPORT_CONTEXT.isCalledFromBukkit() || this.plugin.setbackManager.getSetbackCheck(playerTeleportEvent.getPlayer()) != null)) {
            this.highestAirY.put(playerTeleportEvent.getPlayer(), playerTeleportEvent.getTo().getY());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        this.highestAirY.remove(playerRespawnEvent.getPlayer());
    }

    static Map getHighestAirYMap(NoFall noFall) {
        return noFall.highestAirY;
    }

    static Queue getPendingFallChecks(NoFall noFall) {
        return noFall.pendingFallChecks;
    }

    static Set getGroundedPlayers(NoFall noFall) {
        return noFall.groundedPlayers;
    }
}

