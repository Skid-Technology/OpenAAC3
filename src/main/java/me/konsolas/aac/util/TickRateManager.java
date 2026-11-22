package me.konsolas.aac.util;

import java.util.HashSet;
import java.util.Set;
import me.konsolas.aac.AAC;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public class TickRateManager {
    private final Set<HackType> temporarilyDisabledChecks;
    private final AAC plugin;

    public TickRateManager(AAC aAC) {
        this.temporarilyDisabledChecks = new HashSet<HackType>();
        this.plugin = aAC;
        for (HackType hackType : HackType.values()) {
            if (!this.plugin.getConfig().getBoolean(hackType.getName() + ".enabled")) continue;
            this.temporarilyDisabledChecks.add(hackType);
        }
        Server server = Bukkit.getServer();
        server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::sampleTickRate, 20L, 13L);
    }

    private void sampleTickRate() {
        double d = TickRateMonitor.getTps();
        if (d > 18.5) {
            for (HackType hackType : this.temporarilyDisabledChecks) {
                boolean flag = true;
                HackType hackType2 = hackType;
                this.plugin.checkManager.enableCheck(hackType2, flag);
            }
        } else {
            String str = "(TPSManager) TPS dropped to " + d;
            this.plugin.logManager.logViolation(str);
            if (d < 17.5) {
                boolean flag2 = true;
                HackType hackType = HackType.FIGHTSPEED;
                this.plugin.checkManager.disableCheck(hackType, flag2);
                boolean flag3 = true;
                HackType hackType3 = HackType.NOSWING;
                this.plugin.checkManager.disableCheck(hackType3, flag3);
            }
            if (d < 17.0) {
                boolean flag4 = true;
                HackType hackType = HackType.HITBOX;
                this.plugin.checkManager.disableCheck(hackType, flag4);
            } else if (d < 15.0) {
                String str2 = "(TPSManager) TPS is critical";
                this.plugin.logManager.logViolation(str2);
                boolean flag5 = true;
                HackType hackType = HackType.FLY;
                this.plugin.checkManager.disableCheck(hackType, flag5);
            } else if (d < 12.0) {
                boolean flag6 = true;
                HackType hackType = HackType.SPEED;
                this.plugin.checkManager.disableCheck(hackType, flag6);
            }
        }
    }
}

