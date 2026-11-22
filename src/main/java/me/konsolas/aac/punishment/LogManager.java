package me.konsolas.aac.punishment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.util.PlayerDebugMessage;
import me.konsolas.aac.compat.PlayerPingAccess;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class LogManager {
    private boolean debugEnabled;
    public final Set verboseSubscribers = new HashSet();
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private final AAC plugin;
    private FileWriter debugFileWriter;
    private PrintWriter debugWriter;
    private final Thread debugWriterThread;
    private boolean logToConsole = false;
    private boolean verboseEnabled = false;
    private final Thread logWriterThread;
    private final BlockingQueue debugQueue = new LinkedBlockingQueue();
    private final BlockingQueue logQueue = new LinkedBlockingQueue();
    private final Queue chatQueue = new ConcurrentLinkedQueue();

    public LogManager(AAC aAC) {
        this.plugin = aAC;
        this.logToConsole = this.plugin.getConfig().getBoolean("log_console", this.logToConsole);
        this.verboseEnabled = this.plugin.getConfig().getBoolean("verbose", this.verboseEnabled);
        this.logWriterThread = new LogWriterThread(this, "AAC-LogWriter");
        this.debugWriterThread = new DebugWriterThread(this, "AAC-DebugWriter");
        if (this.verboseEnabled) {
            Server server = Bukkit.getServer();
            server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::flushVerboseMessages, 1L, 1L);
        }
        this.logWriterThread.start();
        this.debugWriterThread.start();
    }

    public void logViolation(String str) {
        if (this.logToConsole && this.verboseEnabled) {
            this.plugin.getLogger().info("[VERBOSE] " + str);
        }
        this.logQueue.add(str);
        if (this.verboseEnabled) {
            this.chatQueue.add(str);
        }
        if (this.debugEnabled) {
            this.debugQueue.add(new PlayerDebugMessage(this, null, str));
        }
    }

    public boolean isDebugEnabled() {
        return this.debugEnabled;
    }

    public void logPlayerDebug(Player player, String str) {
        if (this.debugEnabled) {
            this.debugQueue.add(new PlayerDebugMessage(this, player, str));
        }
    }

    public String startDebugRecording(long ticks) {
        long ticks2 = (ticks = 0x3D362E22EE6CL ^ ticks) ^ 0x290092E9E80FL;
        UUID uUID = UUID.randomUUID();
        try {
            String str = "debug." + uUID;
            this.openDebugLog(str);
        }
        catch (IOException iOException) {
            long cfr_ignored_0 = 0x1A941503D8610789L ^ ticks;
            return "Failed to setup debug log file for writing";
        }
        this.debugWriter.println("AAC debug log (" + uUID + ")");
        this.debugWriter.println("version: " + AAC.class.getPackage().getImplementationVersion());
        this.debugWriter.println("");
        this.debugWriter.println("- Active configuration");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(this.plugin.getDataFolder(), "config.yml")));
            Throwable throwable = null;
            try {
                String str2;
                while ((str2 = bufferedReader.readLine()) != null) {
                    int value = str2.indexOf(35);
                    if ((str2 = str2.substring(0, value == -1 ? str2.length() : value)).trim().length() <= 0) continue;
                    this.debugWriter.println(str2);
                }
            }
            catch (Throwable throwable2) {
                try {
                    throwable = throwable2;
                    throw throwable2;
                }
                catch (Throwable throwable3) {
                    if (throwable != null) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable throwable4) {
                            throwable.addSuppressed(throwable4);
                        }
                    } else {
                        bufferedReader.close();
                    }
                    throw throwable3;
                }
            }
            bufferedReader.close();
        }
        catch (IOException iOException) {
            long cfr_ignored_1 = 0x4E8979A04A961BE3L ^ ticks;
            return "Failed to read config.yml";
        }
        this.debugWriter.println("");
        this.writeDebugEntry(new PlayerDebugMessage(this, null, "--- BEGIN DEBUG DATA ---"), ticks2);
        this.debugEnabled = true;
        Bukkit.getLogger().info("Debug was activated (" + uUID + ")");
        return "Now saving data (id:" + uUID + ")";
    }

    public String stopDebugSession(long ticks) {
        long ticks2 = (ticks = 0x3D362E22EE6CL ^ ticks) ^ 0x513D022A6AC2L;
        if (this.debugEnabled) {
            this.writeDebugEntry(new PlayerDebugMessage(this, null, "--- END DEBUG DATA ---"), ticks2);
            this.debugEnabled = false;
            this.closeDebugLog();
            Bukkit.getLogger().info("Debug was deactivated");
            long cfr_ignored_0 = 0x5308AD547EEEC4A4L ^ ticks;
            return "Finished debugging";
        }
        return "No active debug session";
    }

    private void writeDebugEntry(PlayerDebugMessage playerDebugMessage, long ticks) {
        long ticks2 = (ticks = 0x3D362E22EE6CL ^ ticks) ^ 0x6E719B0451A0L;
        if (this.debugWriter != null) {
            Date date = new Date();
            if (PlayerDebugMessage.getPlayer(playerDebugMessage) != null) {
                Vector vector;
                Vector vector2 = vector = PlayerDebugMessage.getPlayer(playerDebugMessage).getLocation().toVector();
                long ticks3 = ticks2;
                Player player = PlayerDebugMessage.getPlayer(playerDebugMessage);
                this.debugWriter.println(PlayerDebugMessage.getPlayer(playerDebugMessage).getName() + " @" + this.timestampFormat.format(date) + "/[" + MathUtilities.formatVector(vector2) + "] : " + PlayerDebugMessage.getMessage(playerDebugMessage) + " [ping:" + PlayerPingAccess.getPing(player, ticks3) + "]");
            } else {
                this.debugWriter.println("<> @" + this.timestampFormat.format(date) + ": " + PlayerDebugMessage.getMessage(playerDebugMessage));
            }
            this.debugWriter.flush();
        }
    }

    private void openDebugLog(String str) throws IOException {
        File file;
        File file2 = this.plugin.getDataFolder();
        if (!file2.exists()) {
            file2.mkdir();
        }
        if (!(file = new File(file2, str + ".debug.log")).exists()) {
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }
        this.debugFileWriter = new FileWriter(file, true);
        this.debugWriter = new PrintWriter(this.debugFileWriter);
    }

    private void closeDebugLog() {
        try {
            Bukkit.getLogger().info("[AAC] Saving log file...");
            this.debugWriter.close();
        }
        catch (NullPointerException nullPointerException) {
            Bukkit.getLogger().warning("[AAC] Could not save log file as none was created.");
        }
    }

    public boolean isDebugActive() {
        return this.debugEnabled;
    }

    private void flushVerboseMessages() {
        String str;
        int value = 0;
        while ((str = (String)this.chatQueue.poll()) != null && value++ < 10) {
            Server server = Bukkit.getServer();
            for (Player player : server.getOnlinePlayers()) {
                if (!player.hasPermission("AAC.verbose") || !this.verboseSubscribers.contains(player.getUniqueId())) continue;
                player.sendMessage(" * \u00a74[AAC] \u00a77" + str);
            }
        }
        if (this.chatQueue.size() > 0) {
            Server server = Bukkit.getServer();
            for (Player player : server.getOnlinePlayers()) {
                if (!player.hasPermission("AAC.verbose") || !this.verboseSubscribers.contains(player.getUniqueId())) continue;
                player.sendMessage(" * \u00a74[AAC] \u00a77 (VerboseLogger) Truncated " + this.chatQueue.size() + " lines from chat logging");
                this.chatQueue.clear();
            }
        }
    }

    static BlockingQueue getLogQueue(LogManager logManager) {
        return logManager.logQueue;
    }

    static AAC getPlugin(LogManager logManager) {
        return logManager.plugin;
    }

    static BlockingQueue getDebugQueue(LogManager logManager) {
        return logManager.debugQueue;
    }

    static void appendDebugMessage(LogManager logManager, PlayerDebugMessage playerDebugMessage, long ticks) {
        long ticks2 = (ticks = 0x3D362E22EE6CL ^ ticks) ^ 0x17C3B138472AL;
        logManager.writeDebugEntry(playerDebugMessage, ticks2);
    }

}

