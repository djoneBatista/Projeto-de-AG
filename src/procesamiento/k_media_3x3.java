
package procesamiento;

public class k_media_3x3 extends FiltroEspacialPassaBaixo{
    
    /** Creates a new instance of k_media_3x3 */
    public k_media_3x3() {
        super();
        //orden del filtro
        orden_filtro=3;
        //coeficientes del filtro
        coeficientes_filtro=new float[9]; 
        coeficientes_filtro[0]=1;
        coeficientes_filtro[1]=1;
        coeficientes_filtro[2]=1;
        coeficientes_filtro[3]=1;
        coeficientes_filtro[4]=1;
        coeficientes_filtro[5]=1;
        coeficientes_filtro[6]=1;
        coeficientes_filtro[7]=1;
        coeficientes_filtro[8]=1;

    }
    
}
