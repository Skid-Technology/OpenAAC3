package me.konsolas.aac.api;

import me.konsolas.aac.api.HackType;
import org.bukkit.entity.Player;

public interface AACAPI {
    public void reloadAAC();

    public void reloadPermissionCache();

    public void enableCheck(HackType hackType);

    public void disableCheck(HackType hackType);

    public boolean isEnabled(HackType hackType);

    public boolean isBypassed(Player player);

    public double getTPS();

    public int getPing(Player player);

    public int getViolationLevel(Player player, HackType hackType);

    public void setViolationLevel(Player player, HackType hackType, int i);

    public boolean isPlayerOnGround(Player player);
}

