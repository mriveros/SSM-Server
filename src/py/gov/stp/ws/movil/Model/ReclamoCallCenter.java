package py.gov.stp.ws.movil.Model;

public class ReclamoCallCenter {
	
	private String nombre;
	private String cedula;
	private int nrocarnet;
	private String telefono;
	private String localidad;
	private String centroSalud;
	private String reclamo;
	private String fechareclamo;
	
	public ReclamoCallCenter() {
		super();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNrocarnet() {
		return nrocarnet;
	}

	public void setNrocarnet(int nrocarnet) {
		this.nrocarnet = nrocarnet;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getCentroSalud() {
		return centroSalud;
	}

	public void setCentroSalud(String centroSalud) {
		this.centroSalud = centroSalud;
	}

	public String getReclamo() {
		return reclamo;
	}

	public void setReclamo(String reclamo) {
		this.reclamo = reclamo;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getFechareclamo() {
		return fechareclamo;
	}

	public void setFechareclamo(String fechareclamo) {
		this.fechareclamo = fechareclamo;
	}

	@Override
	public String toString() {
		return "ReclamoCallCenter [nombre=" + nombre + ", nrocarnet="
				+ nrocarnet + ", telefono=" + telefono + ", localidad="
				+ localidad + ", centroSalud=" + centroSalud + ", reclamo="
				+ reclamo + "]";
	}
	
	
}
