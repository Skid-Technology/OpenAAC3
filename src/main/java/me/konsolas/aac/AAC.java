package me.konsolas.aac;

import com.comphenix.protocol.ProtocolLibrary;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import me.konsolas.aac.api.AACAPI;
import me.konsolas.aac.checks.CheckManager;
import me.konsolas.aac.checks.EnablePluginTask;
import me.konsolas.aac.checks.combat.RecentCombatTracker;
import me.konsolas.aac.checks.movement.PlayerEventStateTracker;
import me.konsolas.aac.checks.movement.SetbackManager;
import me.konsolas.aac.command.CommandController;
import me.konsolas.aac.command.CommandRegistrar;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.compat.ServerVersionAccess;
import me.konsolas.aac.gui.InventoryMenu;
import me.konsolas.aac.hooks.McMmoHook;
import me.konsolas.aac.metrics.MetricsLite;
import me.konsolas.aac.punishment.LogManager;
import me.konsolas.aac.punishment.PunishmentManager;
import me.konsolas.aac.util.LanguageConfig;
import me.konsolas.aac.util.TickRateManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AAC
extends JavaPlugin
implements Listener {
    public static volatile AACAPI api = null;
    public static String messagePrefix = "\u00a76[AAC] \u00a7r> \u00a76";
    public static final Random random = new Random();
    public static ServerVersionAccess serverVersion;
    private static AAC instance;
    private final AAC plugin = this;
    public LogManager logManager;
    public LanguageConfig messages;
    public TickRateManager tickRateManager;
    public PlayerEventStateTracker playerState;
    public SetbackManager setbackManager;
    public CheckManager checkManager;
    public RecentCombatTracker combatTracker;
    public PunishmentManager punishmentManager;
    public CommandController commandController;
    private final SimpleDateFormat logFilenameDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private final SimpleDateFormat logTimestampFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    private FileWriter logFileWriter;
    private PrintWriter logWriter;

    public static AAC getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        Server server = this.getServer();
        for (World world : server.getWorlds()) {
            Entity entity = world.spawnEntity(new Location(world, 0.0, 0.0, 0.0), EntityType.ARMOR_STAND);
            entity.remove();
        }
        new MetricsLite(this);
        serverVersion = new ServerVersionAccess(Bukkit.getVersion());
        CollisionAccess.isValidLocation((Location)null);
        CommandController commandController = new CommandController(this);
        this.commandController = commandController;
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents((Listener)this, (Plugin)this);
        pluginManager.registerEvents((Listener)commandController, (Plugin)this);
        this.checkManager = new CheckManager(this);
        this.setbackManager = new SetbackManager(this);
        pluginManager.registerEvents((Listener)this.setbackManager, (Plugin)this);
        if (this.getConfig().getBoolean("usetps")) {
            this.tickRateManager = new TickRateManager(this);
        }
        this.logManager = new LogManager(this);
        this.saveDefaultConfig();
        this.openLog();
        this.reloadConfig();
        CommandRegistrar.registerCommands(this, commandController);
        server.getScheduler().scheduleSyncDelayedTask((Plugin)this, (Runnable)new EnablePluginTask(this), 10L);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent playerLoginEvent) {
        if (api == null) {
            playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server still starting up");
        }
    }

    private void initializeHooks() {
        Server server = Bukkit.getServer();
        String str = "mcMMO";
        PluginManager pluginManager = server.getPluginManager();
        if (pluginManager.getPlugin(str) != null) {
            Bukkit.getLogger().info("[AAC] Detected mcMMO, setting up hooks...");
            Server server2 = Bukkit.getServer();
            AAC aAC = this;
            AAC aAC2 = this;
            McMmoHook mcMmoHook = new McMmoHook(aAC);
            PluginManager pluginManager2 = server2.getPluginManager();
            pluginManager2.registerEvents((Listener)mcMmoHook, (Plugin)aAC2);
        }
    }

    public void onDisable() {
        try {
            api = null;
            Bukkit.getLogger().info("[AAC] Disabling checks...");
            this.playerState.cleanup();
            this.punishmentManager.saveBannedPlayers();
            InventoryMenu.resetClickListener();
            HandlerList.unregisterAll((Listener)this);
            Server server = Bukkit.getServer();
            server.getScheduler().cancelTasks((Plugin)this);
            ProtocolLibrary.getProtocolManager().removePacketListeners((Plugin)this);
            this.checkManager = null;
            this.closeLog();
        }
        catch (NullPointerException nullPointerException) {}
    }

    public void openLog() {
        try {
            if (!this.getConfig().getBoolean("log_file", true)) {
                Bukkit.getLogger().warning("[AAC] Logger functionality has been disabled in the configuration file.");
                return;
            }
            File file = new File(this.getDataFolder(), "logs");
            if (!file.exists() && !file.mkdir()) {
                throw new IOException("Failed to create data dir");
            }
            Date date = new Date();
            File file2 = new File(this.getDataFolder(), "logs/aac_" + this.logFilenameDateFormat.format(date) + ".log");
            if (!file2.exists() && !file2.createNewFile()) {
                throw new IOException("Failed to create new file");
            }
            this.logFileWriter = new FileWriter(file2, true);
            this.logWriter = new PrintWriter(this.logFileWriter);
        }
        catch (IOException iOException) {
            Bukkit.getLogger().severe("[AAC] Error: Could not setup log file for writing");
            Bukkit.getLogger().severe("[AAC] " + iOException.getMessage());
            Bukkit.getLogger().severe("[AAC] Logger functionality has been disabled.");
        }
    }

    private void closeLog() {
        try {
            Bukkit.getLogger().info("[AAC] Saving log file...");
            this.logWriter.close();
        }
        catch (NullPointerException nullPointerException) {
            Bukkit.getLogger().warning("[AAC] Could not save log file as none was created.");
        }
    }

    public void writeLog(String str) {
        if (this.logWriter != null) {
            Date date = new Date();
            this.logWriter.println("[" + this.logTimestampFormat.format(date) + "] " + str);
            this.logWriter.flush();
        }
    }

    public void reloadPlugin() {
        Bukkit.getLogger().info("[AAC] Beginning disable...");
        Server server = Bukkit.getServer();
        AAC aAC = this;
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.disablePlugin((Plugin)aAC);
        ProtocolLibrary.getProtocolManager().removePacketListeners((Plugin)this);
        Server server2 = Bukkit.getServer();
        AAC aAC2 = this;
        PluginManager pluginManager2 = server2.getPluginManager();
        pluginManager2.enablePlugin((Plugin)aAC2);
    }

    public static AAC getPlugin(AAC aAC) {
        return aAC.plugin;
    }

    public static void initializeHooks(AAC aAC) {
        aAC.initializeHooks();
    }
}

