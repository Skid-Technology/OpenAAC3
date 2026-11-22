package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.UseEntityPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class KillauraModule {
    private final AAC plugin;
    private final Consumer<KillauraViolation> violationConsumer;
    private final WeakHashMap<Player, Double> playerScores = new WeakHashMap<>();

    public KillauraModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        this.plugin = aAC;
        this.violationConsumer = consumer;
    }

    public AAC getPlugin() {
        return this.plugin;
    }

    protected boolean isAttackPacket(PacketContainer packetContainer) {
        if (packetContainer.getType() != PacketType.Play.Client.USE_ENTITY) {
            return false;
        }
        PacketContainer packetContainer2 = packetContainer;
        UseEntityPacket useEntityPacket = new UseEntityPacket(packetContainer2);
        return useEntityPacket.getAction() == EnumWrappers.EntityUseAction.ATTACK;
    }

    protected boolean isMovementPacket(PacketType packetType) {
        return packetType == PacketType.Play.Client.FLYING || packetType == PacketType.Play.Client.POSITION || packetType == PacketType.Play.Client.POSITION_LOOK || packetType == PacketType.Play.Client.LOOK;
    }

    protected void reportViolation(Player player, String str, String str2) {
        this.violationConsumer.accept(new KillauraViolation(this, player, str, str2, null));
    }

    protected void setPlayerScore(Player player, double d) {
        this.playerScores.put(player, d);
    }

    protected double getPlayerScore(Player player) {
        return this.playerScores.getOrDefault(player, 0.0);
    }

    protected void addPlayerScore(Player player, double d) {
        this.playerScores.put(player, this.getPlayerScore(player) + d);
    }

    public abstract void onTick();

    public abstract void onPacket(PacketEvent event);

    public abstract void onPlayerMove(PlayerMoveEvent event);

    public abstract void onEntityDamageByEntity(EntityDamageByEntityEvent event);

}

