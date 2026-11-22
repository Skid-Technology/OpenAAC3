package me.konsolas.aac.command;

import java.util.Set;
import java.util.UUID;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.LanguageConfig;
import me.konsolas.aac.punishment.LogManager;
import me.konsolas.aac.gui.MainMenu;
import me.konsolas.aac.compat.NearbyBlockRefresher;
import me.konsolas.aac.punishment.PunishmentManager;
import me.konsolas.aac.punishment.StaffNotificationTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CommandController
implements CommandExecutor,
Listener {
    private final AAC plugin;
    private final String registeredUser;

    public CommandController(AAC aAC) {
        this.plugin = aAC;
        this.registeredUser = "3.5.0";
    }


    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        AsyncPlayerChatEvent asyncPlayerChatEvent2 = asyncPlayerChatEvent;
        String str = "AAC.notify";
        Player player = asyncPlayerChatEvent2.getPlayer();
        if (player.hasPermission(str)) {
            AsyncPlayerChatEvent asyncPlayerChatEvent3 = asyncPlayerChatEvent;
            CommandController commandController = this;
            String str2 = "prefix_to_speak";
            LanguageConfig languageConfig = commandController.plugin.messages;
            String str3 = languageConfig.getMessage(str2);
            String str4 = asyncPlayerChatEvent3.getMessage();
            if (str4.startsWith(str3)) {
                boolean flag = true;
                AsyncPlayerChatEvent asyncPlayerChatEvent4 = asyncPlayerChatEvent;
                asyncPlayerChatEvent4.setCancelled(flag);
                CommandController commandController2 = this;
                AAC aAC = commandController2.plugin;
                Server server = aAC.getServer();
                CommandController commandController3 = this;
                StaffNotificationTask staffNotificationTask = new StaffNotificationTask(this, asyncPlayerChatEvent);
                AAC aAC2 = commandController3.plugin;
                BukkitScheduler bukkitScheduler = server.getScheduler();
                bukkitScheduler.scheduleSyncDelayedTask((Plugin)aAC2, (Runnable)staffNotificationTask);
            }
        }
    }

    private boolean handleBanCommand(CommandSender commandSender, String[] stringArray) {
        if (stringArray.length != 1) {
            String str = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            String str2 = "Incorrect number of arguments.";
            StringBuilder stringBuilder2 = stringBuilder.append(str);
            StringBuilder stringBuilder3 = stringBuilder2.append(str2);
            String str3 = stringBuilder3.toString();
            CommandSender commandSender2 = commandSender;
            commandSender2.sendMessage(str3);
            return false;
        }
        CommandController commandController = this;
        AAC aAC = commandController.plugin;
        PunishmentManager punishmentManager = aAC.punishmentManager;
        CommandController commandController2 = this;
        String str4 = stringArray[0];
        if (punishmentManager.isBanned(str4)) {
            String str5 = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            CommandController commandController3 = this;
            String str6 = stringArray[0];
            StringBuilder stringBuilder4 = stringBuilder.append(str5);
            String str7 = " is already banned";
            StringBuilder stringBuilder5 = stringBuilder4.append(str6);
            StringBuilder stringBuilder6 = stringBuilder5.append(str7);
            String str8 = stringBuilder6.toString();
            CommandSender commandSender3 = commandSender;
            commandSender3.sendMessage(str8);
            return false;
        }
        CommandController commandController4 = this;
        String str9 = stringArray[0];
        if (Bukkit.getPlayer((String)str9) == null) {
            String str10 = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            CommandController commandController5 = this;
            String str11 = stringArray[0];
            StringBuilder stringBuilder7 = stringBuilder.append(str10);
            String str12 = " is not online";
            StringBuilder stringBuilder8 = stringBuilder7.append(str11);
            StringBuilder stringBuilder9 = stringBuilder8.append(str12);
            String str13 = stringBuilder9.toString();
            CommandSender commandSender4 = commandSender;
            commandSender4.sendMessage(str13);
            return false;
        }
        CommandController commandController6 = this;
        AAC aAC2 = commandController6.plugin;
        CommandController commandController7 = this;
        String str14 = stringArray[0];
        Player player = Bukkit.getPlayer((String)str14);
        PunishmentManager punishmentManager2 = aAC2.punishmentManager;
        Player player2 = player;
        punishmentManager2.markBanned(player2);
        CommandController commandController8 = this;
        String str15 = stringArray[0];
        String str16 = AAC.messagePrefix;
        StringBuilder stringBuilder = new StringBuilder();
        CommandController commandController9 = this;
        String str17 = "ban_message";
        LanguageConfig languageConfig = commandController9.plugin.messages;
        String str18 = languageConfig.getMessage(str17);
        StringBuilder stringBuilder10 = stringBuilder.append(str16);
        StringBuilder stringBuilder11 = stringBuilder10.append(str18);
        String str19 = stringBuilder11.toString();
        Player player3 = Bukkit.getPlayer((String)str15);
        player3.kickPlayer(str19);
        String str20 = AAC.messagePrefix;
        StringBuilder stringBuilder12 = new StringBuilder();
        CommandController commandController10 = this;
        String str21 = stringArray[0];
        StringBuilder stringBuilder13 = stringBuilder12.append(str20);
        String str22 = " has been banned";
        StringBuilder stringBuilder14 = stringBuilder13.append(str21);
        StringBuilder stringBuilder15 = stringBuilder14.append(str22);
        String str23 = stringBuilder15.toString();
        CommandSender commandSender5 = commandSender;
        commandSender5.sendMessage(str23);
        CommandController commandController11 = this;
        AAC aAC3 = commandController11.plugin;
        CommandController commandController12 = this;
        String str24 = stringArray[0];
        StringBuilder stringBuilder16 = new StringBuilder();
        String str25 = " was banned by ";
        StringBuilder stringBuilder17 = stringBuilder16.append(str24);
        CommandSender commandSender6 = commandSender;
        String str26 = commandSender6.getName();
        StringBuilder stringBuilder18 = stringBuilder17.append(str25);
        StringBuilder stringBuilder19 = stringBuilder18.append(str26);
        String str27 = stringBuilder19.toString();
        LogManager logManager = aAC3.logManager;
        String str28 = str27;
        logManager.logViolation(str28);
        return true;
    }

    private boolean handleVerboseCommand(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            String str = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            String str2 = "Please use the config file to toggle console logging.";
            StringBuilder stringBuilder2 = stringBuilder.append(str);
            StringBuilder stringBuilder3 = stringBuilder2.append(str2);
            String str3 = stringBuilder3.toString();
            CommandSender commandSender2 = commandSender;
            commandSender2.sendMessage(str3);
            return true;
        }
        CommandController commandController = this;
        AAC aAC = commandController.plugin;
        LogManager logManager = aAC.logManager;
        Set set = logManager.verboseSubscribers;
        Player player = (Player)commandSender;
        UUID uUID = player.getUniqueId();
        if (set.contains(uUID)) {
            CommandController commandController2 = this;
            AAC aAC2 = commandController2.plugin;
            LogManager logManager2 = aAC2.logManager;
            Player player2 = (Player)commandSender;
            UUID uUID2 = player2.getUniqueId();
            Set set2 = logManager2.verboseSubscribers;
            set2.remove(uUID2);
            String str4 = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            String str5 = "AAC will no longer give you verbose output.";
            StringBuilder stringBuilder4 = stringBuilder.append(str4);
            StringBuilder stringBuilder5 = stringBuilder4.append(str5);
            String str6 = stringBuilder5.toString();
            CommandSender commandSender3 = commandSender;
            commandSender3.sendMessage(str6);
        } else {
            CommandController commandController3 = this;
            AAC aAC3 = commandController3.plugin;
            LogManager logManager3 = aAC3.logManager;
            Player player3 = (Player)commandSender;
            UUID uUID3 = player3.getUniqueId();
            Set set3 = logManager3.verboseSubscribers;
            set3.add(uUID3);
            String str7 = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            String str8 = "AAC will now give you verbose output.";
            StringBuilder stringBuilder6 = stringBuilder.append(str7);
            StringBuilder stringBuilder7 = stringBuilder6.append(str8);
            String str9 = stringBuilder7.toString();
            CommandSender commandSender4 = commandSender;
            commandSender4.sendMessage(str9);
        }
        return true;
    }

    private boolean handleUnbanCommand(CommandSender commandSender, String[] stringArray) {
        if (stringArray.length != 1) {
            String str = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            String str2 = "Incorrect number of arguments.";
            StringBuilder stringBuilder2 = stringBuilder.append(str);
            StringBuilder stringBuilder3 = stringBuilder2.append(str2);
            String str3 = stringBuilder3.toString();
            CommandSender commandSender2 = commandSender;
            commandSender2.sendMessage(str3);
            return false;
        }
        CommandController commandController = this;
        AAC aAC = commandController.plugin;
        PunishmentManager punishmentManager = aAC.punishmentManager;
        CommandController commandController2 = this;
        String str4 = stringArray[0];
        if (!punishmentManager.isBanned(str4)) {
            String str5 = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            CommandController commandController3 = this;
            String str6 = stringArray[0];
            StringBuilder stringBuilder4 = stringBuilder.append(str5);
            String str7 = " is not already banned";
            StringBuilder stringBuilder5 = stringBuilder4.append(str6);
            StringBuilder stringBuilder6 = stringBuilder5.append(str7);
            String str8 = stringBuilder6.toString();
            CommandSender commandSender3 = commandSender;
            commandSender3.sendMessage(str8);
            return false;
        }
        CommandController commandController4 = this;
        AAC aAC2 = commandController4.plugin;
        CommandController commandController5 = this;
        String str9 = stringArray[0];
        PunishmentManager punishmentManager2 = aAC2.punishmentManager;
        punishmentManager2.unban(str9);
        String str10 = AAC.messagePrefix;
        StringBuilder stringBuilder = new StringBuilder();
        CommandController commandController6 = this;
        String str11 = stringArray[0];
        StringBuilder stringBuilder7 = stringBuilder.append(str10);
        String str12 = " has been unbanned";
        StringBuilder stringBuilder8 = stringBuilder7.append(str11);
        StringBuilder stringBuilder9 = stringBuilder8.append(str12);
        String str13 = stringBuilder9.toString();
        CommandSender commandSender4 = commandSender;
        commandSender4.sendMessage(str13);
        CommandController commandController7 = this;
        AAC aAC3 = commandController7.plugin;
        CommandController commandController8 = this;
        String str14 = stringArray[0];
        StringBuilder stringBuilder10 = new StringBuilder();
        String str15 = " was unbanned by ";
        StringBuilder stringBuilder11 = stringBuilder10.append(str14);
        CommandSender commandSender5 = commandSender;
        String str16 = commandSender5.getName();
        StringBuilder stringBuilder12 = stringBuilder11.append(str15);
        StringBuilder stringBuilder13 = stringBuilder12.append(str16);
        String str17 = stringBuilder13.toString();
        LogManager logManager = aAC3.logManager;
        String str18 = str17;
        logManager.logViolation(str18);
        return true;
    }

    private boolean handleAdminGuiCommand(CommandSender commandSender) {
        Player player;
        Player player2;
        if (!(commandSender instanceof Player)) {
            String str = "You can't see an inventory GUI so...";
            CommandSender commandSender2 = commandSender;
            commandSender2.sendMessage(str);
            return false;
        }
        Player player3 = player2 = (player = (Player)commandSender);
        MainMenu.open(player3);
        return true;
    }

    private boolean handleReloadCommand(CommandSender commandSender) {
        String str = AAC.messagePrefix;
        StringBuilder stringBuilder = new StringBuilder();
        String str2 = "Reload request has been registered.";
        StringBuilder stringBuilder2 = stringBuilder.append(str);
        StringBuilder stringBuilder3 = stringBuilder2.append(str2);
        String str3 = stringBuilder3.toString();
        CommandSender commandSender2 = commandSender;
        commandSender2.sendMessage(str3);
        CommandController commandController = this;
        AAC aAC = commandController.plugin;
        String str4 = "Reload requested by ";
        StringBuilder stringBuilder4 = new StringBuilder();
        CommandSender commandSender3 = commandSender;
        String str5 = commandSender3.getName();
        StringBuilder stringBuilder5 = stringBuilder4.append(str4);
        StringBuilder stringBuilder6 = stringBuilder5.append(str5);
        String str6 = stringBuilder6.toString();
        LogManager logManager = aAC.logManager;
        String str7 = str6;
        logManager.logViolation(str7);
        CommandController commandController2 = this;
        AAC aAC2 = commandController2.plugin;
        aAC2.reloadPlugin();
        return true;
    }

    private boolean handleKickCommand(CommandSender commandSender, String[] stringArray, long ticks) {
        Player player;
        long ticks2 = (ticks = 0x23D7D13131BL ^ ticks) ^ 0x267BEDCCA0E5L;
        if (stringArray.length < 2) {
            String str = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            String str2 = "Incorrect number of arguments.";
            StringBuilder stringBuilder2 = stringBuilder.append(str);
            StringBuilder stringBuilder3 = stringBuilder2.append(str2);
            String str3 = stringBuilder3.toString();
            CommandSender commandSender2 = commandSender;
            commandSender2.sendMessage(str3);
            return false;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < stringArray.length; ++i) {
            String str4 = stringArray[i];
            StringBuilder stringBuilder4 = stringBuilder;
            stringBuilder4.append(str4);
            char c = ' ';
            StringBuilder stringBuilder5 = stringBuilder;
            stringBuilder5.append(c);
        }
        CommandController commandController = this;
        String str5 = stringArray[0];
        Player player2 = Bukkit.getPlayer((String)str5);
        if (player2 == null || !(player = player2).isOnline()) {
            CommandController commandController2 = this;
            String str6 = stringArray[0];
            StringBuilder stringBuilder6 = new StringBuilder();
            String str7 = " is not online.";
            StringBuilder stringBuilder7 = stringBuilder6.append(str6);
            StringBuilder stringBuilder8 = stringBuilder7.append(str7);
            String str8 = stringBuilder8.toString();
            CommandSender commandSender3 = commandSender;
            commandSender3.sendMessage(str8);
            return false;
        }
        CommandController commandController3 = this;
        AAC aAC = commandController3.plugin;
        long ticks3 = ticks2;
        Player player3 = player2;
        PunishmentManager punishmentManager = aAC.punishmentManager;
        punishmentManager.recordPunishment(player3, ticks3);
        String str9 = AAC.messagePrefix;
        StringBuilder stringBuilder9 = new StringBuilder();
        StringBuilder stringBuilder10 = stringBuilder;
        String str10 = stringBuilder10.toString();
        StringBuilder stringBuilder11 = stringBuilder9.append(str9);
        StringBuilder stringBuilder12 = stringBuilder11.append(str10);
        String str11 = stringBuilder12.toString();
        Player player4 = player2;
        player4.kickPlayer(str11);
        CommandController commandController4 = this;
        AAC aAC2 = commandController4.plugin;
        String str12 = "aackick_broadcast";
        FileConfiguration fileConfiguration = aAC2.getConfig();
        if (fileConfiguration.getBoolean(str12)) {
            String str13 = AAC.messagePrefix;
            StringBuilder stringBuilder13 = new StringBuilder();
            Player player5 = player2;
            String str14 = player5.getName();
            StringBuilder stringBuilder14 = stringBuilder13.append(str13);
            CommandController commandController5 = this;
            String str15 = "aac_kick_broadcast";
            LanguageConfig languageConfig = commandController5.plugin.messages;
            String str16 = languageConfig.getMessage(str15);
            StringBuilder stringBuilder15 = stringBuilder14.append(str14);
            StringBuilder stringBuilder16 = stringBuilder15.append(str16);
            String str17 = stringBuilder16.toString();
            Bukkit.broadcastMessage((String)str17);
        }
        return true;
    }

    private boolean handleDebugCommand(CommandSender commandSender, String[] stringArray, long ticks) {
        long ticks2 = ticks = 0x23D7D13131BL ^ ticks;
        long ticks3 = ticks2 ^ 0x1C1B30AA4A4DL;
        long ticks4 = ticks2 ^ 0x6426A069C880L;
        CommandController commandController = this;
        AAC aAC = commandController.plugin;
        LogManager logManager = aAC.logManager;
        if (logManager.isDebugActive()) {
            String str = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            CommandController commandController2 = this;
            AAC aAC2 = commandController2.plugin;
            long ticks5 = ticks4;
            LogManager logManager2 = aAC2.logManager;
            String str2 = logManager2.stopDebugSession(ticks5);
            StringBuilder stringBuilder2 = stringBuilder.append(str);
            StringBuilder stringBuilder3 = stringBuilder2.append(str2);
            String str3 = stringBuilder3.toString();
            CommandSender commandSender2 = commandSender;
            commandSender2.sendMessage(str3);
        } else {
            String str4 = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            CommandController commandController3 = this;
            AAC aAC3 = commandController3.plugin;
            long ticks6 = ticks3;
            LogManager logManager3 = aAC3.logManager;
            String str5 = logManager3.startDebugRecording(ticks6);
            StringBuilder stringBuilder4 = stringBuilder.append(str4);
            StringBuilder stringBuilder5 = stringBuilder4.append(str5);
            String str6 = stringBuilder5.toString();
            CommandSender commandSender3 = commandSender;
            commandSender3.sendMessage(str6);
        }
        return true;
    }

    private boolean handleMessageCommand(CommandSender commandSender, String[] stringArray) {
        if (stringArray.length < 2) {
            String str = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            String str2 = "Incorrect number of arguments.";
            StringBuilder stringBuilder2 = stringBuilder.append(str);
            StringBuilder stringBuilder3 = stringBuilder2.append(str2);
            String str3 = stringBuilder3.toString();
            CommandSender commandSender2 = commandSender;
            commandSender2.sendMessage(str3);
            return false;
        }
        CommandController commandController = this;
        String str4 = stringArray[0];
        Player player = Bukkit.getPlayer((String)str4);
        if (player == null) {
            CommandController commandController2 = this;
            String str5 = stringArray[0];
            StringBuilder stringBuilder = new StringBuilder();
            String str6 = " is not online.";
            StringBuilder stringBuilder4 = stringBuilder.append(str5);
            StringBuilder stringBuilder5 = stringBuilder4.append(str6);
            String str7 = stringBuilder5.toString();
            CommandSender commandSender3 = commandSender;
            commandSender3.sendMessage(str7);
            return false;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < stringArray.length; ++i) {
            String str8 = stringArray[i];
            StringBuilder stringBuilder6 = stringBuilder;
            stringBuilder6.append(str8);
            char c = ' ';
            StringBuilder stringBuilder7 = stringBuilder;
            stringBuilder7.append(c);
        }
        String str9 = AAC.messagePrefix;
        StringBuilder stringBuilder8 = new StringBuilder();
        StringBuilder stringBuilder9 = stringBuilder;
        String str10 = stringBuilder9.toString();
        StringBuilder stringBuilder10 = stringBuilder8.append(str9);
        StringBuilder stringBuilder11 = stringBuilder10.append(str10);
        String str11 = stringBuilder11.toString();
        Player player2 = player;
        player2.sendMessage(str11);
        return true;
    }

    private boolean handleStaffNotifyCommand(CommandSender commandSender, String[] stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] stringArray2 = stringArray;
        int value = stringArray2.length;
        for (int i = 0; i < value; ++i) {
            String str;
            String str2 = str = stringArray2[i];
            StringBuilder stringBuilder2 = stringBuilder;
            stringBuilder2.append(str2);
            char c = ' ';
            StringBuilder stringBuilder3 = stringBuilder;
            stringBuilder3.append(c);
        }
        CommandController commandController = this;
        String str3 = "format";
        LanguageConfig languageConfig = commandController.plugin.messages;
        String str4 = languageConfig.getMessage(str3);
        char c2 = '&';
        CommandSender commandSender2 = commandSender;
        String str5 = commandSender2.getName();
        String str6 = "{sender}";
        String str7 = ChatColor.translateAlternateColorCodes((char)c2, (String)str4);
        String str8 = str7.replace(str6, str5);
        StringBuilder stringBuilder4 = new StringBuilder();
        StringBuilder stringBuilder5 = stringBuilder;
        String str9 = stringBuilder5.toString();
        StringBuilder stringBuilder6 = stringBuilder4.append(str8);
        StringBuilder stringBuilder7 = stringBuilder6.append(str9);
        String str10 = "AAC.notify";
        String str11 = stringBuilder7.toString();
        Bukkit.broadcast((String)str11, (String)str10);
        return true;
    }

    private boolean handleRestoreChunkCommand(CommandSender commandSender, String[] stringArray) {
        if (stringArray.length != 1) {
            String str = AAC.messagePrefix;
            StringBuilder stringBuilder = new StringBuilder();
            String str2 = "Incorrect number of arguments.";
            StringBuilder stringBuilder2 = stringBuilder.append(str);
            StringBuilder stringBuilder3 = stringBuilder2.append(str2);
            String str3 = stringBuilder3.toString();
            CommandSender commandSender2 = commandSender;
            commandSender2.sendMessage(str3);
            return false;
        }
        CommandController commandController = this;
        String str4 = stringArray[0];
        Player player = Bukkit.getPlayer((String)str4);
        if (player == null) {
            CommandController commandController2 = this;
            String str5 = stringArray[0];
            StringBuilder stringBuilder = new StringBuilder();
            String str6 = " is not online.";
            StringBuilder stringBuilder4 = stringBuilder.append(str5);
            StringBuilder stringBuilder5 = stringBuilder4.append(str6);
            String str7 = stringBuilder5.toString();
            CommandSender commandSender3 = commandSender;
            commandSender3.sendMessage(str7);
            return false;
        }
        String str8 = AAC.messagePrefix;
        StringBuilder stringBuilder = new StringBuilder();
        String str9 = "Trying to restore chunks for ";
        StringBuilder stringBuilder6 = stringBuilder.append(str8);
        Player player2 = player;
        String str10 = player2.getName();
        StringBuilder stringBuilder7 = stringBuilder6.append(str9);
        StringBuilder stringBuilder8 = stringBuilder7.append(str10);
        String str11 = stringBuilder8.toString();
        CommandSender commandSender4 = commandSender;
        commandSender4.sendMessage(str11);
        Player player3 = player;
        NearbyBlockRefresher.refreshNearbyBlocks(player3);
        return true;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String str, String[] stringArray) {
        long ticks = 129427348397213L;
        long ticks2 = 5199498928997L;
        if (command.getName().equalsIgnoreCase("aac")) {
            commandSender.sendMessage(AAC.messagePrefix + "AAC " + this.registeredUser + ": Haxor rekker (~konsolas)");
            return true;
        }
        Command command3 = command;
        String str2 = "aacban";
        String str3 = command3.getName();
        if (str3.equalsIgnoreCase(str2)) {
            String[] stringArray2 = stringArray;
            CommandSender commandSender2 = commandSender;
            CommandController commandController = this;
            String[] stringArray3 = stringArray2;
            CommandSender commandSender3 = commandSender2;
            return commandController.handleBanCommand(commandSender3, stringArray3);
        }
        Command command4 = command;
        String str4 = "aacverbose";
        String str5 = command4.getName();
        if (str5.equalsIgnoreCase(str4)) {
            CommandSender commandSender4 = commandSender;
            CommandController commandController = this;
            CommandSender commandSender5 = commandSender4;
            return commandController.handleVerboseCommand(commandSender5);
        }
        Command command5 = command;
        String str6 = "aacunban";
        String str7 = command5.getName();
        if (str7.equalsIgnoreCase(str6)) {
            String[] stringArray4 = stringArray;
            CommandSender commandSender6 = commandSender;
            CommandController commandController = this;
            String[] stringArray5 = stringArray4;
            CommandSender commandSender7 = commandSender6;
            return commandController.handleUnbanCommand(commandSender7, stringArray5);
        }
        Command command6 = command;
        String str8 = "aacadmin";
        String str9 = command6.getName();
        if (str9.equalsIgnoreCase(str8)) {
            CommandSender commandSender8 = commandSender;
            CommandController commandController = this;
            CommandSender commandSender9 = commandSender8;
            return commandController.handleAdminGuiCommand(commandSender9);
        }
        Command command7 = command;
        String str10 = "aacreload";
        String str11 = command7.getName();
        if (str11.equalsIgnoreCase(str10)) {
            CommandSender commandSender10 = commandSender;
            CommandController commandController = this;
            CommandSender commandSender11 = commandSender10;
            return commandController.handleReloadCommand(commandSender11);
        }
        Command command8 = command;
        String str12 = "aackick";
        String str13 = command8.getName();
        if (str13.equalsIgnoreCase(str12)) {
            long ticks3 = ticks;
            String[] stringArray6 = stringArray;
            CommandSender commandSender12 = commandSender;
            CommandController commandController = this;
            return commandController.handleKickCommand(commandSender12, stringArray6, ticks3);
        }
        Command command9 = command;
        String str14 = "aacdebug";
        String str15 = command9.getName();
        if (str15.equalsIgnoreCase(str14)) {
            long ticks4 = ticks2;
            String[] stringArray7 = stringArray;
            CommandSender commandSender13 = commandSender;
            CommandController commandController = this;
            return commandController.handleDebugCommand(commandSender13, stringArray7, ticks4);
        }
        Command command10 = command;
        String str16 = "aacmessage";
        String str17 = command10.getName();
        if (str17.equalsIgnoreCase(str16)) {
            String[] stringArray8 = stringArray;
            CommandSender commandSender14 = commandSender;
            CommandController commandController = this;
            String[] stringArray9 = stringArray8;
            CommandSender commandSender15 = commandSender14;
            return commandController.handleMessageCommand(commandSender15, stringArray9);
        }
        Command command11 = command;
        String str18 = "aacstaffnotify";
        String str19 = command11.getName();
        if (str19.equalsIgnoreCase(str18)) {
            String[] stringArray10 = stringArray;
            CommandSender commandSender16 = commandSender;
            CommandController commandController = this;
            String[] stringArray11 = stringArray10;
            CommandSender commandSender17 = commandSender16;
            return commandController.handleStaffNotifyCommand(commandSender17, stringArray11);
        }
        Command command12 = command;
        String str20 = "aacrestorechunk";
        String str21 = command12.getName();
        if (str21.equalsIgnoreCase(str20)) {
            String[] stringArray12 = stringArray;
            CommandSender commandSender18 = commandSender;
            CommandController commandController = this;
            String[] stringArray13 = stringArray12;
            CommandSender commandSender19 = commandSender18;
            return commandController.handleRestoreChunkCommand(commandSender19, stringArray13);
        }
        return false;
    }

    public static String getRegisteredUser(CommandController commandController) {
        CommandController commandController2 = commandController;
        return commandController2.registeredUser;
    }

    public static AAC getPlugin(CommandController commandController) {
        CommandController commandController2 = commandController;
        return commandController2.plugin;
    }
}

