package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraClickModule
extends KillauraModule {
    private final Map<Player, Long> lastMovePacketTimes = new WeakHashMap<>();
    private final Set<Player> fastClickPlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public KillauraClickModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketType.Play.Client.ARM_ANIMATION) {
            if (System.currentTimeMillis() - this.lastMovePacketTimes.getOrDefault(packetEvent.getPlayer(), Long.MAX_VALUE) < 40L) {
                if (this.fastClickPlayers.contains(packetEvent.getPlayer())) {
                    this.addPlayerScore(packetEvent.getPlayer(), 1.25);
                    if (this.getPlayerScore(packetEvent.getPlayer()) > 5.0) {
                        this.reportViolation(packetEvent.getPlayer(), "click", "attacking too quickly");
                    }
                } else {
                    this.fastClickPlayers.add(packetEvent.getPlayer());
                }
                this.setPlayerScore(packetEvent.getPlayer(), this.getPlayerScore(packetEvent.getPlayer()) * 0.9);
            }
        } else if (this.isMovementPacket(packetEvent.getPacketType())) {
            this.fastClickPlayers.remove(packetEvent.getPlayer());
            this.lastMovePacketTimes.put(packetEvent.getPlayer(), System.currentTimeMillis());
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    }
}

