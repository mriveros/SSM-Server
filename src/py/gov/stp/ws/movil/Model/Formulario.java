package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class Formulario {
	
	private int codigo_form;
	private String descrip_form;
	private ArrayList<Seccion>secciones;
	
	public Formulario() {
		super();
	}

	public int getCodigo_form() {
		return codigo_form;
	}

	public void setCodigo_form(int codigo_form) {
		this.codigo_form = codigo_form;
	}

	public String getDescrip_form() {
		return descrip_form;
	}

	public void setDescrip_form(String descrip_form) {
		this.descrip_form = descrip_form;
	}

	public ArrayList<Seccion> getSecciones() {
		return secciones;
	}

	public void setSecciones(ArrayList<Seccion> secciones) {
		this.secciones = secciones;
	}
}
