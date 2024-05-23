package Launch;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.JOptionPane;

public class Inicio {
    public void Iniciar(Interfaz Inter) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException, CloneNotSupportedException {
        long lStartTime;
        boolean firstPrint = true; // variable para imprimir encabezados de printout3, borrarla si se cambia de printout
        float NFEProm = 0; //promedios
        float MatchProm = 0;
        bechmark benchmark = new bechmark();
        String dir = "C:\\seqs" + "\\";                       //carpeta de trabajo 
        LinkedList< String> listaBenchmark = benchmark.listar(dir);

        for (String archBenchmark : listaBenchmark) {                                          //control de seqs benchmark
            archBenchmark = dir + archBenchmark;                                //archivo actual benchmark
            
            for (int rpt = 1; rpt <= Inter.Glob_NCorridas; rpt++) {                       // repetidor de corridas    
                Generador Gen = new Generador(archBenchmark);
                lStartTime = new Date().getTime();                                        //start time        
                printer Printer = new printer();
                int cter = 0;
                ArrayList PActual = new ArrayList();
                ArrayList crios = new ArrayList();
                ArrayList Phijos = new ArrayList();
                ArrayList Mutados = new ArrayList();
                IndMultiple ind1 = new IndMultiple();
                System.out.println("Numero de Secuencias:  " + Gen.Matrix.size());

                int NFE = 0; //contador de funciones evaluadas
                int Generaciones = 0; // cuenta de las generaciones
                boolean brake = false;
                int Ncruza = Inter.Inic_NCruza; // numero de cruzas que se hacen en una poblacion. Cada cruza es con diferentes padres
                int ciclosMutacion = Inter.Inic_CMut_Hijos; //vueltas que le da al proceso de mutacion de hijos. Es independiende de mutaInsert
                boolean enRescate = false; // avisa al contador de estancamiento en guardaBest, que hay un recate en curso, para que de tiempo de generar otro bloque (fas) de generaciones
                int guardaGen = 0; // toma el numero de generaciones al momento de iniciar un rescate
                int rescateHasta = 0; // establece el limite hasta el que el recate termina. es guardaGen + fas
                int meta = Inter.Glob_Meta; // el ciclo puede termianr cuando se acaban los rescates o cuando se alcanza la meta
                int tamanoBloqueGap = Inter.Inic_TBloqGap; // al mutar insertando, se genera un numero aleatorio de cero a tamanoBloqueGap, para el numero de gaps contiguos a insertar

                // PARAMETROS----------------
                int Pini = Inter.Glob_Pob_Inic;  //Poblacion inicial
                int GenCtrl = Inter.Glob_Pob_Limit; //trunca poblacion
                int fas = Inter.Glob_Fas; //limite de generaciones en estancamiento antes de sacudir
                int nucleos = Inter.Glob_Nucleos; //numero de nucleos para evaluacion
                // PARAMETROS----------------
                
                PActual = Gen.Poblacion_Inic(Pini);                                          //POBLACION INICIAL
                PActual = Gen.MutaGaps(PActual);
                NFE = PActual.size(); //contador de funciones evaluadas

                Gen.EvaluaOrdenaTrunca(PActual, GenCtrl,nucleos);                                    //EVULACION
                Gen.GuardaBest(PActual, fas, enRescate);
                while (!brake) {
                    Generaciones++;
                    System.out.println("Generación: " + Generaciones);
                    for (int j = 0; j < Ncruza; j++) {
                        crios = Gen.Cruzar(PActual, tamanoBloqueGap);                         //CRUZA
                        Phijos.addAll(crios);
                        crios.clear();
                    }

                    for (int M = 0; M < ciclosMutacion; M++) {                             // MUTACION
                        Mutados = Gen.MutaGaps(Phijos);
                    }

                    PActual.addAll(Mutados);
                    Mutados.clear();
                    Phijos.clear();

                    NFE = NFE + PActual.size(); //contador de funciones evaluadas

                    Gen.EvaluaOrdenaTrunca(PActual, GenCtrl, nucleos);  //  EVALUACION

                    if (enRescate & guardaGen < rescateHasta) { // cuenta las generaciones de gracia para rescatar
                        guardaGen++;
                    } else {
                        enRescate = false; // cuando se acaba el rescate, avisa a guardaBest para que cuente el estancamiento
                    }

                    brake = (Gen.GuardaBest(PActual, fas, enRescate));// guarda al mejor y calcula si se acab el fas y cuenta los rescates
                    cter++;

                    IndMultiple indi = (IndMultiple) PActual.get(0);
                    if (indi.col100 >= meta) { // detiene todo si se llega a la meta
                        brake = true;
                    }

                }// while de algoritmo genetico (while brake)
                System.out.println(" solución:  ");
                Gen.veryBest.printSol();
                Gen.veryBest.autovalida("veryBest", archBenchmark);
                
                Printer.printout3(rpt, archBenchmark, lStartTime, NFE, Gen.veryBest.fitness,firstPrint);             //imprime variables Globales (time, NFE)
                Printer.printout4(rpt, Gen.veryBest, archBenchmark, Gen.names);                  //imprime archivo de alineamiento en turno

                
                System.out.println(archBenchmark);
                System.out.println("NFE = " + NFE + " en la corrida " + rpt);               //imprime NFE
                System.out.println("Time = " + (new Date().getTime() - lStartTime));        //imprime Tiempo
                System.out.println("-------------------------------------");

              
                NFEProm = NFEProm + (float) NFE;      //casi siempre son 30 corridas en int rpt
                System.out.println("match count antes" + MatchProm);
                MatchProm = MatchProm + (float) ind1.col100;
                System.out.println("match count despues" + MatchProm);
                if (rpt == Inter.Glob_NCorridas) {
                    MatchProm = MatchProm / (float) Inter.Glob_NCorridas;
                    NFEProm = NFEProm / (float) Inter.Glob_NCorridas;
                    NFEProm = 0;
                    MatchProm = 0;
                }

                firstPrint = false; //bolean para imprimir los encabezados solo la primera vez
                PActual.clear();
                ind1 = null;
                NFE = 0;

                fas = 0;
                Generaciones = 0;
                guardaGen = 0;
                rescateHasta = 0;
                lStartTime =0;
                
                crios.clear();
                Phijos.clear();
                Gen.ListaMejores.clear();
                Gen.Matrix.clear();
                Gen.cutmax = 0;
                Gen.seqs.clear();
                Gen.veryBest = null;
                Gen = null;
                Mutados.clear();
                
            }                                                                         //for repeat corridas
        }                                                                               // control benchmark
        JOptionPane.showMessageDialog(null, "Alineamientos Generados");
    }
}
