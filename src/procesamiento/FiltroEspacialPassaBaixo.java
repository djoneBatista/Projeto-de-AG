

package procesamiento;
import javax.media.jai.RenderedOp;
import javax.media.jai.*;
import java.awt.image.renderable.*;
import procesamiento.Dados;


public class FiltroEspacialPassaBaixo extends FiltroEspacial{
    
    /** Creates a new instance of FiltroEspacialPasoBajo */
    public FiltroEspacialPassaBaixo() {
        super();
    }
    public void aplicarFiltro(){
        
        //normalicemos k_media_3x3
        divisor=0f;
        for(int i=0;i< orden_filtro*orden_filtro;i++){
            divisor=divisor+ coeficientes_filtro[i];
        }
        
        if ( divisor > 0.0F ) {
            for ( int i = 0; i < orden_filtro*orden_filtro; i++ ) {
                coeficientes_filtro[i] = coeficientes_filtro[i] / divisor;
            }
        } else {
            divisor = 1.0F;
        }
        
        ParameterBlock bloque = new ParameterBlock();
        bloque.addSource(fuente);
        KernelJAI kernel =new KernelJAI(orden_filtro, orden_filtro,coeficientes_filtro);
        bloque.add(kernel);
        Dados.imagen_resultado = (RenderedOp)JAI.create("convolve", bloque,null);
        
    }
    
}
