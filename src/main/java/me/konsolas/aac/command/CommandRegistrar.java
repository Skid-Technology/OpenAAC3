package me.konsolas.aac.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegistrar {
    static boolean commandsRegistered;
    public static int registryFlags;

    public static void registerCommands(JavaPlugin javaPlugin, CommandExecutor commandExecutor) {
        boolean flag;
        String str = "aac";
        JavaPlugin javaPlugin2 = javaPlugin;
        CommandExecutor commandExecutor2 = commandExecutor;
        PluginCommand pluginCommand = javaPlugin2.getCommand(str);
        pluginCommand.setExecutor(commandExecutor2);
        String str2 = "aacunban";
        JavaPlugin javaPlugin3 = javaPlugin;
        CommandExecutor commandExecutor3 = commandExecutor;
        PluginCommand pluginCommand2 = javaPlugin3.getCommand(str2);
        pluginCommand2.setExecutor(commandExecutor3);
        String str3 = "aacban";
        JavaPlugin javaPlugin4 = javaPlugin;
        CommandExecutor commandExecutor4 = commandExecutor;
        PluginCommand pluginCommand3 = javaPlugin4.getCommand(str3);
        pluginCommand3.setExecutor(commandExecutor4);
        String str4 = "aacadmin";
        JavaPlugin javaPlugin5 = javaPlugin;
        CommandExecutor commandExecutor5 = commandExecutor;
        PluginCommand pluginCommand4 = javaPlugin5.getCommand(str4);
        pluginCommand4.setExecutor(commandExecutor5);
        String str5 = "aackick";
        JavaPlugin javaPlugin6 = javaPlugin;
        CommandExecutor commandExecutor6 = commandExecutor;
        PluginCommand pluginCommand5 = javaPlugin6.getCommand(str5);
        pluginCommand5.setExecutor(commandExecutor6);
        String str6 = "aacreload";
        JavaPlugin javaPlugin7 = javaPlugin;
        CommandExecutor commandExecutor7 = commandExecutor;
        PluginCommand pluginCommand6 = javaPlugin7.getCommand(str6);
        pluginCommand6.setExecutor(commandExecutor7);
        String str7 = "aacdebug";
        JavaPlugin javaPlugin8 = javaPlugin;
        CommandExecutor commandExecutor8 = commandExecutor;
        PluginCommand pluginCommand7 = javaPlugin8.getCommand(str7);
        pluginCommand7.setExecutor(commandExecutor8);
        String str8 = "aacverbose";
        JavaPlugin javaPlugin9 = javaPlugin;
        CommandExecutor commandExecutor9 = commandExecutor;
        PluginCommand pluginCommand8 = javaPlugin9.getCommand(str8);
        pluginCommand8.setExecutor(commandExecutor9);
        String str9 = "aacmessage";
        JavaPlugin javaPlugin10 = javaPlugin;
        CommandExecutor commandExecutor10 = commandExecutor;
        PluginCommand pluginCommand9 = javaPlugin10.getCommand(str9);
        pluginCommand9.setExecutor(commandExecutor10);
        String str10 = "aacstaffnotify";
        JavaPlugin javaPlugin11 = javaPlugin;
        CommandExecutor commandExecutor11 = commandExecutor;
        PluginCommand pluginCommand10 = javaPlugin11.getCommand(str10);
        pluginCommand10.setExecutor(commandExecutor11);
        String str11 = "aacrestorechunk";
        JavaPlugin javaPlugin12 = javaPlugin;
        CommandExecutor commandExecutor12 = commandExecutor;
        PluginCommand pluginCommand11 = javaPlugin12.getCommand(str11);
        pluginCommand11.setExecutor(commandExecutor12);
        commandsRegistered = flag = true;
    }


    static {
        int i;
        boolean flag;
        commandsRegistered = flag = false;
        registryFlags = i = 4096;
    }
}

