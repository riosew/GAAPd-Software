package Launch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FastaFileConsole 
{
     ArrayList<Secuencia> secuencias;
     
    public  void use(String path) {
        // TODO code application logic here
        ArchivoFasta af = new ArchivoFasta();
      this.secuencias = af.AbrirArchivo(path);
    }
    
        public ArrayList armaSeqs(){
            ArrayList seqs = new ArrayList();
            
            for(int x=0;x<secuencias.size();x++) {
      seqs.add((String)secuencias.get(x)._secuencia);
    } 
     return seqs;       
        }
        
       public ArrayList armaNames(){
            ArrayList names = new ArrayList();
            
            for(int x=0;x<secuencias.size();x++) {
      names.add((String)secuencias.get(x)._nombre);
    } 
     return names;       
        }
 
    public static class Secuencia
    {
        public String _nombre;
        public String _secuencia;
    }
    
    
    
    public static class ArchivoFasta
    {
        
        public ArrayList<Secuencia> secuencias;
        public ArrayList<Secuencia> AbrirArchivo(String ruta)
        {
            secuencias = new ArrayList<Secuencia>();
            int numero=1;
             try
            {
                BufferedReader in = new BufferedReader(new FileReader(ruta));
                String line = null;
                while ((line = in.readLine()) != null)
                {
                    int n=line.length();                    
                    if(n>0)
                    {
                        boolean i = line.startsWith(">");
                        if(i)
                        {
                                Secuencia _s=new Secuencia();
                                _s._nombre=line;
                                secuencias.add(_s);
                        }
                        else
                        { if (secuencias.get(secuencias.size()-1)._secuencia==null) secuencias.get(secuencias.size()-1)._secuencia=line;
                        else secuencias.get(secuencias.size()-1)._secuencia+=line;
                        }
                    }
                }
                         
                in.close();
            }
            catch(IOException ex)
            {
                System.out.println(numero);
                System.out.println(ex.getMessage());
            } 
             return secuencias;
        }
        public Secuencia BuscarCadena(int n)
        {
            if(n<Tamaño())
                return secuencias.get(n);
            else
                return null;
        }
        public int Tamaño()
        {
            return secuencias.size();
        }
    }
    
    
    
    

}
