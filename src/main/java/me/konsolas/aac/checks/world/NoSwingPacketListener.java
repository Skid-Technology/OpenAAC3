package me.konsolas.aac.checks.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.compat.PlayerPingAccess;
import me.konsolas.aac.compat.UseEntityPacket;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NoSwingPacketListener
extends PacketAdapter {
    final NoSwing detector;

    public NoSwingPacketListener(NoSwing noSwing, Plugin plugin, PacketType[] packetTypeArray) {        super(plugin, packetTypeArray);

        this.detector = noSwing;
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        PacketContainer packetContainer;
        Player player = packetEvent.getPlayer();
        if (((AAC)this.plugin).checkManager.isPlayerExempt(player)) {
            return;
        }
        if (packetEvent.getPacketType() == UseEntityPacket.TYPE && new UseEntityPacket(packetContainer = packetEvent.getPacket()).getAction() != EnumWrappers.EntityUseAction.ATTACK) {
            return;
        }
        long now = System.currentTimeMillis();
        Player player2 = packetEvent.getPlayer();
        AAC aAC = (AAC)this.plugin;
        Server server = Bukkit.getServer();
        server.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.verifyAttackSwing(aAC, player2, now), 5L);
    }

    private void verifyAttackSwing(AAC aAC, Player player, long ticks) {
        long ticks2 = 113218805911140L;
        long ticks3 = 103276215108508L;
        if (aAC.logManager.isDebugEnabled()) {
            aAC.logManager.logPlayerDebug(player, "(NoSwing) PlayClientUseEntity lastAnimated[e:" + ticks + ",r:" + aAC.playerState.getLastArmSwingTime(player) + "]");
        }
        try {
            long ticks4 = ticks3;
            Player player2 = player;
            double d = 300 + PlayerPingAccess.getPing(player2, ticks4);
            double d2 = aAC.playerState.getLastArmSwingTime(player);
            double d3 = ticks;
            if (!MathUtilities.approximatelyEquals(d3, d2, d)) {
                this.detector.recordViolation(player.getUniqueId(), 1, HackType.NOSWING, player.getName() + " failed NoSwing: didn't swing arm (attack)", ticks2);
            } else {
                this.detector.decreaseViolationLevel(player.getUniqueId(), 2);
            }
        }
        catch (NullPointerException nullPointerException) {}
    }
}

