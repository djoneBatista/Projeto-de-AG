package procesamiento;
import javax.swing.*;
import java.awt.*; 
import java.awt.image.*; 
import java.io.*; 
import testes.Experimentos;


public class Imagem extends JPanel {
    //////////////////////////////////
    static final int DOWNSAMPLE_HEIGHT =12;
    static final int DOWNSAMPLE_WIDTH = 9;
    DefaultListModel letterListModel;
    protected Principal owner;
    protected Experimentos owner2;
    
    public Image entryImage;
    public Image ImagenReconocer;
    
    
    public Graphics entryGraphics;
    protected int lastX = -1;
    protected int lastY = -1;
    protected Amostra sample;
    protected int downSampleLeft;
    protected int downSampleRight;
    protected int downSampleTop;
    
    protected int downSampleBottom;
    public int NF=0;
    public int nl=0;
    public int topePintar=0;
    
    protected double ratioX;
    public String salidad;
//&&&    ////////esto le aumente
    public String ImaBin;
    /////////
    
    protected double ratioY;
    boolean pintar;
    
    protected int pixelMap[];
    
    Listado lista=new Listado();
    
    Imagem(Principal owner,DefaultListModel letterListModel) {
        lista.addLinea(0,0,0,0);
        pintar=false;
        sample = new Amostra(DOWNSAMPLE_WIDTH,DOWNSAMPLE_HEIGHT);
        this.owner = owner;
        this.letterListModel= letterListModel;
        salidad=" ";
//&&&    ////////esto le aumente
        ImaBin="\n";
///////////////////////
        
    }
    
     public   Imagem(Experimentos owner,DefaultListModel letterListModel) {
        lista.addLinea(0,0,0,0);
        pintar=false;
        sample = new Amostra(DOWNSAMPLE_WIDTH,DOWNSAMPLE_HEIGHT);
        this.owner2 = owner;
        this.letterListModel= letterListModel;
        salidad=" ";
//&&&    ////////esto le aumente
        ImaBin="\n";
///////////////////////
        
    }
    
    protected void initImage() {
        entryImage = createImage(getWidth(),getHeight());
        entryGraphics = entryImage.getGraphics();
        entryGraphics.setColor(Color.white);
        entryGraphics.fillRect(0,0,getWidth(),getHeight());
    }
    
    public void paint(Graphics g) {
        if ( entryImage==null )
            initImage();
        g.drawImage(entryImage,0,0,this);
        g.setColor(Color.white);
        g.drawRect(0,0,getWidth(),getHeight());
        g.setColor(Color.red);
        
        if(pintar){
            
            for (int i = topePintar+1; i <lista.listado.size(); i++) {
                
                g.drawRect(((Linea)lista.listado.get(i)).l,((Linea)lista.listado.get(i)).t,
                        ((Linea)lista.listado.get(i)).r-((Linea)lista.listado.get(i)).l+1,
                        ((Linea)lista.listado.get(i)).b-((Linea)lista.listado.get(i)).t+1);
                
                if(!((Linea)lista.listado.get(i)).letras.isEmpty()){
                    for (int j = 0; j < ((Linea)lista.listado.get(i)).letras.size(); j++) {
                        
                        g.drawRect( ((Letra)((Linea)lista.listado.get(i)).letras.get(j)).l,((Letra)((Linea)lista.listado.get(i)).letras.get(j)).t,
                                ((Letra)((Linea)lista.listado.get(i)).letras.get(j)).r-((Letra)((Linea)lista.listado.get(i)).letras.get(j)).l+1,
                                ((Letra)((Linea)lista.listado.get(i)).letras.get(j)).b-((Letra)((Linea)lista.listado.get(i)).letras.get(j)).t+1);
                    }
                }
                
            }
        }
        Dados.h=g;
    }
    public void borrarLineas(){
        pintar=false;
    }
    public void  borrarSalida(){
        NF=0;
        salidad=null;
        lastX = -1;
        lastY = -1;
        nl=0;
        topePintar=0;
        lista=new Listado();
    }
    public void setSample(Amostra s) {
        sample = s;
    }
    public Amostra getSample() {
        return sample;
    }
    
    protected boolean hLineClear(int w1,int y, int u) {
        int w = ImagenReconocer.getWidth(this)-2;
        for ( int i=u;i<w1;i++ ) {
            if ( pixelMap[(y*w)+i] !=-1 )
                return false;
        }
        return true;
    }
    protected boolean vLineClear(int x, int h,int t) {
        int w = ImagenReconocer.getWidth(this)-2;
        //int h = ImagenReconocer.getHeight(this);
        for ( int i=t;i<h;i++ ) {
            if ( pixelMap[(i*w)+x] !=-1 )
                return false;
        }
        return true;
    }
    protected void findBounds(int w,int h, int t, int nf,int u, int contLet) {
        int top=0;
        int botton=0;
        int left=0;
        int right=0;
        
        // linea tope
        
        for ( int y=t;y<h;y++ ) {
            if ( !hLineClear(w,y,u) ) {
                downSampleTop=y;
                top=y;
                break;
            }
            
        }
        // linea base
        for ( int y=h-1;y>=0;y-- ) {
            if ( !hLineClear(w,y,u) ) {
                downSampleBottom=y;
                botton=y;
                break;
            }
        }
        // limite izquierdo
        
        for ( int x=u;x<w;x++ ) {
            if ( !vLineClear(x,h,t) ) {
                downSampleLeft = x;
                left=x;
                break;
            }
        }
        
        // limite derecho
        for ( int x=w-1;x>=0;x-- ) {
            if ( !vLineClear(x,h,t) ) {
                downSampleRight = x;
                right=x;
                break;
            }
        }
        
        if(contLet==0){
            lista.addLinea(downSampleTop,downSampleBottom, downSampleLeft,downSampleRight);
        }else
            ((Linea)lista.listado.get(nf)).addletra(top,botton, left,right);
    }
    
