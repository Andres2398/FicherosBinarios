package b;

import java.io.*;

public class ArbolBinario {
	private RandomAccessFile raf;
	private long raiz = -1;

	public ArbolBinario(String nombreArchivo) throws IOException {
		raf = new RandomAccessFile(nombreArchivo, "rw");
		if (raf.length() > 0) {
			raf.seek(0);
			raiz = raf.readLong();
		} else {
			raf.seek(0);
			raf.writeLong(-1); // puntero raíz vacío
		}
	}

	public void insertar(int id, long posDato) throws IOException {
		NodoArbol nuevo = new NodoArbol(id, posDato);
		nuevo.posNodo = raf.length();

		if (raiz == -1) {
			raiz = nuevo.posNodo;
			raf.seek(0);
			raf.writeLong(raiz);
			raf.seek(nuevo.posNodo);
			nuevo.escribir(raf);
			return;
		}

		insertarRec(raiz, nuevo);
	}

	private void insertarRec(long posActual, NodoArbol nuevo) throws IOException {
		NodoArbol actual = leerNodo(posActual);

		if (nuevo.id < actual.id) {
			if (actual.izq == -1) {
				actual.izq = nuevo.posNodo;
				escribirNodo(actual);
				raf.seek(nuevo.posNodo);
				nuevo.escribir(raf);
			} else {
				insertarRec(actual.izq, nuevo);
			}
		} else {
			if (actual.der == -1) {
				actual.der = nuevo.posNodo;
				escribirNodo(actual);
				raf.seek(nuevo.posNodo);
				nuevo.escribir(raf);
			} else {
				insertarRec(actual.der, nuevo);
			}
		}
	}

	public long buscar(int id) throws IOException {
		return buscarRec(raiz, id);
	}

	private long buscarRec(long posActual, int id) throws IOException {
		if (posActual == -1)
			return -1;
		
		NodoArbol actual = leerNodo(posActual);

		if (id == actual.id)
			return actual.posDato;
		if (id < actual.id)
			return buscarRec(actual.izq, id);
		else
			return buscarRec(actual.der, id);
	}

	private NodoArbol leerNodo(long pos) throws IOException {
		raf.seek(pos);
		NodoArbol n = new NodoArbol();
		n.posNodo = pos;
		n.leer(raf);
		return n;
	}

	private void escribirNodo(NodoArbol n) throws IOException {
		raf.seek(n.posNodo);
		n.escribir(raf);
	}

	public void cerrar() throws IOException {
		raf.seek(0);
		raf.writeLong(raiz);
		raf.close();
	}
}
