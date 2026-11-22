package me.konsolas.aac.checks.combat;


public class NaturalAimState {
    int streakCount;
    boolean switchedOffTarget;
    double missedDeltaAvg;
    int missedSampleCount;
    double hitDeltaAvg;
    int hitSampleCount;
    final KillauraNaturalModule module;

    private NaturalAimState(KillauraNaturalModule killauraNaturalModule) {
        this.module = killauraNaturalModule;
    }

    public String toString() {
        return this.streakCount + "/" + this.hitSampleCount + "/" + String.format("%.3f", this.hitDeltaAvg) + "/" + this.missedSampleCount;
    }

    public NaturalAimState(KillauraNaturalModule killauraNaturalModule, NaturalAimStateAccess naturalAimStateAccess) {
        this(killauraNaturalModule);
    }
}

