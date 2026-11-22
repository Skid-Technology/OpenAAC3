package me.konsolas.aac.checks.movement;

import org.bukkit.entity.EntityType;

public class VehicleTypeSwitch {
    static final int[] ENTITY_ORDINALS = new int[EntityType.values().length];

    static {
        try {
            VehicleTypeSwitch.ENTITY_ORDINALS[EntityType.BOAT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            VehicleTypeSwitch.ENTITY_ORDINALS[EntityType.HORSE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            VehicleTypeSwitch.ENTITY_ORDINALS[EntityType.PIG.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
    }
}

