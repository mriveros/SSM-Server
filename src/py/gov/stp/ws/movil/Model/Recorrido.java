package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class Recorrido {
	private String inicio;
	private String fin;
	private int duracion;
	private String usuario;
	private String fecha;
	private ArrayList<Ubicacion>tracking;
	
	public Recorrido() {
		super();
	}

	public String getInicio() {
		return inicio;
	}

	public void setInicio(String inicio) {
		this.inicio = inicio;
	}

	public String getFin() {
		return fin;
	}

	public void setFin(String fin) {
		this.fin = fin;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public ArrayList<Ubicacion> getTracking() {
		return tracking;
	}

	public void setTracking(ArrayList<Ubicacion> tracking) {
		this.tracking = tracking;
	}
}
