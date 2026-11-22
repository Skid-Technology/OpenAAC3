package me.konsolas.aac.checks.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClickIntervalHistory {
    private final List<Long> intervals;
    private long lastClickTime;
    final FightSpeed detector;

    private ClickIntervalHistory(FightSpeed fightSpeed) {
        this.detector = fightSpeed;
        this.intervals = Collections.synchronizedList(new ArrayList<>());
        this.lastClickTime = System.currentTimeMillis();
    }

    private void recordClick() {
        long now = System.currentTimeMillis();
        this.intervals.add(now - this.lastClickTime);
        this.lastClickTime = now;
    }

    static void recordClick(ClickIntervalHistory clickIntervalHistory) {
        clickIntervalHistory.recordClick();
    }

    public ClickIntervalHistory(FightSpeed fightSpeed, FightSpeedPacketListener fightSpeedPacketListener) {
        this(fightSpeed);
    }

    static List<Long> getIntervals(ClickIntervalHistory clickIntervalHistory) {
        return clickIntervalHistory.intervals;
    }
}

