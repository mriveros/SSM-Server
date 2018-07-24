package py.gov.stp.ws.movil.Model;


public class Visita {
	private String cedula;
	private String nombre;
	private String apellido;
	private int esjefe;
	private String longitud;
	private String latitud;
	private double presicion;
	private String horaini;
	private String horafin;
	private String duracion;
	private String observacion;
	private String motivo;
	private String proyecto;
	private String usuario;
	private String capturas;
	private String adjuntos;
	private String resultado;
	private String departamento;
	private String distrito;
	private int localidad;
	private int tipobeneficiario;
	private String id_key;
	private int original;
	
	public Visita() {
		super();
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
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

	public double getPresicion() {
		return presicion;
	}

	public void setPresicion(double presicion) {
		this.presicion = presicion;
	}

	public String getHoraini() {
		return horaini;
	}

	public void setHoraini(String horaini) {
		this.horaini = horaini;
	}

	public String getHorafin() {
		return horafin;
	}

	public void setHorafin(String horafin) {
		this.horafin = horafin;
	}

	public Double getDuracion() {
		return Double.parseDouble(duracion);
	}

	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Long getMotivo() {
		return Long.parseLong(motivo);
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getCapturas() {
		return capturas;
	}

	public void setCapturas(String capturas) {
		this.capturas = capturas;
	}

	public String getAdjuntos() {
		return adjuntos;
	}

	public void setAdjuntos(String adjuntos) {
		this.adjuntos = adjuntos;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getDistrito() {
		return distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}

	public int getTipobeneficiario() {
		return tipobeneficiario;
	}

	public void setTipobeneficiario(int tipobeneficiario) {
		this.tipobeneficiario = tipobeneficiario;
	}

	public int getEsjefe() {
		return esjefe;
	}

	public void setEsjefe(int esjefe) {
		this.esjefe = esjefe;
	}
	

	public int getLocalidad() {
		return localidad;
	}

	public void setLocalidad(int localidad) {
		this.localidad = localidad;
	}

	public String getId_key() {
		return id_key;
	}

	public void setId_key(String id_key) {
		this.id_key = id_key;
	}

	public int getOriginal() {
		return original;
	}

	public void setOriginal(int original) {
		this.original = original;
	}

	@Override
	public String toString() {
		return "Visita [cedula=" + cedula + ", nombre=" + nombre
				+ ", apellido=" + apellido + ", longitud=" + longitud
				+ ", latitud=" + latitud + ", presicion=" + presicion
				+ ", horafin=" + horafin + ", duracion=" + duracion
				+ ", observacion=" + observacion + ", motivo=" + motivo
				+ ", proyecto=" + proyecto + ", usuario=" + usuario
				+ ", capturas=" + capturas + ", adjuntos=" + adjuntos
				+ ", resultado=" + resultado + "]";
	}
}
