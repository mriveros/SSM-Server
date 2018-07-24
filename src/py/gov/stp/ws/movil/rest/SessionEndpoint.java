package py.gov.stp.ws.movil.rest;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import java.sql.SQLException;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.database.ConsultaDB;
import py.gov.stp.ws.movil.database.UpdateBD;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Path("v6/session")
public class SessionEndpoint {
	
	@PUT
	@Path("salir")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response cerrarSession(@Context HttpHeaders headers,
								@FormParam("usuario") String usuario){
		
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		Respuesta respuesta = new Respuesta();
		try {
			UpdateBD.cambioEstadoLogin(usuario, false);
			respuesta.setRespuesta(0);
			UpdateBD.fechaCerrarSession(ConsultaDB.getLastIdSessionByUser(usuario));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(respuesta);
		return Response.ok(json.toString()).build();
	}
}
