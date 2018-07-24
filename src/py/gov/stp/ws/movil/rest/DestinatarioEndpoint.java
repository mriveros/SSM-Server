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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import py.gov.stp.ws.movil.Model.Destinatario;
import py.gov.stp.ws.movil.database.ConsultaMovil;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;
import javax.ws.rs.core.Response;

@Path("v6/destinatarios")
public class DestinatarioEndpoint {
	
	
	@GET
	@Path("asignados/{cedtecnico}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getDestinatarios(@Context HttpHeaders headers,
									 @PathParam("cedtecnico") String cedtecnico){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();
		try {
			destinatarios = ConsultaMovil.getDestinatariosAsig(cedtecnico);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(destinatarios);
		JsonArray jaArray=json.getAsJsonArray();
		return Response.ok(jaArray.toString()).build();
	}
	
	
}
