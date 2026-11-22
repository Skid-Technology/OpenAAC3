package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.WeakHashMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class FightSpeed
extends Detector {
    private int maxHit;
    private int maxSwing;
    private long consistentThreshold;
    private boolean consistentCheck;
    private final Map<Player, ClickIntervalHistory> attackHistories;
    private final Map<Player, ClickIntervalHistory> swingHistories;

    public FightSpeed(AAC aAC) {
        super(aAC);
        this.maxHit = 15;
        this.maxSwing = 19;
        this.consistentThreshold = 25L;
        this.consistentCheck = true;
        this.attackHistories = Collections.synchronizedMap(new WeakHashMap<>());
        this.swingHistories = Collections.synchronizedMap(new WeakHashMap<>());
        this.maxHit = this.plugin.getConfig().getInt("fightspeed.max_hit", this.maxHit);
        this.maxSwing = this.plugin.getConfig().getInt("fightspeed.max_swing", this.maxSwing);
        this.consistentCheck = this.plugin.getConfig().getBoolean("fightspeed.consistent_check", this.consistentCheck);
        this.consistentThreshold = this.plugin.getConfig().getLong("fightspeed.consistent_threshold", this.consistentThreshold);
        FightSpeedPacketListener fightSpeedPacketListener = new FightSpeedPacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.ARM_ANIMATION});
        this.packetListeners.add(fightSpeedPacketListener);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)fightSpeedPacketListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onTick(long ticks, long ticks2) {
        long ticks3 = ticks2;
        long ticks4 = ticks3 ^ 0L;
        long ticks5 = ticks3 ^ 0x4DEAE20E7407L;
        super.onTick(ticks, ticks4);
        if (ticks % 20L == 0L) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                OptionalLong optionalLong;
                OptionalLong optionalLong2;
                ClickIntervalHistory clickIntervalHistory = this.attackHistories.put(player, new ClickIntervalHistory(this, null));
                ClickIntervalHistory clickIntervalHistory2 = this.swingHistories.put(player, new ClickIntervalHistory(this, null));
                if (clickIntervalHistory != null) {
                    if (ClickIntervalHistory.getIntervals(clickIntervalHistory).size() > this.maxHit) {
                        this.recordViolation(player.getUniqueId(), ClickIntervalHistory.getIntervals(clickIntervalHistory).size() - this.maxHit + 2, HackType.FIGHTSPEED, player.getName() + " failed FightSpeed: hit target " + ClickIntervalHistory.getIntervals(clickIntervalHistory).size() + " times in one second", ticks5);
                    } else {
                        this.decreaseViolationLevel(player.getUniqueId(), 1);
                    }
                }
                if (clickIntervalHistory2 != null) {
                    if (ClickIntervalHistory.getIntervals(clickIntervalHistory2).size() > this.maxSwing) {
                        this.recordViolation(player.getUniqueId(), ClickIntervalHistory.getIntervals(clickIntervalHistory2).size() - this.maxHit, HackType.FIGHTSPEED, player.getName() + " failed FightSpeed: clicking with a CPS of " + ClickIntervalHistory.getIntervals(clickIntervalHistory2).size(), ticks5);
                    } else {
                        this.decreaseViolationLevel(player.getUniqueId(), 1);
                    }
                }
                if (!this.consistentCheck || clickIntervalHistory == null) continue;
                List<Long> list = ClickIntervalHistory.getIntervals(clickIntervalHistory);
                synchronized (list) {
                    optionalLong2 = ClickIntervalHistory.getIntervals(clickIntervalHistory).stream().mapToLong(Long::longValue).min();
                    optionalLong = ClickIntervalHistory.getIntervals(clickIntervalHistory).stream().mapToLong(Long::longValue).max();
                }
                if (!optionalLong2.isPresent()) continue;
                long ticks6 = optionalLong.getAsLong() - optionalLong2.getAsLong();
                if (ClickIntervalHistory.getIntervals(clickIntervalHistory).size() <= 5 || ticks6 >= this.consistentThreshold) continue;
                this.recordViolation(player.getUniqueId(), ClickIntervalHistory.getIntervals(clickIntervalHistory).size() - 4, HackType.FIGHTSPEED, player.getName() + " failed FightSpeed: attacking consistently at a rate of " + ClickIntervalHistory.getIntervals(clickIntervalHistory).size() + "/sec", ticks5);
            }
        }
    }

    static Map getAttackHistories(FightSpeed fightSpeed) {
        return fightSpeed.attackHistories;
    }

    static Map getSwingHistories(FightSpeed fightSpeed) {
        return fightSpeed.swingHistories;
    }
}

