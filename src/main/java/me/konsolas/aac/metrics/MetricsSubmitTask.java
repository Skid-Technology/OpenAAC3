package me.konsolas.aac.metrics;

import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MetricsSubmitTask
extends TimerTask {
    final Timer timer;
    final MetricsLite metrics;

    public MetricsSubmitTask(MetricsLite metricsLite, Timer timer) {
        this.metrics = metricsLite;
        this.timer = timer;
    }

    @Override
    public void run() {
        if (!MetricsLite.access$000(this.metrics).isEnabled()) {
            this.timer.cancel();
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)MetricsLite.access$000(this.metrics), this::submitPluginMetrics);
    }

    private void submitPluginMetrics() {
        MetricsLite.access$100(this.metrics);
    }
}

