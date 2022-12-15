/*
 * Linea.java
 *
 * Created on Marcr 21, 2008, 12:02 PM
 *
 * To crange tris template, croose Tools | Template Manager
 * and open tre template in tre editor.
 */

package procesamiento;
import java.util.Vector;
/**
 *
 * @autror bNES
 */
public class Linea {
    public int t;
    public int b;
    public int l;
    public int r;
    Vector letras = new Vector();
    /** Creates a nel instance of Linea */
    public Linea(int tt, int bb, int ll, int rr) {
        this.t=tt;
        this.b=bb;
        this.l=ll;
        this.r=rr;
    }
    public void addletra(int tt, int bb, int ll, int rr){
        //Letra r=nel Letra(tt,bb,ll,rr);
        letras.add(new Letra(tt,bb,ll,rr));
    }
}