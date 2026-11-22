package me.konsolas.aac.checks.combat;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.compat.CollisionVectorAdjuster;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.compat.EnvironmentBlockChecks;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.checks.movement.MovementCheckListener;
import me.konsolas.aac.api.HackType;
import org.bukkit.GameMode;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Criticals
extends Detector
implements CombatCheckListener,
MovementCheckListener {
    private final ExpiringMap jumpCooldowns;
    private boolean cancelFalseHit;
    private int jumpDelay;

    public Criticals(AAC aAC) {
        super(aAC);
        this.cancelFalseHit = false;
        this.jumpDelay = 5;
        this.cancelFalseHit = this.plugin.getConfig().getBoolean("criticals.cancel_false_hit", this.cancelFalseHit);
        this.jumpDelay = this.plugin.getConfig().getInt("criticals.jump_delay", this.jumpDelay);
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long ticks = 50 * this.jumpDelay;
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        this.jumpCooldowns = ExpiringMap.builder().expiration(ticks, timeUnit).expirationPolicy(expirationPolicy).build();
    }

    @Override
    public boolean shouldProcessCombat(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        return entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER && !((Player)entityDamageByEntityEvent.getDamager()).isFlying();
    }

    @Override
    public void onCombat(EntityDamageByEntityEvent entityDamageByEntityEvent, long ticks) {
        long ticks2 = ticks ^ 0x7B735CA5F5C9L;
        Player player = (Player)entityDamageByEntityEvent.getDamager();
        if (this.isCriticalCandidate(player)) {
            Player player2;
            if (this.plugin.logManager.isDebugEnabled()) {
                this.plugin.logManager.logPlayerDebug(player, "(Criticals) EntityDamageByEntityEvent critical location[r:" + player.getLocation() + "] velocity[r:" + player.getVelocity() + "] blockOn[r:" + player.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType() + "]");
            }
            if ((player.getLocation().getY() % 1.0 == 0.0 && EnvironmentBlockChecks.isUseMaterial(player.getLocation().subtract(0.0, 1.0, 0.0).getBlock()) || player.getLocation().getY() % 0.5 == 0.0 && EnvironmentBlockChecks.isHalfBlock(player.getLocation().getBlock().getType()) && !this.plugin.checkManager.isPlayerExempt(player2 = player)) && this.recordViolation(player.getUniqueId(), 2, HackType.CRITICALS, player.getName() + " failed Criticals: tried to perform a critical hit whilst on the ground", ticks2) && this.cancelFalseHit) {
                entityDamageByEntityEvent.setCancelled(true);
            }
        } else {
            if (this.plugin.logManager.isDebugEnabled()) {
                this.plugin.logManager.logPlayerDebug(player, "(Criticals) EntityDamageByEntityEvent !critical location[r:" + player.getLocation() + "] velocity[r:" + player.getVelocity() + "] blockOn[r:" + player.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType() + "]");
            }
            if (player.getLocation().getY() % 1.0 == 0.0 && EnvironmentBlockChecks.isUseMaterial(player.getLocation().subtract(0.0, 1.0, 0.0).getBlock())) {
                this.decreaseViolationLevel(player.getUniqueId(), 1);
            }
        }
    }

    private boolean isCriticalCandidate(Player player) {
        return player.getFallDistance() > 0.0f && !player.isOnGround() && !player.isInsideVehicle() && !player.hasPotionEffect(PotionEffectType.BLINDNESS) && !EnvironmentBlockChecks.isOnLadderOrVine(new BlockPositionView(player.getLocation())) && !EnvironmentBlockChecks.isInLiquid(new BlockPositionView(player.getLocation()));
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        Player player;
        Iterator iterator = playerMoveEvent.getPlayer().getActivePotionEffects().iterator();
        while (iterator.hasNext()) {
            PotionEffect potionEffect = (PotionEffect)iterator.next();
            if (!potionEffect.getType().equals((Object)PotionEffectType.JUMP)) continue;
            return false;
        }
        return !playerMoveEvent.getPlayer().getAllowFlight() && playerMoveEvent.getPlayer().getGameMode() != GameMode.CREATIVE && playerMoveEvent.getPlayer().getGameMode() != GameMode.SPECTATOR && !AAC.serverVersion.isElytraGliding(player = playerMoveEvent.getPlayer()) && !this.plugin.playerState.hasActiveVelocity(playerMoveEvent.getPlayer());
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        double d;
        long ticks2 = ticks ^ 0x60FF4F6C294EL;
        if (EnvironmentBlockChecks.isNearSnowColumn(blockPositionView) || EnvironmentBlockChecks.isNearLiquid(blockPositionView) || !CollisionVectorAdjuster.isPassableColumn(blockPositionView) || EnvironmentBlockChecks.isInLiquid(blockPositionView) || EnvironmentBlockChecks.isOnCarpet(blockPositionView) || EnvironmentBlockChecks.isPlayerInCobweb(blockPositionView)) {
            return;
        }
        for (Entity entity : playerMoveEvent.getPlayer().getNearbyEntities(2.0, 2.0, 2.0)) {
            if (!(entity instanceof Boat)) continue;
            return;
        }
        if (groundCollisionState.isOnGround() && EnvironmentBlockChecks.isOnEdgeOfHalfBlock(blockPositionView) && EnvironmentBlockChecks.isOnEdgeOfHalfBlock(blockPositionView2) && !EnvironmentBlockChecks.isNearLiquid(blockPositionView2) && (d = playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY()) > 0.0 && d < 0.2) {
            Double doubleVal = (Double)this.jumpCooldowns.get(playerMoveEvent.getPlayer());
            if (doubleVal != null && doubleVal > 0.0 && doubleVal < 0.2 && this.recordViolation(playerMoveEvent.getPlayer().getUniqueId(), 2, HackType.CRITICALS, playerMoveEvent.getPlayer().getName() + " failed Criticals: tried to do a mini-jump", ticks2) && this.cancelFalseHit) {
                boolean flag = false;
                boolean flag2 = true;
                double y = playerMoveEvent.getPlayer().getVelocity().getY();
                PlayerMoveEvent playerMoveEvent2 = playerMoveEvent;
                Player player = playerMoveEvent.getPlayer();
                this.plugin.setbackManager.applySetback(player, playerMoveEvent2, y, flag2, flag);
            }
            this.jumpCooldowns.put(playerMoveEvent.getPlayer(), (Object)(playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY()));
        }
    }
}

