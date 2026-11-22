package me.konsolas.aac.punishment;

import me.konsolas.aac.api.HackType;
import org.bukkit.entity.Player;

public class PunishmentCommandTask
implements Runnable {
    private final PunishmentThreshold threshold;
    private final Player player;
    private final HackType hackType;
    final PunishmentScanTask scanTask;

    public PunishmentCommandTask(PunishmentScanTask punishmentScanTask, PunishmentThreshold punishmentThreshold, Player player, HackType hackType) {
        this.scanTask = punishmentScanTask;
        this.threshold = punishmentThreshold;
        this.player = player;
        this.hackType = hackType;
    }

    @Override
    public void run() {
        long ticks = 13038675528805L;
        long ticks2 = 14906959182713L;
        String str = "(Punisher) ->" + this.hackType.toString() + ": Dispatching command " + PunishmentManager.formatCommandBridge(this.scanTask.manager, this.threshold.getCommand(), this.player, ticks);
        PunishmentManager.getPlugin((PunishmentManager)this.scanTask.manager).logManager.logViolation(str);
        this.threshold.executeCommand(this.player, this.hackType, ticks2);
    }
}

