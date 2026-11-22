package me.konsolas.aac.checks.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import org.bukkit.plugin.Plugin;

public final class Derp
extends Detector {
    public Derp(AAC aAC) {
        super(aAC);
        HeadRollPacketListener headRollPacketListener = new HeadRollPacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION_LOOK});
        this.packetListeners.add(headRollPacketListener);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)headRollPacketListener);
    }
}

