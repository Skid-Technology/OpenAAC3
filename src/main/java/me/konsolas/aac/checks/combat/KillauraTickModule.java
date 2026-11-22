package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraTickModule
extends KillauraModule {
    private final PlayerSampleData yawDeltaSamples = new PlayerSampleData(8);

    public KillauraTickModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
            long ticks = this.getPlugin().playerState.getLastArmSwingTime(packetEvent.getPlayer());
            long now = System.currentTimeMillis();
            float f = now - ticks;
            double d = f - 50.0f * AAC.serverVersion.getAttackCooldownTicks(packetEvent.getPlayer());
            if (d < 2500.0) {
                this.yawDeltaSamples.addSample(packetEvent.getPlayer(), Math.abs(d));
                if (this.yawDeltaSamples.hasFullSampleData(packetEvent.getPlayer())) {
                    double d2 = this.yawDeltaSamples.getMean(packetEvent.getPlayer());
                    if (d2 < (double)AAC.serverVersion.getAttackCooldownTicks(packetEvent.getPlayer()) * 1.5) {
                        this.addPlayerScore(packetEvent.getPlayer(), 1.0);
                        if (this.getPlayerScore(packetEvent.getPlayer()) > 5.0) {
                            this.reportViolation(packetEvent.getPlayer(), "tick", "attacking with cooldowns (e=" + d2 + ")");
                        }
                    } else {
                        this.setPlayerScore(packetEvent.getPlayer(), this.getPlayerScore(packetEvent.getPlayer()) * 0.8);
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    }
}

