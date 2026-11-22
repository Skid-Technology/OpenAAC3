package me.konsolas.aac.util;

import org.bukkit.entity.EntityType;

public class EntityTypeChecks {
    public static boolean isStandardMob(EntityType entityType) {
        switch (entityType) {
            case PLAYER: 
            case VILLAGER: 
            case ZOMBIE: 
            case PIG_ZOMBIE: 
            case SKELETON: 
            case WITCH: 
            case SNOWMAN: 
            case CREEPER: 
            case CHICKEN: 
            case ENDERMAN: 
            case SILVERFISH: 
            case ENDERMITE: 
            case RABBIT: 
            case BAT: 
            case WOLF: {
                return true;
            }
        }
        return false;
    }

    public static boolean isCommonCombatTarget(EntityType entityType) {
        switch (entityType) {
            case PLAYER: 
            case VILLAGER: 
            case ZOMBIE: 
            case PIG_ZOMBIE: 
            case SKELETON: 
            case WITCH: 
            case SNOWMAN: 
            case CREEPER: 
            case CHICKEN: 
            case ENDERMAN: {
                return true;
            }
        }
        return false;
    }

    public static boolean isHumanoidTarget(EntityType entityType) {
        switch (entityType) {
            case PLAYER: 
            case VILLAGER: 
            case ZOMBIE: 
            case PIG_ZOMBIE: 
            case SKELETON: 
            case WITCH: 
            case SNOWMAN: {
                return true;
            }
        }
        return false;
    }

    public static boolean isKnockbackTarget(EntityType entityType) {
        switch (entityType) {
            case PRIMED_TNT: {
                return false;
            }
        }
        return true;
    }
}

