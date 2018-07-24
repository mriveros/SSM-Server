package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class Pregunta {
	
	private String idpreg;
	private String pregunta;
	private String tipo;
	private int requerido;
	private int totalizable;
	private int visible;
	private ArrayList<PosibleRespuestas> posibles_respuestas;
	private ArrayList<PreguntaCondicion> preguntas_condicionadas;
	private ArrayList<Validaciones>	validaciones;
	private ArrayList<String>respuestas;
	
	public Pregunta() {
		super();
	}

	public String getIdpreg() {
		return idpreg;
	}

	public void setIdpreg(String idpreg) {
		this.idpreg = idpreg;
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public ArrayList<PosibleRespuestas> getPosibleRespuestas() {
		return posibles_respuestas;
	}

	public void setPosibleRespuestas(ArrayList<PosibleRespuestas> posibleRespuestas) {
		this.posibles_respuestas = posibleRespuestas;
	}

	public int getRequerido() {
		return requerido;
	}

	public void setRequerido(int requerido) {
		this.requerido = requerido;
	}

	public ArrayList<PreguntaCondicion> getPreguntas_condicionadas() {
		return preguntas_condicionadas;
	}

	public void setPreguntas_condicionadas(ArrayList<PreguntaCondicion> preguntas_condicionadas) {
		this.preguntas_condicionadas = preguntas_condicionadas;
	}

	public int getTotalizable() {
		return totalizable;
	}

	public void setTotalizable(int totalizable) {
		this.totalizable = totalizable;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public ArrayList<PosibleRespuestas> getPosibles_respuestas() {
		return posibles_respuestas;
	}

	public void setPosibles_respuestas(
			ArrayList<PosibleRespuestas> posibles_respuestas) {
		this.posibles_respuestas = posibles_respuestas;
	}

	public ArrayList<Validaciones> getValidaciones() {
		return validaciones;
	}

	public void setValidaciones(ArrayList<Validaciones> validaciones) {
		this.validaciones = validaciones;
	}

	public ArrayList<String> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(ArrayList<String> respuestas) {
		this.respuestas = respuestas;
	}
}
