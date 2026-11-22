package me.konsolas.aac.checks.movement;

import me.konsolas.aac.util.BlockPositionView;
import me.konsolas.aac.util.GroundCollisionState;
import org.bukkit.event.player.PlayerMoveEvent;

public interface MovementCheckListener {
    public void onMovement(PlayerMoveEvent event, GroundCollisionState groundCollisionState, GroundCollisionState groundCollisionState2, BlockPositionView blockPositionView, BlockPositionView blockPositionView2, long ticks);

    public boolean shouldProcessMovement(PlayerMoveEvent event);
}

