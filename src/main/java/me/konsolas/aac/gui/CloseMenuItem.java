package me.konsolas.aac.gui;

import org.bukkit.entity.Player;

public class CloseMenuItem
extends CallbackMenuItem {
    public CloseMenuItem() {
        super(null);
    }

    @Override
    public void onClick(Player player) {
        super.onClick(player);
        player.closeInventory();
    }
}

