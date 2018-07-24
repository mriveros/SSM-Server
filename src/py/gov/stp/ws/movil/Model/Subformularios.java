package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class Subformularios {
	private long idrelevamiento;
	private ArrayList<Pregunta> arr_preguntas;
	
	public Subformularios() {
		super();
	}

	public long getIdrelevamiento() {
		return idrelevamiento;
	}

	public void setIdrelevamiento(long idrelevamiento) {
		this.idrelevamiento = idrelevamiento;
	}

	public ArrayList<Pregunta> getArr_preguntas() {
		return arr_preguntas;
	}

	public void setArr_preguntas(ArrayList<Pregunta> arr_preguntas) {
		this.arr_preguntas = arr_preguntas;
	}
}
