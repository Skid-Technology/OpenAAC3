package me.konsolas.aac.checks.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.checks.movement.MovementCheckListener;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public final class Killaura
extends Detector
implements MovementCheckListener,
CombatCheckListener {
    private boolean cancelHit;
    private int minVl;
    private final Map<String, Integer> moduleWeights;
    private final Map<String, KillauraModule> modules;

    public Killaura(AAC aAC) {        super(aAC);

        this.cancelHit = true;
        this.minVl = 6;
        this.moduleWeights = new HashMap();
        Killaura killaura = this;
        this.modules = new KillauraModuleRegistry(killaura);
        this.cancelHit = this.plugin.getConfig().getBoolean("killaura.cancel_hit", this.cancelHit);
        this.minVl = this.plugin.getConfig().getInt("killaura.min_vl", this.minVl);
        HashSet<String> hashSet = new HashSet<String>();
        for (String str : this.modules.keySet()) {
            this.moduleWeights.put(str, this.plugin.getConfig().getInt("killaura.modules." + str, 1));
            if ((Integer)this.moduleWeights.get(str) >= 0) continue;
            hashSet.add(str);
        }
        for (String str2 : hashSet) {
            this.modules.remove(str2);
        }
        PacketListener packetListener = new KillauraPacketListener(this, (Plugin)this.plugin, new PacketType[]{PacketType.Play.Server.ENTITY_DESTROY, PacketType.Play.Server.ENTITY, PacketType.Play.Server.ENTITY_HEAD_ROTATION, PacketType.Play.Server.REL_ENTITY_MOVE, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_TELEPORT, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK});
        this.packetListeners.add(packetListener);
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
        Server server = Bukkit.getServer();
        this.scheduledTaskIds.add(server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::decayKillauraViolations, 35L, 23L));
    }

    @Override
    public void onTick(long ticks, long ticks2) {
        this.modules.values().forEach(arg_0 -> Killaura.dispatchModuleTick(ticks, arg_0));
    }

    private void reportModuleViolation(KillauraViolation killauraViolation, long ticks) {
        long ticks2 = (ticks = 0x410E426F8C36L ^ ticks) ^ 0x14745BB2CF6AL;
        this.recordViolation(killauraViolation.getPlayer().getUniqueId(), (Integer)this.moduleWeights.get(killauraViolation.getModuleName()), HackType.KILLAURA, killauraViolation.getPlayer().getName() + " failed Killaura: (" + killauraViolation.getModuleName() + ") " + killauraViolation.getMessage(), ticks2);
    }

    @Override
    public void onCombat(EntityDamageByEntityEvent entityDamageByEntityEvent, long ticks) {
        this.modules.values().forEach(arg_0 -> Killaura.dispatchModuleEntityDamage(entityDamageByEntityEvent, arg_0));
        if (this.cancelHit && this.getViolationLevel((Player)entityDamageByEntityEvent.getDamager()) > this.minVl) {
            entityDamageByEntityEvent.setCancelled(true);
        }
    }

    @Override
    public boolean shouldProcessCombat(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        return entityDamageByEntityEvent.getDamager() instanceof Player;
    }

    @Override
    public void onMovement(PlayerMoveEvent playerMoveEvent, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks) {
        this.modules.values().forEach(arg_0 -> Killaura.dispatchModulePlayerMove(playerMoveEvent, arg_0));
    }

    @Override
    public boolean shouldProcessMovement(PlayerMoveEvent playerMoveEvent) {
        return this.plugin.combatTracker.hasRecentCombat(playerMoveEvent.getPlayer());
    }

    private static void dispatchModulePlayerMove(PlayerMoveEvent playerMoveEvent, KillauraModule killauraModule) {
        PlayerMoveEvent playerMoveEvent2 = playerMoveEvent;
        killauraModule.onPlayerMove(playerMoveEvent2);
    }

    private static void dispatchModuleEntityDamage(EntityDamageByEntityEvent entityDamageByEntityEvent, KillauraModule killauraModule) {
        EntityDamageByEntityEvent entityDamageByEntityEvent2 = entityDamageByEntityEvent;
        killauraModule.onEntityDamageByEntity(entityDamageByEntityEvent2);
    }

    private static void dispatchModuleTick(long ticks, KillauraModule killauraModule) {
        killauraModule.onTick();
    }

    private void decayKillauraViolations() {
        Bukkit.getOnlinePlayers().forEach(this::decayPlayerViolation);
    }

    private void decayPlayerViolation(Player player) {
        this.decreaseViolationLevel(player.getUniqueId(), 1);
    }


















    static void dispatchModuleViolation(Killaura killaura, KillauraViolation killauraViolation, long ticks) {
        long ticks2 = (ticks = 0x410E426F8C36L ^ ticks) ^ 0x18DD464A0375L;
        killaura.reportModuleViolation(killauraViolation, ticks2);
    }

    static Map<String, KillauraModule> getModules(Killaura killaura) {
        return killaura.modules;
    }
}

