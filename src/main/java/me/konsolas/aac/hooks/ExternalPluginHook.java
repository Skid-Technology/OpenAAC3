package me.konsolas.aac.hooks;

import me.konsolas.aac.AAC;
import org.bukkit.event.Listener;

public abstract class ExternalPluginHook
implements Listener {
    protected final AAC plugin;

    public ExternalPluginHook(AAC aAC) {
        this.plugin = aAC;
    }

    static {
        if (null != null) {
        }
    }
}

