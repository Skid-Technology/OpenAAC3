package me.konsolas.aac.checks.movement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public final class Jesus
extends Detector
implements MovementCheckListener {
    private final Map liquidTicks;
    private final Set enteredLiquidPlayers;
    private boolean teleport;

    public Jesus(AAC aAC) {        super(aAC);

        this.liquidTicks = new HashMap();
        this.enteredLiquidPlayers = new HashSet();
        this.teleport = true;
        this.teleport = this.plugin.getConfig().getBoolean("jesus.teleport");
        Server server = Bukkit.getServer();
        this.scheduledTaskIds.add(server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::decayJesusViolations, 20L, 100L));
    }

    static boolean isAboveLiquidSurface(BlockPositionView blockPositionView) {
        if (Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.0, 0.5, 0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.3, 0.6, 0.0)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.0, 0.6, -0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(-0.3, 0.6, 0.0)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(-0.3, 0.6, -0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(-0.3, 0.6, 0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.3, 0.6, -0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.3, 0.6, 0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.0, 0.0, 0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.3, 0.0, 0.0)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.0, 0.0, -0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(-0.3, 0.0, 0.0)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(-0.3, 0.0, -0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(-0.3, 0.0, 0.3)) || Jesus.isSolidOrDry(blockPositionView.getBlockAtNegOffset(0.3, 0.0, -0.3))) {
            return false;
        }
        return blockPositionView.getBlockAtNegOffset(0.0, 0.5, 0.0).isLiquid() || blockPositionView.getBlockAtNegOffset(0.0, 1.5, 0.0).isLiquid() || blockPositionView.getBlock().isLiquid();
    }

    private static boolean isSolidOrDry(Block block) {
        return !block.isLiquid() && !block.isEmpty();
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        if (playerMoveEvent.getPlayer().isFlying() || playerMoveEvent.getPlayer().getVehicle() != null) {
            return false;
        }
        for (Entity entity : playerMoveEvent.getPlayer().getNearbyEntities(3.0, 3.0, 3.0)) {
            if (!(entity instanceof Boat)) continue;
            return false;
        }
        Player player = playerMoveEvent.getPlayer();
        return !AAC.serverVersion.isElytraGliding(player);
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        long ticks2 = ticks ^ 0x60FF4F6C294EL;
        if (Jesus.isAboveLiquidSurface(blockPositionView) && playerMoveEvent.getPlayer().getLocation().add(0.0, 0.5, 0.0).getBlock().isEmpty() && (playerMoveEvent.getPlayer().getLocation().subtract(0.0, 1.5, 0.0).getBlock().isLiquid() || playerMoveEvent.getPlayer().getLocation().subtract(0.0, 0.5, 0.0).getBlock().isLiquid()) && !this.plugin.combatTracker.hasRecentDamage(playerMoveEvent.getPlayer())) {
            if (playerMoveEvent.getPlayer().getLocation().subtract(0.0, 0.2, 0.0).getBlock().isLiquid()) {
                if (!this.liquidTicks.containsKey(playerMoveEvent.getPlayer().getUniqueId())) {
                    this.liquidTicks.put(playerMoveEvent.getPlayer().getUniqueId(), 0);
                }
                this.enteredLiquidPlayers.add(playerMoveEvent.getPlayer().getUniqueId());
            }
            if (this.enteredLiquidPlayers.contains(playerMoveEvent.getPlayer().getUniqueId())) {
                if (!this.liquidTicks.containsKey(playerMoveEvent.getPlayer().getUniqueId())) {
                    this.liquidTicks.put(playerMoveEvent.getPlayer().getUniqueId(), 0);
                }
                this.liquidTicks.put(playerMoveEvent.getPlayer().getUniqueId(), (Integer)this.liquidTicks.get(playerMoveEvent.getPlayer().getUniqueId()) + 1);
                if ((Integer)this.liquidTicks.get(playerMoveEvent.getPlayer().getUniqueId()) > 20 && this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 1, HackType.JESUS, playerMoveEvent.getPlayer().getName() + " failed Jesus: tried to float above water", ticks2) && this.teleport) {
                    playerMoveEvent.setTo(playerMoveEvent.getFrom().subtract(0.0, 0.1, 0.0));
                }
            }
        } else {
            this.liquidTicks.remove(playerMoveEvent.getPlayer().getUniqueId());
        }
    }

    private double findWaterSurfaceY(Location location, int i) {
        if (this.withY(location, location.getY() + 1.5).getBlock().isLiquid()) {
            return 256.0;
        }
        for (double y = location.getY() + 1.0; y > location.getY() - (double)i; y -= 1.0) {
            if (!this.withY(location, y).getBlock().isLiquid()) continue;
            return this.withY(location, y).getBlock().getY() + 1;
        }
        return 256.0;
    }

    private Location withY(Location location, double d) {
        location.setY(d);
        return location;
    }

    private void decayJesusViolations() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.decreaseViolationLevel(player.getUniqueId(), 1);
        }
    }
}

