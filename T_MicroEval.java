package Launch;

import java.util.ArrayList;
import java.util.Iterator;
public class T_MicroEval extends Thread {
    int col100;
    int indelcnt;
    double hits;
    ArrayList aminoacidos;
    ArrayList<ArrayList<Character>> SubMatrix = new ArrayList<ArrayList<Character>>();

    @Override
    public void run() {
        col100 = 0;
        indelcnt = 0;
        hits = 0;
        ArrayList vector = new ArrayList();                                     //arma lista con casillas de una columna
        for (int isq = 0; isq < SubMatrix.get(0).size(); isq++) {               // corre columnas derecha
            for (int imx = 0; imx < SubMatrix.size(); imx++) {                  // corre renglones abajo
                vector.add(SubMatrix.get(imx).get(isq));
            }
            Iterator columna = vector.iterator();                               //Blosum Score
            Iterator corre_columna = vector.iterator();
            while (columna.hasNext()){                                          //corre columna
                Character L1 = (Character) columna.next();          
                while (corre_columna.hasNext()){                                //sobre si misma
                Character L2 = (Character) corre_columna.next();
                if (aminoacidos.contains(L2)&& aminoacidos.contains(L1)){
                    int score = Blosum.getDistance(L1, L2);
                    if(score > 0){
                        hits= hits + Blosum.getDistance(L1, L2);                //hit
                    }
                }else if (L1 == '-' || L2 =='-'){
                    hits = hits-2;
                }                                                               //penalizacion
                }
            }
                vector.clear();
        }
        vector = null;
        SubMatrix=null;
    }
}
