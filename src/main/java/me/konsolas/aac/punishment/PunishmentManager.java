package me.konsolas.aac.punishment;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.CleanupHandler;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.compat.PlayerPingAccess;
import me.konsolas.aac.util.TickRateMonitor;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class PunishmentManager
implements Listener,
CleanupHandler {
    private final Map<UUID, Integer> punishmentCounts = new HashMap<>();
    private final AAC plugin;
    private final BukkitTask thresholdTask;
    private int banThreshold;
    private int kickTicks;
    private final ExpiringMap<UUID, Long> recentDisconnects;
    private final Map<String, UUID> bannedPlayers;
    private final EnumMap<HackType, List<PunishmentThreshold>> thresholds;
    private FileConfiguration bannedConfig;
    private File bannedFile;

    public PunishmentManager(AAC aAC) {
        this.banThreshold = 5;
        this.kickTicks = 200;
        this.bannedPlayers = new HashMap();
        this.thresholds = new EnumMap(HackType.class);
        this.bannedConfig = null;
        this.bannedFile = null;
        this.plugin = aAC;
        this.banThreshold = this.plugin.getConfig().getInt("ban_threshold", this.banThreshold);
        this.kickTicks = this.plugin.getConfig().getInt("kick_ticks", this.kickTicks);
        this.saveDefaultBannedConfig();
        List<String> list = this.getBannedConfig().getStringList("banned");
        for (String str : list) {
            String[] stringArray = str.split(":");
            this.bannedPlayers.put(stringArray[0], UUID.fromString(stringArray[1]));
        }
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = this.kickTicks * 50;
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        this.recentDisconnects = ExpiringMap.builder().expiration(ticks, timeUnit).expirationPolicy(expirationPolicy).build();
        for (HackType hackType : HackType.values()) {
            List<PunishmentThreshold> arrayList = new ArrayList<>();
            ConfigurationSection configurationSection = this.plugin.getConfig().getConfigurationSection(hackType.getName() + ".thresholds");
            if (configurationSection == null) {
                this.plugin.getLogger().warning("(Punisher) WARN: Failed to get thresholds for check " + (Object)((Object)hackType));
                continue;
            }
            for (String str2 : configurationSection.getKeys(false)) {
                int i;
                Object obj = configurationSection.get(str2);
                try {
                    i = Integer.parseInt(str2);
                }
                catch (NumberFormatException numberFormatException) {
                    this.plugin.getLogger().warning("(Punisher) WARN: Failed to parse integer threshold: " + numberFormatException.getMessage());
                    continue;
                }
                if (obj instanceof String) {
                    arrayList.add(new PunishmentThreshold(this, i, configurationSection.getString(str2)));
                    continue;
                }
                List<String> commands = configurationSection.getStringList(str2);
                if (!commands.isEmpty()) {
                    for (String command : commands) {
                        arrayList.add(new PunishmentThreshold(this, i, command));
                    }
                    continue;
                }
                this.plugin.getLogger().warning("(Punisher) WARN: Failed to get multiple commands for check " + (Object)((Object)hackType) + ", threshold " + i);
            }
            this.thresholds.put(hackType, arrayList);
        }
        Server server = Bukkit.getServer();
        this.thresholdTask = server.getScheduler().runTaskTimerAsynchronously((Plugin)this.plugin, (Runnable)new PunishmentScanTask(this, null), 20L, 17L);
    }

    public void saveBannedPlayers() {
        ArrayList<String> arrayList = new ArrayList<String>();
        Iterator iterator = this.bannedPlayers.values().iterator();
        while (iterator.hasNext()) {
            UUID uUID = (UUID)iterator.next();
            arrayList.add(Bukkit.getOfflinePlayer((UUID)uUID).getName() + ":" + uUID);
        }
        this.getBannedConfig().set("banned", arrayList);
        this.saveBannedConfig();
    }

    public void recordPunishment(Player player, long ticks) {
        long ticks2 = (ticks = 0x2A4126456AF3L ^ ticks) ^ 0x3F0130C2BDEL;
        if (this.punishmentCounts.containsKey(player.getUniqueId())) {
            this.punishmentCounts.put(player.getUniqueId(), (Integer)this.punishmentCounts.get(player.getUniqueId()) + 1);
        } else {
            this.punishmentCounts.put(player.getUniqueId(), 1);
        }
        if ((Integer)this.punishmentCounts.get(player.getUniqueId()) > this.banThreshold && this.banThreshold != -1) {
            this.punishmentCounts.remove(player.getUniqueId());
            for (String str : this.plugin.getConfig().getString("ban_command").split(";")) {
                String str2 = this.formatCommand(str, player, ticks2);
                ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
                Server server = Bukkit.getServer();
                server.dispatchCommand((CommandSender)consoleCommandSender, str2);
            }
        }
    }

    public void markBanned(Player player) {
        String str = player.getName() + " has been banned for hacking. (" + player.getUniqueId() + ")";
        this.plugin.logManager.logViolation(str);
        this.bannedPlayers.put(player.getName(), player.getUniqueId());
        this.punishmentCounts.remove(player.getUniqueId());
    }

    private String formatCommand(String str, Player player, long ticks) {
        long ticks2 = (ticks = 0x2A4126456AF3L ^ ticks) ^ 0xFEDFAEE0721L;
        str = str.replace("{player}", player.getName());
        long ticks3 = ticks2;
        Player player2 = player;
        str = str.replace("{ping}", PlayerPingAccess.getPing(player2, ticks3) + "");
        str = str.replace("{tps}", (double)Math.round(TickRateMonitor.getTps() * 100.0) / 100.0 + "");
        str = str.replace("{world}", player.getWorld().getName());
        return str;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        this.recentDisconnects.put(playerQuitEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent playerLoginEvent) {
        if (playerLoginEvent.getPlayer().hasPermission("AAC.bypass")) {
            return;
        }
        if (this.recentDisconnects.containsKey(playerLoginEvent.getPlayer().getUniqueId())) {
            playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, AAC.messagePrefix + this.plugin.messages.getMessage("relog_message"));
        }
        if (!this.punishmentCounts.containsKey(playerLoginEvent.getPlayer().getUniqueId())) {
            this.punishmentCounts.put(playerLoginEvent.getPlayer().getUniqueId(), 0);
        }
        if (this.bannedPlayers.values().contains(playerLoginEvent.getPlayer().getUniqueId())) {
            playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_BANNED, AAC.messagePrefix + this.plugin.messages.getMessage("ban_message"));
        }
    }

    public boolean isBanned(String str) {
        return this.bannedPlayers.containsKey(str);
    }

    public void unban(String str) {
        this.punishmentCounts.put(this.bannedPlayers.get(str), 0);
        this.bannedPlayers.remove(str);
    }

    public FileConfiguration getBannedConfig() {
        if (this.bannedConfig == null) {
            this.reloadBannedConfig();
        }
        return this.bannedConfig;
    }

    private void reloadBannedConfig() {
        if (this.bannedFile == null) {
            this.bannedFile = new File(this.plugin.getDataFolder(), "banned.yml");
        }
        this.bannedConfig = YamlConfiguration.loadConfiguration((File)this.bannedFile);
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(this.plugin.getResource("banned.yml"), "UTF8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
        if (inputStreamReader != null) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(inputStreamReader);
            this.bannedConfig.setDefaults((Configuration)yamlConfiguration);
        }
    }

    private void saveDefaultBannedConfig() {
        if (this.bannedFile == null) {
            this.bannedFile = new File(this.plugin.getDataFolder(), "banned.yml");
        }
        if (!this.bannedFile.exists()) {
            this.plugin.saveResource("banned.yml", false);
        }
    }

    private void saveBannedConfig() {
        if (this.bannedConfig == null || this.bannedFile == null) {
            return;
        }
        try {
            this.getBannedConfig().save(this.bannedFile);
        }
        catch (IOException iOException) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.bannedFile, iOException);
        }
    }

    @Override
    public void cleanup() {
        this.thresholdTask.cancel();
        this.saveBannedConfig();
    }


    static String formatCommandBridge(PunishmentManager punishmentManager, String str, Player player, long ticks) {
        long ticks2 = (ticks = 0x2A4126456AF3L ^ ticks) ^ 0x59DBE96C08D8L;
        return punishmentManager.formatCommand(str, player, ticks2);
    }

    static AAC getPlugin(PunishmentManager punishmentManager) {
        return punishmentManager.plugin;
    }

    static EnumMap<HackType, List<PunishmentThreshold>> getThresholds(PunishmentManager punishmentManager) {
        return punishmentManager.thresholds;
    }
}

