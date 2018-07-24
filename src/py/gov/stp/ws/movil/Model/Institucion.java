package py.gov.stp.ws.movil.Model;

public class Institucion {
	private String descripcion;
	private String codigo;
	
	public Institucion(String descripcion, String codigo) {
		super();
		this.descripcion = descripcion;
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getCodigo() {
		return codigo;
	}

	@Override
	public String toString() {
		return "Institucion [descripcion=" + descripcion + ", codigo=" + codigo
				+ "]";
	}
}
