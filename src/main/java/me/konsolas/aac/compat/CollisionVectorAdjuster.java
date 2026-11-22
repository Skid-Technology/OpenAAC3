package me.konsolas.aac.compat;

import java.util.HashMap;
import java.util.List;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.util.EntityDimensions;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CollisionVectorAdjuster {
    public static boolean isPassableSpecialBlock(Block block) {
        switch (CollisionMaterialSwitch.MATERIAL_ORDINALS[block.getType().ordinal()]) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                return true;
            }
        }
        return false;
    }

    public static boolean isStepThroughBlock(Block block) {
        switch (CollisionMaterialSwitch.MATERIAL_ORDINALS[block.getType().ordinal()]) {
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: {
                return true;
            }
        }
        return false;
    }

    public static CollisionQueryResult adjustVectorForCollisions(Entity entity, Location location, Vector vector) {
        double d;
        Entity entity2 = entity;
        EntityDimensions entityDimensions = EntityDimensions.forEntity(entity2);
        if (entity instanceof Player && AAC.serverVersion.getVersion() != ServerVersion.VER_1_8) {
            entityDimensions = new EntityDimensions(0.6, ((Player)entity).isSneaking() ? 1.65 : 1.8);
        }
        AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(location.toVector(), entityDimensions);
        AxisAlignedBoundingBox axisAlignedBoundingBox2 = axisAlignedBoundingBox.expandTowards(vector);
        Entity entity3 = entity;
        List<AxisAlignedBoundingBox> list = CollisionAccess.getCollidingBoxes(entity3, axisAlignedBoundingBox2);
        double y = d = location.getY();
        if (!list.isEmpty()) {
            HashMap<CollisionOffsetPair, CollisionAdjustmentCandidates> iterator = new HashMap<>();
            block0: for (AxisAlignedBoundingBox axisAlignedBoundingBox3 : list) {
                CollisionAdjustmentCandidates collisionAdjustmentCandidates = iterator.computeIfAbsent(new CollisionOffsetPair((int)Math.floor(axisAlignedBoundingBox3.getMinimum().getX()), (int)Math.floor(axisAlignedBoundingBox3.getMinimum().getZ())), CollisionVectorAdjuster::createAdjustmentCandidates);
                if (CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).size() == 0) {
                    CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).add(axisAlignedBoundingBox3);
                    CollisionAdjustmentCandidates.setBestDistance(collisionAdjustmentCandidates, axisAlignedBoundingBox3.getMaximum().getY());
                }
                for (int i = 0; i < CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).size(); ++i) {
                    if (!(axisAlignedBoundingBox3.getMaximum().getY() > ((AxisAlignedBoundingBox)CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).get(i)).getMaximum().getY())) continue;
                    CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).add(i, axisAlignedBoundingBox3);
                    if (axisAlignedBoundingBox3.getMaximum().getY() < CollisionAdjustmentCandidates.getBestDistance(collisionAdjustmentCandidates)) {
                        if (i != CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).size() - 1 && !(((AxisAlignedBoundingBox)CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).get(i + 1)).getMinimum().getY() >= axisAlignedBoundingBox3.getMaximum().getY() + entityDimensions.getHeight())) continue block0;
                        CollisionAdjustmentCandidates.setBestDistance(collisionAdjustmentCandidates, axisAlignedBoundingBox3.getMaximum().getY());
                        continue block0;
                    }
                    if (!(axisAlignedBoundingBox3.getMinimum().getY() < CollisionAdjustmentCandidates.getBestDistance(collisionAdjustmentCandidates) + entityDimensions.getHeight())) continue block0;
                    for (int j = i; j < CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).size(); ++j) {
                        if (j == CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).size() - 1) {
                            CollisionAdjustmentCandidates.setBestDistance(collisionAdjustmentCandidates, ((AxisAlignedBoundingBox)CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).get(j)).getMaximum().getY());
                            continue;
                        }
                        if (!(((AxisAlignedBoundingBox)CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).get(j + 1)).getMinimum().getY() >= ((AxisAlignedBoundingBox)CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).get(j)).getMaximum().getY() + entityDimensions.getHeight())) continue;
                        CollisionAdjustmentCandidates.setBestDistance(collisionAdjustmentCandidates, ((AxisAlignedBoundingBox)CollisionAdjustmentCandidates.getCandidates(collisionAdjustmentCandidates).get(j)).getMaximum().getY());
                        continue block0;
                    }
                    continue block0;
                }
            }
            y = iterator.values().stream().mapToDouble(CollisionVectorAdjuster::extractBestHeight).min().orElse(d);
            for (Object obj : list) {
                double d2;
                if (!(axisAlignedBoundingBox.getMaximum().getX() > ((AxisAlignedBoundingBox)obj).getMinimum().getX()) || !(axisAlignedBoundingBox.getMinimum().getX() < ((AxisAlignedBoundingBox)obj).getMaximum().getX()) || !(axisAlignedBoundingBox.getMaximum().getZ() > ((AxisAlignedBoundingBox)obj).getMinimum().getZ()) || !(axisAlignedBoundingBox.getMinimum().getZ() < ((AxisAlignedBoundingBox)obj).getMaximum().getZ())) continue;
                if (vector.getY() > 0.0 && axisAlignedBoundingBox.getMaximum().getY() <= ((AxisAlignedBoundingBox)obj).getMinimum().getY()) {
                    d2 = ((AxisAlignedBoundingBox)obj).getMinimum().getY() - axisAlignedBoundingBox.getMaximum().getY();
                    if (!(d2 < vector.getY())) continue;
                    vector.setY(d2);
                    continue;
                }
                if (!(vector.getY() < 0.0) || !(axisAlignedBoundingBox.getMinimum().getY() >= ((AxisAlignedBoundingBox)obj).getMaximum().getY()) || !((d2 = ((AxisAlignedBoundingBox)obj).getMaximum().getY() - axisAlignedBoundingBox.getMinimum().getY()) > vector.getY())) continue;
                vector.setY(d2);
            }
            axisAlignedBoundingBox = axisAlignedBoundingBox.offset(new Vector(0.0, vector.getY(), 0.0));
            for (Object obj2 : list) {
                double d3;
                if (!(axisAlignedBoundingBox.getMaximum().getY() > ((AxisAlignedBoundingBox)obj2).getMinimum().getY()) || !(axisAlignedBoundingBox.getMinimum().getY() < ((AxisAlignedBoundingBox)obj2).getMaximum().getY()) || !(axisAlignedBoundingBox.getMaximum().getZ() > ((AxisAlignedBoundingBox)obj2).getMinimum().getZ()) || !(axisAlignedBoundingBox.getMinimum().getZ() < ((AxisAlignedBoundingBox)obj2).getMaximum().getZ())) continue;
                if (vector.getX() > 0.0 && axisAlignedBoundingBox.getMaximum().getX() <= ((AxisAlignedBoundingBox)obj2).getMinimum().getX()) {
                    d3 = ((AxisAlignedBoundingBox)obj2).getMinimum().getX() - axisAlignedBoundingBox.getMaximum().getX();
                    if (!(d3 < vector.getX())) continue;
                    vector.setX(d3);
                    continue;
                }
                if (!(vector.getX() < 0.0) || !(axisAlignedBoundingBox.getMinimum().getX() >= ((AxisAlignedBoundingBox)obj2).getMaximum().getX()) || !((d3 = ((AxisAlignedBoundingBox)obj2).getMaximum().getX() - axisAlignedBoundingBox.getMinimum().getX()) > vector.getX())) continue;
                vector.setX(d3);
            }
            axisAlignedBoundingBox = axisAlignedBoundingBox.offset(new Vector(vector.getX(), 0.0, 0.0));
            for (Object obj3 : list) {
                double d4;
                if (!(axisAlignedBoundingBox.getMaximum().getY() > ((AxisAlignedBoundingBox)obj3).getMinimum().getY()) || !(axisAlignedBoundingBox.getMinimum().getY() < ((AxisAlignedBoundingBox)obj3).getMaximum().getY()) || !(axisAlignedBoundingBox.getMaximum().getX() > ((AxisAlignedBoundingBox)obj3).getMinimum().getX()) || !(axisAlignedBoundingBox.getMinimum().getX() < ((AxisAlignedBoundingBox)obj3).getMaximum().getX())) continue;
                if (vector.getZ() > 0.0 && axisAlignedBoundingBox.getMaximum().getZ() <= ((AxisAlignedBoundingBox)obj3).getMinimum().getZ()) {
                    d4 = ((AxisAlignedBoundingBox)obj3).getMinimum().getZ() - axisAlignedBoundingBox.getMaximum().getZ();
                    if (!(d4 < vector.getZ())) continue;
                    vector.setZ(d4);
                    continue;
                }
                if (!(vector.getZ() < 0.0) || !(axisAlignedBoundingBox.getMinimum().getZ() >= ((AxisAlignedBoundingBox)obj3).getMaximum().getZ()) || !((d4 = ((AxisAlignedBoundingBox)obj3).getMaximum().getZ() - axisAlignedBoundingBox.getMinimum().getZ()) > vector.getZ())) continue;
                vector.setZ(d4);
            }
            axisAlignedBoundingBox = axisAlignedBoundingBox.offset(new Vector(0.0, 0.0, vector.getZ()));
        }
        return new CollisionQueryResult(location.getWorld(), axisAlignedBoundingBox, list, y - d, null);
    }

    public static boolean isClearColumn(BlockPositionView blockPositionView) {
        return !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.0, 0.0, 0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 0.0, 0.0)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.0, 0.0, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 0.0, 0.0)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 0.0, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 0.0, 0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 0.0, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 0.0, 0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.0, 1.64, 0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 1.64, 0.0)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.0, 1.64, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 1.64, 0.0)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 1.64, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 1.64, 0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 1.64, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 1.64, 0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.0, 0.9, 0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 0.9, 0.0)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.0, 0.9, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 0.9, 0.0)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 0.9, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(-0.3, 0.9, 0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 0.9, -0.3)) && !EnvironmentBlockChecks.isUseMaterial(blockPositionView.getBlockAtOffset(0.3, 0.9, 0.3));
    }

    public static boolean isPassableColumn(BlockPositionView blockPositionView) {
        return !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 0.0, 0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 0.0, 0.0)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 0.0, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 0.0, 0.0)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 0.0, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 0.0, 0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 0.0, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 0.0, 0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 1.64, 0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 1.64, 0.0)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 1.64, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 1.64, 0.0)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 1.64, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 1.64, 0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 1.64, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 1.64, 0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 0.9, 0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 0.9, 0.0)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.0, 0.9, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 0.9, 0.0)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 0.9, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(-0.3, 0.9, 0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 0.9, -0.3)) && !EnvironmentBlockChecks.isWalkableBlock(blockPositionView.getBlockAtOffset(0.3, 0.9, 0.3));
    }

    private static double extractBestHeight(CollisionAdjustmentCandidates collisionAdjustmentCandidates) {
        return CollisionAdjustmentCandidates.getBestDistance(collisionAdjustmentCandidates);
    }

    private static CollisionAdjustmentCandidates createAdjustmentCandidates(CollisionOffsetPair collisionOffsetPair) {
        return new CollisionAdjustmentCandidates();
    }
}

