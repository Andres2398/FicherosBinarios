package a;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BaseDatos {
	Scanner sc = new Scanner(System.in);
	List listaIndices = new LinkedList<Integer>();
	List listaCursos = new LinkedList<Integer>();
	Map<Integer, String> cursos = new HashMap<>();

	public static void main(String[] args) {

		File f = new File("./src/BaseDatos");
		File fichero = new File(f.getAbsolutePath());
		BaseDatos bd = new BaseDatos();

		try (FileOutputStream fileout = new FileOutputStream(fichero);
				DataOutputStream dataOS = new DataOutputStream(fileout)) {

			bd.alta(dataOS);
			bd.baja(fichero, dataOS);
			bd.modificar(fichero,dataOS);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void modificar(File fichero, DataOutputStream dataOS) {
		System.out.println("Introduce el id del alumno que quieres modificar");

		int id = comprobaNumeros("el id");
		
		try (FileInputStream fileint = new FileInputStream(fichero);
				DataInputStream dataIN = new DataInputStream(fileint)) {

			while (dataIN.read() != -1) {
				if (dataIN.readInt() == id) {
					if(cambiar("nombre"))
						
					
				}

			}

		} catch (Exception e) {
			
		}
		
	}

	private boolean cambiar(String string) {
		System.out.println("Quieres cambiar " +string+ " del alumno: (S/N)");
		
		return sc.nextLine().equals("s") || sc.nextLine().equals("S");
	}

	private void baja(File fichero, DataOutputStream dataOS) {

		System.out.println("Introduce el id del alumno a dar a baja");
		int id = comprobaNumeros("el id");
		boolean encontrado = false;
		try (FileInputStream fileint = new FileInputStream(fichero);
				DataInputStream dataIN = new DataInputStream(fileint)) {

			while (dataIN.read() != -1) {
				if (dataIN.readInt() == id) {
					dataOS.writeInt(0);
					System.out.println("Alumno con id " + id + " borrado correctamente");
				}

			}

		} catch (Exception e) {
			
		}

	}

	private void alta(DataOutputStream dataOS) throws IOException {

		/**
		 * Orden de entrdada a fichero
		 * 
		 * ID---> int
		 * 
		 * Activo----> int
		 * 
		 * nombre-----> String [40]
		 * 
		 * edad ----- > int
		 * 
		 * curso -----> String [20]
		 *
		 * 
		 * 
		 */

		// id
		int indice = contadorIndicesID();

		// activo
		int activo = 1;
		dataOS.writeInt(activo);

		// nombre
		System.out.println("Introduce el nombre del alumno");
		String nombre = sc.nextLine();
		StringBuffer buffer = new StringBuffer(nombre);
		buffer.setLength(40);
		dataOS.writeChars(buffer.toString());

		// edad
		int edad = comprobaNumeros("la edad");
		dataOS.write(edad);

		// curso
		System.out.println("Introduce el curso del alumno");
		String curso = sc.nextLine();

		mapearCurso(curso);
		buffer = new StringBuffer(curso);
		buffer.setLength(20);
		dataOS.writeChars(buffer.toString());

	}

	private void mapearCurso(String curso) {

		if (!cursos.containsValue(curso)) {
			int clave = hash(curso);
			cursos.put(clave, curso);
		}

	}

	private int hash(String curso) {
		int hash = 0;
		for (int i = 0; i < curso.length(); i++) {
			hash += curso.charAt(i);
		}
		return hash;

	}

	private int comprobaNumeros(String string) {
		boolean edadCorrecta = false;
		int edad = -1;
		do {

			System.out.println("Introduce " + string + " del alumno");
			String input = sc.nextLine();

			try {
				edad = Integer.parseInt(input);
				if (edad < 0 || edad > 100)
					throw new IllegalArgumentException();
				edadCorrecta = true;
			} catch (IllegalArgumentException e) {
				System.out.println("No se permite " + string + "es negativas, ni " + string + "es superiores a 100");
			} catch (Exception e) {
				System.out.println("Has introducido un caracter erroneo");
			}
		} while (!edadCorrecta);

		return edad;
	}

	private int contadorIndicesID() {
		int contador = 0;
		File fichero = new File("./src/indicesID.txt");

		try {
			// Se comprueba si existe el fichero para que en caso de que no exista,
			// inicializarlo con el indice 0
			if (!fichero.exists()) {
				try (FileWriter fw = new FileWriter(fichero)) {
					fw.write("0");
				}
			}

			try (Scanner sc = new Scanner(fichero)) {
				if (sc.hasNextInt()) {
					contador = sc.nextInt();
				}
			}

			contador++;
			try (FileWriter fw = new FileWriter(fichero)) {
				fw.write(String.valueOf(contador));
			}

		} catch (IOException e) {
			System.out.println(e);
		}

		return contador;
	}

	private int contadorIndicesCurso() {
		int contador = 0;
		File fichero = new File("./src/indicesID.txt");

		try {
			// Se comprueba si existe el fichero para que en caso de que no exista,
			// inicializarlo con el indice 0
			if (!fichero.exists()) {
				try (FileWriter fw = new FileWriter(fichero)) {
					fw.write("0");
				}
			}

			try (Scanner sc = new Scanner(fichero)) {
				if (sc.hasNextInt()) {
					contador = sc.nextInt();
				}
			}

			contador++;
			try (FileWriter fw = new FileWriter(fichero)) {
				fw.write(String.valueOf(contador));
			}

		} catch (IOException e) {
			System.out.println(e);
		}

		return contador;
	}

}
