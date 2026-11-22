package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.util.EntityDimensions;
import me.konsolas.aac.util.EntityTypeChecks;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class KillauraMissModule
extends KillauraModule {
    private final ExpiringMap<UUID, Boolean> pendingMissChecks;
    private final ExpiringMap<UUID, Boolean> attackedThisTick;
    private final ExpiringMap<UUID, Entity> recentTargets;

    public KillauraMissModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 1000L;
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        this.recentTargets = ExpiringMap.builder().expiration(ticks, timeUnit).expirationPolicy(expirationPolicy).build();
        TimeUnit timeUnit2 = TimeUnit.MILLISECONDS;
        long ticks2 = 15L;
        ExpirationPolicy expirationPolicy2 = ExpirationPolicy.CREATED;
        ExpirationListener<UUID, Boolean> expirationListener = this::scheduleMissCheck;
        this.pendingMissChecks = ExpiringMap.builder().expiration(ticks2, timeUnit2).expirationPolicy(expirationPolicy2).expirationListener(expirationListener).build();
        TimeUnit timeUnit3 = TimeUnit.MILLISECONDS;
        long ticks3 = 15L;
        ExpirationPolicy expirationPolicy3 = ExpirationPolicy.CREATED;
        this.attackedThisTick = ExpiringMap.builder().expiration(ticks3, timeUnit3).expirationPolicy(expirationPolicy3).build();
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPacket(PacketEvent packetEvent) {
        if (!packetEvent.isServerPacket()) {
            if (packetEvent.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                if (packetEvent.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
                    int i;
                    this.pendingMissChecks.remove(packetEvent.getPlayer().getUniqueId());
                    this.attackedThisTick.put(packetEvent.getPlayer().getUniqueId(), false);
                    try {
                        i = ((Entity)this.recentTargets.get(packetEvent.getPlayer().getUniqueId())).getEntityId();
                    }
                    catch (NullPointerException nullPointerException) {
                        return;
                    }
                    if (i == (Integer)packetEvent.getPacket().getIntegers().read(0)) {
                        this.recentTargets.resetExpiration(packetEvent.getPlayer().getUniqueId());
                    }
                }
            } else if (packetEvent.getPacketType() == PacketType.Play.Client.ARM_ANIMATION && !this.pendingMissChecks.containsKey(packetEvent.getPlayer().getUniqueId()) && !this.attackedThisTick.containsKey(packetEvent.getPlayer().getUniqueId())) {
                Vector vector = packetEvent.getPlayer().getEyeLocation().toVector();
                Vector vector2 = packetEvent.getPlayer().getLocation().getDirection();
                boolean flag = false;
                Entity entity = (Entity)this.recentTargets.get(packetEvent.getPlayer().getUniqueId());
                if (entity != null && EntityTypeChecks.isCommonCombatTarget(entity.getType())) {
                    double d;
                    try {
                        d = packetEvent.getPlayer().getEyeLocation().distance(entity.getLocation());
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                        return;
                    }
                    if (d < 1.5 || d > 3.5) {
                        return;
                    }
                    Entity entity2 = entity;
                    AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(entity.getLocation().toVector(), EntityDimensions.forEntity(entity2)).expand(new Vector(-0.05, -0.2, -0.05));
                    if (axisAlignedBoundingBox.intersectsRay(vector, vector2)) {
                        flag = true;
                    }
                    this.pendingMissChecks.put(packetEvent.getPlayer().getUniqueId(), flag);
                }
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        this.recentTargets.put(entityDamageByEntityEvent.getDamager().getUniqueId(), entityDamageByEntityEvent.getEntity());
    }

    private void scheduleMissCheck(UUID uUID, Boolean boolValue) {
        Server server = Bukkit.getServer();
        server.getScheduler().scheduleSyncDelayedTask((Plugin)this.getPlugin(), () -> this.verifyMissedSwing(uUID, boolValue));
    }

    private void verifyMissedSwing(UUID uUID, Boolean boolValue) {
        Player player = Bukkit.getPlayer((UUID)uUID);
        try {
            Block block = player.getTargetBlock((Set)null, 5);
            if (block != null && block.getType() != Material.AIR) {
                return;
            }
        }
        catch (Exception exception) {
        }
        if (player != null && boolValue.booleanValue() && this.recentTargets.containsKey(uUID)) {
            this.reportViolation(player, "miss", "attack packets not correlating with swings");
        }
    }
}

