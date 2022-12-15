package hibrido;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/*
 * ENTRENAR LA RED SIN USAR EL ALGORITMO GENETICO
 * Created on 4 de septiembre de 2008, 10:52 PM
 */

/**
 *
 * @author djoneuspiano
 */
public class TestTrainKohonen {
    
    /**
     * Constructor de TestTrainKohonen
     */
    public TestTrainKohonen() {
    }
    public static void Main(String[] args) {
        Rede main=new Rede(); //c
        main.carregar_padroes();
        main.train();
        main.guardarRed();
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        try {
            
            int s1 = Integer.parseInt(br.readLine());
            main.Reconocer();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
      //  main.guardarRed();
    }
    
    
}
