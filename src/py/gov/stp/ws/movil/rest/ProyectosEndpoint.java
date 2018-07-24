package py.gov.stp.ws.movil.rest;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import py.gov.stp.ws.movil.Model.Proyecto;
import py.gov.stp.ws.movil.database.ConsultaMovil;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import javax.ws.rs.core.Response;

@Path("v6/proyectos")
public class ProyectosEndpoint {
	
	@GET
	@Path("/{cedtecnico}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getEncuesta(@Context HttpHeaders headers,
								@PathParam("cedtecnico") String cedtecnico){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		ArrayList<Proyecto>proyectos = null;
		try {
			proyectos = ConsultaMovil.getProyectos(cedtecnico);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(proyectos);
		return Response.ok(json.toString()).build();
	}
}
