package me.konsolas.aac.checks.combat;

import java.util.Iterator;
import java.util.Locale;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.AxisAlignedBoundingBox;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.EntityDimensions;
import me.konsolas.aac.compat.ServerVersion;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public final class Hitbox
extends Detector
implements CombatCheckListener {
    private final WeakHashMap previousTargets;
    private final WeakHashMap currentTargets;
    private final WeakHashMap<Entity, Long> lastViolationTimes;
    private double maxReach;
    private double lenience;
    private boolean cancel;
    private double moveCompensation;
    private int minVl;
    private int reduceDelay;
    static final boolean assertionsDisabled = !Hitbox.class.desiredAssertionStatus();

    public Hitbox(AAC aAC) {        super(aAC);

        this.previousTargets = new WeakHashMap();
        this.currentTargets = new WeakHashMap();
        this.lastViolationTimes = new WeakHashMap<>();
        this.maxReach = 3.5;
        this.lenience = 0.6;
        this.cancel = true;
        this.moveCompensation = 5.0;
        this.minVl = 1;
        this.reduceDelay = 60;
        this.lenience = this.plugin.getConfig().getDouble("hitbox.lenience", this.lenience);
        this.cancel = this.plugin.getConfig().getBoolean("hitbox.cancel", this.cancel);
        this.maxReach = this.plugin.getConfig().getDouble("hitbox.max_reach", this.maxReach);
        this.minVl = this.plugin.getConfig().getInt("hitbox.min_vl", this.minVl);
        this.moveCompensation = this.plugin.getConfig().getDouble("hitbox.move_compensation", this.moveCompensation);
        this.reduceDelay = this.plugin.getConfig().getInt("hitbox.reduce_delay", this.reduceDelay);
    }

    @Override
    public boolean shouldProcessCombat(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        return entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER && ((Player)entityDamageByEntityEvent.getDamager()).getGameMode() != GameMode.CREATIVE && entityDamageByEntityEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK;
    }

    @Override
    public void onCombat(EntityDamageByEntityEvent entityDamageByEntityEvent, long ticks) {
        double d;
        double d2;
        Vector vector;
        long ticks2 = ticks ^ 0x7B735CA5F5C9L;
        if (entityDamageByEntityEvent.getEntity().getType() == EntityType.SLIME || entityDamageByEntityEvent.getEntity().getType() == EntityType.MAGMA_CUBE) {
            return;
        }
        if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_12 && entityDamageByEntityEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            return;
        }
        if (!assertionsDisabled && !(entityDamageByEntityEvent.getDamager() instanceof Player)) {
            throw new AssertionError();
        }
        Location location = entityDamageByEntityEvent.getDamager().getLocation();
        Entity entity = entityDamageByEntityEvent.getEntity();
        EntityDimensions entityDimensions = EntityDimensions.forEntity(entity);
        Location location2 = entityDamageByEntityEvent.getDamager().getLocation();
        Location location3 = location.clone();
        Location location4 = location3.distanceSquared(location2) < 1.0 ? location3.add(0.0, ((Player)entityDamageByEntityEvent.getDamager()).getEyeHeight(), 0.0) : location2.add(0.0, ((Player)entityDamageByEntityEvent.getDamager()).getEyeHeight(), 0.0);
        Location location5 = entityDamageByEntityEvent.getEntity().getLocation();
        double d3 = 0.0;
        if (this.previousTargets.containsKey(entityDamageByEntityEvent.getDamager()) && (vector = (Vector)((HitboxTargetCache)this.previousTargets.get((Object)entityDamageByEntityEvent.getDamager())).targets.get(entityDamageByEntityEvent.getEntity().getEntityId())) != null) {
            d3 = Math.max(location5.distance(location) - vector.distance(location.toVector()), 0.0) * this.moveCompensation;
        }
        if ((d2 = Math.min(location4.distance(location5), location4.distance(location5.clone().add(0.0, entityDimensions.getHeight(), 0.0)))) > (d = this.maxReach + entityDimensions.getWidth() / 2.0 + d3) && this.recordViolation(entityDamageByEntityEvent.getDamager().getUniqueId(), 2, HackType.HITBOX, String.format(Locale.UK, entityDamageByEntityEvent.getDamager().getName() + " failed Hitbox: attacked an entity too far away (%.3f blocks, expected %.2f + %.2f + %.2f)", d2, this.maxReach, entityDimensions.getWidth() / 2.0, d3), ticks2)) {
            this.lastViolationTimes.put((Player)entityDamageByEntityEvent.getDamager(), System.currentTimeMillis());
            if (this.cancel && this.getViolationLevel((Player)entityDamageByEntityEvent.getDamager()) > this.minVl) {
                entityDamageByEntityEvent.setCancelled(true);
            }
            return;
        }
        Entity entity2 = entityDamageByEntityEvent.getEntity();
        AxisAlignedBoundingBox axisAlignedBoundingBox = new AxisAlignedBoundingBox(entityDamageByEntityEvent.getEntity().getLocation().toVector(), EntityDimensions.forEntity(entity2)).expand(new Vector(this.lenience, this.lenience, this.lenience));
        if (entityDamageByEntityEvent.getDamager().getVehicle() != null) {
            axisAlignedBoundingBox.expand(new Vector(0.3, 0.3, 0.3));
        }
        Vector vector2 = location4.getDirection();
        boolean flag = axisAlignedBoundingBox.intersectsRay(location4.toVector(), vector2);
        if (!flag && this.recordViolation(entityDamageByEntityEvent.getDamager().getUniqueId(), 1, HackType.HITBOX, String.format(Locale.UK, entityDamageByEntityEvent.getDamager().getName() + " failed Hitbox: attacked an entity out of their line of sight (reach: %.3f blocks)", d2), ticks2)) {
            this.lastViolationTimes.put((Player)entityDamageByEntityEvent.getDamager(), System.currentTimeMillis());
            if (this.cancel && this.getViolationLevel((Player)entityDamageByEntityEvent.getDamager()) > this.minVl) {
                entityDamageByEntityEvent.setCancelled(true);
            }
            return;
        }
        if (System.currentTimeMillis() - this.lastViolationTimes.getOrDefault(entityDamageByEntityEvent.getDamager(), 0L) > (long)(this.reduceDelay * 50)) {
            this.decreaseViolationLevel(entityDamageByEntityEvent.getDamager().getUniqueId(), 1);
        }
    }

    @Override
    public void onTick(long ticks, long ticks2) {
        Iterator iterator = Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            if (this.currentTargets.containsKey(player)) {
                this.previousTargets.put(player, this.currentTargets.get(player));
            }
            HitboxTargetCache hitboxTargetCache = new HitboxTargetCache(this, player.getNearbyEntities(6.0, 6.0, 6.0).stream().filter(Hitbox::isLivingEntity).distinct().collect(Collectors.toMap(Entity::getEntityId, Hitbox::getEntityPosition, this::warnDuplicateEntityId)));
            this.currentTargets.put(player, hitboxTargetCache);
        }
    }

    private Vector warnDuplicateEntityId(Vector vector, Vector vector2) {
        this.plugin.getLogger().warning("Duplicate entity ID returned from #getNearbyEntities (" + vector + " / " + vector2 + ")");
        return vector;
    }

    private static Vector getEntityPosition(Entity entity) {
        return entity.getLocation().toVector();
    }

    private static boolean isLivingEntity(Entity entity) {
        return entity instanceof LivingEntity;
    }
}

