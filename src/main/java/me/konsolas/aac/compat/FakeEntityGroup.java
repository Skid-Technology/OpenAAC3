package me.konsolas.aac.compat;

import com.comphenix.protocol.events.PacketContainer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import me.konsolas.aac.checks.combat.KillauraNormalModule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class FakeEntityGroup {
    private final Map<FakeEntityOffset, Integer> entityIds;
    private final long createdAt;
    private long lastAttackAt;
    final KillauraNormalModule module;

    private FakeEntityGroup(KillauraNormalModule killauraNormalModule) {
        this.module = killauraNormalModule;
        this.entityIds = new EnumMap<>(FakeEntityOffset.class);
        this.createdAt = System.currentTimeMillis();
        this.markAttacked();
    }

    private void markAttacked() {
        this.lastAttackAt = System.currentTimeMillis();
    }

    private boolean isSpawnSettled() {
        return System.currentTimeMillis() - this.createdAt > 400L;
    }

    private boolean isAttackTimedOut() {
        return System.currentTimeMillis() - this.lastAttackAt > 2000L;
    }

    private Optional<FakeEntityOffset> findOffsetForId(int i) {
        Iterator<Map.Entry<FakeEntityOffset, Integer>> iterator = this.entityIds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<FakeEntityOffset, Integer> entry = iterator.next();
            if ((Integer)entry.getValue() != i) continue;
            return Optional.of(entry.getKey());
        }
        return Optional.empty();
    }

    private Collection<PacketContainer> createSpawnPackets(Entity entity) {
        ArrayList<PacketContainer> arrayList = new ArrayList<PacketContainer>();
        FakeEntityOffset[] fakeEntityOffsetArray = FakeEntityOffset.values();
        for (FakeEntityOffset fakeEntityOffset : fakeEntityOffsetArray) {
            int i;
            int value = i = FakeEntityIdGenerator.nextEntityId(entity.getWorld());
            Entity entity2 = entity;
            PacketContainer packetContainer = this.createSpawnPacket(entity2, value);
            this.entityIds.put(fakeEntityOffset, i);
            arrayList.add(packetContainer);
        }
        return arrayList;
    }

    private PacketContainer createSpawnPacket(Entity entity, int i) {
        SpawnLivingEntityPacket spawnLivingEntityPacket = new SpawnLivingEntityPacket();
        spawnLivingEntityPacket.setEntityId(i);
        spawnLivingEntityPacket.setX(entity.getLocation().getX());
        spawnLivingEntityPacket.setY(entity.getLocation().getY());
        spawnLivingEntityPacket.setZ(entity.getLocation().getZ());
        spawnLivingEntityPacket.setType(EntityType.ZOMBIE);
        World world = entity.getWorld();
        EntityType entityType = entity.getType();
        spawnLivingEntityPacket.setMetadata(InvisibleEntityMetadata.getInvisibleMetadata(entityType, world));
        return spawnLivingEntityPacket.getHandle();
    }

    private Collection<PacketContainer> createTeleportPackets(Location location) {
        ArrayList<PacketContainer> arrayList = new ArrayList<PacketContainer>();
        for (Map.Entry<FakeEntityOffset, Integer> entry : this.entityIds.entrySet()) {
            EntityTeleportPacket entityTeleportPacket = new EntityTeleportPacket();
            Location location2 = ((FakeEntityOffset)((Object)entry.getKey())).offset(0.0625, location);
            entityTeleportPacket.setPitch(location2.getPitch());
            entityTeleportPacket.setYaw(location2.getYaw());
            entityTeleportPacket.setX(location2.getX());
            entityTeleportPacket.setY(location2.getY() + 0.1);
            entityTeleportPacket.setZ(location2.getZ());
            entityTeleportPacket.setEntityId((Integer)entry.getValue());
            arrayList.add(entityTeleportPacket.getHandle());
        }
        return arrayList;
    }

    private Collection<PacketContainer> createDestroyPackets() {
        ArrayList<PacketContainer> arrayList = new ArrayList<PacketContainer>();
        EntityDestroyPacket entityDestroyPacket = new EntityDestroyPacket();
        entityDestroyPacket.setEntityIds(this.entityIds.values().stream().mapToInt(FakeEntityGroup::unboxEntityId).toArray());
        arrayList.add(entityDestroyPacket.getHandle());
        return arrayList;
    }

    private Collection<PacketContainer> clonePacketsForEntities(PacketContainer packetContainer) {
        ArrayList<PacketContainer> arrayList = new ArrayList<PacketContainer>();
        for (Integer intVal : this.entityIds.values()) {
            PacketContainer packetContainer2 = packetContainer.shallowClone();
            packetContainer2.getIntegers().write(0, intVal);
            arrayList.add(packetContainer2);
        }
        return arrayList;
    }

    private static int unboxEntityId(Integer intVal) {
        return intVal;
    }

    public static Collection<PacketContainer> getDestroyPackets(FakeEntityGroup fakeEntityGroup) {
        return fakeEntityGroup.createDestroyPackets();
    }

    public static boolean isExpired(FakeEntityGroup fakeEntityGroup) {
        return fakeEntityGroup.isAttackTimedOut();
    }

    public static Collection<PacketContainer> getTeleportPackets(FakeEntityGroup fakeEntityGroup, Location location) {
        return fakeEntityGroup.createTeleportPackets(location);
    }

    public static Collection<PacketContainer> clonePackets(FakeEntityGroup fakeEntityGroup, PacketContainer packetContainer) {
        return fakeEntityGroup.clonePacketsForEntities(packetContainer);
    }

    public static boolean isSettled(FakeEntityGroup fakeEntityGroup) {
        return fakeEntityGroup.isSpawnSettled();
    }

    public static Optional<FakeEntityOffset> getOffsetForEntityId(FakeEntityGroup fakeEntityGroup, int i) {
        return fakeEntityGroup.findOffsetForId(i);
    }

    public static void updateLastAttack(FakeEntityGroup fakeEntityGroup) {
        fakeEntityGroup.markAttacked();
    }

    public FakeEntityGroup(KillauraNormalModule killauraNormalModule, FakeEntityAccess fakeEntityAccess) {
        this(killauraNormalModule);
    }

    public static Collection<PacketContainer> getSpawnPackets(FakeEntityGroup fakeEntityGroup, Entity entity) {
        return fakeEntityGroup.createSpawnPackets(entity);
    }
}

