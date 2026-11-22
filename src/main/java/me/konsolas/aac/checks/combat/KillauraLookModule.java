package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraLookModule
extends KillauraModule {
    private final Set lookPlayers = new HashSet();
    private final Set positionLookPlayers = new HashSet();
    private final Set attackingPlayers = new HashSet();

    public KillauraLookModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
        Iterator iterator = Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            if (this.lookPlayers.contains(player) && this.positionLookPlayers.contains(player) && this.attackingPlayers.contains(player)) {
                this.addPlayerScore(player, 1.0);
                if (!(this.getPlayerScore(player) > 5.0)) continue;
                this.reportViolation(player, "look", "unexpected flying packet without position");
                this.setPlayerScore(player, this.getPlayerScore(player) * 0.6);
                continue;
            }
            this.setPlayerScore(player, this.getPlayerScore(player) * 0.9);
        }
        this.lookPlayers.clear();
        this.positionLookPlayers.clear();
        this.attackingPlayers.clear();
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketType.Play.Client.POSITION_LOOK) {
            this.positionLookPlayers.add(packetEvent.getPlayer());
        } else if (packetEvent.getPacketType() == PacketType.Play.Client.LOOK) {
            this.lookPlayers.add(packetEvent.getPlayer());
        } else if (this.isAttackPacket(packetEvent.getPacket())) {
            this.attackingPlayers.add(packetEvent.getPlayer());
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    }
}

