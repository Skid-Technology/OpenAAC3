package me.konsolas.aac.punishment;

import java.util.Iterator;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PunishmentScanTask
implements Runnable {
    final PunishmentManager manager;

    private PunishmentScanTask(PunishmentManager punishmentManager) {
        this.manager = punishmentManager;
    }

    @Override
    public void run() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            for (HackType hackType : HackType.values()) {
                if (!PunishmentManager.getPlugin((PunishmentManager)this.manager).checkManager.isCheckEnabled(hackType)) continue;
                int value = PunishmentManager.getPlugin((PunishmentManager)this.manager).checkManager.getDetector(hackType).getViolationLevel(player);
                if (PunishmentManager.getThresholds(this.manager).get((Object)hackType) == null) continue;
                for (PunishmentThreshold punishmentThreshold : PunishmentManager.getThresholds(this.manager).get(hackType)) {
                    if (!punishmentThreshold.crossedThreshold(player, value)) continue;
                    Server server2 = Bukkit.getServer();
                    server2.getScheduler().scheduleSyncDelayedTask((Plugin)PunishmentManager.getPlugin(this.manager), (Runnable)new PunishmentCommandTask(this, punishmentThreshold, player, hackType));
                }
            }
        }
    }

    public PunishmentScanTask(PunishmentManager punishmentManager, PunishmentScanAccess punishmentScanAccess) {
        this(punishmentManager);
    }
}

