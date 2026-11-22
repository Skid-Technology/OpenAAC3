package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraRoundingModule
extends KillauraModule {
    private final Map lastRoundedYaws = new HashMap();

    public KillauraRoundingModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        double yaw = Math.abs(playerMoveEvent.getTo().getYaw() - playerMoveEvent.getFrom().getYaw()) % 360.0f;
        if (yaw > 1.0 && (double)Math.round(yaw) == yaw) {
            if (this.lastRoundedYaws.containsKey(playerMoveEvent.getPlayer()) && (double)((Float)this.lastRoundedYaws.get(playerMoveEvent.getPlayer())).floatValue() == yaw) {
                this.reportViolation(playerMoveEvent.getPlayer(), "round", "rounded aiming values (y=" + yaw + ")");
            }
        } else {
            this.lastRoundedYaws.remove(playerMoveEvent.getPlayer());
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    }
}

