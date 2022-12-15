package hibrido;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;

public class Rede implements Runnable {

    static final int PatternWidth = 9;
    static final int PatternHeight = 12;
    public DefaultListModel letterListModel = new DefaultListModel();
    public Kohonen net;
    public String nomeRede;
    Thread trainThread = null;
    private BufferedReader br;
    private String str;
    public static String ESPACIO = "W"; //WORD
    public static String ENTER = "E"; //ENTER

    public Rede() {
    }

    //aqui se carga el fichero ya procesado de la imagen a reconocer
    public void Reconocer() {
        if (net == null) {
            System.out.println("I need to be trained first!");
            return;
        }
        double input[] = new double[9 * 12];//Change the numbers
        int idx = 0;
        double normfac[] = new double[1];
        double synth[] = new double[1];
        File file;
        String cadena = "";
        int counter = 0;
        try {
            file = new File("./procesado.txt");//Change
            String line;
            char l = 'h';//Initialize with any character
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter("resultado.txt");//Change
            PrintWriter pw = new PrintWriter(fw);

            fr = new FileReader(file);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                counter++;
            }
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            //System.out.println("******************");
            br.readLine(); //el primer enter que no se usa , el -2 es por que al final hay otro enter de mas :((
            while ((line = br.readLine()) != null && idx < counter - 2) {
                if (line.equals(ESPACIO)) {
                    //  line=br.readLine();
                    cadena += " ";
                    continue;
                } else if (line.equals(ENTER)) {
                    // line=br.readLine();
                    cadena += "\n";
                    continue;

                } else {
                    for (int i = 0; i < line.length(); i++) {
                        l = line.charAt(i);
                        if (l == '1') {
                            input[i] = .5;
                        } else {
                            input[i] = -.5;
                        }

                    }

                    int best = net.winner(input, normfac, synth);
                    char map[] = mapNeurons();
                    pw.print(map[best]);
                    cadena += map[best];
                }
                idx++;
            }
            pw.close();

        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println(cadena);
    }

    public String Reconocer(int a) {
        if (net == null) {
            System.out.println("I need to be trained first!");
            return "?";
        }
        double input[] = new double[9 * 12];//Change the numbers
        int idx = 0;
        double normfac[] = new double[1];
        double synth[] = new double[1];
        File file;
        String cadena = "";
        int counter = 0;
        try {
            file = new File("./procesado.txt");//Change
            String line;
            char l = 'h';//Initialize with any character
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter("resultado.txt");//Change
            PrintWriter pw = new PrintWriter(fw);

            fr = new FileReader(file);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                counter++;
            }
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            //System.out.println("******************");
            br.readLine(); //el primer enter que no se usa , el -2 es por que al final hay otro enter de mas :((
            while ((line = br.readLine()) != null && idx < counter - 2) {
                if (line.equals(ESPACIO)) {
                    //  line=br.readLine();
                    cadena += " ";
                    continue;
                } else if (line.equals(ENTER)) {
                    // line=br.readLine();
                    cadena += "\n";
                    continue;

                } else {
                    for (int i = 0; i < line.length(); i++) {
                        l = line.charAt(i);
                        if (l == '1') {
                            input[i] = .5;
                        } else {
                            input[i] = -.5;
                        }

                    }

                    int best = net.winner(input, normfac, synth);
                    char map[] = mapNeurons();
                    pw.print(map[best]);
                    cadena += map[best];
                }
                idx++;
            }
            pw.close();

        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println(cadena);
        return cadena;
    }

