/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.widget.DisplayJAI;
import hibrido.Rede;
import java.awt.Image;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.swing.DefaultListModel;
import procesamiento.Dados;
import procesamiento.Imagem;
import procesamiento.Amostra;
import procesamiento.k_media_3x3;

/**
 *
 * @author djoneuspiano
 */
public class Experimentos extends javax.swing.JFrame {

    private RenderedOp fuente;
    private RenderedOp binarizado;
    private RenderedOp erode;
    private RenderedOp img;
    final int DOWNSAMPLE_WIDTH = 9;
    final int DOWNSAMPLE_HEIGHT = 12;
    Image est;
    Imagem entry;
    Amostra sample;
    DefaultListModel letterListModel = new DefaultListModel();
    DisplayJAI display_imagen_fuente = new DisplayJAI();

    public void Teste1() throws IOException {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
        //String directorio = "/Volumes/GoogleDrive/Meu Drive/Trabalho_AG/OCRs/OCR/"
        //"C:\\Users\\djoneuspiano\\Google Drive\\Disciplinas_de_2018\\"
        //+ "Introdução aos Sistemas Evolutivos\\Trabalho_AG\\OCRs\\OCR\\"
        //+ "DatasetSinteticas/";
        //"DatasetMao\\";
       // String directorio = "C:\\Users\\djoneuspiano\\Downloads\\Projeto-de-AG-master\\DatasetMao\\";
          String directorio = "C:\\Users\\djoneuspiano\\Downloads\\Projeto-de-AG-master\\DatasetSinteticas\\";
          String letras[] = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"};

        int acerto[] = new int[letras.length]; // 26
        //abrir file 
        int num_amostras = 11;
        for (int j = 0; j < letras.length; j++) {
            for (int i = 1; i <= num_amostras; i++) {
                String path = directorio + letras[j] + i + ".JPG";
                abrirImagem(path);
                procesarImagen();
                String obtido = reconocer();

                if (letras[j].trim().compareTo(obtido) == 0) {
                    acerto[j]++;
                }
            }
            System.out.println("\n\n");
        }
        for (int i = 0; i < acerto.length; i++) {
            System.out.println(letras[i] + "\t" + acerto[i] * 100 / num_amostras + "%");
        }
    }

//    public void Teste2() throws IOException {
//        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
//        //String directorio = "/Volumes/GoogleDrive/Meu Drive/Trabalho_AG/OCRs/OCR/"
//        //"C:\\Users\\djoneuspiano\\Google Drive\\Disciplinas_de_2018\\"
//        //+ "Introdução aos Sistemas Evolutivos\\Trabalho_AG\\OCRs\\OCR\\"
//        //+ "DatasetSinteticas/";
//        //"DatasetMao\\";
//        String directorio = "C:\\Users\\djoneuspiano\\Downloads\\Projeto-de-AG-master\\DatasetMao\\";
//        String directorio = "C:\\Users\\djoneuspiano\\Downloads\\Projeto-de-AG-master\\DatasetSinteticas\\";
//
//        String letra = "A";
//
//        int acerto = 0;
//        //abrir file 
//        int num_amostras = 11;
//        for (int i = 1; i <= num_amostras; i++) {
//            String path = directorio + letra + i + ".JPG";
//            abrirImagem(path);
//            procesarImagen();
//            String obtido = reconocer();
//
//            if (letra.trim().compareTo(obtido) == 0) {
//                acerto++;
//            }
//        }
//        System.out.println("\n\n");
//
//        System.out.println(letra + "\t" + acerto * 100 / num_amostras + "%");
//    }

    public static void main(String[] args) throws IOException {

        Experimentos obj = new Experimentos();
        obj.Teste1();
        //obj.Teste2();

    }

    private void abrirImagem(String path) {
        fuente = JAI.create("fileload", path); //lado derecho
        img = JAI.create("fileload", path); //lado izquierdo 
        //////////////////////////filtro media 3x3////////////
        Dados.imagen_fuente = fuente;
        k_media_3x3 filtro = new k_media_3x3();
        filtro.aplicarFiltro();
        //pegar la imagen que ya debe estar convertida en Gris en datos
        // display_imagen_resultado.set(Datos.imagen_resultado);
        ////////////////////////////////////////////////////////
        ///////////////////////////////Binarizar //////////////////////////////////
        double corte = 150;//nivel de gris de referencia para cortar

        ParameterBlockJAI pb = new ParameterBlockJAI("BandSelect");
        pb.setSource(Dados.imagen_resultado, 0);
        pb.setParameter("bandIndices", new int[]{0});
        RenderedOp bandImage = JAI.create("BandSelect", pb);
        pb = new ParameterBlockJAI("Binarize");
        pb.setSource(bandImage, 0);
        pb.setParameter("threshold", corte);
        binarizado = JAI.create("Binarize", pb);
        ///////////////////////////////////////////////////////////////////
        ///////////////////////////erode/////////////////////////
        KernelJAI kernel = new KernelJAI(1, 1, new float[]{1});
        ParameterBlock pb2 = new ParameterBlock();
        pb2.addSource(binarizado);
        pb2.add(kernel);

        erode = JAI.create("erode", pb2);
        ///////////////////////////////////////////////////////////
        ///////////////////////codificar en bmp/////////////////////
        String path2 = "./temp.bmp";
        try {
            BMPEncodeParam salida = new BMPEncodeParam();
            FileOutputStream os = new FileOutputStream(path2);
            ImageEncoder encoder = ImageCodec.createImageEncoder("BMP", os, salida);
            encoder.encode(erode);
            os.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ee) {
        }
    }

    private void procesarImagen() throws IOException {
        //realiza o processamento de imagem
        String path2 = "./temp.bmp";
        entry = new Imagem(this, letterListModel);
        sample = new Amostra(DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);
        entry.setSample(sample);

        File file2 = new File(path2);
        est = ImageIO.read(file2);
        entry.entryImage = est;
        entry.setSample(sample);
        entry.dividir_linea(entry.entryImage, false);

    }

    private String reconocer() {

        Rede main = new Rede(); //c
        main.carregar_padroes();
        main.cargarRed("./red.dat");
        return main.Reconocer(1);

    }
}
