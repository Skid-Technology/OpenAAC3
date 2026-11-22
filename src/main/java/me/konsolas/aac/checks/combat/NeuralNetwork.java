package me.konsolas.aac.checks.combat;

import java.util.Random;

public class NeuralNetwork {
    private final NeuralLayer[] layers;
    private final int inputSize;

    public NeuralNetwork(int i2, int[] nArray) {
        this.inputSize = i2;
        this.layers = new NeuralLayer[nArray.length];
        Random random = new Random(1L);
        for (int i = 0; i < nArray.length; ++i) {
            int value = i == 0 ? i2 : nArray[i - 1];
            this.layers[i] = new NeuralLayer(this, value, nArray[i], random, null);
        }
        NeuralLayer.setSigmoidEnabled(this.layers[nArray.length - 1], false);
    }

    public double[] evaluate(double[] dArray) {
        if (dArray.length != this.inputSize) {
            throw new IllegalArgumentException();
        }
        double[] dArray2 = dArray;
        for (NeuralLayer neuralLayer : this.layers) {
            dArray2 = NeuralLayer.forwardPass(neuralLayer, dArray2);
        }
        return dArray2;
    }

    public void train(double[] dArray, double[] dArray2, double d, double d2) {
        int i2;
        double[] dArray3 = this.evaluate(dArray);
        double[] dArray4 = new double[dArray3.length];
        for (i2 = 0; i2 < dArray4.length; ++i2) {
            dArray4[i2] = dArray2[i2] - dArray3[i2];
        }
        for (i2 = this.layers.length - 1; i2 >= 0; --i2) {
            dArray4 = NeuralLayer.backwardPass(this.layers[i2], dArray4, d, d2);
        }
    }
}

