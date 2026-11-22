package me.konsolas.aac.hooks;

import me.konsolas.aac.api.HackType;

public class McMmoHackTypeSwitch {
    static final int[] HACK_TYPE_ORDINALS = new int[HackType.values().length];

    static {
        try {
            McMmoHackTypeSwitch.HACK_TYPE_ORDINALS[HackType.HITBOX.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            McMmoHackTypeSwitch.HACK_TYPE_ORDINALS[HackType.KILLAURA.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            McMmoHackTypeSwitch.HACK_TYPE_ORDINALS[HackType.NOSWING.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            McMmoHackTypeSwitch.HACK_TYPE_ORDINALS[HackType.FASTBREAK.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            McMmoHackTypeSwitch.HACK_TYPE_ORDINALS[HackType.NUKER.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            McMmoHackTypeSwitch.HACK_TYPE_ORDINALS[HackType.INTERACT.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            McMmoHackTypeSwitch.HACK_TYPE_ORDINALS[HackType.PHASE.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
    }
}

