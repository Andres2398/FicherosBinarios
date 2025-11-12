package b;



import java.io.IOException;
import java.io.RandomAccessFile;

public class NodoArbol {
    int id;          // clave primaria (ID)
    long posDato;    // posición del alumno en BaseDatos.dat
    long izq;        // posición del hijo izquierdo
    long der;        // posición del hijo derecho
    long posNodo;    // posición del propio nodo en arbolIndex.dat

    public static final int TAM_NODO = 4 + 8 + 8 + 8; // = 28 bytes

    public NodoArbol(int id, long posDato) {
        this.id = id;
        this.posDato = posDato;
        this.izq = -1;
        this.der = -1;
    }

    public NodoArbol() {}

    public void escribir(RandomAccessFile raf) throws IOException {
        raf.writeInt(id);
        raf.writeLong(posDato);
        raf.writeLong(izq);
        raf.writeLong(der);
    }

    public void leer(RandomAccessFile raf) throws IOException {
        id = raf.readInt();
        posDato = raf.readLong();
        izq = raf.readLong();
        der = raf.readLong();
    }
}
