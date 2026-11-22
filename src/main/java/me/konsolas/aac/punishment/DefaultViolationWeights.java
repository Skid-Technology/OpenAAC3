package me.konsolas.aac.punishment;

import java.util.EnumMap;
import me.konsolas.aac.api.HackType;

public final class DefaultViolationWeights
extends EnumMap<HackType, Double> {
    public DefaultViolationWeights(Class<HackType> clazz) {
        super(clazz);
        this.put(HackType.SPAM, 0.0);
        this.put(HackType.HITBOX, 1.2);
        this.put(HackType.FASTBOW, 2.0);
        this.put(HackType.FIGHTSPEED, 3.0);
        this.put(HackType.KNOCKBACK, 2.5);
        this.put(HackType.REGEN, 3.0);
        this.put(HackType.FASTUSE, 4.0);
        this.put(HackType.KILLAURA, 2.0);
        this.put(HackType.SPEED, 0.3);
        this.put(HackType.PHASE, 0.4);
        this.put(HackType.FLY, 0.35);
        this.put(HackType.VEHICLES, 0.8);
        this.put(HackType.ELYTRA, 0.8);
        this.put(HackType.NOFALL, 2.0);
        this.put(HackType.JESUS, 3.0);
        this.put(HackType.DERP, 20.0);
        this.put(HackType.CRITICALS, 8.0);
        this.put(HackType.FASTBREAK, 4.0);
        this.put(HackType.FASTPLACE, 4.0);
        this.put(HackType.NUKER, 20.0);
        this.put(HackType.NOSWING, 0.4);
        this.put(HackType.INTERACT, 1.0);
        this.put(HackType.VELOCITY, 4.0);
        this.put(HackType.MOVE, 0.6);
    }
}

