package me.konsolas.aac.checks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.PlayerPingAccess;
import me.konsolas.aac.util.TickRateMonitor;
import me.konsolas.aac.api.HackType;
import me.konsolas.aac.api.PlayerViolationEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class Detector
implements Listener,
CleanupHandler {
    protected final AAC plugin;
    protected final Map<UUID, Integer> violationLevels = new HashMap<>();
    protected final List<Integer> scheduledTaskIds = new ArrayList<>();
    protected final List<PacketListener> packetListeners = new ArrayList<>();

    public Detector(AAC aAC) {
        this.initializeOnlinePlayers();
        this.plugin = aAC;
    }

    public AAC getPlugin() {
        return this.plugin;
    }

    public int getViolationLevel(Player player) {
        if (player == null) {
            return 0;
        }
        if (this.violationLevels.get(player.getUniqueId()) != null) {
            return (Integer)this.violationLevels.get(player.getUniqueId());
        }
        return 0;
    }

    private void initializeOnlinePlayers() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.violationLevels.put(player.getUniqueId(), 0);
        }
    }

    @Override
    public void cleanup() {
        Iterator iterator = this.scheduledTaskIds.iterator();
        while (iterator.hasNext()) {
            int value = (Integer)iterator.next();
            Server server = Bukkit.getServer();
            server.getScheduler().cancelTask(value);
        }
        for (PacketListener packetListener : this.packetListeners) {
            ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
        }
    }

    public boolean recordViolation(UUID uUID, int i, HackType hackType, String str, long ticks) {
        long ticks2 = ticks ^ 0x3B1510A8CDF8L;
        Player player = Bukkit.getPlayer((UUID)uUID);
        PlayerViolationEvent playerViolationEvent = new PlayerViolationEvent(player, hackType, i, str);
        if (this.getViolationLevel(player) + i == 0) {
            if (this.plugin.logManager.isDebugEnabled()) {
                long ticks3 = ticks2;
                Player player2 = player;
                this.plugin.logManager.logPlayerDebug(player, "(VL0)" + str + " [ping:" + PlayerPingAccess.getPing(player2, ticks3) + " tps:" + (double)Math.round(TickRateMonitor.getTps() * 100.0) / 100.0 + "] VL:" + this.violationLevels.get(uUID));
            }
            return true;
        }
        Bukkit.getServer().getPluginManager().callEvent(playerViolationEvent);
        if (!playerViolationEvent.isCancelled()) {
            if (!this.violationLevels.containsKey(uUID)) {
                this.violationLevels.put(uUID, 1);
            }
            this.violationLevels.put(uUID, (Integer)this.violationLevels.get(uUID) + playerViolationEvent.getViolations());
            if (playerViolationEvent.getViolations() != i) {
                String str2 = "(PlayerViolationEvent) modified " + i + " -> " + playerViolationEvent.getViolations() + " by other plugin";
                this.plugin.logManager.logViolation(str2);
            }
            long ticks4 = ticks2;
            Player player3 = player;
            String str3 = str + " [ping:" + PlayerPingAccess.getPing(player3, ticks4) + " tps:" + (double)Math.round(TickRateMonitor.getTps() * 100.0) / 100.0 + "] VL:" + this.violationLevels.get(uUID);
            this.plugin.logManager.logViolation(str3);
            return true;
        }
        return false;
    }

    public void decreaseViolationLevel(UUID uUID, int i) {
        if (this.violationLevels.containsKey(uUID)) {
            this.violationLevels.put(uUID, (Integer)this.violationLevels.get(uUID) - i < 0 ? 0 : (Integer)this.violationLevels.get(uUID) - i);
        } else {
            this.violationLevels.put(uUID, 0);
        }
    }

    public void setViolationLevel(Player player, int i) {
        this.violationLevels.put(player.getUniqueId(), i);
    }

    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        this.violationLevels.put(playerJoinEvent.getPlayer().getUniqueId(), 0);
    }

    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        this.violationLevels.remove(playerQuitEvent.getPlayer().getUniqueId());
    }

    public void onPlayerKick(PlayerKickEvent playerKickEvent) {
        this.violationLevels.remove(playerKickEvent.getPlayer().getUniqueId());
    }

    public void onTick(long ticks, long ticks2) {
    }

}

