package hibrido;
 

public class Treinamento {
    //Data  

    protected double input[][];
    protected double output[][];
    protected int inputCounter;
    protected int outputCounter;
    protected int trainingCounter;
    //Constructor

    Treinamento(int ic, int oc) {
        this.inputCounter = ic;
        this.outputCounter = oc;
        trainingCounter = 0;
    }
    //Functions

    //Getting the input and output and training counters
    public int getinputCounter() {
        return inputCounter;
    }

    public int getoutputCounter() {
        return outputCounter;
    }

    public int gettrainingCounter() {
        return trainingCounter;
    }

    //Setting the training counters
    public void settrainingCounter(int trainingCounter) {
        this.trainingCounter = trainingCounter;
        input = new double[trainingCounter][inputCounter];
        output = new double[trainingCounter][outputCounter];
    }

    double getInput(int index_x, int index_y)
            throws RuntimeException {
        if ((index_x < 0) || (index_x >= trainingCounter)) {
            throw (new RuntimeException("Error: " + index_x));
        }
        if ((index_y < 0) || (index_y >= inputCounter)) {
            throw (new RuntimeException("Error: " + index_y));
        }
        return input[index_x][index_y];
    }

    double getOutput(int index_x, int index_y)
            throws RuntimeException {
        if ((index_x < 0) || (index_x >= trainingCounter)) {
            throw (new RuntimeException("Error: " + index_x));
        }
        if ((index_y < 0) || (index_x >= outputCounter)) {
            throw (new RuntimeException("Error: " + index_y));
        }
        return output[index_x][index_y];
    }

    void setInput(int index_x, int index_y, double value)
            throws RuntimeException {
        if ((index_x < 0) || (index_x >= trainingCounter)) {
            throw (new RuntimeException("Error: " + index_x));
        }
        if ((index_y < 0) || (index_y >= inputCounter)) {
            throw (new RuntimeException("Error: " + index_y));
        }
        input[index_x][index_y] = value;
    }

    void setOutput(int index_x, int index_y, double value)
            throws RuntimeException {
        if ((index_x < 0) || (index_x >= trainingCounter)) {
            throw (new RuntimeException("Error: " + index_x));
        }
        if ((index_y < 0) || (index_x >= outputCounter)) {
            throw (new RuntimeException("Error: " + index_y));
        }
        output[index_x][index_y] = value;
    }

    double[] getInputSet(int index_x)
            throws RuntimeException {
        if ((index_x < 0) || (index_x >= trainingCounter)) {
            throw (new RuntimeException("Error:" + index_x));
        }
        return input[index_x];
    }

    double[] getOutputSet(int index_x)
            throws RuntimeException {
        if ((index_x < 0) || (index_x >= trainingCounter)) {
            throw (new RuntimeException("Error:" + index_x));
        }
        return output[index_x];
    }
}
