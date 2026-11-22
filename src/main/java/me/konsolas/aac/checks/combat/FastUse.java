package me.konsolas.aac.checks.combat;

import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.api.HackType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public final class FastUse
extends Detector {
    private int msToUse;

    public FastUse(AAC aAC) {        super(aAC);

        this.msToUse = 1200;
        this.msToUse = this.plugin.getConfig().getInt("fastuse.ms_to_use");
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent playerItemConsumeEvent) {
        Player player;
        long ticks = 113218805911140L;
        Player player2 = playerItemConsumeEvent.getPlayer();
        if (this.plugin.checkManager.isPlayerExempt(player2) || AAC.serverVersion.isElytraGliding(player = playerItemConsumeEvent.getPlayer())) {
            return;
        }
        long now = System.currentTimeMillis();
        long ticks2 = now - this.plugin.playerState.getLastInteractionTime(playerItemConsumeEvent.getPlayer());
        if (ticks2 < (long)this.msToUse) {
            if (this.recordViolation(playerItemConsumeEvent.getPlayer().getUniqueId(), 1, HackType.FASTUSE, playerItemConsumeEvent.getPlayer().getName() + " failed FastUse: Tried to consume an item too quickly (" + ticks2 + "ms)", ticks)) {
                playerItemConsumeEvent.setCancelled(true);
                playerItemConsumeEvent.getPlayer().getInventory().setHeldItemSlot((playerItemConsumeEvent.getPlayer().getInventory().getHeldItemSlot() + 1) % 9);
            }
        } else {
            this.decreaseViolationLevel(playerItemConsumeEvent.getPlayer().getUniqueId(), 1);
        }
    }
}

