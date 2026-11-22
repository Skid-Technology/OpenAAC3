package me.konsolas.aac.compat;

import org.bukkit.Material;

public class CollisionMaterialSwitch {
    static final int[] MATERIAL_ORDINALS = new int[Material.values().length];

    static {
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.ACACIA_FENCE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.BIRCH_FENCE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.COBBLE_WALL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.DARK_OAK_FENCE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.FENCE.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.IRON_FENCE.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.JUNGLE_FENCE.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.NETHER_FENCE.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.SPRUCE_FENCE.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.STAINED_GLASS_PANE.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.THIN_GLASS.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.ACACIA_DOOR.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.BIRCH_DOOR.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.DARK_OAK_DOOR.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.IRON_DOOR_BLOCK.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.JUNGLE_DOOR.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.SPRUCE_DOOR.ordinal()] = 17;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            CollisionMaterialSwitch.MATERIAL_ORDINALS[Material.WOODEN_DOOR.ordinal()] = 18;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
    }
}

