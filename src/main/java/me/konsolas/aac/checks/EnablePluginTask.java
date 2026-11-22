package me.konsolas.aac.checks;

import me.konsolas.aac.AAC;
import me.konsolas.aac.api.internal.AACAPIImplementation;
import me.konsolas.aac.util.LanguageConfig;
import me.konsolas.aac.checks.movement.PlayerEventStateTracker;
import me.konsolas.aac.punishment.PunishmentManager;
import me.konsolas.aac.checks.combat.RecentCombatTracker;
import me.konsolas.aac.util.TickRateMonitor;
import me.konsolas.aac.api.AACAPILoadedEvent;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class EnablePluginTask
implements Runnable {
    final AAC plugin;

    public EnablePluginTask(AAC aAC) {
        this.plugin = aAC;
    }

    @Override
    public void run() {
        AAC plugin = AAC.getPlugin(this.plugin);
        plugin.getLogger().info("Beginning Enable...");
        plugin.punishmentManager = new PunishmentManager(plugin);
        AAC.initializeHooks(this.plugin);
        Server server = Bukkit.getServer();
        CheckManager checkManager = this.plugin.checkManager;
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents((Listener)checkManager, (Plugin)plugin);
        plugin.getLogger().info("Checking config file...");
        for (HackType hackType : HackType.values()) {
            if (this.plugin.getConfig().getBoolean(hackType.getName() + ".enabled")) {
                plugin.getLogger().info("(CheckManager) Enabling check " + hackType);
                this.plugin.checkManager.enableCheck(hackType, false);
                continue;
            }
            plugin.getLogger().info("(CheckManager) Check " + hackType + " is disabled");
        }
        plugin.getLogger().info("Starting API...");
        AAC.api = AACAPIImplementation.create(plugin);
        Server server2 = Bukkit.getServer();
        AACAPILoadedEvent aACAPILoadedEvent = new AACAPILoadedEvent(AAC.api);
        PluginManager pluginManager2 = server2.getPluginManager();
        pluginManager2.callEvent((Event)aACAPILoadedEvent);
        this.plugin.combatTracker = new RecentCombatTracker(plugin);
        this.plugin.playerState = new PlayerEventStateTracker(plugin);
        this.plugin.messages = new LanguageConfig(plugin);
        Server server3 = Bukkit.getServer();
        PluginManager pluginManager3 = server3.getPluginManager();
        pluginManager3.registerEvents((Listener)this.plugin.combatTracker, (Plugin)plugin);
        Server server4 = Bukkit.getServer();
        PluginManager pluginManager4 = server4.getPluginManager();
        pluginManager4.registerEvents((Listener)this.plugin.playerState, (Plugin)plugin);
        Server server5 = Bukkit.getServer();
        PluginManager pluginManager5 = server5.getPluginManager();
        pluginManager5.registerEvents((Listener)this.plugin.punishmentManager, (Plugin)plugin);
        this.plugin.logManager.logViolation("AAC has been enabled");
        plugin.getLogger().info("AAC has been enabled.");
        AAC.messagePrefix = ChatColor.translateAlternateColorCodes('&', this.plugin.messages.getMessage("prefix"));
        TickRateMonitor.start((Plugin)plugin);
    }
}

