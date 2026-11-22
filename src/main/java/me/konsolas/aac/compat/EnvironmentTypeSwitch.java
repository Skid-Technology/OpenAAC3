package me.konsolas.aac.compat;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class EnvironmentTypeSwitch {
    static final int[] ENVIRONMENT_ORDINALS;
    static final int[] MATERIAL_ORDINALS;

    static {
        MATERIAL_ORDINALS = new int[EntityType.values().length];
        try {
            EnvironmentTypeSwitch.MATERIAL_ORDINALS[EntityType.HORSE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.MATERIAL_ORDINALS[EntityType.PIG.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.MATERIAL_ORDINALS[EntityType.BOAT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        ENVIRONMENT_ORDINALS = new int[Material.values().length];
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.STEP.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.WOOD_STEP.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.DAYLIGHT_DETECTOR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.STONE_SLAB2.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.WATER_LILY.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.SKULL.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.PURPUR_SLAB.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.ACACIA_STAIRS.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.BIRCH_WOOD_STAIRS.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.BRICK_STAIRS.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.COBBLESTONE_STAIRS.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.DARK_OAK_STAIRS.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.JUNGLE_WOOD_STAIRS.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.NETHER_BRICK_STAIRS.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.QUARTZ_STAIRS.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.RED_SANDSTONE_STAIRS.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.SANDSTONE_STAIRS.ordinal()] = 17;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.SMOOTH_STAIRS.ordinal()] = 18;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.SPRUCE_WOOD_STAIRS.ordinal()] = 19;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.WOOD_STAIRS.ordinal()] = 20;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.PURPUR_STAIRS.ordinal()] = 21;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.DIODE_BLOCK_OFF.ordinal()] = 22;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.DIODE_BLOCK_ON.ordinal()] = 23;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.REDSTONE_COMPARATOR_OFF.ordinal()] = 24;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.REDSTONE_COMPARATOR_ON.ordinal()] = 25;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.BREWING_STAND.ordinal()] = 26;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.SNOW.ordinal()] = 27;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.ANVIL.ordinal()] = 28;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.FLOWER_POT.ordinal()] = 29;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.DAYLIGHT_DETECTOR_INVERTED.ordinal()] = 30;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.THIN_GLASS.ordinal()] = 31;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.STAINED_GLASS_PANE.ordinal()] = 32;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.BED_BLOCK.ordinal()] = 33;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.TRAP_DOOR.ordinal()] = 34;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.IRON_TRAPDOOR.ordinal()] = 35;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.END_ROD.ordinal()] = 36;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.ENDER_PORTAL_FRAME.ordinal()] = 37;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.ENCHANTMENT_TABLE.ordinal()] = 38;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.WHITE_SHULKER_BOX.ordinal()] = 39;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.ORANGE_SHULKER_BOX.ordinal()] = 40;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.MAGENTA_SHULKER_BOX.ordinal()] = 41;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.LIGHT_BLUE_SHULKER_BOX.ordinal()] = 42;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.YELLOW_SHULKER_BOX.ordinal()] = 43;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.LIME_SHULKER_BOX.ordinal()] = 44;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.PINK_SHULKER_BOX.ordinal()] = 45;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.GRAY_SHULKER_BOX.ordinal()] = 46;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.SILVER_SHULKER_BOX.ordinal()] = 47;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.CYAN_SHULKER_BOX.ordinal()] = 48;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.PURPLE_SHULKER_BOX.ordinal()] = 49;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.BLUE_SHULKER_BOX.ordinal()] = 50;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.BROWN_SHULKER_BOX.ordinal()] = 51;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.GREEN_SHULKER_BOX.ordinal()] = 52;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.RED_SHULKER_BOX.ordinal()] = 53;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EnvironmentTypeSwitch.ENVIRONMENT_ORDINALS[Material.BLACK_SHULKER_BOX.ordinal()] = 54;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
    }
}

