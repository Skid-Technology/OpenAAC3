package me.konsolas.aac.util;

import me.konsolas.aac.punishment.LogManager;
import org.bukkit.entity.Player;

public class PlayerDebugMessage {
    private final Player player;
    private final String message;
    final LogManager logManager;

    public PlayerDebugMessage(LogManager logManager, Player player, String str) {
        this.logManager = logManager;
        this.player = player;
        this.message = str;
    }

    public static Player getPlayer(PlayerDebugMessage playerDebugMessage) {
        return playerDebugMessage.player;
    }

    public static String getMessage(PlayerDebugMessage playerDebugMessage) {
        return playerDebugMessage.message;
    }
}

