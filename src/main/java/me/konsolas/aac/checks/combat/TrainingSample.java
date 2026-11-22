package me.konsolas.aac.checks.combat;

public class TrainingSample {
    private final double[] inputs;
    private final double[] expectedOutputs;

    public TrainingSample(double[] dArray, double[] dArray2) {
        this.inputs = dArray;
        this.expectedOutputs = dArray2;
    }

    public double[] getInputs() {
        return this.inputs;
    }

    public double[] getExpectedOutputs() {
        return this.expectedOutputs;
    }
}

