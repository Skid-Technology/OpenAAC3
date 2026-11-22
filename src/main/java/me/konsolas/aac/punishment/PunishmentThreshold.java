package me.konsolas.aac.punishment;

import java.util.HashMap;
import java.util.Map;
import me.konsolas.aac.api.HackType;
import me.konsolas.aac.api.PlayerViolationCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

public class PunishmentThreshold {
    private final int threshold;
    private final String command;
    private final Map armedPlayers;
    final PunishmentManager punishmentManager;

    public PunishmentThreshold(PunishmentManager punishmentManager, int i, String str) {
        this.punishmentManager = punishmentManager;
        this.armedPlayers = new HashMap();
        this.threshold = i;
        this.command = str;
    }

    void executeCommand(Player player, HackType hackType, long ticks) {
        long ticks2 = (ticks = 0x5C2169ABD87CL ^ ticks) ^ 0x5A746835EF60L;
        PlayerViolationCommandEvent playerViolationCommandEvent = new PlayerViolationCommandEvent(player, hackType, this.command);
        Server server = Bukkit.getServer();
        PlayerViolationCommandEvent playerViolationCommandEvent2 = playerViolationCommandEvent;
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.callEvent((Event)playerViolationCommandEvent2);
        if (!playerViolationCommandEvent.isCancelled()) {
            String str = PunishmentManager.formatCommandBridge(this.punishmentManager, playerViolationCommandEvent.getCommand(), player, ticks2);
            ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
            Server server2 = Bukkit.getServer();
            server2.dispatchCommand((CommandSender)consoleCommandSender, str);
        }
    }

    boolean crossedThreshold(Player player, int i) {
        if (!this.armedPlayers.containsKey(player.getUniqueId())) {
            this.armedPlayers.put(player.getUniqueId(), true);
        }
        if (((Boolean)this.armedPlayers.get(player.getUniqueId())).booleanValue()) {
            if (i > this.threshold) {
                this.armedPlayers.put(player.getUniqueId(), false);
                return true;
            }
        } else if (i < this.threshold) {
            this.armedPlayers.put(player.getUniqueId(), true);
        }
        return false;
    }

    public String getCommand() {
        return this.command;
    }

}

