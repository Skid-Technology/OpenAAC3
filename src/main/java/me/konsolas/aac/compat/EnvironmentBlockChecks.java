package me.konsolas.aac.compat;

import java.util.ArrayList;
import java.util.HashSet;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.util.GroundCollisionState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class EnvironmentBlockChecks {
    public static boolean isUseMaterial(Block block) {
        return AAC.serverVersion.isSolidUseMaterial(block.getType());
    }

    public static boolean isWalkableBlock(Block block) {
        if (block.getType() == Material.AIR || !block.getType().isBlock()) {
            return false;
        }
        if (block.getRelative(BlockFace.UP).getType() == Material.CARPET || block.getRelative(BlockFace.UP).getType() == Material.WATER_LILY) {
            return true;
        }
        return AAC.serverVersion.isBlockingMaterial(block.getType());
    }

    private static boolean isNonClimbableSolid(Block block) {
        return EnvironmentBlockChecks.isWalkableBlock(block) && block.getType() != Material.LADDER && block.getType() != Material.VINE;
    }

    private static boolean isSpecialWaterCollidable(Block block) {
        return EnvironmentBlockChecks.isNotSpecialShapeBlock(block) && EnvironmentBlockChecks.isWalkableBlock(block);
    }

    private static boolean isLiquidOrWaterCollidable(Block block) {
        return block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER || EnvironmentBlockChecks.isSpecialWaterCollidable(block);
    }

    public static boolean isHalfBlock(Material material) {
        switch (material) {
            case STEP: 
            case WOOD_STEP: 
            case DAYLIGHT_DETECTOR: 
            case STONE_SLAB2: 
            case WATER_LILY: 
            case SKULL: 
            case PURPUR_SLAB: {
                return true;
            }
        }
        return false;
    }

    private static boolean isStairMaterial(Material material) {
        switch (material) {
            case ACACIA_STAIRS: 
            case BIRCH_WOOD_STAIRS: 
            case BRICK_STAIRS: 
            case COBBLESTONE_STAIRS: 
            case DARK_OAK_STAIRS: 
            case JUNGLE_WOOD_STAIRS: 
            case NETHER_BRICK_STAIRS: 
            case QUARTZ_STAIRS: 
            case RED_SANDSTONE_STAIRS: 
            case SANDSTONE_STAIRS: 
            case SMOOTH_STAIRS: 
            case SPRUCE_WOOD_STAIRS: 
            case WOOD_STAIRS: 
            case PURPUR_STAIRS: {
                return true;
            }
        }
        return false;
    }

    private static boolean isSlabOrStair(Material material) {
        return EnvironmentBlockChecks.isHalfBlock(material) || EnvironmentBlockChecks.isStairMaterial(material);
    }

    private static boolean isFenceOrWall(Material material) {
        return material == Material.FENCE || material == Material.FENCE_GATE || material == Material.ACACIA_FENCE || material == Material.BIRCH_FENCE || material == Material.DARK_OAK_FENCE || material == Material.JUNGLE_FENCE || material == Material.NETHER_FENCE || material == Material.SPRUCE_FENCE || material == Material.ACACIA_FENCE_GATE || material == Material.BIRCH_FENCE_GATE || material == Material.JUNGLE_FENCE_GATE || material == Material.DARK_OAK_FENCE_GATE || material == Material.SPRUCE_FENCE_GATE || material == Material.COBBLE_WALL;
    }

    private static boolean isChest(Block block) {
        return block.getType() == Material.CHEST || block.getType() == Material.ENDER_CHEST || block.getType() == Material.TRAPPED_CHEST;
    }

    private static boolean isNotSpecialShapeBlock(Block block) {
        return !EnvironmentBlockChecks.isFenceOrWall(block.getType()) && !EnvironmentBlockChecks.isChest(block) && !CollisionVectorAdjuster.isStepThroughBlock(block);
    }

    private static boolean isStandingInSpecialEnvironment(BlockPositionView blockPositionView, double d, double d2) {
        byte by;
        boolean flag;
        boolean flag2;
        if (blockPositionView.getBlock().isLiquid() || blockPositionView.getBlockAtOffset(0.0, 1.0, 0.0).isLiquid() || blockPositionView.getBlockAtOffset(0.0, 1.7, 0.0).isLiquid() || blockPositionView.getBlockAtOffset(0.0, 2.5, 0.0).isLiquid()) {
            return true;
        }
        if (blockPositionView.getBlock().getType() == Material.WEB && blockPositionView.getY() % 1.0 != 0.0) {
            return false;
        }
        if (EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(0.0, d2, d)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(d, d2, 0.0)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(0.0, d2, -d)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(-d, d2, 0.0)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(-d, d2, -d)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(-d, d2, d)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(d, d2, -d)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(d, d2, d))) {
            return true;
        }
        if (EnvironmentBlockChecks.isChest(blockPositionView.getBlockAtNegOffset(0.0, d2, d - 0.65)) || EnvironmentBlockChecks.isChest(blockPositionView.getBlockAtNegOffset(d - 0.65, d2, 0.0)) || EnvironmentBlockChecks.isChest(blockPositionView.getBlockAtNegOffset(0.0, d2, -(d - 0.65))) || EnvironmentBlockChecks.isChest(blockPositionView.getBlockAtNegOffset(-(d - 0.65), d2, 0.0)) || EnvironmentBlockChecks.isChest(blockPositionView.getBlockAtNegOffset(-(d - 0.65), d2, -(d - 0.65))) || EnvironmentBlockChecks.isChest(blockPositionView.getBlockAtNegOffset(-(d - 0.65), d2, d - 0.65)) || EnvironmentBlockChecks.isChest(blockPositionView.getBlockAtNegOffset(d - 0.65, d2, -(d - 0.65))) || EnvironmentBlockChecks.isChest(blockPositionView.getBlockAtNegOffset(d - 0.65, d2, d - 0.65))) {
            return true;
        }
        boolean flag3 = flag2 = CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.0, d2 + 0.5, 0.1)) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.1, d2 + 0.5, 0.0)) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.0, d2 + 0.5, -0.1)) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-0.1, d2 + 0.5, 0.0)) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-0.1, d2 + 0.5, -0.1)) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-0.1, d2 + 0.5, 0.1)) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.1, d2 + 0.5, -0.1)) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.1, d2 + 0.5, 0.1));
        if (CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.0, d2, d)) && (!CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.0, d)) || flag2) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(d, d2, 0.0)) && (!CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(d, 0.0, 0.0)) || flag2) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.0, d2, -d)) && (!CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.0, -d)) || flag2) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-d, d2, 0.0)) && (!CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-d, 0.0, 0.0)) || flag2) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-d, d2, -d)) && (!CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-d, 0.0, -d)) || flag2) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-d, d2, d)) && (!CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(-d, 0.0, d)) || flag2) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(d, d2, -d)) && (!CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(d, 0.0, -d)) || flag2) || CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(d, d2, d)) && (!CollisionVectorAdjuster.isStepThroughBlock(blockPositionView.getBlockAtNegOffset(d, 0.0, d)) || flag2)) {
            return true;
        }
        boolean flag4 = flag = EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(0.0, d2 + 0.5, 0.1)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(0.1, d2 + 0.5, 0.0)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(0.0, d2 + 0.5, -0.1)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(-0.1, d2 + 0.5, 0.0)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(-0.1, d2 + 0.5, -0.1)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(-0.1, d2 + 0.5, 0.1)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(0.1, d2 + 0.5, -0.1)) || EnvironmentBlockChecks.isLiquidOrWaterCollidable(blockPositionView.getBlockAtNegOffset(0.1, d2 + 0.5, 0.1));
        if (EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(0.0, 0.6, d)) && (!EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(0.0, 0.3, d)) || flag) || EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(d, 0.6, 0.0)) && (!EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(d, 0.3, 0.0)) || flag) || EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(0.0, 0.6, -d)) && (!EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(0.0, 0.3, -d)) || flag) || EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(-d, 0.6, 0.0)) && (!EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(-d, 0.3, 0.0)) || flag) || EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(-d, 0.6, -d)) && (!EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(-d, 0.3, -d)) || flag) || EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(-d, 0.6, d)) && (!EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(-d, 0.3, d)) || flag) || EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(d, 0.6, -d)) && (!EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(d, 0.3, -d)) || flag) || EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(d, 0.6, d)) && (!EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getMaterialAtNegOffset(d, 0.3, d)) || flag)) {
            return true;
        }
        Block block = EnvironmentBlockChecks.findHighestThinBlockBelow(blockPositionView, d);
        double y = blockPositionView.getY();
        int value = (int)y;
        y -= (double)value;
        if (block != null && (block.getType() == Material.SNOW ? (by = block.getData()) > 0 && y - 0.1 < (double)by * 0.125 : EnvironmentBlockChecks.isRedstoneFlatBlock(block.getType()) && y - 0.1 < 0.125)) {
            return true;
        }
        return blockPositionView.getMaterialAtNegOffset(0.0, 0.6, 0.0) == Material.COBBLE_WALL && EnvironmentBlockChecks.isFenceOrWall(blockPositionView.getBlock().getType()) && blockPositionView.getBlock().getType() != Material.COBBLE_WALL;
    }

    public static boolean isOnEdgeOfHalfBlock(BlockPositionView blockPositionView) {
        return !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.0, 0.7)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.7, 0.0, 0.0)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.0, -0.7)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.7, 0.0, 0.0)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.7, 0.0, -0.7)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.7, 0.0, 0.7)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.7, 0.0, -0.7)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.7, 0.0, 0.7)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlock()) && (EnvironmentBlockChecks.isSpecialWaterCollidable(blockPositionView.getBlockAtNegOffset(0.0, 0.1, 0.7)) || EnvironmentBlockChecks.isSpecialWaterCollidable(blockPositionView.getBlockAtNegOffset(0.7, 0.1, 0.0)) || EnvironmentBlockChecks.isSpecialWaterCollidable(blockPositionView.getBlockAtNegOffset(0.0, 0.1, -0.7)) || EnvironmentBlockChecks.isSpecialWaterCollidable(blockPositionView.getBlockAtNegOffset(-0.7, 0.1, 0.0)) || EnvironmentBlockChecks.isSpecialWaterCollidable(blockPositionView.getBlockAtNegOffset(-0.7, 0.1, -0.7)) || EnvironmentBlockChecks.isSpecialWaterCollidable(blockPositionView.getBlockAtNegOffset(-0.7, 0.1, 0.7)) || EnvironmentBlockChecks.isSpecialWaterCollidable(blockPositionView.getBlockAtNegOffset(0.7, 0.1, -0.7)) || EnvironmentBlockChecks.isSpecialWaterCollidable(blockPositionView.getBlockAtNegOffset(0.7, 0.1, 0.7)));
    }

    public static boolean isNearWalkableBlock(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isNonClimbableSolid(blockPositionView.getBlockAtNegOffset(0.0, 0.1, 0.3)) || EnvironmentBlockChecks.isNonClimbableSolid(blockPositionView.getBlockAtNegOffset(0.3, 0.1, 0.0)) || EnvironmentBlockChecks.isNonClimbableSolid(blockPositionView.getBlockAtNegOffset(0.0, 0.1, -0.3)) || EnvironmentBlockChecks.isNonClimbableSolid(blockPositionView.getBlockAtNegOffset(-0.3, 0.1, 0.0)) || EnvironmentBlockChecks.isNonClimbableSolid(blockPositionView.getBlockAtNegOffset(-0.3, 0.1, -0.3)) || EnvironmentBlockChecks.isNonClimbableSolid(blockPositionView.getBlockAtNegOffset(-0.3, 0.1, 0.3)) || EnvironmentBlockChecks.isNonClimbableSolid(blockPositionView.getBlockAtNegOffset(0.3, 0.1, -0.3)) || EnvironmentBlockChecks.isNonClimbableSolid(blockPositionView.getBlockAtNegOffset(0.3, 0.1, 0.3));
    }

    private static Block findHighestThinBlockBelow(BlockPositionView blockPositionView, double d) {
        HashSet<Block> hashSet = new HashSet<Block>();
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, -0.1, d));
        hashSet.add(blockPositionView.getBlockAtOffset(d, -0.1, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, -0.1, -d));
        hashSet.add(blockPositionView.getBlockAtOffset(-d, -0.1, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(-d, -0.1, -d));
        hashSet.add(blockPositionView.getBlockAtOffset(-d, -0.1, d));
        hashSet.add(blockPositionView.getBlockAtOffset(d, -0.1, -d));
        hashSet.add(blockPositionView.getBlockAtOffset(d, -0.1, d));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, -0.1, 0.0));
        Block block = null;
        byte by = -1;
        for (Block block2 : hashSet) {
            byte by2;
            if (block2.getType() != Material.SNOW || (by2 = block2.getData()) <= by) continue;
            by = by2;
            block = block2;
        }
        for (Block block2 : hashSet) {
            if (!EnvironmentBlockChecks.isRedstoneFlatBlock(block2.getType()) || 1 <= by) continue;
            by = 1;
            block = block2;
        }
        return block;
    }

    private static boolean isClimbableMaterial(Material material) {
        return material == Material.LADDER || material == Material.VINE;
    }

    public static boolean isOnLadderOrVine(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.0, 0.0, 0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.3, 0.0, 0.0)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.0, 0.0, -0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(-0.3, 0.0, 0.0)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(-0.3, 0.0, -0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(-0.3, 0.0, 0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.3, 0.0, -0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.3, 0.0, 0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.0, 1.0, 0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.3, 1.0, 0.0)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.0, 1.0, -0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(-0.3, 1.0, 0.0)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(-0.3, 1.0, -0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(-0.3, 1.0, 0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.3, 1.0, -0.3)) || EnvironmentBlockChecks.isClimbableMaterial(blockPositionView.getMaterialAtNegOffset(0.3, 1.0, 0.3));
    }

    public static boolean isInLiquid(BlockPositionView blockPositionView) {
        return blockPositionView.getBlockAtOffset(0.0, 0.0, 0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 0.0, 0.0).isLiquid() || blockPositionView.getBlockAtOffset(0.0, 0.0, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 0.0, 0.0).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 0.0, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 0.0, 0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 0.0, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 0.0, 0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.0, 0.8, 0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 0.8, 0.0).isLiquid() || blockPositionView.getBlockAtOffset(0.0, 0.8, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 0.8, 0.0).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 0.8, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 0.8, 0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 0.8, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 0.8, 0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.0, 1.45, 0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 1.45, 0.0).isLiquid() || blockPositionView.getBlockAtOffset(0.0, 1.45, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 1.45, 0.0).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 1.45, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(-0.3, 1.45, 0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 1.45, -0.3).isLiquid() || blockPositionView.getBlockAtOffset(0.3, 1.45, 0.3).isLiquid();
    }

    private static boolean isRedstoneFlatBlock(Material material) {
        switch (material) {
            case DIODE_BLOCK_OFF: 
            case DIODE_BLOCK_ON: 
            case REDSTONE_COMPARATOR_OFF: 
            case REDSTONE_COMPARATOR_ON: {
                return true;
            }
        }
        return false;
    }

    public static boolean isNearShortBlock(BlockPositionView blockPositionView) {
        ArrayList<Material> arrayList = new ArrayList<Material>();
        arrayList.add(blockPositionView.getMaterialAtNegOffset(0.0, 0.0, 0.7));
        arrayList.add(blockPositionView.getMaterialAtNegOffset(0.7, 0.0, 0.0));
        arrayList.add(blockPositionView.getMaterialAtNegOffset(0.0, 0.0, -0.7));
        arrayList.add(blockPositionView.getMaterialAtNegOffset(-0.7, 0.0, 0.0));
        arrayList.add(blockPositionView.getMaterialAtNegOffset(-0.7, 0.0, -0.7));
        arrayList.add(blockPositionView.getMaterialAtNegOffset(-0.7, 0.0, 0.7));
        arrayList.add(blockPositionView.getMaterialAtNegOffset(0.7, 0.0, -0.7));
        arrayList.add(blockPositionView.getMaterialAtNegOffset(0.7, 0.0, 0.7));
        for (Material material : arrayList) {
            if (EnvironmentBlockChecks.isSlabOrStair(material)) {
                return true;
            }
            switch (material) {
                case DAYLIGHT_DETECTOR: 
                case SKULL: 
                case BREWING_STAND: 
                case SNOW: 
                case ANVIL: 
                case FLOWER_POT: 
                case DAYLIGHT_DETECTOR_INVERTED: 
                case THIN_GLASS: 
                case STAINED_GLASS_PANE: 
                case BED_BLOCK: 
                case TRAP_DOOR: 
                case IRON_TRAPDOOR: 
                case END_ROD: 
                case ENDER_PORTAL_FRAME: 
                case ENCHANTMENT_TABLE: {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInSnow(BlockPositionView blockPositionView) {
        return blockPositionView.getMaterialAtNegOffset(0.0, 0.1, 0.4) == Material.SNOW || blockPositionView.getMaterialAtNegOffset(0.4, 0.1, 0.0) == Material.SNOW || blockPositionView.getMaterialAtNegOffset(0.0, 0.1, -0.4) == Material.SNOW || blockPositionView.getMaterialAtNegOffset(-0.4, 0.1, 0.0) == Material.SNOW || blockPositionView.getMaterialAtNegOffset(-0.4, 0.1, -0.4) == Material.SNOW || blockPositionView.getMaterialAtNegOffset(-0.4, 0.1, 0.4) == Material.SNOW || blockPositionView.getMaterialAtNegOffset(0.4, 0.1, -0.4) == Material.SNOW || blockPositionView.getMaterialAtNegOffset(0.4, 0.1, 0.4) == Material.SNOW;
    }

    private static boolean isShulkerBox(Material material) {
        switch (material) {
            case WHITE_SHULKER_BOX: 
            case ORANGE_SHULKER_BOX: 
            case MAGENTA_SHULKER_BOX: 
            case LIGHT_BLUE_SHULKER_BOX: 
            case YELLOW_SHULKER_BOX: 
            case LIME_SHULKER_BOX: 
            case PINK_SHULKER_BOX: 
            case GRAY_SHULKER_BOX: 
            case SILVER_SHULKER_BOX: 
            case CYAN_SHULKER_BOX: 
            case PURPLE_SHULKER_BOX: 
            case BLUE_SHULKER_BOX: 
            case BROWN_SHULKER_BOX: 
            case GREEN_SHULKER_BOX: 
            case RED_SHULKER_BOX: 
            case BLACK_SHULKER_BOX: {
                return true;
            }
        }
        return false;
    }

    public static boolean isNearShulkerBox(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isShulkerBox(blockPositionView.getMaterialAtNegOffset(0.0, 0.9, 0.3)) || EnvironmentBlockChecks.isShulkerBox(blockPositionView.getMaterialAtNegOffset(0.3, 0.9, 0.0)) || EnvironmentBlockChecks.isShulkerBox(blockPositionView.getMaterialAtNegOffset(0.0, 0.9, -0.3)) || EnvironmentBlockChecks.isShulkerBox(blockPositionView.getMaterialAtNegOffset(-0.3, 0.9, 0.0)) || EnvironmentBlockChecks.isShulkerBox(blockPositionView.getMaterialAtNegOffset(-0.3, 0.9, -0.3)) || EnvironmentBlockChecks.isShulkerBox(blockPositionView.getMaterialAtNegOffset(-0.3, 0.9, 0.3)) || EnvironmentBlockChecks.isShulkerBox(blockPositionView.getMaterialAtNegOffset(0.3, 0.9, -0.3)) || EnvironmentBlockChecks.isShulkerBox(blockPositionView.getMaterialAtNegOffset(0.3, 0.9, 0.3));
    }

    public static boolean isNearLiquid(BlockPositionView blockPositionView) {
        return blockPositionView.getBlockAtNegOffset(0.0, 0.0, 0.4).isLiquid() || blockPositionView.getBlockAtNegOffset(0.4, 0.0, 0.0).isLiquid() || blockPositionView.getBlockAtNegOffset(0.0, 0.0, -0.4).isLiquid() || blockPositionView.getBlockAtNegOffset(-0.4, 0.0, 0.0).isLiquid() || blockPositionView.getBlockAtNegOffset(-0.4, 0.0, -0.4).isLiquid() || blockPositionView.getBlockAtNegOffset(-0.4, 0.0, 0.4).isLiquid() || blockPositionView.getBlockAtNegOffset(0.4, 0.0, -0.4).isLiquid() || blockPositionView.getBlockAtNegOffset(0.4, 0.0, 0.4).isLiquid();
    }

    public static boolean isNotSurroundedByLiquid(BlockPositionView blockPositionView) {
        return !blockPositionView.getBlockAtNegOffset(0.0, 0.0, 0.8).isLiquid() || !blockPositionView.getBlockAtNegOffset(0.8, 0.0, 0.0).isLiquid() || !blockPositionView.getBlockAtNegOffset(0.0, 0.0, -0.8).isLiquid() || !blockPositionView.getBlockAtNegOffset(-0.8, 0.0, 0.0).isLiquid() || !blockPositionView.getBlockAtNegOffset(-0.8, 0.0, -0.8).isLiquid() || !blockPositionView.getBlockAtNegOffset(-0.8, 0.0, 0.8).isLiquid() || !blockPositionView.getBlockAtNegOffset(0.8, 0.0, -0.8).isLiquid() || !blockPositionView.getBlockAtNegOffset(0.8, 0.0, 0.8).isLiquid() || !blockPositionView.getBlock().isLiquid();
    }

    public static boolean isNearSnowColumn(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isSlimeOrMovingPiston(blockPositionView.getBlockAtNegOffset(0.0, 0.0, 0.0)) || EnvironmentBlockChecks.isSlimeOrMovingPiston(blockPositionView.getBlockAtNegOffset(0.0, 1.0, 0.0)) || EnvironmentBlockChecks.isSlimeOrMovingPiston(blockPositionView.getBlockAtNegOffset(0.0, 2.0, 0.0)) || EnvironmentBlockChecks.isSlimeOrMovingPiston(blockPositionView.getBlockAtNegOffset(0.0, 3.0, 0.0)) || EnvironmentBlockChecks.isSlimeOrMovingPiston(blockPositionView.getBlockAtNegOffset(0.0, 4.0, 0.0));
    }

    public static boolean isStandingOnSoulSand(BlockPositionView blockPositionView) {
        return !(blockPositionView.getMaterialAtNegOffset(0.0, 0.8, 0.3) != Material.SOUL_SAND || blockPositionView.getMaterialAtNegOffset(0.3, 0.8, 0.0) != Material.SOUL_SAND || blockPositionView.getMaterialAtNegOffset(0.0, 0.8, -0.3) != Material.SOUL_SAND || blockPositionView.getMaterialAtNegOffset(-0.3, 0.8, 0.0) != Material.SOUL_SAND || blockPositionView.getMaterialAtNegOffset(-0.3, 0.8, -0.3) != Material.SOUL_SAND || blockPositionView.getMaterialAtNegOffset(-0.3, 0.8, 0.3) != Material.SOUL_SAND || blockPositionView.getMaterialAtNegOffset(0.3, 0.8, -0.3) != Material.SOUL_SAND || blockPositionView.getMaterialAtNegOffset(0.3, 0.8, 0.3) != Material.SOUL_SAND || !blockPositionView.getBlockAtNegOffset(0.0, 0.0, 0.3).isEmpty() && blockPositionView.getMaterialAtNegOffset(0.0, 0.0, 0.3) != Material.SOUL_SAND || !blockPositionView.getBlockAtNegOffset(0.3, 0.0, 0.0).isEmpty() && blockPositionView.getMaterialAtNegOffset(0.3, 0.0, 0.0) != Material.SOUL_SAND || !blockPositionView.getBlockAtNegOffset(0.0, 0.0, -0.3).isEmpty() && blockPositionView.getMaterialAtNegOffset(0.0, 0.0, -0.3) != Material.SOUL_SAND || !blockPositionView.getBlockAtNegOffset(-0.3, 0.0, 0.0).isEmpty() && blockPositionView.getMaterialAtNegOffset(-0.3, 0.0, 0.0) != Material.SOUL_SAND || !blockPositionView.getBlockAtNegOffset(-0.3, 0.0, -0.3).isEmpty() && blockPositionView.getMaterialAtNegOffset(-0.3, 0.0, -0.3) != Material.SOUL_SAND || !blockPositionView.getBlockAtNegOffset(-0.3, 0.0, 0.3).isEmpty() && blockPositionView.getMaterialAtNegOffset(-0.3, 0.0, 0.3) != Material.SOUL_SAND || !blockPositionView.getBlockAtNegOffset(0.3, 0.0, -0.3).isEmpty() && blockPositionView.getMaterialAtNegOffset(0.3, 0.0, -0.3) != Material.SOUL_SAND || !blockPositionView.getBlockAtNegOffset(0.3, 0.0, 0.3).isEmpty() && blockPositionView.getMaterialAtNegOffset(0.3, 0.0, 0.3) != Material.SOUL_SAND);
    }

    private static boolean isSlimeOrMovingPiston(Block block) {
        return block.getType() == Material.SLIME_BLOCK || block.getType() == Material.PISTON_MOVING_PIECE;
    }

    public static boolean isInFlowingLiquid(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.0, 0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.0, 0.0)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.0, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.0, 0.0)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.0, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.0, 0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.0, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.0, 0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.8, 0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.8, 0.0)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.8, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.8, 0.0)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.8, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.8, 0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.8, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.8, 0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.0, 1.6, 0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 1.6, 0.0)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.0, 1.6, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 1.6, 0.0)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 1.6, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(-0.5, 1.6, 0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 1.6, -0.5)) || EnvironmentBlockChecks.isFlowingLiquidBlock(blockPositionView.getBlockAtNegOffset(0.5, 1.6, 0.5));
    }

    private static boolean isFlowingLiquidBlock(Block block) {
        byte by = block.getData();
        return (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.STATIONARY_LAVA) && by != 0 && by != 8;
    }

    public static boolean isAboveOrOnSlimeBlock(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.1, 0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.1, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.0, 0.1, -0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.1, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.1, -0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.5, 0.1, 0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.1, -0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.5, 0.1, 0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.0, -0.2, 0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.5, -0.2, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.0, -0.2, -0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.5, -0.2, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.5, -0.2, -0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(-0.5, -0.2, 0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.5, -0.2, -0.5)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtNegOffset(0.5, -0.2, 0.5));
    }

    public static boolean isOnCarpet(BlockPositionView blockPositionView) {
        return blockPositionView.getMaterialAtNegOffset(0.0, 0.0, 0.49) == Material.CARPET || blockPositionView.getMaterialAtNegOffset(0.49, 0.0, 0.0) == Material.CARPET || blockPositionView.getMaterialAtNegOffset(0.0, 0.0, -0.49) == Material.CARPET || blockPositionView.getMaterialAtNegOffset(-0.49, 0.0, 0.0) == Material.CARPET || blockPositionView.getMaterialAtNegOffset(-0.49, 0.0, -0.49) == Material.CARPET || blockPositionView.getMaterialAtNegOffset(-0.49, 0.0, 0.49) == Material.CARPET || blockPositionView.getMaterialAtNegOffset(0.49, 0.0, -0.49) == Material.CARPET || blockPositionView.getMaterialAtNegOffset(0.49, 0.0, 0.49) == Material.CARPET;
    }

    public static boolean isOnWaterLily(BlockPositionView blockPositionView) {
        return blockPositionView.getMaterialAtNegOffset(0.0, 0.0, 0.49) == Material.WATER_LILY || blockPositionView.getMaterialAtNegOffset(0.49, 0.0, 0.0) == Material.WATER_LILY || blockPositionView.getMaterialAtNegOffset(0.0, 0.0, -0.49) == Material.WATER_LILY || blockPositionView.getMaterialAtNegOffset(-0.49, 0.0, 0.0) == Material.WATER_LILY || blockPositionView.getMaterialAtNegOffset(-0.49, 0.0, -0.49) == Material.WATER_LILY || blockPositionView.getMaterialAtNegOffset(-0.49, 0.0, 0.49) == Material.WATER_LILY || blockPositionView.getMaterialAtNegOffset(0.49, 0.0, -0.49) == Material.WATER_LILY || blockPositionView.getMaterialAtNegOffset(0.49, 0.0, 0.49) == Material.WATER_LILY;
    }

    public static boolean isVehicleOnGround(Entity entity) {
        GroundCollisionState groundCollisionState = GroundCollisionState.TRUE_NORMAL;
        Vector vector = new Vector(0, 0, 0);
        Location location = entity.getLocation();
        Entity entity2 = entity;
        Location location2 = entity.getLocation();
        if (CollisionAccess.checkGroundCollision(entity2, location2, location, vector, groundCollisionState).isOnGround()) {
            return true;
        }
        switch (entity.getType()) {
            case HORSE: {
                return EnvironmentBlockChecks.isStandingInSpecialEnvironment(new BlockPositionView(entity.getLocation()), 0.9, 0.1);
            }
            case PIG: {
                return EnvironmentBlockChecks.isStandingInSpecialEnvironment(new BlockPositionView(entity.getLocation()), 0.45, 0.1);
            }
            case BOAT: {
                return EnvironmentBlockChecks.isStandingInSpecialEnvironment(new BlockPositionView(entity.getLocation()), 0.6875, 0.1);
            }
        }
        return true;
    }

    public static boolean isClimbingWithLowCeiling(BlockPositionView blockPositionView, double d) {
        return EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 2.65, d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(d, 2.65, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 2.65, -d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-d, 2.65, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-d, 2.65, -d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-d, 2.65, d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(d, 2.65, -d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(d, 2.65, d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 2.65, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 1.65, d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(d, 1.65, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 1.65, -d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-d, 1.65, 0.0)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-d, 1.65, -d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-d, 1.65, d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(d, 1.65, -d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(d, 1.65, d)) || EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 1.65, 0.0));
    }

    public static boolean isNearStairs(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtOffset(0.0, -0.5, 0.5)) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtOffset(0.5, -0.5, 0.0)) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtOffset(0.0, -0.5, -0.5)) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtOffset(-0.5, -0.5, 0.0)) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtOffset(-0.5, -0.5, -0.5)) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtOffset(-0.5, -0.5, 0.5)) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtOffset(0.5, -0.5, -0.5)) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtOffset(0.5, -0.5, 0.5)) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getBlock().getType()) || EnvironmentBlockChecks.isStairMaterial(blockPositionView.getMaterialAtNegOffset(0.0, 1.5, 0.0));
    }

    public static boolean isNearSlabOrStair(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtOffset(0.0, -0.5, 0.5)) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtOffset(0.5, -0.5, 0.0)) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtOffset(0.0, -0.5, -0.5)) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtOffset(-0.5, -0.5, 0.0)) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtOffset(-0.5, -0.5, -0.5)) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtOffset(-0.5, -0.5, 0.5)) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtOffset(0.5, -0.5, -0.5)) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtOffset(0.5, -0.5, 0.5)) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getBlock().getType()) || EnvironmentBlockChecks.isSlabOrStair(blockPositionView.getMaterialAtNegOffset(0.0, 1.5, 0.0));
    }

    public static boolean isNearRedstoneRepeater(BlockPositionView blockPositionView) {
        return EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getMaterialAtNegOffset(0.0, 0.0, 0.4)) || EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getMaterialAtNegOffset(0.4, 0.0, 0.0)) || EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getMaterialAtNegOffset(0.0, 0.0, -0.4)) || EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getMaterialAtNegOffset(-0.4, 0.0, 0.0)) || EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getMaterialAtNegOffset(-0.4, 0.0, -0.4)) || EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getMaterialAtNegOffset(-0.4, 0.0, 0.4)) || EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getMaterialAtNegOffset(0.4, 0.0, -0.4)) || EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getMaterialAtNegOffset(0.4, 0.0, 0.4)) || EnvironmentBlockChecks.isRedstoneFlatBlock(blockPositionView.getBlock().getType());
    }

    public static boolean isInCobweb(BlockPositionView blockPositionView) {
        return blockPositionView.getMaterialAtNegOffset(0.0, 0.0, 0.1) == Material.WEB || blockPositionView.getMaterialAtNegOffset(0.1, 0.0, 0.0) == Material.WEB || blockPositionView.getMaterialAtNegOffset(0.0, 0.0, -0.1) == Material.WEB || blockPositionView.getMaterialAtNegOffset(-0.1, 0.0, 0.0) == Material.WEB || blockPositionView.getMaterialAtNegOffset(-0.1, 0.0, -0.1) == Material.WEB || blockPositionView.getMaterialAtNegOffset(-0.1, 0.0, 0.1) == Material.WEB || blockPositionView.getMaterialAtNegOffset(0.1, 0.0, -0.1) == Material.WEB || blockPositionView.getMaterialAtNegOffset(0.1, 0.0, 0.1) == Material.WEB || blockPositionView.getMaterialAtOffset(0.0, 1.0, 0.1) == Material.WEB || blockPositionView.getMaterialAtOffset(0.1, 1.0, 0.0) == Material.WEB || blockPositionView.getMaterialAtOffset(0.0, 1.0, -0.1) == Material.WEB || blockPositionView.getMaterialAtOffset(-0.1, 1.0, 0.0) == Material.WEB || blockPositionView.getMaterialAtOffset(-0.1, 1.0, -0.1) == Material.WEB || blockPositionView.getMaterialAtOffset(-0.1, 1.0, 0.1) == Material.WEB || blockPositionView.getMaterialAtOffset(0.1, 1.0, -0.1) == Material.WEB || blockPositionView.getMaterialAtOffset(0.1, 1.0, 0.1) == Material.WEB;
    }

    public static boolean hasClearHeadroomOnHeadHit(PlayerMoveEvent playerMoveEvent, boolean flag, boolean flag2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2) {
        return !flag || !blockPositionView2.getBlockAtOffset(0.0, 3.0, 0.0).getType().isSolid() && !blockPositionView2.getBlockAtOffset(0.0, 2.0, 0.0).getType().isSolid() || !blockPositionView2.getBlockAtNegOffset(0.0, 1.0, 0.0).getType().isSolid() && !blockPositionView2.getBlockAtNegOffset(0.0, 2.0, 0.0).getType().isSolid();
    }

    public static boolean isPlayerInCobweb(BlockPositionView blockPositionView) {
        HashSet<Block> hashSet = new HashSet<Block>();
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 0.0, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 0.0, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 0.0, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 0.0, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 0.0, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 0.0, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 0.0, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 0.0, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 0.0, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 0.9, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 0.9, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 0.9, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 0.9, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 0.9, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 0.9, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 0.9, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 0.9, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 0.9, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 1.8, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 1.8, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 1.8, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 1.8, 0.0));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 1.8, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(-0.3, 1.8, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 1.8, -0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.3, 1.8, 0.3));
        hashSet.add(blockPositionView.getBlockAtOffset(0.0, 1.8, 0.0));
        for (Block block : hashSet) {
            if (!EnvironmentBlockChecks.isCobweb(block)) continue;
            return true;
        }
        return false;
    }

    public static boolean isNearPiston(BlockPositionView blockPositionView) {
        HashSet<Block> hashSet = new HashSet<Block>();
        hashSet.add(blockPositionView.getBlockAtNegOffset(0.0, 0.1, 0.3));
        hashSet.add(blockPositionView.getBlockAtNegOffset(0.3, 0.1, 0.0));
        hashSet.add(blockPositionView.getBlockAtNegOffset(0.0, 0.1, -0.3));
        hashSet.add(blockPositionView.getBlockAtNegOffset(-0.3, 0.1, 0.0));
        hashSet.add(blockPositionView.getBlockAtNegOffset(-0.3, 0.1, -0.3));
        hashSet.add(blockPositionView.getBlockAtNegOffset(-0.3, 0.1, 0.3));
        hashSet.add(blockPositionView.getBlockAtNegOffset(0.3, 0.1, -0.3));
        hashSet.add(blockPositionView.getBlockAtNegOffset(0.3, 0.1, 0.3));
        hashSet.add(blockPositionView.getBlockAtNegOffset(0.0, 0.1, 0.0));
        for (Block block : hashSet) {
            Block block2 = block.getRelative(BlockFace.UP);
            Block block3 = block.getRelative(BlockFace.DOWN);
            if (!EnvironmentBlockChecks.isPistonBlock(block) && !EnvironmentBlockChecks.isPistonBlock(block3) && !EnvironmentBlockChecks.isPistonBlock(block2)) continue;
            return true;
        }
        return false;
    }

    private static boolean isCobweb(Block block) {
        return block != null && block.getType() == Material.WEB;
    }

    private static boolean isPistonBlock(Block block) {
        return block.getType() == Material.PISTON_BASE || block.getType() == Material.PISTON_EXTENSION || block.getType() == Material.PISTON_MOVING_PIECE || block.getType() == Material.PISTON_STICKY_BASE;
    }
}

