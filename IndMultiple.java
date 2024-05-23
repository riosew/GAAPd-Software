package Launch;

import java.util.*;
import java.util.Random;

class IndMultiple implements Comparable {

    ArrayList<ArrayList<Character>> Matrix = new ArrayList<ArrayList<Character>>();
    ArrayList<ArrayList<Character>> SubMatrixA = new ArrayList<ArrayList<Character>>();
    ArrayList<ArrayList<Character>> SubMatrixB = new ArrayList<ArrayList<Character>>();
    ArrayList<ArrayList<Character>> SubMatrixC = new ArrayList<ArrayList<Character>>();
    int indelcnt;

    float fitness;
    int col100;
    double hits;

    public int compareTo(Object o) {
        IndMultiple ind = (IndMultiple) o;
        int ret = 0;
        if (this.fitness > ind.fitness) {
            ret = -1;
        }
        if (this.fitness < ind.fitness) {
            ret = 1;
        }

        return ret;
    }

    public boolean autovalida(String org, String archBenchmark) throws CloneNotSupportedException {
        validador validador = new validador(clonaInd(this.Matrix), archBenchmark);
        validador.removeGaps();
        return validador.sonMismaseqs(org) == true;
    }



    public String convertArrayListToString2(ArrayList arrL) {
        String str = "";
        for (Object arrL1 : arrL) {                                         // arma sub seqs en string caracter por caracter para PD
            char oj = (Character)arrL1;
            if (oj != '-') str = str + oj;
        }
        return str;
    }

    public String convertArrayListToString(ArrayList arrL) {
        String str = "-";
        for (Object arrL1 : arrL) {                                         // arma sub seqs en string caracter por caracter para PD
            str = str + arrL1;
        }
        return str;
    }

    public ArrayList<Character> convertStringToArraylist(String str) {
        ArrayList<Character> ArrlstS = new ArrayList<Character>();
        for (int i = 0; i < str.length(); i++) {
            Character ch = str.charAt(i);
            ArrlstS.add(ch.charValue());
        }
        return (ArrayList<Character>) ArrlstS.clone();
    }

    public IndMultiple clonaInd(ArrayList Matriz) {
        IndMultiple indn = new IndMultiple();
        for (int i = 0; i < Matriz.size(); i++) {

            ArrayList seq = (ArrayList) Matriz.get(i);
            ArrayList sq = new ArrayList();
            for (int x = 0; x < seq.size(); x++) {
                Character tp = (Character) seq.get(x);
                Character cln = new Character(tp);
                sq.add(cln.charValue());

            }
            indn.Matrix.add((ArrayList<Character>) sq.clone());

        }
        return indn;
    }

    public void autoCompleta() throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {// esto inserta gaps al final de las secuancias hasta que se emparejen con la mas grande

        int mayor;
        Iterator iter1 = Matrix.iterator();
        Iterator iter2 = Matrix.iterator();
        int ctr = 0;

        ArrayList Mtx;
        mayor = 0;
        while (iter1.hasNext()) {  // encuentra el mayor
            Mtx = (ArrayList) iter1.next();
            if (Mtx.size() > mayor) {
                mayor = Mtx.size();
            }
        }
        while (iter2.hasNext()) {   // inserta gaps
            Mtx = (ArrayList) iter2.next();
            if (Mtx.size() < mayor) {
                while (Mtx.size() != mayor) {
                    Mtx.add('-');
                }  //while
            }      //if
        }      //while
        //elimina columnas de gap
        for (int isq = 0; isq < Matrix.get(0).size(); isq++) {// toma un caracter de la primer seq
            ctr = 0;
            char cr = (char) Matrix.get(0).get(isq);
            if (cr == '-') {
                for (int imx = 0; imx < Matrix.size(); imx++) { // compara cr (en isq) contra las correspondientes casillas en otras seqs
                    char ck = Matrix.get(imx).get(isq);
                    if (cr == ck) {

                        ctr++;
                    }
                }

                if (ctr == Matrix.size()) {
                    Iterator itM = Matrix.iterator();
                    while (itM.hasNext()) {   // borra el gap en cada seq
                        ArrayList seqx = (ArrayList) itM.next();
                        seqx.remove(isq);
                    }
                }
            }
        }

    }

