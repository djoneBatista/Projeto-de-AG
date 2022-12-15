
package procesamiento;
import java.util.Vector;

public class Listado {
    Vector listado = new Vector();
    /** Creates a new instance of Listado */
    public Listado(){
    }
    public void addLinea(int t, int b, int l, int r) {
        listado.add(new Linea(t,b,l,r));
    }
    
}
