package me.konsolas.aac.gui;

import java.util.HashMap;

final class CheckDescriptions
extends HashMap {
    public CheckDescriptions() {
        this.put("combat.Hitbox", new String[]{"\u00a77When a player tries to hit another without looking", "\u00a77at its hitbox"});
        this.put("combat.Criticals", new String[]{"\u00a77When a player tries to deal extra damage whilst", "\u00a77on the ground"});
        this.put("combat.Fastbow", new String[]{"\u00a77When a player tries to fire arrows or use a bow too", "\u00a77quickly"});
        this.put("combat.FightSpeed", new String[]{"\u00a77When a player attacks or interacts with entities", "\u00a77faster than normal"});
        this.put("combat.Knockback", new String[]{"\u00a77When a player does not take knockback after", "\u00a77being attacked"});
        this.put("combat.Regen", new String[]{"\u00a77When a player tries to speed up their regeneration", "\u00a77to become invincible"});
        this.put("combat.FastUse", new String[]{"\u00a77When a player tries to eat/consume an item without", "\u00a77waiting correctly"});
        this.put("combat.Killaura", new String[]{"\u00a77When a player uses combat modifications to gain an", "\u00a77unfair advantage over others"});
        this.put("movement.Speed", new String[]{"\u00a77When a player tries to move too quickly"});
        this.put("movement.Phase", new String[]{"\u00a77When a player tries to step into or through a solid block"});
        this.put("movement.Fly", new String[]{"\u00a77When a player tries to fly (or move incorrectly on the y axis)", "\u00a77in survival mode"});
        this.put("movement.Elytra", new String[]{"\u00a77When a player tries to move incorrectly with an elytra", "\u00a77in survival mode"});
        this.put("movement.Vehicles", new String[]{"\u00a77When a player tries to move incorrectly in a vehicle", "\u00a77in survival mode"});
        this.put("movement.NoFall", new String[]{"\u00a77When a player tries to avoid fall damage taken after falling", "\u00a77more than three blocks"});
        this.put("movement.Jesus", new String[]{"\u00a77When a player tries to walk on water"});
        this.put("movement.Derp", new String[]{"\u00a77When a player moves their head in an abnormal way"});
        this.put("world.FastBreak", new String[]{"\u00a77When a player tries to break blocks too quickly"});
        this.put("world.FastPlace", new String[]{"\u00a77When a player tries to place blocks too quickly"});
        this.put("world.Nuker", new String[]{"\u00a77When a player tries to break multiple blocks at", "\u00a77the same time, in both survival and creative mode"});
        this.put("world.NoSwing", new String[]{"\u00a77When a player interacts with the world or attacks", "\u00a77without swinging their arm"});
        this.put("world.Interact", new String[]{"\u00a77When a player places blocks without looking or ", "\u00a77attacks an entity behind a wall"});
        this.put("world.Velocity", new String[]{"\u00a77When a player ignores velocity commands sent by ", "\u00a77the server, e.g. knockback"});
        this.put("chat.Spam", new String[]{"\u00a77When a player tries to flood the chat by repeating", "\u00a77themselves, or other people too quickly"});
    }
}

