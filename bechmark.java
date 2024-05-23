package Launch;

import java.util.*;
import java.io.File;

public class bechmark {
public LinkedList listar  (String dir){
   String tipoArchivo = ".fas";                              // segun la carpeta BaliBase el tipo archivo puede ser tfa o in_tfa
   LinkedList< String > lista = new LinkedList< String >();
    File carpeta = new File(dir);
String[] listado = carpeta.list();
if (listado == null || listado.length == 0) {
    System.out.println("No hay elementos en carpeta (clase benchmark)");
}
else {
       for (String listado1 : listado) {
           if (listado1.endsWith(tipoArchivo)) {
               lista.add(listado1);
           }
       }
}
System.out.println("Clase benchmark: numero de archivos");
  System.out.println(lista.size());
return (lista);
}
}