    public void paralFitness(int nucleos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {
        this.autoCompleta();
        int indxI = 0;
        int offset = Matrix.get(0).size() / nucleos;
        System.out.println("nucleos: "+nucleos);
        int indxF = indxI + offset;
        ArrayList evaluadores = new ArrayList();
        ArrayList Seqx;
        this.indelcnt = 0;
        this.col100 = 0;
        this.fitness = 0;
        this.hits = 0;
        Class miclase = Class.forName("Launch.T_MicroEval");
        ArrayList aminoacidos = new ArrayList();
        aminoacidos.add('A');
        aminoacidos.add('R');
        aminoacidos.add('N');
        aminoacidos.add('D');
        aminoacidos.add('Q');
        aminoacidos.add('C');
        aminoacidos.add('E');
        aminoacidos.add('G');
        aminoacidos.add('H');
        aminoacidos.add('I');
        aminoacidos.add('L');
        aminoacidos.add('K');
        aminoacidos.add('M');
        aminoacidos.add('F');
        aminoacidos.add('P');
        aminoacidos.add('S');
        aminoacidos.add('T');
        aminoacidos.add('W');
        aminoacidos.add('Y');
        aminoacidos.add('V');

        for (int x = 0; x < nucleos; x = x + 1) {                                // crea evaluadores segun nucleos disponibles
            T_MicroEval T_Eval = (T_MicroEval) miclase.newInstance();
            for (int i = 0; i < Matrix.size(); i++) {                            //obtiene secuencias de la matriz
                Seqx = Matrix.get(i);
                Seqx = new ArrayList(Seqx.subList(indxI, indxF));
                T_Eval.SubMatrix.add(Seqx);
                T_Eval.aminoacidos = aminoacidos;
            }
            if ((indxF + offset) < Matrix.get(0).size()) {                       //recorre los index de corte
                indxI = indxI + offset;
                indxF = indxF + offset;
            } else {                                                 //asegura que no se salga del tamaño de la seq en el ultimo corte
                indxI = indxF;
                indxF = Matrix.get(0).size();
            }
            T_Eval.start();
            evaluadores.add(T_Eval);

        }
        boolean done;
        done = false;
        while (done == false) { //espera a todos los evaluadores
            for (int i = 0; i != evaluadores.size(); i++) {
                T_MicroEval evalx = (T_MicroEval) evaluadores.get(i);
                if (evalx.isAlive()) {
                    break;
                }
                if (i == evaluadores.size() - 1) {
                    done = true;
                }
            }
        }

        Iterator iteval = evaluadores.iterator();                                // recorre evaluadores sumando resultados
        while (iteval.hasNext()) {
            T_MicroEval MicroEvalx = (T_MicroEval) iteval.next();
            indelcnt = indelcnt + MicroEvalx.indelcnt;
            col100 = col100 + MicroEvalx.col100;
            hits = hits + MicroEvalx.hits;
        }
        evaluadores.clear();

        fitness = (float) (hits);
    }

    public void nuevoMultiple(ArrayList Matriz) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException { // crea un nuevo individuo con la matrix en una nueva instancia y sin mutacion
        for (int i = 0; i < Matriz.size(); i++) {
            ArrayList seq = (ArrayList) Matriz.get(i);
            ArrayList sq = new ArrayList();
            for (int x = 0; x < seq.size(); x++) {
                Character tp = (Character) seq.get(x);
                sq.add(tp.charValue());
            }
            this.Matrix.add((ArrayList<Character>) sq.clone());
        }

        this.autoCompleta();
    }



    public void variaGaps() throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {
        this.autoCompleta();
        Random rd = new Random();

        //int r = rd.nextInt(this.Matrix.size());
        for (int p =0; p< this.Matrix.size(); p++){
        int numGap = rd.nextInt(200);
        for (int i = 1; i < numGap; i++) {                                      //inserta gaps al comienzo
            this.Matrix.get(p).add(0, '-');
        }
        }

        int numGapp = rd.nextInt(300);                                            //Inserta gaps aleatorio
        for (int i = 1; i < numGapp; i++) {
            int pk = rd.nextInt(Matrix.size());
            int gp = rd.nextInt(Matrix.get(pk).size());
            for (int f = 1; f < 2; f++) {
                Matrix.get(pk).add(gp, '-');
            }

        }

        int numColumnas = rd.nextInt(100);
        for (int i = 1; i < numColumnas; i++) {
            Iterator correSeqs = Matrix.iterator();                                 //Crea columnas de gaps
            int posicion = rd.nextInt(Matrix.get(0).size()-1);
            while (correSeqs.hasNext()) {
                ArrayList sek = (ArrayList) correSeqs.next();
                if (posicion < sek.size()-1){
                    sek.add(posicion, '-');
                }
                
            }
        }

        int nume = rd.nextInt(300);
        for (int i = 1; i < nume; i++) {
            boolean done = false;
            while (done == false) {                                                  //remueve gaps aleatorio
                int k = rd.nextInt(Matrix.size());
                ArrayList suq = Matrix.get(k);
                int p = rd.nextInt(suq.size());
                if ((Character) suq.get(p) == '-') {
                    suq.remove(p);
                    done = true;
                }
            }
        }

    }

    public void Fragmentar(int cutA, int cutB) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {

        IndMultiple respaldo = this.clonaInd(this.Matrix);

        Iterator itMx = this.Matrix.iterator();
        int indx;
        int indxA = 0;
        int indxB = 0;
        this.SubMatrixA.clear();
        this.SubMatrixB.clear();
        this.SubMatrixC.clear();
        this.autoCompleta();
        while (itMx.hasNext()) {
            indx = 0;
            indxA = 0;
            indxB = 0;
            ArrayList seq = (ArrayList) itMx.next();
            for (int i = 0; i < seq.size(); i++) {

                if (indx == cutA) {
                    indxA = i;
                }
                if (indx == cutB) {
                    indxB = i;
                }
                if ((Character) seq.get(i) != '-') {  //recorre buscando los puntos de corte ignorando los gaps
                    indx++;
                }

            }

            ArrayList tmpA = new ArrayList();
            ArrayList tmpB = new ArrayList();
            ArrayList tmpC = new ArrayList();

            Iterator itA = new ArrayList(seq.subList(0, indxA)).iterator();
            Iterator itB = new ArrayList(seq.subList(indxA, indxB)).iterator();
            Iterator itC = new ArrayList(seq.subList(indxB, seq.size())).iterator();

            while (itA.hasNext()) {
                Character c = (Character) itA.next();
                tmpA.add(c);
            }

            while (itB.hasNext()) {
                Character c = (Character) itB.next();
                tmpB.add(c);
            }

            while (itC.hasNext()) {
                Character c = (Character) itC.next();
                tmpC.add(c);
            }
            this.SubMatrixA.add(tmpA);
            this.SubMatrixB.add(tmpB);
            this.SubMatrixC.add(tmpC);

        }
        this.Matrix = respaldo.Matrix;
    }



    public IndMultiple cruzaMultiple(IndMultiple padre, int cutmax, int tamanoBloqueGap) throws CloneNotSupportedException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        IndMultiple hijo = new IndMultiple();

        IndMultiple madre = new IndMultiple();
        madre = this.clonaInd(this.Matrix);
        padre = this.clonaInd(padre.Matrix);

        madre.autoCompleta();
        padre.autoCompleta();

        Random rnds = new Random();
        boolean done = false;
        int cutA = 0;
        int cutB = 0;

        while (done == false) {                 // asegura que los cuts sean validos
            cutA = rnds.nextInt(cutmax / 2);
            cutB = rnds.nextInt(cutmax);
            if ((cutA > 5) && cutB > (cutA + 5) && cutB < cutmax - 5) {
                done = true;
            }
        }

        padre.Fragmentar(cutA, cutB);
        madre.Fragmentar(cutA, cutB);

        //armar hijo
        Random rnb = new Random();
        boolean cr_1 = rnb.nextBoolean();

        int numseq = 0;

        for (int i = 0; i < padre.Matrix.size(); i++) {
            hijo.Matrix.add(new ArrayList());
        }

        Iterator itrH1 = hijo.Matrix.iterator();
        Iterator itrH2 = hijo.Matrix.iterator();
        Iterator itrH3 = hijo.Matrix.iterator();

        if (cr_1 == true) {                                 // arma hijo
            numseq = 0;
            while (itrH1.hasNext()) {
                ArrayList seq = (ArrayList) itrH1.next();
//                seq.addAll((Collection) padre.SubMatrixA.get(numseq).clone());
                Iterator itAP = padre.SubMatrixA.get(numseq).iterator();
                while (itAP.hasNext()) {
                    Character ct = (Character) itAP.next();
                    seq.add(ct.charValue());
                }
                numseq++;
            }
            numseq = 0;
            while (itrH2.hasNext()) {
                ArrayList seq = (ArrayList) itrH2.next();
//                seq.addAll((Collection) madre.SubMatrixB.get(numseq).clone());
                Iterator itBM = madre.SubMatrixB.get(numseq).iterator();
                while (itBM.hasNext()) {
                    Character ct = (Character) itBM.next();
                    seq.add(ct.charValue());
                }
                numseq++;
            }

            numseq = 0;
            while (itrH3.hasNext()) {
                ArrayList seq = (ArrayList) itrH3.next();
//                seq.addAll((Collection) padre.SubMatrixC.get(numseq).clone());
                Iterator itPC = padre.SubMatrixC.get(numseq).iterator();
                while (itPC.hasNext()) {
                    Character ct = (Character) itPC.next();
                    seq.add(ct.charValue());
                }
                numseq++;
            }

        } else {
            numseq = 0;
            while (itrH1.hasNext()) {
                ArrayList seq = (ArrayList) itrH1.next();
//                seq.addAll((Collection) madre.SubMatrixA.get(numseq).clone());
                Iterator itMA = madre.SubMatrixA.get(numseq).iterator();
                while (itMA.hasNext()) {
                    Character ct = (Character) itMA.next();
                    seq.add(ct.charValue());
                }
                numseq++;
            }
            numseq = 0;
            while (itrH2.hasNext()) {
                ArrayList seq = (ArrayList) itrH2.next();
//                seq.addAll((Collection) padre.SubMatrixB.get(numseq).clone());
                Iterator itPB = padre.SubMatrixB.get(numseq).iterator();
                while (itPB.hasNext()) {
                    Character ct = (Character) itPB.next();
                    seq.add(ct.charValue());
                }
                numseq++;
            }

            numseq = 0;
            while (itrH3.hasNext()) {
                ArrayList seq = (ArrayList) itrH3.next();
//                seq.addAll((Collection) madre.SubMatrixC.get(numseq).clone());
                Iterator itMC = madre.SubMatrixC.get(numseq).iterator();
                while (itMC.hasNext()) {
                    Character ct = (Character) itMC.next();
                    seq.add(ct.charValue());
                }
                numseq++;
            }

        }

        padre.SubMatrixA.clear();
        padre.SubMatrixB.clear();
        padre.SubMatrixC.clear();
        madre.SubMatrixA.clear();
        madre.SubMatrixB.clear();
        madre.SubMatrixC.clear();
        hijo.autoCompleta();

        IndMultiple hijoR = new IndMultiple();
        hijoR = hijo.clonaInd(hijo.Matrix);
        hijo = null;
        return hijoR;
    }
    

    public void printSol() {
        System.out.println("");
        Iterator itr = Matrix.iterator();
        while (itr.hasNext()) {
            ArrayList seq = (ArrayList) itr.next();
            System.out.println(seq);
        }

    }

    public void printftnss() {
    }

    public void AutoEval(int nucleos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {
        this.paralFitness(nucleos);
    }
}
