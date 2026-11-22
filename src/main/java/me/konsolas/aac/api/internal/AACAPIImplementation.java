package me.konsolas.aac.api.internal;

import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.CheckManager;
import me.konsolas.aac.compat.CollisionAccess;
import me.konsolas.aac.checks.Detector;
import me.konsolas.aac.util.GroundCollisionState;
import me.konsolas.aac.compat.PlayerPingAccess;
import me.konsolas.aac.util.TickRateMonitor;
import me.konsolas.aac.api.AACAPI;
import me.konsolas.aac.api.HackType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public final class AACAPIImplementation
implements AACAPI {
    private final AAC plugin;

    public static AACAPIImplementation create(AAC aAC) {
        return new AACAPIImplementation(aAC);
    }

    private AACAPIImplementation(AAC aAC) {
        this.plugin = aAC;
    }

    @Override
    public void reloadAAC() {
        AACAPIImplementation aACAPIImplementation = this;
        AAC aAC = aACAPIImplementation.plugin;
        aAC.reloadPlugin();
    }

    @Override
    public void reloadPermissionCache() {
        AACAPIImplementation aACAPIImplementation = this;
        CheckManager checkManager = aACAPIImplementation.plugin.checkManager;
        checkManager.clearBypassCache();
    }

    @Override
    public void enableCheck(HackType hackType) {
        AACAPIImplementation aACAPIImplementation = this;
        boolean flag = false;
        HackType hackType2 = hackType;
        CheckManager checkManager = aACAPIImplementation.plugin.checkManager;
        boolean flag2 = flag;
        HackType hackType3 = hackType2;
        checkManager.enableCheck(hackType3, flag2);
    }

    @Override
    public void disableCheck(HackType hackType) {
        AACAPIImplementation aACAPIImplementation = this;
        boolean flag = false;
        HackType hackType2 = hackType;
        CheckManager checkManager = aACAPIImplementation.plugin.checkManager;
        boolean flag2 = flag;
        HackType hackType3 = hackType2;
        checkManager.disableCheck(hackType3, flag2);
    }

    @Override
    public boolean isEnabled(HackType hackType) {
        AACAPIImplementation aACAPIImplementation = this;
        HackType hackType2 = hackType;
        CheckManager checkManager = aACAPIImplementation.plugin.checkManager;
        return checkManager.isCheckEnabled(hackType2);
    }

    @Override
    public boolean isBypassed(Player player) {
        AACAPIImplementation aACAPIImplementation = this;
        Player player2 = player;
        CheckManager checkManager = aACAPIImplementation.plugin.checkManager;
        Player player3 = player2;
        return checkManager.isPlayerExempt(player3);
    }

    @Override
    public double getTPS() {
        return TickRateMonitor.getTps();
    }

    @Override
    public int getPing(Player player) {
        long ticks = 103276215108508L;
        long ticks2 = ticks;
        Player player2 = player;
        return PlayerPingAccess.getPing(player2, ticks2);
    }

    @Override
    public int getViolationLevel(Player player, HackType hackType) {
        AACAPIImplementation aACAPIImplementation = this;
        HackType hackType2 = hackType;
        CheckManager checkManager = aACAPIImplementation.plugin.checkManager;
        Player player2 = player;
        Detector detector = checkManager.getDetector(hackType2);
        return detector.getViolationLevel(player2);
    }

    @Override
    public void setViolationLevel(Player player, HackType hackType, int i) {
        AACAPIImplementation aACAPIImplementation = this;
        HackType hackType2 = hackType;
        CheckManager checkManager = aACAPIImplementation.plugin.checkManager;
        int value = i;
        Player player2 = player;
        Detector detector = checkManager.getDetector(hackType2);
        detector.setViolationLevel(player2, value);
    }

    @Override
    public boolean isPlayerOnGround(Player player) {
        Player player2 = player;
        Player player3 = player;
        GroundCollisionState groundCollisionState = GroundCollisionState.TRUE_NORMAL;
        PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(player, player2.getLocation(), player3.getLocation());
        GroundCollisionState groundCollisionState2 = CollisionAccess.getGroundCollisionState(playerMoveEvent, groundCollisionState);
        return groundCollisionState2.isOnGround();
    }
}

