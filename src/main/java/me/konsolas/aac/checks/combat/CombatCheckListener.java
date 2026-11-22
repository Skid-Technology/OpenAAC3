package me.konsolas.aac.checks.combat;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface CombatCheckListener {
    public void onCombat(EntityDamageByEntityEvent event, long ticks);

    public boolean shouldProcessCombat(EntityDamageByEntityEvent event);
}

