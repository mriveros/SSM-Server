package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class Validaciones {
	
	private int idpregunta;
	private int condicion;
	private int valor_max;
	private int valor_min;
	private ArrayList<String> valores;
	
	public Validaciones() {
		super();
	}

	public int getIdpregunta() {
		return idpregunta;
	}

	public void setIdpregunta(int idpregunta) {
		this.idpregunta = idpregunta;
	}

	public int getCondicion() {
		return condicion;
	}

	public void setCondicion(int condicion) {
		this.condicion = condicion;
	}

	public int getValor_max() {
		return valor_max;
	}

	public void setValor_max(int valor_max) {
		this.valor_max = valor_max;
	}

	public int getValor_min() {
		return valor_min;
	}

	public void setValor_min(int valor_min) {
		this.valor_min = valor_min;
	}

	public ArrayList<String> getValores() {
		return valores;
	}

	public void setValores(ArrayList<String> valores) {
		this.valores = valores;
	}
}
