package me.konsolas.aac.checks.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TrainingSet
implements Iterable {
    private final List samples = new ArrayList();

    public synchronized void addSample(TrainingSample trainingSample) {
        this.samples.add(trainingSample);
    }

    public synchronized int size() {
        return this.samples.size();
    }

    public synchronized TrainingSample getRandomSample() {
        int value = ThreadLocalRandom.current().nextInt(this.samples.size());
        return (TrainingSample)this.samples.get(value);
    }

    public Iterator iterator() {
        return Collections.unmodifiableList(this.samples).iterator();
    }
}

