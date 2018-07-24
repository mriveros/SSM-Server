package py.gov.stp.ws.movil.Model;

public class Motivos {
	
	private String motivo;
	private String codigo_motivo;
	private Formulario formulario;
	
	private int totalDestinatarios;
	private int totalRelevamientos;
	
	public Motivos() {
		super();
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getCodigo_motivo() {
		return codigo_motivo;
	}

	public void setCodigo_motivo(String codigo_motivo) {
		this.codigo_motivo = codigo_motivo;
	}

	public Formulario getFormulario() {
		return formulario;
	}

	public void setFormulario(Formulario formulario) {
		this.formulario = formulario;
	}

	public int getTotalDestinatarios() {
		return totalDestinatarios;
	}

	public void setTotalDestinatarios(int totalDestinatarios) {
		this.totalDestinatarios = totalDestinatarios;
	}

	public int getTotalRelevamientos() {
		return totalRelevamientos;
	}

	public void setTotalRelevamientos(int totalRelevamientos) {
		this.totalRelevamientos = totalRelevamientos;
	}
}
