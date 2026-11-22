package me.konsolas.aac.punishment;

import java.util.List;
import org.bukkit.entity.Player;

public class PlayerViolationSummary {
    private final Player player;
    private final int violationTotal;
    private final List violations;

    private PlayerViolationSummary(Player player, int i, List list) {
        this.player = player;
        this.violationTotal = i;
        this.violations = list;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getViolationTotal() {
        return this.violationTotal;
    }

    public List getViolations() {
        return this.violations;
    }

    public PlayerViolationSummary(Player player, int i, List list, DefaultViolationWeights defaultViolationWeights) {
        this(player, i, list);
    }
}

