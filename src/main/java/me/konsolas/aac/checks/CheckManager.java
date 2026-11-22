package me.konsolas.aac.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.UUID;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.checks.combat.CombatCheckListener;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.util.MathUtilities;
import me.konsolas.aac.checks.movement.MovementBoundsPacketListener;
import me.konsolas.aac.checks.movement.MovementCheckListener;
import me.konsolas.aac.util.TickRateMonitor;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CheckManager
implements Listener {
    public static final Set identityLookupState = new HashSet();
    private final EnumMap<HackType, Detector> detectors = new EnumMap<>(HackType.class);
    private final List<HackType> disabledChecks = new ArrayList<>();
    private final Set<UUID> bypassPlayers = new HashSet<>();
    private final Map<UUID, Long> bypassRefreshTimes = new HashMap<>();
    private final Map<UUID, GroundCollisionState> teleportTimes = new HashMap<>();
    private final AAC plugin;
    public final Consumer<PlayerMoveEvent> movementCallback = this::onPlayerMove;
    private long tickCounter = 0L;

    public CheckManager(AAC aAC) {
        this.plugin = aAC;
        for (HackType hackType : HackType.values()) {
            this.detectors.put(hackType, null);
        }
        Server server = Bukkit.getServer();
        server.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this::tickDetectors, 1L, 1L);
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new MovementBoundsPacketListener(this, (Plugin)aAC, ListenerPriority.LOWEST, new PacketType[]{PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.POSITION}));
    }

    private boolean isOutOfBounds(double d, double d2) {
        return !(d >= -3.0E7 && d2 >= -3.0E7 && d < 3.0E7 && d2 < 3.0E7);
    }

    public boolean enableCheck(HackType hackType, boolean flag) {
        Class<?> clazz;
        if (this.detectors.get(hackType) != null) {
            return false;
        }
        if (flag) {
            if (this.disabledChecks.contains(hackType)) {
                return false;
            }
        } else {
            this.disabledChecks.remove(hackType);
        }
        try {
            String className = hackType == HackType.MOVE
                ? "me.konsolas.aac.checks.movement.MovementManager"
                : "me.konsolas.aac.checks." + hackType;
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException classNotFoundException) {
            String str = "Failed to initialize " + hackType.getName() + " (class not found)";
            this.plugin.logManager.logViolation(str);
            return false;
        }
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> constructor = null;
        for (Constructor<?> candidate : constructors) {
            if (candidate.getParameterTypes().length == 1) {
                constructor = candidate;
                break;
            }
        }
        if (constructor == null) {
            throw new RuntimeException("Could not get valid constuctor");
        }
        Detector detector;
        try {
            constructor.setAccessible(true);
            detector = (Detector)constructor.newInstance(new Object[]{this.plugin});
        }
        catch (Exception exception) {
            exception.printStackTrace();
            String str2 = "Failed to initialize " + hackType.getName() + " (instance creation failed)";
            this.plugin.logManager.logViolation(str2);
            return false;
        }
        this.detectors.put(hackType, detector);
        Server server = Bukkit.getServer();
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(detector, this.plugin);
        String str3 = "(CheckManager) Enabled check " + (Object)((Object)hackType);
        this.plugin.logManager.logViolation(str3);
        return true;
    }

    public boolean disableCheck(HackType hackType, boolean flag) {
        if (!this.isCheckEnabled(hackType)) {
            return false;
        }
        if (!flag) {
            this.disabledChecks.add(hackType);
        }
        ((Detector)this.detectors.get((Object)hackType)).cleanup();
        HandlerList.unregisterAll((Listener)((Listener)this.detectors.get((Object)hackType)));
        this.detectors.put(hackType, null);
        String str = "(CheckManager) Disabled check " + (Object)((Object)hackType);
        this.plugin.logManager.logViolation(str);
        return true;
    }

    public void disableAllChecks() {
        HackType[] hackTypeArray = HackType.values();
        int value = hackTypeArray.length;
        for (int i = 0; i < value; ++i) {
            HackType hackType = hackTypeArray[i];
            if (this.isCheckEnabled(hackType)) {
                this.plugin.getLogger().info("(CheckManager) Disabling check " + (Object)((Object)hackType));
                boolean flag = false;
                HackType hackType2 = hackType;
                this.disableCheck(hackType2, flag);
                continue;
            }
            this.plugin.getLogger().info("(CheckManager) Check " + (Object)((Object)hackType) + " is not enabled");
        }
    }

    public void enableConfiguredChecks() {
        HackType[] hackTypeArray = HackType.values();
        for (HackType hackType : hackTypeArray) {
            boolean flag = true;
            HackType hackType2 = hackType;
            this.enableCheck(hackType2, flag);
        }
    }

    public Detector getDetector(HackType hackType) {
        return (Detector)this.detectors.get((Object)hackType);
    }

    public boolean isCheckEnabled(HackType hackType) {
        return this.detectors.get((Object)hackType) != null;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent playerKickEvent) {
        HackType[] hackTypeArray = HackType.values();
        int value = hackTypeArray.length;
        for (int i = 0; i < value; ++i) {
            HackType hackType = hackTypeArray[i];
            if (!this.isCheckEnabled(hackType)) continue;
            this.getDetector(hackType).onPlayerKick(playerKickEvent);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        HackType[] hackTypeArray = HackType.values();
        int value = hackTypeArray.length;
        for (int i = 0; i < value; ++i) {
            HackType hackType = hackTypeArray[i];
            if (!this.isCheckEnabled(hackType)) continue;
            this.getDetector(hackType).onPlayerJoin(playerJoinEvent);
        }
        if (this.plugin.logManager.isDebugEnabled()) {
            this.plugin.logManager.logPlayerDebug(null, " -> JOIN " + playerJoinEvent.getPlayer().getName() + " / " + playerJoinEvent.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        HackType[] hackTypeArray = HackType.values();
        int value = hackTypeArray.length;
        for (int i = 0; i < value; ++i) {
            HackType hackType = hackTypeArray[i];
            if (!this.isCheckEnabled(hackType)) continue;
            this.getDetector(hackType).onPlayerQuit(playerQuitEvent);
        }
        if (this.plugin.logManager.isDebugEnabled()) {
            this.plugin.logManager.logPlayerDebug(null, " <- QUIT " + playerQuitEvent.getPlayer().getName() + " / " + playerQuitEvent.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        this.teleportTimes.put(playerTeleportEvent.getPlayer().getUniqueId(), CollisionAccess.getGroundCollisionState(new PlayerMoveEvent(playerTeleportEvent.getPlayer(), playerTeleportEvent.getTo(), playerTeleportEvent.getTo()), this.teleportTimes.getOrDefault(playerTeleportEvent.getPlayer().getUniqueId(), GroundCollisionState.TRUE_NORMAL)));
    }

    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        GroundCollisionState groundCollisionState;
        GroundCollisionState groundCollisionState2;
        long ticks = 6629313046314L;
        long ticks2 = 6629313046314L;
        Player player = playerMoveEvent.getPlayer();
        boolean flag = this.isPlayerExempt(player);
        BlockPositionView blockPositionView = new BlockPositionView(playerMoveEvent.getFrom());
        BlockPositionView blockPositionView2 = new BlockPositionView(playerMoveEvent.getTo());
        if (playerMoveEvent.getTo().distanceSquared(playerMoveEvent.getFrom()) < 625.0) {
            groundCollisionState2 = this.teleportTimes.getOrDefault(playerMoveEvent.getPlayer().getUniqueId(), GroundCollisionState.TRUE_NORMAL);
            groundCollisionState = playerMoveEvent.getTo().getY() < 258.0 && playerMoveEvent.getTo().getY() > -2.0 ? CollisionAccess.getGroundCollisionState(playerMoveEvent, groundCollisionState2) : GroundCollisionState.FALSE_NORMAL;
        } else {
            groundCollisionState2 = this.teleportTimes.getOrDefault(playerMoveEvent.getPlayer().getUniqueId(), GroundCollisionState.TRUE_NORMAL);
            groundCollisionState = GroundCollisionState.FALSE_NORMAL;
        }
        boolean flag2 = groundCollisionState.isOnGround();
        boolean flag3 = groundCollisionState2.isOnGround();
        PlayerMoveEvent playerMoveEvent2 = playerMoveEvent;
        this.plugin.playerState.updateMovementState(playerMoveEvent2, flag3, flag2);
        Location location = playerMoveEvent.getTo();
        for (HackType hackType : HackType.values()) {
            PlayerMoveEvent playerMoveEvent3;
            MovementCheckListener movementCheckListener;
            Detector detector;
            if (!this.isCheckEnabled(hackType) || !((detector = this.getDetector(hackType)) instanceof MovementCheckListener) || !(movementCheckListener = (MovementCheckListener)((Object)detector)).shouldProcessMovement(playerMoveEvent3 = playerMoveEvent) || flag) continue;
            movementCheckListener.onMovement(playerMoveEvent, groundCollisionState2, groundCollisionState, blockPositionView, blockPositionView2, ticks2);
            if (playerMoveEvent.getTo().equals((Object)location)) continue;
            groundCollisionState = CollisionAccess.getGroundCollisionState(playerMoveEvent, groundCollisionState2);
            break;
        }
        this.teleportTimes.put(playerMoveEvent.getPlayer().getUniqueId(), groundCollisionState);
        PlayerMoveEvent playerMoveEvent4 = playerMoveEvent;
        if (this.plugin.setbackManager.shouldProcessMovement(playerMoveEvent4) && !flag) {
            this.plugin.setbackManager.onMovement(playerMoveEvent, groundCollisionState2, groundCollisionState, blockPositionView, blockPositionView2, ticks);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        long ticks = 32485288229805L;
        boolean flag = entityDamageByEntityEvent.getDamager().hasPermission("AAC.bypass") || entityDamageByEntityEvent.getEntity().hasPermission("AAC.bypass");
        for (HackType hackType : HackType.values()) {
            CombatCheckListener combatCheckListener;
            Detector detector;
            if (!this.isCheckEnabled(hackType) || !((detector = this.getDetector(hackType)) instanceof CombatCheckListener) || !(combatCheckListener = (CombatCheckListener)((Object)detector)).shouldProcessCombat(entityDamageByEntityEvent) || flag) continue;
            combatCheckListener.onCombat(entityDamageByEntityEvent, ticks);
        }
    }

    public boolean isPlayerExempt(Player player) {
        boolean flag;
        if (player == null) {
            return true;
        }
        if (!this.bypassRefreshTimes.containsKey(player.getUniqueId())) {
            this.bypassRefreshTimes.put(player.getUniqueId(), 0L);
        }
        if (this.bypassPlayers.contains(player.getUniqueId())) {
            if (System.currentTimeMillis() < (Long)this.bypassRefreshTimes.get(player.getUniqueId())) {
                flag = true;
            } else {
                flag = player.hasPermission("AAC.bypass");
                if (flag) {
                    this.bypassPlayers.add(player.getUniqueId());
                } else {
                    this.bypassPlayers.remove(player.getUniqueId());
                }
                this.bypassRefreshTimes.put(player.getUniqueId(), System.currentTimeMillis() + (long)AAC.random.nextInt(20000) + 20000L);
            }
        } else if (System.currentTimeMillis() < (Long)this.bypassRefreshTimes.get(player.getUniqueId())) {
            flag = false;
        } else {
            flag = player.hasPermission("AAC.bypass");
            if (flag) {
                this.bypassPlayers.add(player.getUniqueId());
            } else {
                this.bypassPlayers.remove(player.getUniqueId());
            }
            this.bypassRefreshTimes.put(player.getUniqueId(), System.currentTimeMillis() + (long)AAC.random.nextInt(20000) + 20000L);
        }
        return flag;
    }

    public void clearBypassCache() {
        this.bypassRefreshTimes.clear();
        this.bypassPlayers.clear();
    }

    private void tickDetectors() {
        long ticks = 47357059873379L;
        ++this.tickCounter;
        HackType[] hackTypeArray = HackType.values();
        int value = hackTypeArray.length;
        for (int i = 0; i < value; ++i) {
            HackType hackType = hackTypeArray[i];
            if (!this.isCheckEnabled(hackType)) continue;
            this.getDetector(hackType).onTick(this.tickCounter, ticks);
        }
        if (this.plugin.logManager != null && this.plugin.logManager.isDebugEnabled()) {
            double d;
            double d2 = d = TickRateMonitor.getTps();
            this.plugin.logManager.logPlayerDebug(null, " -> tick " + this.tickCounter + " tps: " + MathUtilities.formatNumber(d2));
        }
    }

    public static boolean isOutOfBounds(CheckManager checkManager, double d, double d2) {
        return checkManager.isOutOfBounds(d, d2);
    }
}