    @Override
    public void run() {

        try {
            int inputNeuron = this.PatternHeight * this.PatternWidth;
            int outputNeuron = letterListModel.size();

            Treinamento t = new Treinamento(inputNeuron, outputNeuron);
            t.settrainingCounter(letterListModel.size());

            for (int i = 0; i < letterListModel.size(); i++) {
                int idx = 0;
                Padrao p = (Padrao) letterListModel.getElementAt(i);
                for (int y = 0; y < p.getHeight(); y++) {
                    for (int x = 0; x < p.getWidth(); x++) {
                        t.setInput(i, idx++, p.getData(x, y) ? .5 : -.5);
                    }
                }
            }
            net = new Kohonen(inputNeuron, outputNeuron);
            net.setTraining(t);
            net.learn();  //entrneamiento de la red clasica
            System.out.println("red entrenada");

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    char[] mapNeurons() {
        char map[] = new char[letterListModel.size()];
        double normfac[] = new double[1];
        double synth[] = new double[1];

        for (int i = 0; i < map.length; i++) {
            map[i] = '?';
        }
        for (int i = 0; i < letterListModel.size(); i++) {
            double input[] = new double[9 * 12];
            int idx = 0;
            Padrao p = (Padrao) letterListModel.getElementAt(i);
            for (int y = 0; y < p.getHeight(); y++) {
                for (int x = 0; x < p.getWidth(); x++) {
                    input[idx++] = p.getData(x, y) ? .5 : -.5;
                }
            }

            int best = net.winner(input, normfac, synth);
            map[best] = p.getitem();
        }
        return map;
    }

    public void train() {
        if (trainThread == null) {
            System.out.println(" train");
            trainThread = new Thread(this);
            trainThread.start();

        } else {
            net.halt = true;
        }
    }

    public void carregar_padroes() {
        try {
            //System.out.println("load patterns");
            FileReader f;// the actual file stream
            BufferedReader r;// used to read the file line by line
            f = new FileReader(new File("./sampleT1.dat"));//SAMPLESSAMPLE
            r = new BufferedReader(f);
            String line;
            int i = 0;
            letterListModel.clear();
            while ((line = r.readLine()) != null) {
                Padrao p = new Padrao(line.charAt(0), this.PatternWidth, this.PatternHeight);
                letterListModel.add(i++, p);
                int idx = 2;
                for (int y = 0; y < p.getHeight(); y++) {
                    for (int x = 0; x < p.getWidth(); x++) {
                        p.setData(x, y, line.charAt(idx++) == '1');
                    }
                }
            }

            r.close();
            f.close();

        } catch (Exception e) {

            System.out.println("Error: " + e);
        }
        //aunq repetitico mas rato lo arrglo.. cargando al Trinaing Set

        try {
            int inputNeuron = this.PatternHeight * this.PatternWidth;
            int outputNeuron = letterListModel.size();

            Treinamento t = new Treinamento(inputNeuron, outputNeuron);
            t.settrainingCounter(letterListModel.size());

            for (int i = 0; i < letterListModel.size(); i++) {
                int idx = 0;
                Padrao p = (Padrao) letterListModel.getElementAt(i);
                for (int y = 0; y < p.getHeight(); y++) {
                    for (int x = 0; x < p.getWidth(); x++) {
                        t.setInput(i, idx++, p.getData(x, y) ? .5 : -.5);
                    }
                }
            }
            net = new Kohonen(inputNeuron, outputNeuron);
            net.setTraining(t);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void computeOutputs() {
        net.computeOutputs();
    }

    public void reset() {
        net.clearWeights();
    }

    void fromArray(double[] matrix) {
        net.fromArray(matrix);
    }

    double[] toArray() {
        return net.toArray();
    }

    void calcError() {
        net.calcError();
    }

    public double getError(int len) {
        double err = Math.sqrt(net.totalError / (len * net.getOutputNeuronCounter()));
        net.totalError = 0;  // clear the accumulator
        System.out.println("err gettError " + err);
        return err;
    }

    public void guardarRed() {
        //guardar en fichero
        int filas = net.outputWeights.length;
        int columnas = net.outputWeights[0].length;
        try {
            FileWriter fichero = new FileWriter("./rede.dat");
            PrintWriter pw = new PrintWriter(fichero);
            pw.println(filas);
            pw.println(columnas);
            for (int i = 0; i < filas; i++) {

                for (int j = 0; j < columnas; j++) {
                    pw.print(net.outputWeights[i][j] + "\t");
                }
                pw.println();
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void cargarRed(String nomeRede) {
        int filas = 0, columnas = 0;
        StringTokenizer st = null;
        try {
            FileReader f;// the actual file stream
            BufferedReader r;// usado para leer el archivo linea por linea
            //    f = new FileReader( new File("./rede.dat") ); 
            f = new FileReader(new File("./" + nomeRede));
            r = new BufferedReader(f);
            String line;
            int iFilas = 0;
            filas = Integer.parseInt(line = r.readLine());
            columnas = Integer.parseInt(line = r.readLine());
            double nuevo[][] = new double[filas][columnas];

            //las 2 pprimeras lineas son info de la matriz de pesos
            int i = 0;
            while ((line = r.readLine()) != null) {
                st = new StringTokenizer(line, "\t");
                for (int j = 0; st.hasMoreTokens(); j++) {
                    nuevo[i][j] = Double.parseDouble(st.nextToken());
                }
                i++;
            }
            for (i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    net.outputWeights[i][j] = nuevo[i][j];

                }
            }
        } catch (IOException e) {
            System.out.println("Error -- " + e.toString());
        }

    }
}
