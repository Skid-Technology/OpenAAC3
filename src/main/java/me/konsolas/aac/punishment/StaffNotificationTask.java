package me.konsolas.aac.punishment;

import me.konsolas.aac.command.CommandController;
import me.konsolas.aac.util.LanguageConfig;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class StaffNotificationTask
implements Runnable {
    private final AsyncPlayerChatEvent chatEvent;
    final CommandController commandController;

    public StaffNotificationTask(CommandController commandController, AsyncPlayerChatEvent asyncPlayerChatEvent) {
        this.commandController = commandController;
        this.chatEvent = asyncPlayerChatEvent;
    }

    @Override
    public void run() {
        CommandController commandController;
        StaffNotificationTask staffNotificationTask = this;
        AsyncPlayerChatEvent asyncPlayerChatEvent = staffNotificationTask.chatEvent;
        String str = "aacstaffnotify ";
        StringBuilder stringBuilder = new StringBuilder();
        StaffNotificationTask staffNotificationTask2 = this;
        AsyncPlayerChatEvent asyncPlayerChatEvent2 = staffNotificationTask2.chatEvent;
        StaffNotificationTask staffNotificationTask3 = this;
        CommandController commandController2 = commandController = staffNotificationTask3.commandController;
        String str2 = "prefix_to_speak";
        LanguageConfig languageConfig = CommandController.getPlugin((CommandController)commandController2).messages;
        String str3 = languageConfig.getMessage(str2);
        int value = str3.length();
        String str4 = asyncPlayerChatEvent2.getMessage();
        String str5 = str4.substring(value);
        StringBuilder stringBuilder2 = stringBuilder.append(str);
        StringBuilder stringBuilder3 = stringBuilder2.append(str5);
        String str6 = stringBuilder3.toString();
        Player player = asyncPlayerChatEvent.getPlayer();
        Server server = Bukkit.getServer();
        server.dispatchCommand((CommandSender)player, str6);
    }

}

