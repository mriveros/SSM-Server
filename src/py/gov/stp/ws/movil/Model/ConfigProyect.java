package py.gov.stp.ws.movil.Model;

public class ConfigProyect {
	private int min_imagenes;
	private int altas_destinatarios;
	private String entidad_relevar;
	
	public ConfigProyect(int min_imagenes, int altas_destinatarios) {
		super();
		this.min_imagenes = min_imagenes;
		this.altas_destinatarios = altas_destinatarios;
	}
	
	public ConfigProyect(int min_imagenes, int altas_destinatarios,String entidad_relevar) {
		super();
		this.min_imagenes = min_imagenes;
		this.altas_destinatarios = altas_destinatarios;
		this.entidad_relevar = entidad_relevar;
	}

	public int getMin_imagenes() {
		return min_imagenes;
	}

	public int getAltas_destinatarios() {
		return altas_destinatarios;
	}
	
	public String getEntidad_relevar() {
		return entidad_relevar;
	}

	@Override
	public String toString() {
		return "ConfigProyect [min_imagenes=" + min_imagenes
				+ ", altas_destinatarios=" + altas_destinatarios + "]";
	}
}
