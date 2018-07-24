package py.gov.stp.ws.movil.view;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import py.gov.stp.ws.movil.Model.Formulario;
import py.gov.stp.ws.movil.Model.PosibleRespuestas;
import py.gov.stp.ws.movil.Model.Pregunta;
import py.gov.stp.ws.movil.Model.Seccion;
import py.gov.stp.ws.movil.database.ConsultaDB;
import py.gov.stp.ws.movil.database.ConsultaMovil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Path("v5")
public class indexMovil5 {
	
	@GET
	@Path("formulario/{idformulario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getFormulario(@PathParam("idformulario") int idformulario){
		Formulario formulario = new Formulario();
		try {
			formulario = ConsultaMovil.getFormulario(idformulario,false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(formulario);
		return json.toString();
	}
	
	@GET
	@Path("secciones/{idformulario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getSecciones(@PathParam("idformulario") int idformulario){
		ArrayList<Seccion> arr_secciones = new ArrayList<Seccion>();
		try {
			arr_secciones = ConsultaMovil.getSecciones(idformulario, false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(arr_secciones);
		return json.toString();
	}
	
	@GET
	@Path("preguntas/{idsecciones}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getPreguntas(@PathParam("idsecciones") int idsecciones){
		ArrayList<Pregunta> arr_pregunta = new ArrayList<Pregunta>();
		try {
			arr_pregunta = ConsultaDB.getPreguntas(idsecciones);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(arr_pregunta);
		return json.toString();
	}
	
	@GET
	@Path("posibles-respuestas/{idpreguntas}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getPosiblesRespuestas(@PathParam("idpreguntas") int idpreguntas){
		ArrayList<PosibleRespuestas> arr_posibles_respuestas = new ArrayList<PosibleRespuestas>();
		try {
			arr_posibles_respuestas = ConsultaDB.getPosibleRespuestas(idpreguntas);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(arr_posibles_respuestas);
		return json.toString();
	}
}
