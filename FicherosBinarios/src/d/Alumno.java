package d;

import java.util.Objects;

public class Alumno {
	int id;
	String nombre;
	int edad;
	String curso;

	public Alumno(int id, String nombre, int edad, String curso) {

		this.id = id;
		this.nombre = nombre;
		this.edad = edad;
		this.curso = curso;

	}

	@Override
	public int hashCode() {
		return Objects.hash(curso)%100;
	}


}
