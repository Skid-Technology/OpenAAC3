package me.konsolas.aac.checks.combat;

import org.bukkit.entity.Player;

public class KillauraViolation {
    private final Player player;
    private final String moduleName;
    private final String message;
    final KillauraModule module;

    private KillauraViolation(KillauraModule killauraModule, Player player, String str, String str2) {
        this.module = killauraModule;
        this.player = player;
        this.moduleName = str;
        this.message = str2;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public String getMessage() {
        return this.message;
    }

    public KillauraViolation(KillauraModule killauraModule, Player player, String str, String str2, KillauraViolationAccess killauraViolationAccess) {
        this(killauraModule, player, str, str2);
    }

    static {
        if (null == null) {
            // empty if block
        }
    }
}

