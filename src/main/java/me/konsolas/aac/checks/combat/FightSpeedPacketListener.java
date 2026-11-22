package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import java.util.Optional;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.UseEntityPacket;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FightSpeedPacketListener
extends PacketAdapter {
    final FightSpeed detector;

    public FightSpeedPacketListener(FightSpeed fightSpeed, Plugin plugin, PacketType[] packetTypeArray) {
        super(plugin, packetTypeArray);
        this.detector = fightSpeed;
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        Player player = packetEvent.getPlayer();
        if (!((AAC)this.plugin).checkManager.isPlayerExempt(player)) {
            PacketContainer packetContainer;
            if (packetEvent.getPacketType() == PacketType.Play.Client.USE_ENTITY && new UseEntityPacket(packetContainer = packetEvent.getPacket()).getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                Optional.ofNullable(FightSpeed.getAttackHistories(this.detector).get(packetEvent.getPlayer())).ifPresent(FightSpeedPacketListener::recordSwingInterval);
            } else if (((AAC)this.plugin).combatTracker.hasRecentCombat(packetEvent.getPlayer())) {
                Optional.ofNullable(FightSpeed.getSwingHistories(this.detector).get(packetEvent.getPlayer())).ifPresent(FightSpeedPacketListener::recordClickSample);
            }
        }
    }

    private static void recordClickSample(Object obj) {
        ClickIntervalHistory.recordClick((ClickIntervalHistory)obj);
    }

    private static void recordSwingInterval(Object obj) {
        ClickIntervalHistory.recordClick((ClickIntervalHistory)obj);
    }
}

