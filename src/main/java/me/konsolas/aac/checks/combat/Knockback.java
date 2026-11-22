package me.konsolas.aac.checks.combat;

import java.util.concurrent.ConcurrentHashMap;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.EntityTypeChecks;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.checks.movement.MovementCheckListener;
import me.konsolas.aac.compat.PlayerPingAccess;
import me.konsolas.aac.compat.ServerVersion;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public final class Knockback
extends Detector
implements MovementCheckListener {
    private int tickDelay;
    private boolean addPing;
    private int maxPing;
    private double minKnockbackDist;
    private final ConcurrentHashMap activeContexts;

    public Knockback(AAC aAC) {        super(aAC);

        this.tickDelay = 7;
        this.addPing = true;
        this.maxPing = 500;
        this.minKnockbackDist = 0.1;
        this.activeContexts = new ConcurrentHashMap();
        this.tickDelay = this.plugin.getConfig().getInt("knockback.tick_delay", this.tickDelay);
        this.addPing = this.plugin.getConfig().getBoolean("knockback.add_ping", this.addPing);
        this.maxPing = this.plugin.getConfig().getInt("knockback.max_ping", this.maxPing);
        this.minKnockbackDist = this.plugin.getConfig().getDouble("knockback.min_knockback_dist", this.minKnockbackDist);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        AttackContext attackContext;
        BlockPositionView blockPositionView;
        Player player;
        long ticks = 103276215108508L;
        if (entityDamageByEntityEvent.getEntity().getType() != EntityType.PLAYER || entityDamageByEntityEvent.getEntity().getVehicle() != null || this.plugin.checkManager.isPlayerExempt(player = (Player)entityDamageByEntityEvent.getEntity()) || entityDamageByEntityEvent.getCause() == EntityDamageEvent.DamageCause.THORNS) {
            return;
        }
        if (this.activeContexts.containsKey(entityDamageByEntityEvent.getEntity().getUniqueId())) {
            ((AttackContext)this.activeContexts.get(entityDamageByEntityEvent.getEntity().getUniqueId())).cancelTask();
            this.activeContexts.remove(entityDamageByEntityEvent.getEntity().getUniqueId());
        }
        if (!EntityTypeChecks.isKnockbackTarget(entityDamageByEntityEvent.getDamager().getType())) {
            return;
        }
        double d = entityDamageByEntityEvent.getDamager().getLocation().distance(entityDamageByEntityEvent.getEntity().getLocation());
        if (d < 1.2) {
            return;
        }
        Player player2 = (Player)entityDamageByEntityEvent.getEntity();
        if (entityDamageByEntityEvent.getDamager().getType() == EntityType.ARROW) {
            if ((AAC.serverVersion.getVersion() == ServerVersion.VER_1_9 || AAC.serverVersion.getVersion() == ServerVersion.VER_1_10) && player2.isBlocking()) {
                return;
            }
            Arrow arrow = (Arrow)entityDamageByEntityEvent.getDamager();
            if (arrow.getShooter() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)arrow.getShooter();
                if (entityDamageByEntityEvent.getEntity().getEntityId() == livingEntity.getEntityId()) {
                    return;
                }
            }
        }
        if (this.isBlockingEnvironmentMaterial((blockPositionView = new BlockPositionView(player2.getLocation())).getMaterialAtOffset(0.6, 0.0, 0.0)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 0.0, 0.0)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.0, 0.0, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.0, 0.0, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.6, 0.0, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 0.0, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.6, 0.0, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 0.0, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.6, 1.1, 0.0)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 1.1, 0.0)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.0, 1.1, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.0, 1.1, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.6, 1.1, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 1.1, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.6, 1.1, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 1.1, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.6, 2.1, 0.0)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 2.1, 0.0)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.0, 2.1, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.0, 2.1, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.6, 2.1, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 2.1, 0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.6, 2.1, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(-0.6, 2.1, -0.6)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.0, 1.1, 0.0)) || this.isBlockingEnvironmentMaterial(blockPositionView.getMaterialAtOffset(0.0, 2.1, 0.0)) || this.isBlockingEnvironmentMaterial(blockPositionView.getBlock().getType())) {
            return;
        }
        int value = this.tickDelay;
        long ticks2 = ticks;
        Player player3 = player2;
        int value2 = PlayerPingAccess.getPing(player3, ticks2);
        if (this.addPing) {
            value += value2 < this.maxPing ? value2 / 100 : this.maxPing / 100;
        }
        try {
            boolean flag = false;
            Entity entity = entityDamageByEntityEvent.getEntity();
            Entity entity2 = entityDamageByEntityEvent.getDamager();
            attackContext = new AttackContext(entity2, entity, flag);
        }
        catch (IllegalStateException illegalStateException) {
            return;
        }
        this.activeContexts.put(player2.getUniqueId(), attackContext);
        Server server = Bukkit.getServer();
        long delayCheckKey = 113218805911140L;
        attackContext.setTaskId(server.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            AttackContext trackedAttack = (AttackContext)this.activeContexts.get(player2.getUniqueId());
            this.activeContexts.remove(player2.getUniqueId());
            if (player2.isDead()) {
                return;
            }
            double distanceMoved = trackedAttack.getMaxDistance() - trackedAttack.getInitialDistance();
            trackedAttack.getAwayTickCount();
            if (distanceMoved == 0.0) {
                this.recordViolation(player2.getUniqueId(), 2, HackType.KNOCKBACK, player2.getName() + " failed Knockback: didn't move away from his attacker when hit", delayCheckKey);
            } else if (distanceMoved < this.minKnockbackDist) {
                this.recordViolation(player2.getUniqueId(), 2, HackType.KNOCKBACK, player2.getName() + " failed Knockback: didn't move far enough when hit (" + distanceMoved + " blocks" + (trackedAttack.isInWater() ? ", inWater)" : ")"), delayCheckKey);
            } else {
                this.decreaseViolationLevel(player2.getUniqueId(), 1);
            }
        }, (long)value));
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        return true;
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        if (this.activeContexts.containsKey(playerMoveEvent.getPlayer().getUniqueId())) {
            AttackContext attackContext = (AttackContext)this.activeContexts.get(playerMoveEvent.getPlayer().getUniqueId());
            attackContext.updateDistance(playerMoveEvent.getPlayer().getLocation());
        }
    }

    private boolean isBlockingEnvironmentMaterial(Material material) {
        return material == Material.WEB || material == Material.WATER || material == Material.STATIONARY_WATER || material == Material.LAVA || material == Material.STATIONARY_LAVA || material.isSolid() || material.equals((Object)Material.SKULL) || material == Material.FLOWER_POT;
    }
}

