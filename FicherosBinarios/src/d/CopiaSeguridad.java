package d;

import java.io.*;

public class CopiaSeguridad {

	private static final int MAX_DIFFS = 3;
	File copiaPrincipal = new File("copiaPrincipal.bin");
	File[] diffs = { new File("diff1.bin"), new File("diff2.bin"), new File("diff3.bin") };
	File restaurado = new File("restaurado.bin");
	int orden = 0;

	/** Crea archivo de diferencias entre copiaPrincipal y el original actual */
	public boolean crearDiferencias(File baseDatos) throws IOException {

		if (!copiaPrincipal.exists())
			return false;

		try (InputStream inBase = new BufferedInputStream(new FileInputStream(copiaPrincipal));
				InputStream inMod = new BufferedInputStream(new FileInputStream(baseDatos));
				DataOutputStream out = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(diffs[orden])))) {

			long offset = 0;
			int bBase = inBase.read();
			int bMod = inMod.read();

			while (bBase != -1 || bMod != -1) {
				if (bBase != bMod) {
					if (bMod != -1) {
						out.writeLong(offset);
						out.writeByte(bMod);
					}
				}
				offset++;
				if (bBase != -1)
					bBase = inBase.read();
				if (bMod != -1)
					bMod = inMod.read();
			}
		}
		
		orden++;
		if (orden > 2) {
			actualizarBase();
			orden = 0;
		}
		return true;
	}

	/** Aplica diferencias sobre base y genera un nuevo archivo */
	public boolean aplicarDiferencias() throws IOException {
		int diffaplicar = orden - 1;
		if (diffaplicar < 0)
			diffaplicar = 2;
		if (!copiaPrincipal.exists() || !diffs[diffaplicar].exists()) {
			return false;
		}

		try (InputStream inBase = new BufferedInputStream(new FileInputStream(copiaPrincipal));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(restaurado));
				DataInputStream inDiff = new DataInputStream(
						new BufferedInputStream(new FileInputStream(diffs[diffaplicar])))) {
			long offsetDiff = -1;
			int byteDiff = -1;
			boolean hayDiff = true;

			try {
				offsetDiff = inDiff.readLong();
				byteDiff = inDiff.readByte();
			} catch (EOFException e) {
				hayDiff = false;
			}

			long pos = 0;
			int bBase = inBase.read();

			while (bBase != -1) {
				if (hayDiff && pos == offsetDiff) {
					out.write(byteDiff);
					try {
						offsetDiff = inDiff.readLong();
						byteDiff = inDiff.readByte();
					} catch (EOFException e) {
						hayDiff = false;
					}
				} else {
					out.write(bBase);
				}
				pos++;
				bBase = inBase.read();
			}

			while (hayDiff) {
				if (pos == offsetDiff) {
					out.write(byteDiff);
					pos++;
					try {
						offsetDiff = inDiff.readLong();
						byteDiff = inDiff.readByte();
					} catch (EOFException e) {
						hayDiff = false;
					}
				} else {
					pos++;
				}
			}
		}
		return true;
	}

	/**
	 * Copia completa
	 * 
	 * @return
	 */
	public boolean copiarTodo(File origen) throws IOException {
		if (!copiaPrincipal.exists() || copiaPrincipal.length() == 0) {

			try (InputStream in = new FileInputStream(origen);
					OutputStream out = new FileOutputStream(copiaPrincipal)) {

				int c = in.read();
				while (c != -1) {
					out.write(c);
					c = in.read();
				}

			}
			return true;
		} else
			return false;

	}

	/** Aplica diff sobre la base y reemplaza la copia principal */
	private void actualizarBase() throws IOException {
		
		File temp = new File("nuevaBase.tmp");
		aplicarDiferencias(diffs[2], temp);
		
		
		try 
			(InputStream in = new BufferedInputStream(new FileInputStream(temp));
			OutputStream out = new BufferedOutputStream(new FileOutputStream(restaurado))) {
			int c=in.read();
			while(c!=-1) {
				out.write(c);
				c=in.read();
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		temp.delete();
		System.out.println("Actualizada la base copia principal");
	
	}

	private void aplicarDiferencias(File diff, File temp) throws FileNotFoundException, IOException {
		try (InputStream inBase = new BufferedInputStream(new FileInputStream(copiaPrincipal));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(restaurado));
				DataInputStream inDiff = new DataInputStream(
						new BufferedInputStream(new FileInputStream(diff)))) {
			long offsetDiff = -1;
			int byteDiff = -1;
			boolean hayDiff = true;

			try {
				offsetDiff = inDiff.readLong();
				byteDiff = inDiff.readByte();
			} catch (EOFException e) {
				hayDiff = false;
			}

			long pos = 0;
			int bBase = inBase.read();

			while (bBase != -1) {
				if (hayDiff && pos == offsetDiff) {
					out.write(byteDiff);
					try {
						offsetDiff = inDiff.readLong();
						byteDiff = inDiff.readByte();
					} catch (EOFException e) {
						hayDiff = false;
					}
				} else {
					out.write(bBase);
				}
				pos++;
				bBase = inBase.read();
			}

			while (hayDiff) {
				if (pos == offsetDiff) {
					out.write(byteDiff);
					pos++;
					try {
						offsetDiff = inDiff.readLong();
						byteDiff = inDiff.readByte();
					} catch (EOFException e) {
						hayDiff = false;
					}
				} else {
					pos++;
				}
			}
		}
		
	}
		
	
}
	
	