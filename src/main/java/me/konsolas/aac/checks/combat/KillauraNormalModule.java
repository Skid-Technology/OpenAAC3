package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.compat.EntityDestroyPacket;
import me.konsolas.aac.compat.EntityTeleportPacket;
import me.konsolas.aac.compat.FakeEntityGroup;
import me.konsolas.aac.compat.UseEntityPacket;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KillauraNormalModule
extends KillauraModule {
    private final Map<Player, Map<Integer, FakeEntityGroup>> playerBotGroups = Collections.synchronizedMap(new WeakHashMap<>());

    public KillauraNormalModule(AAC aAC, Consumer<KillauraViolation> consumer) {
        super(aAC, consumer);
    }

    @Override
    public void onTick() {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.isServerPacket()) {
            this.playerBotGroups.putIfAbsent(packetEvent.getPlayer(), new ConcurrentHashMap());
            try {
                Player player = packetEvent.getPlayer();
                if (this.getPlugin().checkManager.isPlayerExempt(player)) return;
                if (packetEvent.getPacketType() == PacketType.Play.Server.ENTITY_DESTROY) {
                    PacketContainer packetContainer = packetEvent.getPacket();
                    EntityDestroyPacket entityDestroyPacket = new EntityDestroyPacket(packetContainer);
                    for (int i : entityDestroyPacket.getEntityIds()) {
                        if (!((Map)this.playerBotGroups.get(packetEvent.getPlayer())).containsKey(i)) continue;
                        for (PacketContainer packetContainer2 : FakeEntityGroup.getDestroyPackets((FakeEntityGroup)((Map)this.playerBotGroups.get(packetEvent.getPlayer())).get(i))) {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(packetEvent.getPlayer(), packetContainer2, false);
                        }
                        ((Map)this.playerBotGroups.get(packetEvent.getPlayer())).remove(i);
                    }
                    return;
                }
                if (packetEvent.getPacketType() != PacketType.Play.Server.ENTITY_TELEPORT && packetEvent.getPacketType() != PacketType.Play.Server.REL_ENTITY_MOVE && packetEvent.getPacketType() != PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) return;
                int value = (Integer)packetEvent.getPacket().getIntegers().read(0);
                if (!((Map)this.playerBotGroups.get(packetEvent.getPlayer())).containsKey(value)) {
                    return;
                }
                FakeEntityGroup fakeEntityGroup = (FakeEntityGroup)((Map)this.playerBotGroups.get(packetEvent.getPlayer())).get(value);
                if (FakeEntityGroup.isExpired(fakeEntityGroup)) {
                    for (PacketContainer packetContainer : FakeEntityGroup.getDestroyPackets((FakeEntityGroup)((Map)this.playerBotGroups.get(packetEvent.getPlayer())).get(value))) {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(packetEvent.getPlayer(), packetContainer, false);
                    }
                    ((Map)this.playerBotGroups.get(packetEvent.getPlayer())).remove(value);
                    return;
                }
                if (packetEvent.getPacketType().equals((Object)PacketType.Play.Server.ENTITY_TELEPORT)) {
                    PacketContainer packetContainer = packetEvent.getPacket();
                    EntityTeleportPacket entityTeleportPacket = new EntityTeleportPacket(packetContainer);
                    Location location = new Location(packetEvent.getPlayer().getWorld(), entityTeleportPacket.getX(), entityTeleportPacket.getY(), entityTeleportPacket.getZ(), entityTeleportPacket.getYaw(), entityTeleportPacket.getPitch());
                    for (PacketContainer packetContainer3 : FakeEntityGroup.getTeleportPackets((FakeEntityGroup)((Map)this.playerBotGroups.get(packetEvent.getPlayer())).get(value), location)) {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(packetEvent.getPlayer(), packetContainer3, false);
                    }
                    return;
                }
                for (PacketContainer packetContainer : FakeEntityGroup.clonePackets((FakeEntityGroup)((Map)this.playerBotGroups.get(packetEvent.getPlayer())).get(value), packetEvent.getPacket())) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(packetEvent.getPlayer(), packetContainer, false);
                }
                return;
            }
            catch (InvocationTargetException invocationTargetException) {
                throw new RuntimeException(invocationTargetException);
            }
            catch (NullPointerException nullPointerException) {
                this.getPlugin().getLogger().warning("Failed to manipulate killaura packets: " + packetEvent.getPacket());
                throw new RuntimeException(nullPointerException);
            }
        } else {
            PacketContainer packetContainer;
            UseEntityPacket useEntityPacket;
            this.playerBotGroups.putIfAbsent(packetEvent.getPlayer(), new ConcurrentHashMap());
            if (packetEvent.getPacketType() != PacketType.Play.Client.USE_ENTITY || (useEntityPacket = new UseEntityPacket(packetContainer = packetEvent.getPacket())).getAction() != EnumWrappers.EntityUseAction.ATTACK) return;
            if (((Map)this.playerBotGroups.get(packetEvent.getPlayer())).containsKey(useEntityPacket.getTargetEntityId()) && FakeEntityGroup.isSettled((FakeEntityGroup)((Map)this.playerBotGroups.get(packetEvent.getPlayer())).get(useEntityPacket.getTargetEntityId()))) {
                this.reportViolation(packetEvent.getPlayer(), "normal", "unexpected id in attack packet");
            }
            for (Map.Entry<Integer, FakeEntityGroup> entry : this.playerBotGroups.get(packetEvent.getPlayer()).entrySet()) {
                Optional optional = FakeEntityGroup.getOffsetForEntityId((FakeEntityGroup)entry.getValue(), useEntityPacket.getTargetEntityId());
                if (!optional.isPresent() || packetEvent.getPlayer().getEntityId() == ((Integer)entry.getKey()).intValue()) continue;
                useEntityPacket.setTargetEntityId((Integer)entry.getKey());
                FakeEntityGroup.updateLastAttack((FakeEntityGroup)entry.getValue());
                return;
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getEntity() instanceof Player) {
            Player player = (Player)entityDamageByEntityEvent.getDamager();
            LivingEntity livingEntity = (LivingEntity)entityDamageByEntityEvent.getEntity();
            this.playerBotGroups.putIfAbsent(player, new ConcurrentHashMap());
            if (livingEntity.getVehicle() != null || ((Map)this.playerBotGroups.get(player)).containsKey(livingEntity.getEntityId())) {
                return;
            }
            FakeEntityGroup fakeEntityGroup = new FakeEntityGroup(this, null);
            try {
                for (PacketContainer packetContainer : FakeEntityGroup.getSpawnPackets(fakeEntityGroup, (Entity)livingEntity)) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer, false);
                }
                for (PacketContainer packetContainer : FakeEntityGroup.getTeleportPackets(fakeEntityGroup, livingEntity.getLocation())) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer, false);
                }
                ((Map)this.playerBotGroups.get(player)).put(livingEntity.getEntityId(), fakeEntityGroup);
            }
            catch (InvocationTargetException invocationTargetException) {
                this.getPlugin().getLogger().warning("Failed to spawn killaura entities");
            }
        }
    }

    static {
        if (null != null) {
            // empty if block
        }
    }
}

