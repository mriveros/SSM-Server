package py.gov.stp.ws.movil.Model;

import java.io.InputStream;

public class Captura {
	
	private String nombre;
	private String archivo;
	private String fecha;
	private int numero;
	private String encuestado;
	private long size;
	private InputStream inputStream;
	private boolean existe;
	private String hash;
	
	public Captura() {
		super();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getArchivo() {
		return archivo;
	}

	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getEncuestado() {
		return encuestado;
	}

	public void setEncuestado(String encuestado) {
		this.encuestado = encuestado;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public boolean isExiste() {
		return existe;
	}

	public void setExiste(boolean existe) {
		this.existe = existe;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return "Captura [nombre=" + nombre + ", archivo=" + archivo
				+ ", fecha=" + fecha + ", numero=" + numero + ", encuestado="
				+ encuestado + ", size=" + size + ", inputStream="
				+ inputStream.toString() + "]";
	}
}
