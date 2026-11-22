package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.compat.UseEntityPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraTimingModule
extends KillauraModule {
    private final ExpiringMap lastAttackTimestamps;

    public KillauraTimingModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 3L;
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        this.lastAttackTimestamps = ExpiringMap.builder().expiration(ticks, timeUnit).expirationPolicy(expirationPolicy).build();
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
        if (!packetEvent.isServerPacket()) {
            if (packetEvent.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                PacketContainer packetContainer = packetEvent.getPacket();
                UseEntityPacket useEntityPacket = new UseEntityPacket(packetContainer);
                if (useEntityPacket.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                    if (this.lastAttackTimestamps.containsKey(packetEvent.getPlayer().getUniqueId())) {
                        this.addPlayerScore(packetEvent.getPlayer(), 1.0);
                    } else {
                        this.setPlayerScore(packetEvent.getPlayer(), this.getPlayerScore(packetEvent.getPlayer()) * 0.95);
                    }
                }
            } else if (this.isMovementPacket(packetEvent.getPacketType())) {
                this.lastAttackTimestamps.put(packetEvent.getPlayer().getUniqueId(), (Object)false);
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        this.setPlayerScore(playerMoveEvent.getPlayer(), this.getPlayerScore(playerMoveEvent.getPlayer()) * 0.99);
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (this.getPlayerScore((Player)entityDamageByEntityEvent.getDamager()) > 5.0) {
            this.reportViolation((Player)entityDamageByEntityEvent.getDamager(), "timing", "unusual attack patterns");
            this.setPlayerScore((Player)entityDamageByEntityEvent.getDamager(), this.getPlayerScore((Player)entityDamageByEntityEvent.getDamager()) * 0.2);
        }
    }
}

