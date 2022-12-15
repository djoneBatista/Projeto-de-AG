package hibrido;

import java.util.*;

abstract public class Network {

    //Data
    private int inputNeuronCounter;
    private int outputNeuronCounter;

    public final static double ActivedNeuron = 0.9;
    public final static double DesactivedNeuron = 0.1;
    protected double output[];
    protected double totalError;
    protected Random r = new Random(System.currentTimeMillis());
    
    

    //Just Defining Functions
    abstract public void learn()
            throws RuntimeException;

    abstract void trial(double[] input);

    double[] getOutput() {
        return output;
    }

    //Functions
    static double vectorLength(double vector[]) {
        double rtn = 0.0;
        for (int i = 0; i < vector.length; i++) {
            rtn += vector[i] * vector[i];
        }
        return rtn;
    }

    double dotProduct(double vectorA[], double vectorB[]) {
        int i, vector;
        double rtn;
        rtn = 0.0;
        i = vectorA.length;
        vector = 0;
        while ((i--) > 0) {
            rtn += vectorA[vector] * vectorB[vector];
            vector++;
        }
        return rtn;
    }

    void randomizeWeights(double weight[][]) {
        double j;
        int temp = (int) (3.464101615 / (2. * Math.random())); // SQRT(12)=3.464..
        for (int y = 0; y < weight.length; y++) {
            for (int x = 0; x < weight[0].length; x++) {
                j = (double) r.nextInt(Integer.MAX_VALUE) + (double) r.nextInt(Integer.MAX_VALUE)
                        - (double) r.nextInt(Integer.MAX_VALUE) - (double) r.nextInt(Integer.MAX_VALUE);
                weight[y][x] = temp * j;
            }
        }
    }

    public abstract double[][] guardarRed();

    public abstract void carregarRede(double outputWeights[][]);

    public int getInputNeuronCounter() {
        return inputNeuronCounter;
    }

    public int getOutputNeuronCounter() {
        return outputNeuronCounter;
    }

    public void setInputNeuronCounter(int inputNeuronCounter) {
        this.inputNeuronCounter = inputNeuronCounter;
    }

    public void setOutputNeuronCounter(int outputNeuronCounter) {
        this.outputNeuronCounter = outputNeuronCounter;
    }

}
