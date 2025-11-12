package c;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Comprimir {
	
	
	 public static void main(String[] args) {
	        File ficheroOriginal = new File("./src/prueba/prueba");
	        File ficheroComprimido = new File("./src/prueba/comprimido");

	        try (FileReader lectorOri = new FileReader(ficheroOriginal);
	             FileWriter escritorCom = new FileWriter(ficheroComprimido)) {

	            int actual;
	            int anterior = -1;
	            int contador = 0;

	            while ((actual = lectorOri.read()) != -1) {
	                if (actual == anterior) {
	                    contador++;
	                } else {
	                    if (anterior != -1) {
	                        escribirComprimido(escritorCom, anterior, contador);
	                    }
	                    anterior = actual;
	                    contador = 1;
	                }
	            }

	            // Escribir el último grupo
	            if (anterior != -1) {
	                escribirComprimido(escritorCom, anterior, contador);
	            }

	            System.out.println("Archivo comprimido correctamente.");

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        descomprimir();
	    }

	    private static void escribirComprimido(FileWriter escritorCom, int letra, int contador) throws IOException {
	        
	        escritorCom.write(contador + "," + (char) letra + "\n");
	    }

	    private static void descomprimir() {
	        File ficheroComprimido = new File("./src/prueba/comprimido");
	        File ficheroDescomprimido = new File("./src/prueba/pruebaDescomprimida");

	        try (FileReader lectorCom = new FileReader(ficheroComprimido);
	             FileWriter escritorDes = new FileWriter(ficheroDescomprimido)) {

	            String cadena  = "";
	            int c;
	            while ((c = lectorCom.read()) != -1) {
	                char ch = (char) c;
	                if (ch == '\n') {
	                    procesarLinea(cadena, escritorDes);
	                    cadena="";
	                } else {
	                	cadena+=ch;
	                }
	            }
	            // Procesar la última línea si no acaba en ' ' 
	            if (cadena.length() > 0) {
	                procesarLinea(cadena, escritorDes);
	            }

	            System.out.println("Archivo descomprimido correctamente.");

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void procesarLinea(String linea, FileWriter escritorDes) throws IOException {
	       
	        int comaIndice=-1;
	        
	        
	        //Es posible que sea mayor a 9 la cantidad de caracteres siguientes por eso hay que buscar donde
	        //esta la coma
	        for (int i = 0; i < linea.length(); i++) {
				if(linea.charAt(i)==',')
					comaIndice=i;
			}

	        // Verificar que haya una coma
	        if (comaIndice != -1 && comaIndice < linea.length() - 1) {
	            
	            int contador = 0;
	            for (int i = 0; i < comaIndice; i++) {
	                char c = linea.charAt(i);
	                if (Character.isDigit(c)) {
	                    contador = Character.getNumericValue(c); 
	                }
	            }

	            
	            char letra = linea.charAt(comaIndice + 1);

	            // Escribir el carácter repetido "contador" veces
	            for (int i = 0; i < contador; i++) {
	                escritorDes.write(letra);
	            }
	        }
	    }

}
