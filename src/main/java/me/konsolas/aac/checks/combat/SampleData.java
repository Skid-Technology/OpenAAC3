package me.konsolas.aac.checks.combat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class SampleData {
    private final Deque<Double> samples;
    private final int capacity;

    public SampleData(int i) {
        this.capacity = i;
        this.samples = new ArrayDeque<>();
    }

    public void addSample(double d) {
        if (this.samples.size() >= this.capacity) {
            this.samples.poll();
            this.samples.add(d);
        } else {
            this.samples.add(d);
        }
    }

    public Deque<Double> getSamples() {
        return this.samples;
    }

    public double getMean() {
        double d = 0.0;
        Iterator<Double> iterator = this.samples.iterator();
        while (iterator.hasNext()) {
            double d2 = iterator.next();
            d += d2;
        }
        return d / (double)this.samples.size();
    }

    public void clear() {
        this.samples.clear();
    }

    public boolean isFull() {
        return this.samples.size() == this.capacity;
    }

    public String toString() {
        return "SampleData:" + this.samples.toString();
    }
}

