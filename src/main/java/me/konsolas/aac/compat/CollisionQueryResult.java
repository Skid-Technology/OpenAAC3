package me.konsolas.aac.compat;

import java.util.List;
import java.util.stream.Collectors;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import org.bukkit.World;
import org.bukkit.block.Block;

public class CollisionQueryResult {
    private final World world;
    private final AxisAlignedBoundingBox boundingBox;
    private final List<AxisAlignedBoundingBox> collisions;
    private final double adjustment;

    private CollisionQueryResult(World world, AxisAlignedBoundingBox axisAlignedBoundingBox, List<AxisAlignedBoundingBox> list, double d) {
        this.world = world;
        this.boundingBox = axisAlignedBoundingBox;
        this.collisions = list;
        this.adjustment = d;
    }

    public AxisAlignedBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public List<AxisAlignedBoundingBox> getCollisions() {
        return this.collisions;
    }

    public double getAdjustment() {
        return this.adjustment;
    }

    public String toString() {
        return this.getCollisions().stream().map(this::formatBoundingBox).collect(Collectors.joining(" "));
    }

    private String formatBoundingBox(AxisAlignedBoundingBox axisAlignedBoundingBox) {
        Block block = this.world.getBlockAt((int)axisAlignedBoundingBox.getMinimum().getX(), (int)axisAlignedBoundingBox.getMinimum().getY(), (int)axisAlignedBoundingBox.getMinimum().getZ());
        return block.getType() + "@" + block.getLocation();
    }

    public CollisionQueryResult(World world, AxisAlignedBoundingBox axisAlignedBoundingBox, List<AxisAlignedBoundingBox> list, double d, CollisionMaterialSwitch collisionMaterialSwitch) {
        this(world, axisAlignedBoundingBox, list, d);
    }

    static {
        if (null == null) {
        }
    }
}

