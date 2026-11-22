package me.konsolas.aac.util;

import org.bukkit.entity.EntityType;

public class EntityTypePredicateSwitch {
    static final int[] ENTITY_TYPE_ORDINALS = new int[EntityType.values().length];

    static {
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.PLAYER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.VILLAGER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.ZOMBIE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.PIG_ZOMBIE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.SKELETON.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.WITCH.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.SNOWMAN.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.CREEPER.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.CHICKEN.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.ENDERMAN.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.SILVERFISH.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.ENDERMITE.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.RABBIT.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.BAT.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.WOLF.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            EntityTypePredicateSwitch.ENTITY_TYPE_ORDINALS[EntityType.PRIMED_TNT.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
    }
}

