package py.gov.stp.ws.movil.Model;

public class Marcacion {
	private String fecha;
	private Ubicacion entrada;
	private Ubicacion salida;
	
	public Marcacion() {
		super();
	}
	
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Ubicacion getEntrada() {
		return entrada;
	}
	public void setEntrada(Ubicacion entrada) {
		this.entrada = entrada;
	}
	public Ubicacion getSalida() {
		return salida;
	}
	public void setSalida(Ubicacion salida) {
		this.salida = salida;
	}
}
