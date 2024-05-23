package Launch;

import java.util.*;
public class Generador {

    IndMultiple veryBest = new IndMultiple();
    ArrayList ListaMejores = new ArrayList();
    ArrayList<ArrayList<Character>> Matrix = new ArrayList<ArrayList<Character>>();
    int cutmax; // el tamaño de la seq mas corta
    int longmax; // tamaño de la seq mas grande
    ArrayList seqs = new ArrayList();
    ArrayList names = new ArrayList();

    Generador(String archBenchmark) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {
        FastaFileConsole FR = new FastaFileConsole();

        FR.use(archBenchmark);

        seqs.clear();
        names.clear();
        seqs = FR.armaSeqs();
        names = FR.armaNames();
        veryBest.fitness = 0;

        
        ArrayList seq0 = convertStringToArraylist((String) seqs.get(0)); //asigna cutmax al size de la primer seq
        cutmax = seq0.size();

        Iterator itr = seqs.iterator();                                         // asigna cutmax la longitud de la seq mas corta
        while (itr.hasNext()) {
            ArrayList seq = convertStringToArraylist((String) itr.next());
            this.Matrix.add((ArrayList<Character>) seq.clone());                 //arma la Matriz
            if (seq.size() < cutmax) {
                cutmax = seq.size();
            }
        }

        // encuentra la seq mas grande
        longmax = this.Matrix.get(0).size();
        Iterator itl = this.Matrix.iterator();
        while (itl.hasNext()) {
            ArrayList seq = (ArrayList) itl.next();
            if (seq.size() > longmax) {                                          // asigna longmax a la longitud de la seq mas larga
                longmax = seq.size();
            }
        }

//        seqs.clear();
        FR = null;
        //-------------------------------
        IndMultiple indM = new IndMultiple();
        indM.nuevoMultiple(Matrix);
//        autoSlider(indM);
    }


    public ArrayList<Character> convertStringToArraylist(String str) {
        ArrayList<Character> ArrlstS = new ArrayList<Character>();
        for (int i = 0; i < str.length(); i++) {
            Character ch = str.charAt(i);
            ArrlstS.add(ch.charValue());
        }
        return (ArrayList<Character>) ArrlstS.clone();
    }


    public boolean GuardaBest(ArrayList Pob, int fas, boolean enRescate) throws CloneNotSupportedException { // debe recibir una cadena ya ordenada. fas el el limite de estancamiento antes de abortar
        int l = 0; //usado para el num de best IndividuoParess que salen iguales (algoritmo estancado)
        float G = 0;
        IndMultiple indA = new IndMultiple();
        IndMultiple indB = new IndMultiple();

        IndMultiple indHueco = new IndMultiple();
        boolean brake = false;

        indA = (IndMultiple) Pob.get(0);

        if (veryBest.fitness == 0 || veryBest.fitness < indA.fitness) {
            veryBest = null;

            this.veryBest = indA.clonaInd(indA.Matrix);

            veryBest.fitness = indA.fitness;
        }

        indHueco.fitness = indA.fitness;
        this.ListaMejores.add(indHueco);
        System.out.println("best de Generacion: " + indA.fitness + " size: " + indA.Matrix.get(0).size() + " Lista mejores: " + ListaMejores.size());

        if (ListaMejores.size() > 25) {    //mantiene listamejores en tamaño manejable de esto depende tambn el FAS
            ListaMejores.subList(0, 10).clear();
        }
        float k = 0;

        G = (float) indA.fitness;
        if ((int) this.ListaMejores.size() > fas & !enRescate) { //asegura que listamejores tenga suficiente tamaño
            for (int u = 1; u < fas + 1; u++) {// cuenta el estancamiento

                indB = (IndMultiple) this.ListaMejores.get(this.ListaMejores.size() - u); //toma a los ultimos
                k = indB.fitness;// toma el fitness
                if (G == k || G < k) {                     //compara al ultimo con el penultimo
                    l++;                                        // si son iguales aumenta la cuenta de algortimo estancado
                } else {
                    l = 0;
                }                                       // si son diferentes reinicia la cuenta
            }//for
        }//if

        if (l >= fas) {

            brake = true;
            l = 0;//indique cuando el estacamiento limite es alcanzado para entrar en cambio de
        }          //escenario

        return brake;
    }

