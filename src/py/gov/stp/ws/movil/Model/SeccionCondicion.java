package py.gov.stp.ws.movil.Model;

public class SeccionCondicion {
	
	private long idpreguntacondicionante;
	private long idseccionsiguiente;
	private int condicion;
	private String valor;
	
	public SeccionCondicion() {
		super();
	}

	public long getIdpreguntacondicionante() {
		return idpreguntacondicionante;
	}

	public void setIdpreguntacondicionante(long idpreguntacondicionante) {
		this.idpreguntacondicionante = idpreguntacondicionante;
	}

	public long getIdseccionsiguiente() {
		return idseccionsiguiente;
	}

	public void setIdseccionsiguiente(long idseccionsiguiente) {
		this.idseccionsiguiente = idseccionsiguiente;
	}

	public int getCondicion() {
		return condicion;
	}

	public void setCondicion(int condicion) {
		this.condicion = condicion;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
