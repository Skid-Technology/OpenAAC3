package me.konsolas.aac.hooks;

import com.gmail.nossr50.api.AbilityAPI;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.konsolas.aac.AAC;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.api.PlayerViolationEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class McMmoHook
extends ExternalPluginHook {
    private final ExpiringMap<UUID, Boolean> recentFakeBlockBreaks;

    public McMmoHook(AAC aAC) {
        super(aAC);
        TimeUnit timeUnit = TimeUnit.SECONDS;
        long ticks = 1000L;
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        this.recentFakeBlockBreaks = ExpiringMap.builder().expiration(ticks, timeUnit).expirationPolicy(expirationPolicy).build();
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerViolation(PlayerViolationEvent playerViolationEvent) {
        try {
            if (playerViolationEvent.getPlayer() == null || !playerViolationEvent.getPlayer().isOnline()) {
                return;
            }
            switch (McMmoHackTypeSwitch.HACK_TYPE_ORDINALS[playerViolationEvent.getHackType().ordinal()]) {
                case 1: {
                    for (Entity entity : playerViolationEvent.getPlayer().getNearbyEntities(7.0, 7.0, 7.0)) {
                        if (!(entity instanceof LivingEntity) || !AbilityAPI.isBleeding((LivingEntity)((LivingEntity)entity))) continue;
                        playerViolationEvent.setCancelled(true);
                        break;
                    }
                }
                case 2: {
                    if (!AbilityAPI.serratedStrikesEnabled((Player)playerViolationEvent.getPlayer()) && !AbilityAPI.skullSplitterEnabled((Player)playerViolationEvent.getPlayer())) break;
                    playerViolationEvent.setCancelled(true);
                    break;
                }
                case 3: {
                    if (AbilityAPI.isAnyAbilityEnabled((Player)playerViolationEvent.getPlayer())) {
                        playerViolationEvent.setCancelled(true);
                    }
                    try {
                        if (playerViolationEvent.getPlayer().getTargetBlock((Set)null, 5).getType().equals((Object)Material.MOSSY_COBBLESTONE)) {
                            playerViolationEvent.setCancelled(true);
                        }
                    }
                    catch (Exception exception) {
                    }
                    break;
                }
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    if (this.recentFakeBlockBreaks.containsKey(playerViolationEvent.getPlayer().getUniqueId())) {
                        playerViolationEvent.setCancelled(true);
                        return;
                    }
                    if (!AbilityAPI.berserkEnabled((Player)playerViolationEvent.getPlayer()) && !AbilityAPI.gigaDrillBreakerEnabled((Player)playerViolationEvent.getPlayer()) && !AbilityAPI.superBreakerEnabled((Player)playerViolationEvent.getPlayer()) && !AbilityAPI.treeFellerEnabled((Player)playerViolationEvent.getPlayer())) break;
                    playerViolationEvent.setCancelled(true);
                }
            }
        }
        catch (Exception exception) {
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onFakeBlockBreak(FakeBlockBreakEvent fakeBlockBreakEvent) {
        this.recentFakeBlockBreaks.put(fakeBlockBreakEvent.getPlayer().getUniqueId(), false);
        this.recentFakeBlockBreaks.resetExpiration(fakeBlockBreakEvent.getPlayer().getUniqueId());
    }
}

