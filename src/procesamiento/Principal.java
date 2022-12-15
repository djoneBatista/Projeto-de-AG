/*
 * Principal.java 
 */

package procesamiento;

import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.widget.DisplayJAI;
import hibrido.Rede;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author  djoneuspiano
 */
public class Principal extends javax.swing.JFrame {
    
    private RenderedOp fuente;
    private RenderedOp binarizado;
    private RenderedOp erode;
    private RenderedOp img;
    static final int DOWNSAMPLE_WIDTH = 9;
    static final int DOWNSAMPLE_HEIGHT = 12;
    Image est;
    Imagem entry;
    Amostra sample;
    DefaultListModel letterListModel = new DefaultListModel();
    DisplayJAI display_imagen_fuente=new DisplayJAI();
    
    
    
    //   RedKohonen net;
    
    protected Thread worker = null;
    
    public Graphics entryGraphics;
    /** Crea un nuevo formulario Principal */
    public Principal() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //myFrame.setExtendedState(myFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        entry = new Imagem(this,letterListModel);
        sample = new Amostra(DOWNSAMPLE_WIDTH,DOWNSAMPLE_HEIGHT);
        entry.setSample(sample);
        
     //   panelDerecho.setViewportView(entry);
        panelIzquierdo.setViewportView(entry);
        
    }
    
    /** Este metodo es llamado dentro del constructor para inicializar
     * el formulario.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        central = new javax.swing.JPanel();
        horizontal = new javax.swing.JSplitPane();
        Arriba = new javax.swing.JPanel();
        panelIzquierdo = new javax.swing.JScrollPane();
        Abajo = new javax.swing.JPanel();
        panelTexto = new javax.swing.JScrollPane();
        areaTexto = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        abrir = new javax.swing.JMenuItem();
        procesar = new javax.swing.JMenuItem();
        cerrar = new javax.swing.JMenuItem();
        menuReconocer = new javax.swing.JMenu();
        reconocer = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("OCR");
        central.setLayout(new java.awt.BorderLayout());

        horizontal.setDividerLocation(400);
        horizontal.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        Arriba.setLayout(new java.awt.BorderLayout());

        panelIzquierdo.setMinimumSize(new java.awt.Dimension(75, 75));
        panelIzquierdo.setPreferredSize(new java.awt.Dimension(250, 150));
        Arriba.add(panelIzquierdo, java.awt.BorderLayout.CENTER);

        horizontal.setLeftComponent(Arriba);

        Abajo.setLayout(new java.awt.BorderLayout());

        panelTexto.setPreferredSize(new java.awt.Dimension(150, 20));
        areaTexto.setColumns(10);
        areaTexto.setRows(5);
        areaTexto.setTabSize(4);
        panelTexto.setViewportView(areaTexto);

        Abajo.add(panelTexto, java.awt.BorderLayout.CENTER);

        horizontal.setRightComponent(Abajo);

        central.add(horizontal, java.awt.BorderLayout.CENTER);

        getContentPane().add(central, java.awt.BorderLayout.CENTER);

        menuArchivo.setMnemonic('A');
        menuArchivo.setText("Archivo");
        menuArchivo.setToolTipText("archivo de imagen escaneada");
        abrir.setMnemonic('A');
        abrir.setText("Abrir");
        abrir.setToolTipText("abrir imagen escaneada");
        abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirActionPerformed(evt);
            }
        });

        menuArchivo.add(abrir);

        procesar.setMnemonic('P');
        procesar.setText("Procesar");
        procesar.setToolTipText("procesar imagen");
        procesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                procesarActionPerformed(evt);
            }
        });

        menuArchivo.add(procesar);

        cerrar.setMnemonic('C');
        cerrar.setText("Cerrar");
        cerrar.setToolTipText("cerrar");
        cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarActionPerformed(evt);
            }
        });

        menuArchivo.add(cerrar);

        menuBar.add(menuArchivo);

        menuReconocer.setMnemonic('R');
        menuReconocer.setText("Reconocer");
        menuReconocer.setToolTipText("reconcer texto ");
        menuReconocer.setEnabled(false);
        reconocer.setText("Reconocer");
        reconocer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconocerActionPerformed(evt);
            }
        });

        menuReconocer.add(reconocer);

        menuBar.add(menuReconocer);

        setJMenuBar(menuBar);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-434)/2, (screenSize.height-421)/2, 434, 421);
    }// </editor-fold>//GEN-END:initComponents
    
    private void reconocerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconocerActionPerformed
//reconocer
        reconocer();
    }//GEN-LAST:event_reconocerActionPerformed
    
    private void cerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarActionPerformed
//cerrar
        System.exit(0);
    }//GEN-LAST:event_cerrarActionPerformed
    
    private void procesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procesarActionPerformed
// procesar imagen
        procesarImagen();
    }//GEN-LAST:event_procesarActionPerformed
    
    private void abrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirActionPerformed
// abrir
        limpiar(); 
        String  directorio="C:\\Users\\djoneuspiano\\Google Drive\\Disciplinas_de_2018\\Introdu��o aos Sistemas Evolutivos\\Trabalho_AG\\OCRs\\OCR\\";
        String path="";
        JFileChooser fc = new JFileChooser(directorio);
        int rpta = fc.showOpenDialog(this);
        if (rpta != JFileChooser.APPROVE_OPTION)return;
        File file = fc.getSelectedFile();
        path=fc.getSelectedFile().getPath();
        try {
            
            
            fuente=JAI.create("fileload",path); //lado derecho
            img=JAI.create("fileload",path); //lado izquierdo
            display_imagen_fuente.set(img);
            panelIzquierdo.getViewport().add(display_imagen_fuente,null);
            //////////////////////////filtro media 3x3////////////
            Dados.imagen_fuente=fuente;
            k_media_3x3 filtro=new k_media_3x3();
            filtro.aplicarFiltro();
            //pegar la imagen que ya debe estar convertida en Gris en datos
            // display_imagen_resultado.set(Datos.imagen_resultado);
            ////////////////////////////////////////////////////////
            ///////////////////////////////Binarizar //////////////////////////////////
            double corte =150;//nivel de gris de referencia para cortar
            
            
            ParameterBlockJAI pb = new ParameterBlockJAI("BandSelect");
            pb.setSource(Dados.imagen_resultado, 0);
            pb.setParameter("bandIndices",new int [] {0});
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
            
            erode= JAI.create("erode", pb2);
            ///////////////////////////////////////////////////////////
            ///////////////////////codificar en bmp/////////////////////
            String path2="./temp.bmp";
            try {
                BMPEncodeParam salida=new BMPEncodeParam();
                FileOutputStream os = new FileOutputStream(path2);
                ImageEncoder encoder = ImageCodec.createImageEncoder("BMP", os, salida);
                encoder.encode(erode);
                os.close();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }catch (IOException ee) {}
            ///////////////////////////////////////////////////////////////////
            File file2=new File(path2);
            est =ImageIO.read(file2);
            Rectangle rect= new Rectangle(est.getWidth(this),est.getHeight(this));
            
            
            entry.entryImage=est;
            entry.scrollRectToVisible(rect);
            Dimension area = new Dimension(est.getWidth(this),est.getHeight(this));
            entry.setPreferredSize(area);
            //   panelDerecho.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            
        } catch (IOException e) {
            System.out.println("Error -- " + e.toString());
        }
    }//GEN-LAST:event_abrirActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
    
    
    private void procesarImagen() {
        //realiza el procesamiento de la imagen
        entry.dividir_linea(entry.entryImage,false);
        String MENSAJE="Se han realizado el procesamiento de la imagen";
        JOptionPane.showMessageDialog(null, MENSAJE, "Mensaje",  JOptionPane.PLAIN_MESSAGE );
        menuReconocer.setEnabled(true);
    }
    
    private void reconocer() {        
        Rede main=new Rede(); //c
        main.carregar_padroes();
        main.cargarRed("./red.dat");
        main.Reconocer();
        areaTexto.setText(main.Reconocer(1));
        
    }
    
    private void abrirResultado() {
        try {
            FileReader f;// the actual file stream
            BufferedReader r;// usado para leer el archivo linea por linea
            StringBuffer buffer= new StringBuffer();
            f = new FileReader( new File("./resultados.txt") );
            r = new BufferedReader(f);
            String line;
            
            
            //las 2 pprimeras lineas son info de la matriz de pesos
            int i=0;
            while ( (line=r.readLine()) !=null ) {
                buffer.append(line);
            }
            //los muestro en el panelTexto
            areaTexto.setText(buffer.toString());
            
        } catch (IOException e) {
            System.out.println("Error -- " + e.toString());
        }
    }
    
    private void limpiar() {
        entry = new Imagem(this,letterListModel);
        sample = new Amostra(DOWNSAMPLE_WIDTH,DOWNSAMPLE_HEIGHT);
        entry.setSample(sample);
        
        //panelDerecho.setViewportView(entry);        
        panelIzquierdo.setViewportView(entry);        
        areaTexto.setText("");
        menuReconocer.setEnabled(false);//lo desactivo de neuvo
        
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Abajo;
    private javax.swing.JPanel Arriba;
    private javax.swing.JMenuItem abrir;
    private javax.swing.JTextArea areaTexto;
    private javax.swing.JPanel central;
    private javax.swing.JMenuItem cerrar;
    private javax.swing.JSplitPane horizontal;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuReconocer;
    private javax.swing.JScrollPane panelIzquierdo;
    private javax.swing.JScrollPane panelTexto;
    private javax.swing.JMenuItem procesar;
    private javax.swing.JMenuItem reconocer;
    // End of variables declaration//GEN-END:variables
    
}
