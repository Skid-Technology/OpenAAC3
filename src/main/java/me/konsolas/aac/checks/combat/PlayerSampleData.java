package me.konsolas.aac.checks.combat;

import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;

public class PlayerSampleData {
    private final int sampleCapacity;
    private final Map<Player, SampleData> playerSamples = new WeakHashMap<>();

    public PlayerSampleData(int i) {
        this.sampleCapacity = i;
    }

    public SampleData getSampleData(Player player) {
        if (!this.playerSamples.containsKey(player)) {
            this.playerSamples.put(player, new SampleData(this.sampleCapacity));
        }
        return (SampleData)this.playerSamples.get(player);
    }

    public boolean hasFullSampleData(Player player) {
        return this.getSampleData(player).isFull();
    }

    public double getMean(Player player) {
        if (!this.hasFullSampleData(player)) {
            throw new IllegalStateException();
        }
        return this.getSampleData(player).getMean();
    }

    public int getSampleCapacity() {
        return this.sampleCapacity;
    }

    public void addSample(Player player, double d) {
        this.getSampleData(player).addSample(d);
    }

    public void clearSamples(Player player) {
        this.playerSamples.remove(player);
    }
}

