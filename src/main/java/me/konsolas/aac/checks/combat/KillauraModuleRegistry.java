package me.konsolas.aac.checks.combat;

import java.util.HashMap;
import java.util.function.Consumer;
import me.konsolas.aac.AAC;

public class KillauraModuleRegistry
extends HashMap<String, KillauraModule> {
    final Killaura detector;

    public KillauraModuleRegistry(Killaura killaura) {
        this.detector = killaura;
        Consumer<KillauraViolation> consumer = arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0);
        AAC aAC = this.detector.getPlugin();
        this.put("natural", new KillauraNaturalModule(aAC, consumer));
        this.put("round", new KillauraRoundingModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        this.put("split", new KillauraSplitModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        this.put("sporadic", new KillauraSporadicModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        this.put("stable", new KillauraStableModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        Consumer<KillauraViolation> consumer2 = arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0);
        AAC aAC2 = this.detector.getPlugin();
        this.put("switch", new KillauraSwitchModule(aAC2, consumer2));
        this.put("angle", new KillauraAngleModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        this.put("click", new KillauraClickModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        Consumer<KillauraViolation> consumer3 = arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0);
        AAC aAC3 = this.detector.getPlugin();
        this.put("miss", new KillauraMissModule(aAC3, consumer3));
        this.put("tick", new KillauraTickModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        this.put("yaw", new KillauraYawModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        this.put("normal", new KillauraNormalModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        this.put("look", new KillauraLookModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        Consumer<KillauraViolation> consumer4 = arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0);
        AAC aAC4 = this.detector.getPlugin();
        this.put("timing", new KillauraTimingModule(aAC4, consumer4));
        this.put("accel", new KillauraAccelerationModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
        Consumer<KillauraViolation> consumer5 = arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0);
        AAC aAC5 = this.detector.getPlugin();
        this.put("accuracy", new KillauraAccuracyModule(aAC5, consumer5));
        this.put("pattern", new KillauraPatternModule(this.detector.getPlugin(), arg_0 -> KillauraModuleRegistry.reportModuleViolation(this.detector, arg_0)));
    }


















    private static void reportModuleViolation(Killaura killaura, KillauraViolation killauraViolation) {
        Killaura.dispatchModuleViolation(killaura, killauraViolation, 116899669967483L);
    }
}
