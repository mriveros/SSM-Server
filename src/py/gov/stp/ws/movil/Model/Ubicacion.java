package py.gov.stp.ws.movil.Model;

public class Ubicacion {
	
	private String longitud;
	private String latitud;
	private String altitud;
	private String precision;
	private String proveedor;
	private String horaobtenido;
	private String horaguardado;
	private String bateria;
	private String tipo;
	private String usuario;
	
	public Ubicacion() {
		super();
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

	public String getAltitud() {
		return altitud;
	}

	public void setAltitud(String altitud) {
		this.altitud = altitud;
	}

	public Double getPrecision() {
		return Double.parseDouble(precision);
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public String getHoraobtenido() {
		return horaobtenido;
	}

	public void setHoraobtenido(String horaobtenido) {
		this.horaobtenido = horaobtenido;
	}

	public String getHoraguardado() {
		return horaguardado;
	}

	public void setHoraguardado(String horaguardado) {
		this.horaguardado = horaguardado;
	}

	public double getBateria() {
		return Double.parseDouble(bateria);
	}

	public void setBateria(String bateria) {
		this.bateria = bateria;
	}

	public int getTipo() {
		return Integer.parseInt(tipo);
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
