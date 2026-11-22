package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.events.PacketEvent;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.MathUtilities;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraStableModule
extends KillauraModule {
    public KillauraStableModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        double yaw = playerMoveEvent.getTo().getYaw();
        double yaw2 = playerMoveEvent.getFrom().getYaw();
        double d = yaw;
        double d2 = yaw2;
        double d3 = MathUtilities.angleDifferenceAbs(d2, d);
        if (playerMoveEvent.getFrom().getPitch() == playerMoveEvent.getTo().getPitch()) {
            double d4;
            double yaw3 = playerMoveEvent.getTo().getYaw();
            double yaw4 = playerMoveEvent.getFrom().getYaw();
            double d5 = yaw4;
            if (MathUtilities.angleDifferenceAbs(d5, d4 = yaw3) > 3.0 && playerMoveEvent.getFrom().getPitch() != 90.0f && playerMoveEvent.getFrom().getPitch() != -90.0f) {
                this.addPlayerScore(playerMoveEvent.getPlayer(), 0.9);
                if (!(this.getPlayerScore(playerMoveEvent.getPlayer()) > 4.0)) return;
                this.reportViolation(playerMoveEvent.getPlayer(), "stable", "ununsually stable aiming (" + playerMoveEvent.getTo().getPitch() + "/" + d3 + ")");
                this.setPlayerScore(playerMoveEvent.getPlayer(), this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.4);
                return;
            }
        }
        this.setPlayerScore(playerMoveEvent.getPlayer(), this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.65);
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    }
}

