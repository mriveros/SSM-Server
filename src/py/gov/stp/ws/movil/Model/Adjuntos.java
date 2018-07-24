package py.gov.stp.ws.movil.Model;

public class Adjuntos {
	private String nombre;
	private String archivo;
	private String fecha;
	private String encuestado;
	
	public Adjuntos() {
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

	public String getEncuestado() {
		return encuestado;
	}

	public void setEncuestado(String encuestado) {
		this.encuestado = encuestado;
	}
}
