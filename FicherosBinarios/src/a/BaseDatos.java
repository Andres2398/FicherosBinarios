package a;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BaseDatos {
	private static final int TAM_NOMBRE = 40;
	private static final int TAM_CURSO = 20;
	static final int TAM_REGISTRO = 4 + 4 + TAM_NOMBRE * 2 + 4 + TAM_CURSO * 2;
	Scanner sc = new Scanner(System.in);
	Map<Integer, String> cursos = new HashMap<>();
	Random r = new Random();

	private void alta(RandomAccessFile raf) throws IOException {
		
		int id=r.nextInt(1,101);
		while(!buscarIndicesID(id)) {
			 id=r.nextInt(1,101);
		}
		registrarId(id);
		 
		raf.seek(TAM_REGISTRO*(id-1)); 
		
		
		
		raf.writeInt(id); // id
		raf.writeInt(1); // activo

		System.out.println("Introduce el nombre del alumno:");
		String nombre = sc.nextLine();
		writeString(raf, nombre, TAM_NOMBRE);

		int edad = comprobaNumeros("la edad");
		raf.writeInt(edad);

		System.out.println("Introduce el curso:");
		String curso = sc.nextLine();
		mapearCurso(curso);
		writeString(raf, curso, TAM_CURSO);

		System.out.println("Alumno con ID " + id + " guardado correctamente.\n");
	}

	private void registrarId(int id) {
		File f = new File("IndicesID.txt");
		File fichero = new File(f.getAbsolutePath());
		
		
	}

	private void baja(RandomAccessFile raf) throws IOException {
		int id = comprobaNumeros("el id");
		raf.seek(0);

		while (raf.getFilePointer() < raf.length()) {
			long pos = raf.getFilePointer();
			int idLeido = raf.readInt();
			int activo = raf.readInt();

			if (idLeido == id && activo == 1) {
				raf.seek(pos + 4); // saltar id
				raf.writeInt(0); // marcar como inactivo
				System.out.println("Alumno " + id + " dado de baja correctamente.");
				return;
			} else {
				raf.seek(pos + TAM_REGISTRO);
			}
		}
		System.out.println("No se ha encontrado el alumno con id " + id);
	}

	private void modificarAlumno(RandomAccessFile raf) throws IOException {
		int id = comprobaNumeros("el id");
		raf.seek(0);
		boolean encontrado = false;

		while (raf.getFilePointer() < raf.length()) {
			long pos = raf.getFilePointer();
			int idLeido = raf.readInt();
			int activo = raf.readInt();

			if (id == idLeido && activo == 1) {
				encontrado = true;
				raf.seek(pos + 8); // saltar id + activo (8 bytes)

				if (cambiar("el nombre")) {
					System.out.println("Introduce el nuevo nombre:");
					String nombre = sc.nextLine();
					writeString(raf, nombre, TAM_NOMBRE);
				} else {
					raf.skipBytes(TAM_NOMBRE * 2);
				}

				if (cambiar("la edad")) {
					int edad = comprobaNumeros("la edad");
					raf.writeInt(edad);
				} else {
					raf.skipBytes(4);
				}

				if (cambiar("el curso")) {
					System.out.println("Introduce el nuevo curso:");
					String curso = sc.nextLine();
					writeString(raf, curso, TAM_CURSO);
				}

				System.out.println("Alumno con ID " + id + " modificado correctamente.");
				return;
			} else {
				// saltar al siguiente registro completo

				raf.seek(pos + TAM_REGISTRO);
			}
		}

		if (!encontrado)
			System.out.println("No se ha encontrado el alumno con id " + id);
	}

	private void mostrarTodos(RandomAccessFile raf) throws IOException {
		raf.seek(0);
		while (raf.getFilePointer() < raf.length()) {
			int id = raf.readInt();
			int activo = raf.readInt();
			String nombre = readString(raf, TAM_NOMBRE);
			int edad = raf.readInt();
			String curso = readString(raf, TAM_CURSO);

			if (activo == 1) {
				System.out.println("ID: " + id);
				System.out.println("Nombre: " + nombre);
				System.out.println("Edad: " + edad);
				System.out.println("Curso: " + curso);
				System.out.println("-----------------------------");
			}
		}
	}

	private void buscarAlumnoCurso(RandomAccessFile raf) throws IOException {
		if (preguntarBuscar()) {
			buscarCurso(raf);
		} else {
			buscarAlumno(raf);
		}

	}

	private void buscarCurso(RandomAccessFile raf) throws IOException {
		System.out.println("Introduce el curso a buscar");
		raf.seek(0);
		String curso = sc.nextLine();
		while (raf.getFilePointer() < raf.length()) {
			long pos = raf.getFilePointer();
			int id = raf.readInt();

			int activo = raf.readInt(); // leer activo
			if (activo == 1) {
				String nombre = readString(raf, TAM_NOMBRE).trim();
	            int edad = raf.readInt();
	            String cursoLeido = readString(raf, TAM_CURSO).trim();
				if (curso.equals(cursoLeido)) {
		
					System.out.println("ID: " + id);
					System.out.println("Nombre: " + nombre);
					System.out.println("Edad: " + edad);
					System.out.println("Curso: " + curso);
					System.out.println("-----------------------------");

				}
			} else {
				raf.seek(pos + TAM_REGISTRO);
			}

		}

	}

	private void buscarAlumno(RandomAccessFile raf) throws IOException {
		System.out.println("Introduce el nombre a buscar");
		raf.seek(0);
		String nombre = sc.nextLine();
		while (raf.getFilePointer() < raf.length()) {
			long pos = raf.getFilePointer();
			int id = raf.readInt();

			int activo = raf.readInt(); // leer activo
			if (activo == 1) {
				String nombreLeido = readString(raf, TAM_NOMBRE).trim();
	            int edad = raf.readInt();
	            String curso = readString(raf, TAM_CURSO).trim();
				if (curso.equals(nombreLeido)) {
		
					System.out.println("ID: " + id);
					System.out.println("Nombre: " + nombre);
					System.out.println("Edad: " + edad);
					System.out.println("Curso: " + curso);
					System.out.println("-----------------------------");

				}
			} else {
				raf.seek(pos + TAM_REGISTRO);
			}

		}

	}

	// --- Métodos auxiliares ---

	private boolean preguntarBuscar() {
		String input;
		do {
			System.out.println("Quieres buscar por curso o por Alumno");
			System.out.println("1. Curso");
			System.out.println("2. Alumno");
			input = sc.nextLine();
			System.out.println("input es :" +  input);
			if (!input.equals("1") && !input.equals("2")) {
				System.out.println("Caracter incorrecto, introduce 1 o 2.");
			}
		} while (!input.equals("1") && !input.equals("2"));

		return input.equals("1");
	}

	private boolean cambiar(String campo) {
		System.out.println("¿Quieres cambiar " + campo + " del alumno? (S/N)");
		String respuesta = sc.nextLine().trim();
		return respuesta.equalsIgnoreCase("s");
	}

	private void writeString(RandomAccessFile raf, String s, int tam) throws IOException {
		StringBuilder buffer = new StringBuilder(s);
		buffer.setLength(tam);
		raf.writeChars(buffer.toString());
	}

	private String readString(RandomAccessFile raf, int length) throws IOException {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append(raf.readChar());
		return sb.toString();
	}

	private int comprobaNumeros(String campo) {
		int valor = -1;
		boolean valido = false;
		while (!valido) {
			try {
				System.out.println("Introduce " + campo + ":");
				valor = Integer.parseInt(sc.nextLine());
				if (valor < 0 || valor > 100)
					throw new NumberFormatException();
				valido = true;
			} catch (NumberFormatException e) {
				System.out.println("Valor no válido, debe ser entre 0 y 100.");
			}
		}
		return valor;
	}

	private boolean buscarIndicesID(int id) throws FileNotFoundException, IOException {
		File f = new File("./src/indicesID.txt");
		
		
		try (RandomAccessFile raf = new RandomAccessFile(f, "rw")){
			
			if(raf.readInt()==id) {
				return false;
			}
			else {
				raf.seek(8);
			}
		}
		
		
		return true;
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
		return hash % 100;
	}

	// tester
	public static void main(String[] args) {
		File fichero = new File("./src/BaseDatos.dat");
		BaseDatos bd = new BaseDatos();

		try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
			boolean fin = false;

			while (!fin) {
				System.out.println("1. Alta alumno");
				System.out.println("2. Baja alumno");
				System.out.println("3. Modificar alumno");
				System.out.println("4. Mostrar todos");
				System.out.println("0. Salir");
				String input = bd.sc.nextLine();

				switch (input) {
				case "1":
					bd.alta(raf);
					break;
				case "2":
					bd.baja(raf);
					break;
				case "3":
					bd.modificarAlumno(raf);
					break;
				case "4":
					bd.buscarAlumnoCurso(raf);
					break;
				case "5":
					bd.mostrarTodos(raf);
					break;
				case "0":
					fin = true;
					break;
				default:
					System.out.println("Opción no válida.");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
