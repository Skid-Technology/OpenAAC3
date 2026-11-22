package me.konsolas.aac.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockPositionView {
    private final double x;
    private final double y;
    private final double z;
    private final World world;

    public BlockPositionView(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld();
    }

    private static int floorCoord(double d) {
        return (int)(d + 3.0E7) - 30000000;
    }

    public Block getBlockAtOffset(double d, double d2, double d3) {
        return this.world.getBlockAt(BlockPositionView.floorCoord(this.x + d), BlockPositionView.floorCoord(this.y + d2), BlockPositionView.floorCoord(this.z + d3));
    }

    public Block getBlockAtNegOffset(double d, double d2, double d3) {
        return this.world.getBlockAt(BlockPositionView.floorCoord(this.x - d), BlockPositionView.floorCoord(this.y - d2), BlockPositionView.floorCoord(this.z - d3));
    }

    public Material getMaterialAtOffset(double d, double d2, double d3) {
        return this.getBlockAtOffset(d, d2, d3).getType();
    }

    public Material getMaterialAtNegOffset(double d, double d2, double d3) {
        return this.getBlockAtNegOffset(d, d2, d3).getType();
    }

    public Block getBlock() {
        return this.getBlockAtOffset(0.0, 0.0, 0.0);
    }

    public BlockPositionView offset(double d, double d2, double d3) {
        return new BlockPositionView(new Location(this.world, this.x + d, this.y + d2, this.z + d3));
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public World getWorld() {
        return this.world;
    }


    static {
        if (false) {
        }
    }
}

