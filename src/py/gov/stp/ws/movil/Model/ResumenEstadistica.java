package py.gov.stp.ws.movil.Model;

public class ResumenEstadistica {
	private String fecha;
	private int cantRelevamientos;
	
	public ResumenEstadistica(String fecha, int cantRelevamientos) {
		super();
		this.fecha = fecha;
		this.cantRelevamientos = cantRelevamientos;
	}

	public String getFecha() {
		return fecha;
	}

	public int getCantRelevamientos() {
		return cantRelevamientos;
	}
}
