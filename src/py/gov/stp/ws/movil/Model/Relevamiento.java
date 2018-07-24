package py.gov.stp.ws.movil.Model;

public class Relevamiento {

	private long idrelevamiento;
	private String longitud;
	private String latitud;
	private String inicio;
	private String fin;
	private double duracion;
	private Motivos motivo;
	private String observacion;
	private int cantcapturas;
	private int cantadjuntos;
	private boolean hasformulario;
	
	public Relevamiento() {
		super();
	}

	public long getIdrelevamiento() {
		return idrelevamiento;
	}

	public void setIdrelevamiento(long idrelevamiento) {
		this.idrelevamiento = idrelevamiento;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
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

	public double getDuracion() {
		return duracion;
	}

	public void setDuracion(double duracion) {
		this.duracion = duracion;
	}

	public Motivos getMotivo() {
		return motivo;
	}

	public void setMotivo(Motivos motivo) {
		this.motivo = motivo;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public int getCantcapturas() {
		return cantcapturas;
	}

	public void setCantcapturas(int cantcapturas) {
		this.cantcapturas = cantcapturas;
	}

	public int getCantadjuntos() {
		return cantadjuntos;
	}

	public void setCantadjuntos(int cantadjuntos) {
		this.cantadjuntos = cantadjuntos;
	}

	public boolean isHasformulario() {
		return hasformulario;
	}

	public void setHasformulario(boolean hasformulario) {
		this.hasformulario = hasformulario;
	}
}
