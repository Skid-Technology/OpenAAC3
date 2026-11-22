package me.konsolas.aac.checks.combat;

import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.compat.ServerVersion;
import me.konsolas.aac.api.HackType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public final class Regen
extends Detector {
    private long minRegenDelay;
    private long minSatiatedDelay;
    private boolean cancelRegen;

    public Regen(AAC aAC) {        super(aAC);

        this.minRegenDelay = 3850L;
        this.minSatiatedDelay = 450L;
        this.cancelRegen = true;
        this.cancelRegen = this.plugin.getConfig().getBoolean("regen.cancel_regen", this.cancelRegen);
        this.minRegenDelay = this.plugin.getConfig().getLong("regen.min_regen_delay", this.minRegenDelay);
        this.minSatiatedDelay = this.plugin.getConfig().getLong("regen.min_satiated_delay", this.minSatiatedDelay);
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent entityRegainHealthEvent) {
        Player player;
        long ticks = 113218805911140L;
        if (entityRegainHealthEvent.getEntity().getType() != EntityType.PLAYER || this.plugin.checkManager.isPlayerExempt(player = (Player)entityRegainHealthEvent.getEntity())) {
            return;
        }
        Player player2 = (Player)entityRegainHealthEvent.getEntity();
        long ticks2 = this.plugin.playerState.getLastRegenerationTime(player2);
        EntityRegainHealthEvent.RegainReason regainReason = this.plugin.playerState.getLastRegenerationReason(player2);
        long now = System.currentTimeMillis() - ticks2;
        if (regainReason == EntityRegainHealthEvent.RegainReason.SATIATED && entityRegainHealthEvent.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            if (AAC.serverVersion.getVersion() == ServerVersion.VER_1_8) {
                if (now < this.minRegenDelay) {
                    if (this.recordViolation(player2.getUniqueId(), 2, HackType.REGEN, player2.getName() + " failed Regen: tried to regenerate health faster than normal", ticks) && this.cancelRegen) {
                        entityRegainHealthEvent.setCancelled(true);
                    }
                } else {
                    this.decreaseViolationLevel(player2.getUniqueId(), 1);
                }
            } else if (now < this.minSatiatedDelay) {
                if (this.recordViolation(player2.getUniqueId(), 1, HackType.REGEN, player2.getName() + " failed Regen: regenerated health too quickly (" + now + "ms)", ticks) && this.cancelRegen) {
                    entityRegainHealthEvent.setCancelled(true);
                }
            } else if (player2.getFoodLevel() < 20) {
                if (now < this.minRegenDelay && this.recordViolation(player2.getUniqueId(), 1, HackType.REGEN, player2.getName() + " failed Regen: regenerated health too quickly (" + now + "ms)", ticks) && this.cancelRegen) {
                    entityRegainHealthEvent.setCancelled(true);
                }
            } else {
                this.decreaseViolationLevel(player2.getUniqueId(), 1);
            }
        }
    }
}

