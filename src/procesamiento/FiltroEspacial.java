
package procesamiento;
import javax.media.jai.*;
import procesamiento.Dados;

public class FiltroEspacial {
    protected RenderedOp fuente;
    protected int orden_filtro;
     protected float divisor;
     protected float coeficientes_filtro[];

    /** Creates a new instance of FiltroEspacial */
    public FiltroEspacial() {
                    fuente=Dados.imagen_fuente;
    }
    
}