    protected boolean downSampleQuadrant(int x,int y,int nf) {
        int w = ImagenReconocer.getWidth(this)-2;
        int startX = (int)(downSampleLeft+(x*ratioX));
        int startY = (int)(downSampleTop+(y*ratioY));
        int endX = (int)(startX + ratioX);
        int endY = (int)(startY + ratioY);
        
        for ( int yy=startY;yy<=endY;yy++ ) {
            for ( int xx=startX;xx<=endX;xx++ ) {
                int loc = xx+(yy*w);
                
                if ( pixelMap[ loc  ]!= -1 )
                    return true;
            }
        }
        
        return false;
    }
    
    public void downSample(int w, int h, int t, int nf,int u, int contLet,boolean a) {
        
        findBounds(w,h,t,nf,u,contLet);
        
        // hacemos downsample
        DadoAmostra data = sample.getData();
        
        ratioX = (double)(downSampleRight-
                downSampleLeft)/(double)data.getWidth();
        ratioY = (double)(downSampleBottom-
                downSampleTop)/(double)data.getHeight();
        
        for ( int y=0;y<data.getHeight();y++ ) {
            for ( int x=0;x<data.getWidth();x++ ) {
                if ( downSampleQuadrant(x,y,nf) ){
                    data.setData(x,y,true);
                    //&&&    ////////esto le aumente
                    ImaBin+="1";
//////////////////////////////
                } else{
                    data.setData(x,y,false);
                    //&&&    ////////esto le aumente
                    ImaBin+="0";
///////////////////////////////////////////
                }
                
            }
        }
//&&&    ////////esto le aumente
        ImaBin+="\n";
//&&&    ////////esto le aumente
        /*if(a==true){
            sample.repaint();
            repaint();
         
        }*/
    }
    
    public void dividir_linea(Image imag,boolean ima){
        pintar=(!ima);
        ImagenReconocer=imag;
        
        int w = ImagenReconocer.getWidth(this)-2;
        int h = ImagenReconocer.getHeight(this)-2;
        PixelGrabber grabber = new PixelGrabber(ImagenReconocer,1,1,w, h, true);
        
        try {
            
            grabber.grabPixels();
            pixelMap = (int[])grabber.getPixels();
            
        } catch ( InterruptedException e ) {
        }
        
        
        int r=0;//marca si encontro negro
        int t=0;//
        int y=0;
        for ( ;y<h;y++ ) {
            if ( !hLineClear(w,y,0)&&r==0 ) {//si es negro
                r=1; t=y;
                
            }else if (hLineClear(w,y,0)&&r==1){
                
                findBounds(w,y,t,NF,0,0);
                //downSample(w,y,t,NF);
                NF=NF+1;
                r=0;
                t=0;
                // break;
            }
            
        }
        for (int i = topePintar; i < NF; i++) {
            
            dividir_letras(w,((Linea)lista.listado.get(i+1)).b,((Linea)lista.listado.get(i+1)).t,ima);
            salidad+="\n";
//&&&    ////////esto le aumente
            ImaBin+="E"; //ENTER
            ImaBin+="\n";
///////////////////////////////
            
        }
        if(ima==true){
            topePintar=NF;
            escri_archiBin();
        }
//&&&    ////////esto le aumente
        escri_archiBin();  // para que escriba todo al txt despues de haber recorrido tooooda la imagen
///////////////////////////////////
    }
    public void dividir_letras(int w,int h, int t,boolean a){
        int cont=1; //contador de letras
        int x=0;
        int u=0;// de donde empieza el negra
        int r=0;// si encuetro negro
        double blanco=0; // para contar los espacios en blanco
        double blanco1=0; // para contar los espacios en blanco
        double blanco2=100; // para contar los espacios en blanco
        for ( ;x<w;x++ ) {
            if ( !vLineClear(x,h,t)&&r==0 ) {//si es negro
                r=1; u=x;
                //downSampleLeft[nf] = x;
            }else if (vLineClear(x,h,t)&&r==1){//para  los espacios en blanco
                
                
                blanco1=blanco2;
                
                blanco2=blanco;
                blanco=0;
                
                if (blanco2>2.99*blanco1){
                    salidad+=" ";
                    //&&&    ////////esto le aumente
                    ImaBin+="W"; //WORD
                    ImaBin+="\n";
/////////////////////////////////////
                    blanco2=blanco1;
                }
                downSample(x,h,t,NF,u,cont,a);
                nl=nl+1;
                cont=cont+1;
                r=0; u=0;
            }else if (vLineClear(x,h,t)&&r==0) {
                blanco+=1;
            }
        }
    }
    
    
//&&&    ////////esto le aumente
    public void escri_archiBin(){
        try {
            
            FileWriter fw = new FileWriter("procesado.txt"); //procesado
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.print(ImaBin);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
