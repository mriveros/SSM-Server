package py.gov.stp.ws.movil.rest;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.sql.SQLException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import py.gov.stp.ws.movil.Model.Registro;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.database.UpdateBD;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;

@Path("v6/push")
public class PushEndpoint {
	
	@POST
	@Path("register")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response registrar(@Context HttpHeaders headers,
							@FormParam("cedusuario") String cedusuario,
							@FormParam("firebasetoken") String firebasetoken){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		Registro registro = new Registro(cedusuario, firebasetoken);
		Respuesta respuesta = new Respuesta();
		try {
			UpdateBD.registarFirebaseToken(registro);
			respuesta.setRespuesta(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(respuesta);
		return Response.ok(json.toString()).build();
	}
}
