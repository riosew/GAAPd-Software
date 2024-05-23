package Launch;
import java.io.*;
import java.util.*;
public class printer {

    String archivo = "c:/out/out.txt";
    String archivoPromedios = "c:/out/outProm.txt";



    public void printout(IndMultiple ind, long lStartTime, int rescate) {
        PrintWriter pw = null;
        try {
            float pct = (ind.col100 * 100) / 40; //porcentaje
            long lEndTime = new Date().getTime();
            long difference = lEndTime - lStartTime;
            boolean append = true;
            pw = new PrintWriter(new FileWriter(new File(archivo), append));
            pw.println(ind.fitness + "," + ind.col100 + "," + "," + difference + "," + pct + "," + rescate);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }

    public void printout2(int cter, int corrida, IndMultiple ind, long lStartTime, int rescate, int numGen) {
        // para cada generacion: num de corrida - tiempo - fitness - col100 - col75 - num generacion - num rescate 
        // el tiempo que esto imprime es el consumido desde que inicia una corrida para cada una de las generaciones en ella
        // cuando surge una nueva corrida, se resetea la lectura de tiempo inicial

        PrintWriter pw = null;
        try {
            float pct = (ind.col100 * 100) / 40; //porcentaje
            long lEndTime = new Date().getTime();
            long difference = lEndTime - lStartTime;
            // created as a separate variable to emphasize that I'm appending to this file
            boolean append = true;
            pw = new PrintWriter(new FileWriter(new File(archivo), append));
            // imprime: fitness - columas al 100%  - columas al 75% - tiempo de ejecucion -  porcentaje de similitud - numero de rescates empleado

            pw.println("INSERT INTO `t_1000c`(`numGen`, `fitness`, `col100`, `col75`, `timems`, `pcnt`, `escenario`, `numgen`, `corrida`) VALUES (" + cter + "," + ind.fitness + "," + ind.col100 + "," + "," + difference + "," + pct + "," + rescate + "," + numGen + "," + corrida + ");");
        } catch (IOException e) {
            e.printStackTrace();
            // deal with the exception
        } finally {
            pw.close();
        }

    }

    public void printout3(int rpt, String path, long lStartTime, int NFE,float fitnessBest, boolean firstPrint) {
        // genera un archivo csv   

           String carpeta = String.valueOf(rpt);                                 //inserta el num de corrida para crear carpeta
        carpeta = path.substring(0, 8) + "\\run"+carpeta+"\\";
        
             File directorio = new File(carpeta);        //crea carpeta de corrida Run
                 if (!directorio.exists()) {
                     directorio.mkdirs(); }
        
        
        
        PrintWriter pw = null;
        String path2;
        path2 = carpeta;
        path2 = path2 + "\\gobal.csv";

        String fileName;
        fileName = path.substring(8, path.length());
//        fileName = fileName.substring(0, path.length()-3);
        
        try {

            long lEndTime = new Date().getTime();
            long difference = lEndTime - lStartTime;
            // created as a separate variable to emphasize that I'm appending to this file
            boolean append = true;
            pw = new PrintWriter(new FileWriter(new File(path2), append));
            if (firstPrint == true) {// para poner encabezados al imprimir la primer linea
                pw.println("File, Time, NFE, Fitness");

            }
            //encabezados:  Secuencia 1, Secuencia 2,Timepo, Porcentaje, Corrida,  NFE, Coincidencias

            pw.println(fileName + "," + difference + "," + NFE + ","+ fitnessBest);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }

    }

    public void printPromedios(Object Seq1, Object Seq2, float NFEProm, float MatchProm) {
        // genera un archivo csv   

        PrintWriter pw = null;
//        boolean firstPrint = true;
        try {

            boolean append = true;
            pw = new PrintWriter(new FileWriter(new File(archivoPromedios), append));

            pw.println(Seq1 + "," + Seq2 + "," + NFEProm + "," + MatchProm);
        } catch (IOException e) {
            e.printStackTrace();
            // deal with the exception
        } finally {
            pw.close();
        }

    }

    public void printout4(int rpt, IndMultiple ind, String path, ArrayList names) { //imprime archivo fasta por cada alineamiento benchmark
        PrintWriter pw = null;
        int pos = path.lastIndexOf("\\");
         
        String carpeta = String.valueOf(rpt);                                 //inserta el num de corrida para crear carpeta
        carpeta = "\\run"+carpeta+"\\";
        
             File directorio = new File(path.substring(0,pos)+carpeta);        //crea carpeta de corrida Run
                 if (!directorio.exists()) {
                     directorio.mkdirs(); }
        
        path = path.substring(0, pos) + carpeta + path.substring(pos);         //insrta
        path = path.substring(0, path.length() - 3);                           //ajusta la extencion a .fas
        path = path + "fas";
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.close();
        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        }

        try {
            boolean append = true;
            pw = new PrintWriter(new FileWriter(new File(path), append));
            Iterator itr = ind.Matrix.iterator();
            Iterator itn = names.iterator();
            while (itr.hasNext()) {                                                 //recorre matriz
                ArrayList seq = (ArrayList) itr.next();
                pw.println(itn.next());                                             // print name
                // pw.println("");                                                    
                Iterator itseq = seq.iterator();                                    //recorre seq
                while (itseq.hasNext()) {
                    pw.print(itseq.next());
                }
                pw.println("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }

    }


}
