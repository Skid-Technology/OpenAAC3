package me.konsolas.aac.checks.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.compat.PlayerPingAccess;
import me.konsolas.aac.compat.UseEntityPacket;
import me.konsolas.aac.api.HackType;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

public final class NoSwing
extends Detector {
    public NoSwing(AAC aAC) {        super(aAC);

        NoSwingPacketListener noSwingPacketListener = new NoSwingPacketListener(this, (Plugin)this.plugin, new PacketType[]{UseEntityPacket.TYPE});
        this.packetListeners.add(noSwingPacketListener);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)noSwingPacketListener);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player;
        if (blockBreakEvent.getPlayer().getGameMode() == GameMode.CREATIVE || this.plugin.checkManager.isPlayerExempt(player = blockBreakEvent.getPlayer())) {
            return;
        }
        long now = System.currentTimeMillis();
        Player player2 = blockBreakEvent.getPlayer();
        AAC aAC = this.plugin;
        Server server = this.plugin.getServer();
        server.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.verifyBlockBreakSwing(aAC, player2, now), 5L);
    }

    private void verifyBlockBreakSwing(AAC aAC, Player player, long ticks) {
        long ticks2 = 113218805911140L;
        long ticks3 = 103276215108508L;
        if (aAC.logManager.isDebugEnabled()) {
            aAC.logManager.logPlayerDebug(player, "(NoSwing) BlockBreakEvent lastAnimated[e:" + ticks + ",r:" + aAC.playerState.getLastArmSwingTime(player) + "]");
        }
        try {
            long ticks4 = ticks3;
            Player player2 = player;
            double d = 299 + PlayerPingAccess.getPing(player2, ticks4);
            double d2 = aAC.playerState.getLastArmSwingTime(player);
            double d3 = ticks;
            if (!MathUtilities.approximatelyEquals(d3, d2, d)) {
                this.recordViolation(player.getUniqueId(), 1, HackType.NOSWING, player.getName() + " failed NoSwing: didn't swing arm (block break)", ticks2);
            } else {
                this.decreaseViolationLevel(player.getUniqueId(), 2);
            }
        }
        catch (NullPointerException nullPointerException) {}
    }
}

