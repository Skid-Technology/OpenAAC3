package me.konsolas.aac.util;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TickRateMonitor
extends BukkitRunnable {
    private static TickRateMonitor instance;
    private final int sampleCapacity = 40;
    private long lastTickTime = System.currentTimeMillis();
    private final Deque samples = new ArrayDeque<Long>(Collections.nCopies(40, 50L));

    private TickRateMonitor(Plugin plugin) {
        this.runTaskTimer(plugin, 1L, 1L);
    }

    public static double getTps() {
        try {
            double d = 1000.0 / instance.calculateTps();
            return d > 20.0 ? 20.0 : d;
        }
        catch (Exception exception) {
            return 20.0;
        }
    }

    public static void start(Plugin plugin) {
        instance = new TickRateMonitor(plugin);
    }

    public void run() {
        long now = System.currentTimeMillis();
        long ticks = now - this.lastTickTime;
        this.lastTickTime = now;
        this.samples.removeFirst();
        this.samples.addLast(ticks);
    }

    public double calculateTps() {
        int value = 0;
        Iterator iterator = this.samples.iterator();
        while (iterator.hasNext()) {
            long ticks = (Long)iterator.next();
            value = (int)((long)value + ticks);
        }
        return (double)value / 40.0;
    }
}

