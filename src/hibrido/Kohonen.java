package hibrido;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Kohonen extends Network {

    public double outputWeights[][];
    protected int learnMethod = 1;
    protected double learnRate = 0.5;
    protected double quitError = 0.1;
    protected int retries = 10000;
    protected double reduction = .99;
    public boolean halt = false;
    protected Treinamento train; 

    public Kohonen(int inputCount, int outputCount ) {
        int n; 
        totalError = 1.0;
        this.setInputNeuronCounter(inputCount);
        this.setOutputNeuronCounter(outputCount);
        this.outputWeights = new double[getOutputNeuronCounter()][getInputNeuronCounter() + 1];
        this.output = new double[getOutputNeuronCounter()];
    }

    public void setTraining(Treinamento t) {
        train = t;
    }

    public Treinamento getTraining() {
        return train;
    }

    public static void copyWeights(Kohonen dest, Kohonen orig) {
        for (int i = 0; i < orig.outputWeights.length; i++) {
            System.arraycopy(orig.outputWeights[i], 0, dest.outputWeights[i], 0,
                    orig.outputWeights[i].length);
        }
    }

    public void clearWeights() {
        totalError = 1.0;
        for (int y = 0; y < outputWeights.length; y++) {
            for (int x = 0; x < outputWeights[0].length; x++) {
                outputWeights[y][x] = 0;
            }
        }
    }

    void normalizeInput(final double input[], double normfac[], double synth[]) {
        double length, d;
        length = vectorLength(input);
        // just in case it gets too small
        if (length < 1.E-30) {
            length = 1.E-30;
        }
        normfac[0] = 1.0 / Math.sqrt(length);
        synth[0] = 0.0;
    }

    void normalizeWeight(double w[]) {
        int i;
        double len;
        len = vectorLength(w);
        // just incase it gets too small
        if (len < 1.E-30) {
            len = 1.E-30;
        }
        len = 1.0 / Math.sqrt(len);
        for (i = 0; i < getInputNeuronCounter(); i++) {
            w[i] *= len;
        }
        w[getInputNeuronCounter()] = 0;
    }

    @Override
    void trial(double input[]) {
        int i;
        double normfac[] = new double[1], synth[] = new double[1], optr[];
        normalizeInput(input, normfac, synth);
        for (i = 0; i < getOutputNeuronCounter(); i++) {
            optr = outputWeights[i];
            output[i] = dotProduct(input, optr) * normfac[0]
                    + synth[0] * optr[getInputNeuronCounter()];
            // Remap to bipolar (-1,1 to 0,1)
            output[i] = 0.5 * (output[i] + 1.0);
            // account for rounding
            if (output[i] > 1.0) {
                output[i] = 1.0;
            }
            if (output[i] < 0.0) {
                output[i] = 0.0;
            }
        }
    }

    public void computeOutputs() {

        int i, key, tset, iter, n_retry, nwts;
        int won[], winners;
        double work[], correc[][], rate, best_err, dptr[];
        double bigerr[] = new double[1];
        double bigcorr[] = new double[1];
        Kohonen bestnet;  // Preserve best here
        totalError = 1.0;
        for (tset = 0; tset < train.gettrainingCounter(); tset++) {
            dptr = train.getInputSet(tset);
            if (vectorLength(dptr) < 1.E-30) {
                throw (new RuntimeException("Error"));
            }

        }
        bestnet = new Kohonen(getInputNeuronCounter(), getOutputNeuronCounter());
        won = new int[getOutputNeuronCounter()];
        correc = new double[getOutputNeuronCounter()][getInputNeuronCounter() + 1];
        if (learnMethod == 0) {
            work = new double[getInputNeuronCounter() + 1];
        } else {
            work = null;
        }
        rate = learnRate;
        best_err = 1.e30;
// main loop:
        n_retry = 0;
        for (iter = 0;; iter++) {
            evaluateErrors(rate, learnMethod, won, bigerr, correc, work);
            totalError = bigerr[0];
            if (totalError < best_err) {
                best_err = totalError;
                copyWeights(bestnet, this);
            }

            winners = 0;
            for (i = 0; i < won.length; i++) {
                if (won[i] != 0) {
                    winners++;
                }
            }

            if (bigerr[0] < quitError) {
                break;
            }

            if ((winners < getOutputNeuronCounter())
                    && (winners < train.gettrainingCounter())) {
                forceWin(won);
                continue;
            }

            adjustWeights(rate, learnMethod, won, bigcorr, correc);
            if (halt) {
                break;
            }
            Thread.yield();

            if (rate > 0.01) {
                rate *= reduction;
            }
        }

// done
        copyWeights(this, bestnet);
        for (i = 0; i < getOutputNeuronCounter(); i++) {
            normalizeWeight(outputWeights[i]);
        }
        halt = true;
        n_retry++;
        salvaRede(); //salva melhor Rede
    }

    public int winner(double input[], double normfac[], double synth[]) {
        int i, win = 0;
        double biggest, optr[];
        normalizeInput(input, normfac, synth);  // Normalize input
        biggest = -1.E30;
        for (i = 0; i < getOutputNeuronCounter(); i++) {
            optr = outputWeights[i];
            output[i] = dotProduct(input, optr) * normfac[0]
                    + synth[0] * optr[getInputNeuronCounter()];
            // Remap to bipolar(-1,1 to 0,1)
            output[i] = 0.5 * (output[i] + 1.0);
            if (output[i] > biggest) {
                biggest = output[i];
                win = i;
            }
// account for rounding
            if (output[i] > 1.0) {
                output[i] = 1.0;
            }
            if (output[i] < 0.0) {
                output[i] = 0.0;
            }
        }
        return win;
    }

    public void evaluateErrors(double rate, int learn_method, int won[], double bigerr[], double correc[][], double work[]) throws RuntimeException {
        int best, size, tset;
        double dptr[], normfac[] = new double[1];
        double synth[] = new double[1], cptr[], wptr[], length, diff;
// reset correction and winner counts
        for (int y = 0; y < correc.length; y++) {
            for (int x = 0; x < correc[0].length; x++) {
                correc[y][x] = 0;
            }
        }

        for (int i = 0; i < won.length; i++) {
            won[i] = 0;
        }

        bigerr[0] = 0.0;
// loop through all training sets to determine correction
        for (tset = 0; tset < train.gettrainingCounter(); tset++) {
            dptr = train.getInputSet(tset);
            best = winner(dptr, normfac, synth);
            won[best]++;
            wptr = outputWeights[best];
            cptr = correc[best];
            length = 0.0;

            for (int i = 0; i < getInputNeuronCounter(); i++) {
                diff = dptr[i] * normfac[0] - wptr[i];
                length += diff * diff;
                if (learn_method != 0) {
                    cptr[i] += diff;
                } else {
                    work[i] = rate * dptr[i] * normfac[0] + wptr[i];
                }
            }
            diff = synth[0] - wptr[getInputNeuronCounter()];
            length += diff * diff;
            if (learn_method != 0) {
                cptr[getInputNeuronCounter()] += diff;
            } else {
                work[getInputNeuronCounter()] = rate * synth[0] + wptr[getInputNeuronCounter()];
            }

            if (length > bigerr[0]) {
                bigerr[0] = length;
            }

            if (learn_method == 0) {
                normalizeWeight(work);
                for (int i = 0; i <= getInputNeuronCounter(); i++) {
                    cptr[i] += work[i] - wptr[i];
                }
            }
        }
        bigerr[0] = Math.sqrt(bigerr[0]);
    }

    void adjustWeights(double rate, int learn_method, int won[], double bigcorr[], double correc[][]) {
        double corr, cptr[], wptr[], length, f;
        bigcorr[0] = 0.0;
        for (int i = 0; i < getOutputNeuronCounter(); i++) {
            if (won[i] == 0) {
                continue;
            }
            wptr = outputWeights[i];
            cptr = correc[i];
            f = 1.0 / (double) won[i];
            if (learn_method != 0) {
                f *= rate;
            }
            length = 0.0;
            for (int j = 0; j <= getInputNeuronCounter(); j++) {
                corr = f * cptr[j];
                wptr[j] += corr;
                length += corr * corr;
            }
            if (length > bigcorr[0]) {
                bigcorr[0] = length;
            }
        }
        // scale the correction
        bigcorr[0] = Math.sqrt(bigcorr[0]) / rate;
    }

    void forceWin(int won[]) throws RuntimeException {
        int i, tset, best, size, which = 0;
        double dptr[], normfac[] = new double[1];
        double synth[] = new double[1], dist, optr[];
        size = getInputNeuronCounter() + 1;
        dist = 1.E30;
        for (tset = 0; tset < train.gettrainingCounter(); tset++) {
            dptr = train.getInputSet(tset);
            best = winner(dptr, normfac, synth);
            if (output[best] < dist) {
                dist = output[best];
                which = tset;
            }
        }
        dptr = train.getInputSet(which);
        best = winner(dptr, normfac, synth);
        dist = -1.e30;
        i = getOutputNeuronCounter();
        while ((i--) > 0) {
            if (won[i] != 0) {
                continue;
            }
            if (output[i] > dist) {
                dist = output[i];
                which = i;
            }
        }
        optr = outputWeights[which];
        System.arraycopy(dptr, 0, optr, 0, dptr.length);
        optr[getInputNeuronCounter()] = synth[0] / normfac[0];
        normalizeWeight(optr);
    }

    @Override
    public void learn() throws RuntimeException {
        int i, key, tset, iter, n_retry, nwts;
        int won[], winners;
        double work[], correc[][], rate, best_err, dptr[];
        double bigerr[] = new double[1];
        double bigcorr[] = new double[1];
        Kohonen bestnet;  // Preserve best here
        totalError = 1.0;
        for (tset = 0; tset < train.gettrainingCounter(); tset++) {
            dptr = train.getInputSet(tset);
            if (vectorLength(dptr) < 1.E-30) {
                throw (new RuntimeException("Error"));
            }

        }
        bestnet = new Kohonen(getInputNeuronCounter(), getOutputNeuronCounter());
        won = new int[getOutputNeuronCounter()];
        correc = new double[getOutputNeuronCounter()][getInputNeuronCounter() + 1];
        if (learnMethod == 0) {
            work = new double[getInputNeuronCounter() + 1];
        } else {
            work = null;
        }
        rate = learnRate;
        initialize();
        best_err = 1.e30;
// main loop:
        n_retry = 0;
        for (iter = 0;; iter++) {
            evaluateErrors(rate, learnMethod, won, bigerr, correc, work);
            totalError = bigerr[0];
            if (totalError < best_err) {
                best_err = totalError;
                copyWeights(bestnet, this);
            }

            winners = 0;
            for (i = 0; i < won.length; i++) {
                if (won[i] != 0) {
                    winners++;
                }
            }

            if (bigerr[0] < quitError) {
                break;
            }

            if ((winners < getOutputNeuronCounter())
                    && (winners < train.gettrainingCounter())) {
                forceWin(won);
                continue;
            }

            adjustWeights(rate, learnMethod, won, bigcorr, correc);
            if (halt) {
                break;
            }
            Thread.yield();
            if (bigcorr[0] < 1E-5) {
                if (++n_retry > retries) {
                    break;
                }
                initialize();
                iter = -1;
                rate = learnRate;
                continue;
            }

            if (rate > 0.01) {
                rate *= reduction;
            }
        }

// done
        copyWeights(this, bestnet);
        for (i = 0; i < getOutputNeuronCounter(); i++) {
            normalizeWeight(outputWeights[i]);
        }
        halt = true;
        n_retry++;

    }

    public void calcError() {
        int i, key, tset, iter, n_retry, nwts;
        int won[], winners;
        double work[], correc[][], rate, best_err, dptr[];
        double bigerr[] = new double[1];
        double bigcorr[] = new double[1];
        Kohonen bestnet;  // Preserve best here
        totalError = 1.0;
        for (tset = 0; tset < train.gettrainingCounter(); tset++) {
            dptr = train.getInputSet(tset);
            if (vectorLength(dptr) < 1.E-30) {
                throw (new RuntimeException("Error"));
            }

        }
        bestnet = new Kohonen(getInputNeuronCounter(), getOutputNeuronCounter());
        won = new int[getOutputNeuronCounter()];
        correc = new double[getOutputNeuronCounter()][getInputNeuronCounter() + 1];
        if (learnMethod == 0) {
            work = new double[getInputNeuronCounter() + 1];
        } else {
            work = null;
        }
        rate = learnRate;
        //  initialize() ;
        best_err = 1.e30;
// main loop:
        n_retry = 0;
        for (iter = 0;; iter++) {
            evaluateErrors(rate, learnMethod, won, bigerr, correc, work);
            totalError = bigerr[0];
            if (totalError < best_err) {
                best_err = totalError;
                copyWeights(bestnet, this);
            }
            winners = 0;
            for (i = 0; i < won.length; i++) {
                if (won[i] != 0) {
                    winners++;
                }
            }

            if (bigerr[0] < quitError) {
                break;
            }

            if ((winners < getOutputNeuronCounter())
                    && (winners < train.gettrainingCounter())) {
                forceWin(won);
                continue;
            }

            adjustWeights(rate, learnMethod, won, bigcorr, correc);
            if (halt) {
                break;
            }
            Thread.yield();
            if (bigcorr[0] < 1E-5) {
                if (++n_retry > retries) {
                    break;
                }
                System.out.println("initialize Pior caso");
                initialize();
                iter = -1;
                rate = learnRate;
                continue;
            }

            if (rate > 0.01) {
                rate *= reduction;
            }
        }

        // done
        copyWeights(this, bestnet);
        for (i = 0; i < getOutputNeuronCounter(); i++) {
            normalizeWeight(outputWeights[i]);
        }
        halt = true;
        n_retry++;
        salvaRede();
    }

    public void initialize() {
        System.out.println("initialize() Kohonen");
        int i;
        double optr[];
        clearWeights();
        randomizeWeights(outputWeights);
        for (i = 0; i < getOutputNeuronCounter(); i++) {
            optr = outputWeights[i];
            normalizeWeight(optr);
        }
    }

    public double[] toArray() {
        //preenche a matriz de pesos nun array
        //this.outputWeights = new double[outputNeuronCounter][inputNeuronCounter+1];
        int aux = getOutputNeuronCounter() * (getInputNeuronCounter() + 1);
        double result[] = new double[aux];
        int k = 0;
        for (int i = 0; i < getOutputNeuronCounter(); i++) {
            for (int j = 0; j < getInputNeuronCounter() + 1; j++) {
                result[k++] = outputWeights[i][j];
            }
        }
        return result;
    }

    public void fromArray(double array[]) {
        int k = 0;
        for (int i = 0; i < getOutputNeuronCounter(); i++) {
            for (int j = 0; j < getInputNeuronCounter() + 1; j++) {
                outputWeights[i][j] = array[k];
            }
        }

    }

    @Override
    public double[][] guardarRed() {
        return outputWeights;
    }

    @Override
    public void carregarRede(double nuevo[][]) {
        for (int i = 0; i < outputWeights.length; i++) {
            for (int j = 0; j < outputWeights[0].length; j++) {
                outputWeights[i][j] = nuevo[i][j];
            }
        }

    }

    public void salvaRede() {
        int filas = outputWeights.length;
        int columnas = outputWeights[0].length;
        try {
            FileWriter fichero = new FileWriter("./red.dat");
            PrintWriter pw = new PrintWriter(fichero);
            pw.println(filas);
            pw.println(columnas);
            for (int i = 0; i < filas; i++) {

                for (int j = 0; j < columnas; j++) {
                    pw.print(outputWeights[i][j] + "\t");
                }
                pw.println();

            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