    public ArrayList Poblacion_Inic(int num_habitantes) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {
        IndMultiple[] Poblacion = new IndMultiple[num_habitantes];
        ArrayList PoblacionI = new ArrayList();
        Class miclase = Class.forName("Launch.IndMultiple");
        for (int x = 0; x < num_habitantes; x = x + 1) {        //Crea nuevas instancias de indMultiple
            IndMultiple miobjeto = (IndMultiple) miclase.newInstance();
            miobjeto.nuevoMultiple(this.Matrix);
            Poblacion[x] = miobjeto;
        }
        for (int x = 0; x < num_habitantes; x = x + 1) { //Asigna los individuos a una lista
            PoblacionI.add(Poblacion[x]);
        }
        Poblacion = null;
        return PoblacionI;
    }




    public void EvaluaTodos(ArrayList Poblacion, int nucleos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {
        Iterator iter = Poblacion.iterator();
        IndMultiple ind = new IndMultiple();
        while (iter.hasNext()) {
            ind = (IndMultiple) iter.next();
            ind.AutoEval(nucleos);
        }
    }

    public void EvaluaYordena(ArrayList Poblacion, int nucleos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException { // recibe una poblacion y hace que todos se autoevaluen y se ordenen
        this.EvaluaTodos(Poblacion, nucleos);
        Collections.sort(Poblacion);
    }

    public void EvaluaOrdenaTrunca(ArrayList Poblacion, int GenCtrl, int nucleos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException { // recibe una poblacion y hace que todos se autoevaluen y se ordenen
         Random rd = new Random();
        this.EvaluaYordena(Poblacion,nucleos);
        Poblacion.subList(GenCtrl+rd.nextInt(50), Poblacion.size()).clear();
    }

    //esta cruzar v2, obtiene al padre y madre aleatoriamente
    public ArrayList Cruzar(ArrayList Poblacion, int tamanoBloqueGap) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {
        ArrayList hijos = new ArrayList();
        boolean fond = false;
        Random ram1 = new Random();
        IndMultiple Padre = new IndMultiple();
        IndMultiple Madre = new IndMultiple();
        for (int h = 0; h < (Poblacion.size() / 2); h++) { //cada poblacion produce la mitad de su extencion en hijos

            while (fond == false) {
                Padre = (IndMultiple) Poblacion.get(ram1.nextInt(Poblacion.size()));    // obtiene un ind aleatorio
                Madre = (IndMultiple) Poblacion.get(ram1.nextInt(Poblacion.size()));    //existe la posibilidad de que alguno se cruce consigo mismo
                if (Madre.hashCode() != Padre.hashCode()) {
                    fond = true;
                }
            }
            hijos.add((Madre.clonaInd(Madre.Matrix).cruzaMultiple(Padre.clonaInd(Padre.Matrix), cutmax, tamanoBloqueGap)));
            fond = false;
        }

        ram1 = null; //destroy
        return hijos;
    }// Fin Cruza

    
 
    public ArrayList MutaGaps(ArrayList poblacion) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloneNotSupportedException {
        Random rnd = new Random();                                              //toma dos seq de la matriz
        Iterator iter = poblacion.iterator();
        while (iter.hasNext()) {
            int rd = rnd.nextInt(100); 
            if (rd >40 && rd < 60 ){                           //probabilidad de mutar en cada ind
            IndMultiple ix = (IndMultiple) iter.next();
            ix.variaGaps();
        }
        }
        return poblacion;
    }
    
    
    


}
