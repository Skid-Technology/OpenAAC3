package me.konsolas.aac.checks.world;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.compat.BlockBreakSpeedAccess;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.checks.combat.CombatCheckListener;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.EntityDimensions;
import me.konsolas.aac.util.EntityTypeChecks;
import me.konsolas.aac.compat.ItemUseAccess;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class Interact
extends Detector
implements CombatCheckListener {
    private boolean cancelLiquids;
    private boolean cancelGhostBlock;
    private boolean cancelDead;
    private boolean cancelItemAttack;
    private boolean cancelGhostAttack;
    private int cancelVlInteract;
    private int cancelVlPlace;
    private int cancelVlBreak;

    public Interact(AAC aAC) {        super(aAC);

        this.cancelLiquids = true;
        this.cancelGhostBlock = true;
        this.cancelDead = true;
        this.cancelItemAttack = true;
        this.cancelGhostAttack = true;
        this.cancelVlInteract = 5;
        this.cancelVlPlace = 5;
        this.cancelVlBreak = 0;
        this.cancelLiquids = this.plugin.getConfig().getBoolean("interact.cancel_liquids", this.cancelLiquids);
        this.cancelGhostBlock = this.plugin.getConfig().getBoolean("interact.cancel_ghost_block", this.cancelGhostBlock);
        this.cancelDead = this.plugin.getConfig().getBoolean("interact.cancel_dead", this.cancelDead);
        this.cancelItemAttack = this.plugin.getConfig().getBoolean("interact.cancel_item_attack", this.cancelItemAttack);
        this.cancelGhostAttack = this.plugin.getConfig().getBoolean("interact.cancel_ghost_attack", this.cancelGhostAttack);
        this.cancelVlInteract = this.plugin.getConfig().getInt("interact.cancel_vl_interact", this.cancelVlInteract);
        this.cancelVlPlace = this.plugin.getConfig().getInt("interact.cancel_vl_place", this.cancelVlPlace);
        this.cancelVlBreak = this.plugin.getConfig().getInt("interact.cancel_vl_break", this.cancelVlBreak);
        Server server = this.plugin.getServer();
        this.scheduledTaskIds.add(server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::decayInteractViolations, 19L, 43L));
    }

    @Override
    public boolean shouldProcessCombat(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        return entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER;
    }

    @Override
    public void onCombat(EntityDamageByEntityEvent entityDamageByEntityEvent, long ticks) {
        long ticks2 = ticks ^ 0x7B735CA5F5C9L;
        Player player = (Player)entityDamageByEntityEvent.getDamager();
        Player player2 = player;
        if (this.plugin.checkManager.isPlayerExempt(player2)) {
            return;
        }
        if (player.isDead() && this.cancelDead) {
            if (this.recordViolation(player.getUniqueId(), 1, HackType.INTERACT, player.getName() + " failed Interact: attacked whilst dead", ticks2)) {
                entityDamageByEntityEvent.setCancelled(true);
            }
        } else {
            Player player3 = player;
            if (ItemUseAccess.isUsingItem(player3) && this.cancelItemAttack) {
                if (this.recordViolation(player.getUniqueId(), 1, HackType.INTERACT, player.getName() + " failed Interact: attacked whilst using an item", ticks2)) {
                    entityDamageByEntityEvent.setCancelled(true);
                }
            } else if (this.cancelGhostAttack) {
                if (!EntityTypeChecks.isCommonCombatTarget(entityDamageByEntityEvent.getEntityType()) && !EntityTypeChecks.isStandardMob(entityDamageByEntityEvent.getEntityType())) {
                    return;
                }
                Vector vector = ((Player)entityDamageByEntityEvent.getDamager()).getEyeLocation().toVector();
                Vector vector2 = entityDamageByEntityEvent.getDamager().getLocation().getDirection();
                Entity entity = entityDamageByEntityEvent.getEntity();
                AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(entityDamageByEntityEvent.getEntity().getLocation().toVector(), EntityDimensions.forEntity(entity).scale(2.0));
                Entity entity2 = entityDamageByEntityEvent.getDamager();
                AxisAlignedBoundingBox axisAlignedBoundingBox2 = new AxisAlignedBoundingBox(vector, EntityDimensions.forEntity(entity2)).union(axisAlignedBoundingBox);
                Entity entity3 = entityDamageByEntityEvent.getDamager();
                AxisAlignedBoundingBox axisAlignedBoundingBox3 = this.findClosestHitBox(vector, vector2, CollisionAccess.getCollidingBoxes(entity3, axisAlignedBoundingBox2));
                if (axisAlignedBoundingBox3 != null && axisAlignedBoundingBox3.rayTraceDistance(vector, vector2) < axisAlignedBoundingBox.rayTraceDistance(vector, vector2) && this.recordViolation(player.getUniqueId(), 2, HackType.INTERACT, player.getName() + " failed Interact: attacked a player-like entity through a solid wall", ticks2)) {
                    entityDamageByEntityEvent.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        long ticks = 113218805911140L;
        if (playerInteractEvent.getClickedBlock() == null || playerInteractEvent.getClickedBlock().getType() == Material.AIR) {
            return;
        }
        if ((playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) && this.cancelGhostBlock) {
            Block block = null;
            Block block2 = playerInteractEvent.getClickedBlock();
            Player player = playerInteractEvent.getPlayer();
            if (this.checkLineOfSight(player, block2, block) && this.recordViolation(playerInteractEvent.getPlayer().getUniqueId(), playerInteractEvent.getItem() != null && playerInteractEvent.getItem().getType() == Material.MONSTER_EGG ? 2 : 0, HackType.INTERACT, playerInteractEvent.getPlayer().getName() + " failed Interact: interacted with a block out of his line of sight", ticks) && this.getViolationLevel(playerInteractEvent.getPlayer()) >= this.cancelVlInteract) {
                playerInteractEvent.setCancelled(true);
            }
        }
        if (playerInteractEvent.getPlayer().isDead() && this.cancelDead && this.recordViolation(playerInteractEvent.getPlayer().getUniqueId(), 1, HackType.INTERACT, playerInteractEvent.getPlayer().getName() + " failed Interact: interacted whilst dead", ticks)) {
            playerInteractEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player;
        long ticks = 113218805911140L;
        if (blockBreakEvent.getBlock() == null || this.plugin.checkManager.isPlayerExempt(player = blockBreakEvent.getPlayer())) {
            return;
        }
        if (BlockBreakSpeedAccess.getBreakTicks(blockBreakEvent.getBlock(), blockBreakEvent.getPlayer()) <= 0L || blockBreakEvent.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (this.cancelGhostBlock) {
            Block block = blockBreakEvent.getBlock();
            Block block2 = null;
            Block block3 = block;
            Player player2 = blockBreakEvent.getPlayer();
            if (this.checkLineOfSight(player2, block3, block2) && this.recordViolation(blockBreakEvent.getPlayer().getUniqueId(), 2, HackType.INTERACT, blockBreakEvent.getPlayer().getName() + " failed Interact: broke a block out of line of sight", ticks) && this.getViolationLevel(blockBreakEvent.getPlayer()) > this.cancelVlBreak) {
                blockBreakEvent.setCancelled(true);
            }
        }
        this.decreaseViolationLevel(blockBreakEvent.getPlayer().getUniqueId(), 1);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        long ticks = 113218805911140L;
        Player player = blockPlaceEvent.getPlayer();
        if (this.plugin.checkManager.isPlayerExempt(player)) {
            return;
        }
        Material material = blockPlaceEvent.getBlockAgainst().getType();
        if ((material == Material.LAVA || material == Material.WATER || material == Material.STATIONARY_LAVA || material == Material.STATIONARY_WATER) && this.cancelLiquids && blockPlaceEvent.getPlayer().getItemInHand().getType() != Material.WATER_LILY && this.recordViolation(blockPlaceEvent.getPlayer().getUniqueId(), 2, HackType.INTERACT, blockPlaceEvent.getPlayer().getName() + " failed Interact: interacted with a liquid", ticks)) {
            blockPlaceEvent.setCancelled(true);
        }
        if (this.cancelGhostBlock) {
            Block block = blockPlaceEvent.getBlockAgainst();
            Block block2 = blockPlaceEvent.getBlockPlaced();
            if (blockPlaceEvent.getBlockPlaced().equals(blockPlaceEvent.getBlockAgainst())) {
                block2 = null;
            }
            Block block3 = block2;
            Block block4 = block;
            Player player2 = blockPlaceEvent.getPlayer();
            if (this.checkLineOfSight(player2, block4, block3) && this.recordViolation(blockPlaceEvent.getPlayer().getUniqueId(), 2, HackType.INTERACT, blockPlaceEvent.getPlayer().getName() + " failed Interact: placed a block out of line of sight", ticks) && this.getViolationLevel(blockPlaceEvent.getPlayer()) > this.cancelVlPlace) {
                blockPlaceEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {
        long ticks = 113218805911140L;
        if (this.cancelGhostBlock) {
            Block block;
            Block block2;
            Block block3;
            Player player;
            Player player2 = (Player)inventoryOpenEvent.getPlayer();
            Player player3 = player2;
            if (this.plugin.checkManager.isPlayerExempt(player3)) {
                return;
            }
            try {
                if (inventoryOpenEvent.getInventory() == null || inventoryOpenEvent.getInventory().getHolder() == null) {
                    return;
                }
            }
            catch (Exception exception) {
                Bukkit.getLogger().warning("[AAC] Failed to cast inventory holder to player: " + exception.getMessage());
                return;
            }
            if (inventoryOpenEvent.getInventory().getHolder() instanceof Minecart || inventoryOpenEvent.getInventory().getHolder() instanceof LivingEntity) {
                return;
            }
            if (inventoryOpenEvent.getInventory().getHolder() instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest)inventoryOpenEvent.getInventory().getHolder();
                Block block4 = null;
                Player player4 = player2;
                Block block5 = ((BlockState)doubleChest.getLeftSide()).getBlock();
                if (this.checkLineOfSight(player4, block5, block4)) {
                    Block block6 = null;
                    Block block7 = ((BlockState)doubleChest.getRightSide()).getBlock();
                    Player player5 = (Player)inventoryOpenEvent.getPlayer();
                    if (this.checkLineOfSight(player5, block7, block6) && this.recordViolation(inventoryOpenEvent.getPlayer().getUniqueId(), 2, HackType.INTERACT, inventoryOpenEvent.getPlayer().getName() + " failed Interact: opened a container out of line of sight", ticks)) {
                        inventoryOpenEvent.setCancelled(true);
                    }
                }
            } else if (inventoryOpenEvent.getInventory().getHolder() instanceof BlockState && this.checkLineOfSight(player = player2, block3 = (block2 = ((BlockState)inventoryOpenEvent.getInventory().getHolder()).getBlock()), block = null) && this.recordViolation(inventoryOpenEvent.getPlayer().getUniqueId(), 2, HackType.INTERACT, inventoryOpenEvent.getPlayer().getName() + " failed Interact: opened a container out of line of sight", ticks)) {
                inventoryOpenEvent.setCancelled(true);
            }
        }
    }

    private boolean checkLineOfSight(Player player, Block block, Block block2) {
        Player player2 = player;
        AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(player.getLocation().toVector(), EntityDimensions.forEntity((Entity)player2));
        AxisAlignedBoundingBox axisAlignedBoundingBox2 = axisAlignedBoundingBox.union(new AxisAlignedBoundingBox(block.getLocation()));
        Player player3 = player;
        List list = CollisionAccess.getCollidingBoxes((Entity)player3, axisAlignedBoundingBox2);
        list.add(new AxisAlignedBoundingBox(block.getLocation()));
        Vector vector = player.getEyeLocation().toVector();
        Vector vector2 = player.getLocation().getDirection();
        if (this.isPathClearToTarget(vector, vector2, list, block, block2)) {
            Vector vector3 = block.getLocation().add(0.5, 0.5, 0.5).subtract(player.getEyeLocation()).toVector().normalize();
            if (vector3.distance(vector2) > 0.5) {
                return true;
            }
            for (BlockFace blockFace : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN}) {
                Vector vector4 = block.getLocation().add((double)blockFace.getModX() * 0.5, (double)blockFace.getModY() * 0.5, (double)blockFace.getModZ() * 0.5).add(0.5, 0.5, 0.5).subtract(player.getEyeLocation()).toVector().normalize();
                if (this.isPathClearToTarget(vector, vector4, list, block, block2)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean isPathClearToTarget(Vector vector, Vector vector2, List<AxisAlignedBoundingBox> list, Block block, Block block2) {
        AxisAlignedBoundingBox axisAlignedBoundingBox = this.findClosestHitBox(vector, vector2, block2 == null ? list : list.stream().filter(arg_0 -> Interact.isNotTargetBlockBox(block2, arg_0)).collect(Collectors.toList()));
        if (axisAlignedBoundingBox == null) {
            return true;
        }
        if (block2 != null && !new AxisAlignedBoundingBox(block2.getLocation()).intersectsRay(vector, vector2)) {
            return true;
        }
        if (!axisAlignedBoundingBox.getMinimum().toLocation(block.getWorld()).getBlock().equals(block)) {
            return true;
        }
        if (block2 == null) return false;
        AxisAlignedBoundingBox axisAlignedBoundingBox2 = new AxisAlignedBoundingBox(block2.getLocation());
        if (!(axisAlignedBoundingBox.rayTraceDistance(vector, vector2) < axisAlignedBoundingBox2.rayTraceDistance(vector, vector2))) return false;
        return true;
    }

    private AxisAlignedBoundingBox findClosestHitBox(Vector vector, Vector vector2, List<AxisAlignedBoundingBox> list) {
        if (list.isEmpty()) {
            return null;
        }
        double d = Double.MAX_VALUE;
        AxisAlignedBoundingBox axisAlignedBoundingBox = null;
        for (AxisAlignedBoundingBox axisAlignedBoundingBox2 : list) {
            double d2 = axisAlignedBoundingBox2.rayTraceDistance(vector, vector2);
            if (Double.isNaN(d2) || !(d2 < d)) continue;
            d = d2;
            axisAlignedBoundingBox = axisAlignedBoundingBox2;
        }
        return axisAlignedBoundingBox;
    }

    private static boolean isNotTargetBlockBox(Block block, AxisAlignedBoundingBox axisAlignedBoundingBox) {
        return !axisAlignedBoundingBox.getMinimum().toLocation(block.getWorld()).getBlock().equals(block);
    }

    private void decayInteractViolations() {
        Iterator iterator = Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.decreaseViolationLevel(player.getUniqueId(), 1);
        }
    }
}

