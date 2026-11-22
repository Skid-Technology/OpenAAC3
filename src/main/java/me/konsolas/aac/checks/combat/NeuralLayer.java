package me.konsolas.aac.checks.combat;

import java.util.Arrays;
import java.util.Random;

public class NeuralLayer {
    private final double[] outputs;
    private final double[] inputs;
    private final double[] weights;
    private final double[] previousWeightChanges;
    private boolean sigmoidEnabled;
    final NeuralNetwork network;

    private NeuralLayer(NeuralNetwork neuralNetwork, int i2, int i3, Random random) {
        this.network = neuralNetwork;
        this.sigmoidEnabled = true;
        this.outputs = new double[i3];
        this.inputs = new double[i2 + 1];
        this.weights = new double[(1 + i2) * i3];
        this.previousWeightChanges = new double[this.weights.length];
        this.randomizeWeights(random);
    }

    private void randomizeWeights(Random random) {
        for (int i = 0; i < this.weights.length; ++i) {
            this.weights[i] = (random.nextDouble() - 0.5) * 4.0;
        }
    }

    private double[] evaluate(double[] dArray) {
        System.arraycopy(dArray, 0, this.inputs, 0, dArray.length);
        this.inputs[this.inputs.length - 1] = 1.0;
        int value = 0;
        Arrays.fill(this.outputs, 0.0);
        for (int i = 0; i < this.outputs.length; ++i) {
            for (int j = 0; j < this.inputs.length; ++j) {
                int value2 = i;
                this.outputs[value2] = this.outputs[value2] + this.weights[value + j] * this.inputs[j];
            }
            if (this.sigmoidEnabled) {
                this.outputs[i] = 1.0 / (1.0 + Math.exp(-this.outputs[i]));
            }
            value += this.inputs.length;
        }
        return Arrays.copyOf(this.outputs, this.outputs.length);
    }

    private double[] backpropagate(double[] dArray, double d, double d2) {
        int value = 0;
        double[] dArray2 = new double[this.inputs.length];
        for (int i = 0; i < this.outputs.length; ++i) {
            double d3 = dArray[i];
            if (this.sigmoidEnabled) {
                d3 *= this.outputs[i] * (1.0 - this.outputs[i]);
            }
            for (int j = 0; j < this.inputs.length; ++j) {
                int value2 = value + j;
                int value3 = j;
                dArray2[value3] = dArray2[value3] + this.weights[value2] * d3;
                double d4 = this.inputs[j] * d3 * d;
                int value4 = value2;
                this.weights[value4] = this.weights[value4] + (this.previousWeightChanges[value2] * d2 + d4);
                this.previousWeightChanges[value2] = d4;
            }
            value += this.inputs.length;
        }
        return dArray2;
    }

    public NeuralLayer(NeuralNetwork neuralNetwork, int i2, int i3, Random random, NeuralLayerAccess neuralLayerAccess) {
        this(neuralNetwork, i2, i3, random);
    }

    static boolean setSigmoidEnabled(NeuralLayer neuralLayer, boolean flag) {
        neuralLayer.sigmoidEnabled = flag;
        return neuralLayer.sigmoidEnabled;
    }

    static double[] forwardPass(NeuralLayer neuralLayer, double[] dArray) {
        return neuralLayer.evaluate(dArray);
    }

    static double[] backwardPass(NeuralLayer neuralLayer, double[] dArray, double d, double d2) {
        return neuralLayer.backpropagate(dArray, d, d2);
    }
}

