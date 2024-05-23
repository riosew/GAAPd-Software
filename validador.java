package Launch;

import java.util.ArrayList;
import java.util.Iterator;
public class validador {

    ArrayList seqs;
    FastaFileConsole FR;
    IndMultiple ind;

    validador(IndMultiple ind, String archBenchmark) {
        seqs = new ArrayList();
        FR = new FastaFileConsole();
        this.ind = ind;
        FR.use(archBenchmark);
        seqs = FR.armaSeqs();
        FR.armaNames();
    }

    public void removeGaps() {
        Iterator itr = ind.Matrix.iterator();
        ArrayList seq = new ArrayList();
        while (itr.hasNext()) {

            seq = (ArrayList) itr.next();
            while (seq.contains('-')) {
                for (int i = 0; i < seq.size(); i++) {
                    if (seq.get(i).equals('-')) {
                        seq.remove(i);
                        break;
                    }
                }
            }
        }
    }

    private ArrayList<Character> convertStringToArraylist(String str) {
        ArrayList<Character> ArrlstS = new ArrayList<Character>();
        for (int i = 0; i < str.length(); i++) {
                ArrlstS.add(str.charAt(i));
        }
        return ArrlstS;
    }
    
    
   

    public boolean sonMismaseqs(String org) { // valida si las secuencias que entraron son las mismas que salieron
        boolean validado = false;
        Iterator itr1 = ind.Matrix.iterator();
        int idx = 0;
        while (itr1.hasNext()) {
            ArrayList seq1 = (ArrayList) itr1.next();
            if (seq1.equals(convertStringToArraylist((String) seqs.get(idx)))) {
                validado = true;
            } else {
                System.out.println("NO validada seq: *******************************" + idx);
                System.out.println(seq1);
                System.out.println(convertStringToArraylist((String) seqs.get(idx)));
                System.out.println(org);
                validado=false;
            }

            idx++;

        }
        this.FR=null;
        this.ind=null;
        this.seqs=null;
        itr1=null;
        if (validado){
        }else{
            System.out.println("no válido");
            System.out.println("");
        }
return validado;
    }

}
