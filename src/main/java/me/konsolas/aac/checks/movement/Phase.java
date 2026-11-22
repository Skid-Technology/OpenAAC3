package me.konsolas.aac.checks.movement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.compat.CollisionQueryResult;
import me.konsolas.aac.compat.CollisionVectorAdjuster;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.EntityDimensions;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.compat.ServerVersion;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public final class Phase
extends Detector
implements MovementCheckListener {
    private final List excludedMaterials;

    public Phase(AAC aAC) {        super(aAC);

        this.excludedMaterials = new ArrayList();
        for (String str : this.plugin.getConfig().getStringList("phase.exclude")) {
            this.excludedMaterials.add(Material.getMaterial((String)str));
        }
        Server server = Bukkit.getServer();
        this.scheduledTaskIds.add(server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::decayPhaseViolations, 69L, 13L));
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        Player player;
        return playerMoveEvent.getPlayer().getGameMode() != GameMode.SPECTATOR && playerMoveEvent.getPlayer().getGameMode() != GameMode.CREATIVE && !AAC.serverVersion.isElytraGliding(player = playerMoveEvent.getPlayer()) && playerMoveEvent.getPlayer().getVehicle() == null;
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        AxisAlignedBoundingBox axisAlignedBoundingBox;
        AxisAlignedBoundingBox axisAlignedBoundingBox2;
        long ticks2 = ticks ^ 0x60FF4F6C294EL;
        double d = playerMoveEvent.getTo().distance(playerMoveEvent.getFrom());
        if (d > 10.0) {
            Vector vector;
            Vector vector2;
            Object[] objectArray = new Object[3];
            Vector vector3 = vector2 = playerMoveEvent.getFrom().toVector();
            objectArray[0] = MathUtilities.formatVector(vector3);
            Vector vector4 = vector = playerMoveEvent.getTo().toVector();
            objectArray[1] = MathUtilities.formatVector(vector4);
            objectArray[2] = d;
            if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 1, HackType.PHASE, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Phase: moved too far away from safe location %s -> %s (%.2f)", objectArray), ticks2)) {
                boolean flag = false;
                boolean flag2 = false;
                double y = playerMoveEvent.getPlayer().getVelocity().getY();
                PlayerMoveEvent playerMoveEvent2 = playerMoveEvent;
                Player player = playerMoveEvent.getPlayer();
                this.plugin.setbackManager.applySetback(player, playerMoveEvent2, y, flag2, flag);
            }
        }
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
            Player player = playerMoveEvent.getPlayer();
            axisAlignedBoundingBox2 = new AxisAlignedBoundingBox(playerMoveEvent.getFrom().toVector(), EntityDimensions.forEntity((Entity)player));
            Player player2 = playerMoveEvent.getPlayer();
            axisAlignedBoundingBox = new AxisAlignedBoundingBox(playerMoveEvent.getTo().toVector(), EntityDimensions.forEntity((Entity)player2));
        } else {
            Player player = playerMoveEvent.getPlayer();
            axisAlignedBoundingBox2 = new AxisAlignedBoundingBox(playerMoveEvent.getFrom().toVector(), EntityDimensions.forEntity((Entity)player).expand(0.0, -0.2));
            Player player3 = playerMoveEvent.getPlayer();
            axisAlignedBoundingBox = new AxisAlignedBoundingBox(playerMoveEvent.getTo().toVector(), EntityDimensions.forEntity((Entity)player3).expand(0.0, -0.2));
        }
        if (axisAlignedBoundingBox2.intersects(axisAlignedBoundingBox) && !groundCollisionState.isCollided() && !groundCollisionState2.isCollided()) {
            return;
        }
        AxisAlignedBoundingBox axisAlignedBoundingBox3 = axisAlignedBoundingBox2.union(axisAlignedBoundingBox);
        Player player = playerMoveEvent.getPlayer();
        if (CollisionAccess.getCollidingBoxes((Entity)player, axisAlignedBoundingBox3).size() == 0) {
            return;
        }
        AxisAlignedBoundingBox axisAlignedBoundingBox4 = axisAlignedBoundingBox2;
        Player player4 = playerMoveEvent.getPlayer();
        List<AxisAlignedBoundingBox> list2 = CollisionAccess.getCollidingBoxes((Entity)player4, axisAlignedBoundingBox4);
        AxisAlignedBoundingBox axisAlignedBoundingBox5 = axisAlignedBoundingBox;
        Player player5 = playerMoveEvent.getPlayer();
        List<AxisAlignedBoundingBox> list = CollisionAccess.getCollidingBoxes((Entity)player5, axisAlignedBoundingBox5);
        if (!this.excludedMaterials.isEmpty()) {
            list2.removeIf(arg_0 -> this.isExcludedFromBlock(playerMoveEvent, arg_0));
            list.removeIf(arg_0 -> this.isExcludedToBlock(playerMoveEvent, arg_0));
        }
        if (list2.isEmpty() && !list.isEmpty()) {
            AxisAlignedBoundingBox axisAlignedBoundingBox6 = list.get(0);
            Block block = axisAlignedBoundingBox6.getMinimum().toLocation(playerMoveEvent.getPlayer().getWorld()).getBlock();
            if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 1, HackType.PHASE, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Phase: moved into a block (%s at %d,%d,%d)", block.getType().toString(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()), ticks2)) {
                boolean flag3 = false;
                boolean flag4 = false;
                double y2 = playerMoveEvent.getPlayer().getVelocity().getY();
                PlayerMoveEvent playerMoveEvent3 = playerMoveEvent;
                Player player6 = playerMoveEvent.getPlayer();
                this.plugin.setbackManager.applySetback(player6, playerMoveEvent3, y2, flag4, flag3);
            }
            return;
        }
        if (!list2.isEmpty()) {
            if ((d >= 1.0 || list.size() >= list2.size() && !list2.containsAll(list)) && this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 1, HackType.PHASE, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Phase: moved inside blocks (%d,%d,%d to %d,%d,%d)", playerMoveEvent.getFrom().getBlockX(), playerMoveEvent.getFrom().getBlockY(), playerMoveEvent.getFrom().getBlockZ(), playerMoveEvent.getTo().getBlockX(), playerMoveEvent.getTo().getBlockY(), playerMoveEvent.getTo().getBlockZ()), ticks2)) {
                boolean flag5 = false;
                boolean flag6 = false;
                double y3 = playerMoveEvent.getPlayer().getVelocity().getY();
                PlayerMoveEvent playerMoveEvent4 = playerMoveEvent;
                Player player7 = playerMoveEvent.getPlayer();
                this.plugin.setbackManager.applySetback(player7, playerMoveEvent4, y3, flag6, flag5);
            }
            return;
        }
        if (axisAlignedBoundingBox2.intersects(axisAlignedBoundingBox)) {
            if (!(list2.isEmpty() && list.isEmpty() || !this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 1, HackType.PHASE, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Phase: moved through a block (%d,%d,%d to %d,%d,%d)", playerMoveEvent.getFrom().getBlockX(), playerMoveEvent.getFrom().getBlockY(), playerMoveEvent.getFrom().getBlockZ(), playerMoveEvent.getTo().getBlockX(), playerMoveEvent.getTo().getBlockY(), playerMoveEvent.getTo().getBlockZ()), ticks2))) {
                boolean flag7 = false;
                boolean flag8 = false;
                double y4 = playerMoveEvent.getPlayer().getVelocity().getY();
                PlayerMoveEvent playerMoveEvent5 = playerMoveEvent;
                Player player8 = playerMoveEvent.getPlayer();
                this.plugin.setbackManager.applySetback(player8, playerMoveEvent5, y4, flag8, flag7);
            }
        } else {
            Vector vector;
            Vector vector5 = playerMoveEvent.getTo().clone().subtract(playerMoveEvent.getFrom()).toVector();
            Vector vector6 = vector = vector5.clone();
            Location location = playerMoveEvent.getFrom();
            Player player9 = playerMoveEvent.getPlayer();
            CollisionQueryResult collisionQueryResult = CollisionVectorAdjuster.adjustVectorForCollisions((Entity)player9, location, vector6);
            if (!vector.equals((Object)vector5)) {
                if (collisionQueryResult.getAdjustment() < 0.6) {
                    Location location2 = playerMoveEvent.getFrom().clone().add(0.0, collisionQueryResult.getAdjustment() + 0.01, 0.0);
                    vector5 = playerMoveEvent.getTo().clone().subtract(location2).toVector();
                    Vector vector7 = vector = vector5.clone();
                    Location location3 = location2;
                    Player player10 = playerMoveEvent.getPlayer();
                    collisionQueryResult = CollisionVectorAdjuster.adjustVectorForCollisions((Entity)player10, location3, vector7);
                    if (!vector.equals((Object)vector5)) {
                        AxisAlignedBoundingBox axisAlignedBoundingBox7 = (AxisAlignedBoundingBox)collisionQueryResult.getCollisions().get(0);
                        Block block = axisAlignedBoundingBox7.getMinimum().toLocation(playerMoveEvent.getPlayer().getWorld()).getBlock();
                        if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 1, HackType.PHASE, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Phase: stepped through a block (%s at %d,%d,%d)", block.getType().toString(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()), ticks2)) {
                            boolean flag9 = false;
                            boolean flag10 = false;
                            double y5 = playerMoveEvent.getPlayer().getVelocity().getY();
                            PlayerMoveEvent playerMoveEvent6 = playerMoveEvent;
                            Player player11 = playerMoveEvent.getPlayer();
                            this.plugin.setbackManager.applySetback(player11, playerMoveEvent6, y5, flag10, flag9);
                        }
                    }
                } else {
                    AxisAlignedBoundingBox axisAlignedBoundingBox8 = (AxisAlignedBoundingBox)collisionQueryResult.getCollisions().get(0);
                    Block block = axisAlignedBoundingBox8.getMinimum().toLocation(playerMoveEvent.getPlayer().getWorld()).getBlock();
                    if (this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 1, HackType.PHASE, String.format(Locale.UK, playerMoveEvent.getPlayer().getName() + " failed Phase: moved through a block (%s at %d,%d,%d)", block.getType().toString(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()), ticks2)) {
                        boolean flag11 = false;
                        boolean flag12 = false;
                        double y6 = playerMoveEvent.getPlayer().getVelocity().getY();
                        PlayerMoveEvent playerMoveEvent7 = playerMoveEvent;
                        Player player12 = playerMoveEvent.getPlayer();
                        this.plugin.setbackManager.applySetback(player12, playerMoveEvent7, y6, flag12, flag11);
                    }
                }
            }
        }
    }

    private boolean isExcludedToBlock(PlayerMoveEvent playerMoveEvent, AxisAlignedBoundingBox axisAlignedBoundingBox) {
        return this.excludedMaterials.contains(axisAlignedBoundingBox.getMinimum().toLocation(playerMoveEvent.getPlayer().getWorld()).getBlock().getType());
    }

    private boolean isExcludedFromBlock(PlayerMoveEvent playerMoveEvent, AxisAlignedBoundingBox axisAlignedBoundingBox) {
        return this.excludedMaterials.contains(axisAlignedBoundingBox.getMinimum().toLocation(playerMoveEvent.getPlayer().getWorld()).getBlock().getType());
    }

    private void decayPhaseViolations() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.decreaseViolationLevel(player.getUniqueId(), 1);
        }
    }
}

