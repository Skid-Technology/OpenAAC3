package me.konsolas.aac.checks.combat;


public class AccuracyState {
    int checkCount;
    int swingCount;
    int accurateHitCount;
    final KillauraAccuracyModule module;

    private AccuracyState(KillauraAccuracyModule killauraAccuracyModule) {
        this.module = killauraAccuracyModule;
    }

    public AccuracyState(KillauraAccuracyModule killauraAccuracyModule, AccuracyStateAccess accuracyStateAccess) {
        this(killauraAccuracyModule);
    }
}

