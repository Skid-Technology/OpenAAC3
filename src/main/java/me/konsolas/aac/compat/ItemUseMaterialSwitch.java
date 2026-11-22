package me.konsolas.aac.compat;

import org.bukkit.Material;

public class ItemUseMaterialSwitch {
    static final int[] MATERIAL_ORDINALS = new int[Material.values().length];

    static {
        try {
            ItemUseMaterialSwitch.MATERIAL_ORDINALS[Material.STONE_SWORD.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            ItemUseMaterialSwitch.MATERIAL_ORDINALS[Material.DIAMOND_SWORD.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            ItemUseMaterialSwitch.MATERIAL_ORDINALS[Material.IRON_SWORD.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            ItemUseMaterialSwitch.MATERIAL_ORDINALS[Material.GOLD_SWORD.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            ItemUseMaterialSwitch.MATERIAL_ORDINALS[Material.WOOD_SWORD.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            ItemUseMaterialSwitch.MATERIAL_ORDINALS[Material.BOW.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            ItemUseMaterialSwitch.MATERIAL_ORDINALS[Material.SHIELD.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
    }
}

