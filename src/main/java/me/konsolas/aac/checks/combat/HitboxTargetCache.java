package me.konsolas.aac.checks.combat;

import java.util.Map;

public class HitboxTargetCache {
    final Map targets;
    final Hitbox detector;

    public HitboxTargetCache(Hitbox hitbox, Map map) {
        this.detector = hitbox;
        this.targets = map;
    }
}

