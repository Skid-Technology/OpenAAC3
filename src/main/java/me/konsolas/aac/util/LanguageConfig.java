package me.konsolas.aac.util;

import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import me.konsolas.aac.AAC;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageConfig {
    private final AAC plugin;
    private FileConfiguration configuration = null;
    private File file = null;

    public LanguageConfig(AAC aAC) {
        this.plugin = aAC;
        this.saveDefault();
    }

    public String getMessage(String str) {
        return this.getConfig().getString(str);
    }

    public FileConfiguration getConfig() {
        if (this.configuration == null) {
            this.reload();
        }
        return this.configuration;
    }

    public void reload() {
        if (this.file == null) {
            this.file = new File(this.plugin.getDataFolder(), "language.yml");
        }
        this.configuration = YamlConfiguration.loadConfiguration((File)this.file);
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(this.plugin.getResource("language.yml"), "UTF8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
        if (inputStreamReader != null) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(inputStreamReader);
            this.configuration.setDefaults((Configuration)yamlConfiguration);
        }
    }

    public void saveDefault() {
        if (this.file == null) {
            this.file = new File(this.plugin.getDataFolder(), "language.yml");
        }
        if (!this.file.exists()) {
            this.plugin.saveResource("language.yml", false);
        }
    }

}

