package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class Seccion {
	
	private String codSeccion;
	private String seccion;
	private int tipo;
	private int condicionada;
	private int totalizable;
	private ArrayList<Pregunta>preguntas;
	private ArrayList<SeccionCondicion>siguiente_seccion;
	private ArrayList<Encuestados>lista_hogar;
	private String multimedia;
	
	public Seccion() {
		super();
	}

	public String getCodSeccion() {
		return codSeccion;
	}

	public void setCodSeccion(String codSeccion) {
		this.codSeccion = codSeccion;
	}

	public String getSeccion() {
		return seccion;
	}

	public void setSeccion(String seccion) {
		this.seccion = seccion;
	}

	public ArrayList<Pregunta> getPreguntas() {
		return preguntas;
	}

	public void setPreguntas(ArrayList<Pregunta> preguntas) {
		this.preguntas = preguntas;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public int getCondicionada() {
		return condicionada;
	}

	public void setCondicionada(int condicionada) {
		this.condicionada = condicionada;
	}

	public int getTotalizable() {
		return totalizable;
	}

	public void setTotalizable(int totalizable) {
		this.totalizable = totalizable;
	}

	public ArrayList<SeccionCondicion> getSiguiente_seccion() {
		return siguiente_seccion;
	}

	public void setSiguiente_seccion(ArrayList<SeccionCondicion> siguiente_seccion) {
		this.siguiente_seccion = siguiente_seccion;
	}

	public ArrayList<Encuestados> getLista_hogar() {
		return lista_hogar;
	}

	public void setLista_hogar(ArrayList<Encuestados> lista_hogar) {
		this.lista_hogar = lista_hogar;
	}

	public String getMultimedia() {
		return multimedia;
	}

	public void setMultimedia(String multimedia) {
		this.multimedia = multimedia;
	}
}
