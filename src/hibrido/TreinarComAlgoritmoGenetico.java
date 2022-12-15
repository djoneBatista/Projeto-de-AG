package hibrido;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
 
public final class TreinarComAlgoritmoGenetico implements Runnable {
 

    protected Thread worker = null;
    protected final static int NUM_INPUT = 9 * 12;//tamanho da imagem
    protected final static int NUM_OUTPUT = 1; 

    public static String ESPACO = "W"; //WORD
    public static String ENTER = "E"; //ENTER
 
    static final int NUM_AMOSTRAS = 108;
    protected Rede miRed;
    protected Treinamento train;

    Genetic genetic;

    protected double data[][] = new double[NUM_AMOSTRAS][NUM_INPUT];

    public TreinarComAlgoritmoGenetico() {

        miRed = new Rede();
        miRed.carregar_padroes();
        carregarInputGenetica();    
        operacao(); 
        //miRed.carregarRede("rede.dat");
        System.out.println("genetico !!"); 
        //Reconocer();
         

    }

    public static void main(String[] args) {

        TreinarComAlgoritmoGenetico main = new TreinarComAlgoritmoGenetico(); //con esto ya carga e treina ! 

    }

    public double[][] getGrid() {
        double array[][] = new double[NUM_AMOSTRAS][NUM_INPUT];

        for (int i = 0; i < NUM_AMOSTRAS; i++) {
            for (int j = 0; j < NUM_INPUT; j++) {
                array[i][j] = data[i][j];
            }
        }
        return array;
    }

    double[][] getIdeal() {
        double array[][] = new double[NUM_AMOSTRAS][1];
        int target = NUM_INPUT - 1; //9*12
        for (int i = 0; i < NUM_AMOSTRAS; i++) {
            array[i][0] = data[i][target];
        }
        return array;
    }

    public void operacao() {

        if (worker != null) {
            worker = null;
        }
        worker = new Thread(this);
        worker.setPriority(Thread.MIN_PRIORITY);
        worker.start();
    }

    @Override
    public void run() {
        System.out.println("run()");
        genetic();

    }

    protected void genetic() {
        double xorData[][] = getGrid();
        double xorIdeal[][] = getIdeal();
//        int update = 0;
        System.out.println("!Starting ...");
        genetic = new Genetic(miRed);
        genetic.setInput(xorData);
        genetic.setIdeal(xorIdeal);
        genetic.start();

        int x = 0;
        while (genetic.getGlobalError() > .27) { //fitness .27
            x++;
            genetic.generation();//aqui vai chamando para salvar as redes kohonen com melhor resultado
            System.out.println("Genetic Cycle = " + x + ",Error = " + genetic.getGlobalError());
        }
    }

    public void Reconocer() {
        if (miRed.net == null) {
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
            System.out.println("******************");
            br.readLine(); //el primer enter que no se usa , el -2 es por que al final hay otro enter de mas :((
            while ((line = br.readLine()) != null && idx < counter - 2) {
                if (line.equals(ESPACO)) {
                    line = br.readLine();
                    cadena += " ";
                } else if (line.equals(ENTER)) {
                    line = br.readLine();
                    cadena += "\n";
                } else {
                    for (int i = 0; i < line.length(); i++) {
                        l = line.charAt(i);
                        if (l == '1') {
                            input[i] = .5;
                        } else {
                            input[i] = -.5;
                        }

                    }
                }
                int best = miRed.net.winner(input, normfac, synth);
                char map[] = mapNeurons();
                pw.print(map[best]);
                cadena += map[best];
                idx++;
            }
            pw.close();

        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println(cadena);
    }

    char[] mapNeurons() {
        char map[] = new char[miRed.letterListModel.size()];
        double normfac[] = new double[1];
        double synth[] = new double[1];

        for (int i = 0; i < map.length; i++) {
            map[i] = '?';
        }
        for (int i = 0; i < miRed.letterListModel.size(); i++) {
            double input[] = new double[9 * 12];
            int idx = 0;
            Padrao p = (Padrao) miRed.letterListModel.getElementAt(i);
            for (int y = 0; y < p.getHeight(); y++) {
                for (int x = 0; x < p.getWidth(); x++) {
                    input[idx++] = p.getData(x, y) ? .5 : -.5;
                }
            }

            int best = miRed.net.winner(input, normfac, synth);
            map[best] = p.getitem();
        }
        return map;
    }

    private void carregarInputGenetica() {
        //data[][] = new double[NUM_AMOSTRAS][NUM_INPUT];
        double input[] = new double[9 * 12];//Change the numbers
//        int idx = 0;
//        double normfac[] = new double[1];
//        double synth[] = new double[1];
        File file;
        String cadena = "";
//        int counter = 0;
        try {
            file = new File("./sampleTT1.dat");//Change    //SAMPLESSAMPLE
            String line;
            char l = 'h';//Initialize with any character
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            fr = new FileReader(file);
            br = new BufferedReader(fr);
            int cont = 0;
            while ((line = br.readLine()) != null) {

                for (int i = 0; i < line.length(); i++) {
                    l = line.charAt(i);
                    if (l == '1') {
                        input[i] = .5;
                    } else {
                        input[i] = -.5;
                    }

                }
                //voy llenando en las muestras
                for (int j = 0; j < NUM_INPUT; j++) {
                    data[cont][j] = input[j];
                }
                cont++;

            }
        } catch (IOException e) {
            System.out.println(e);
        }

        //despues de q ya reconocio :d
        //put the text into the result
        //cadena reconocida
        System.out.println("******************");
        System.out.println(cadena);
    }

}
