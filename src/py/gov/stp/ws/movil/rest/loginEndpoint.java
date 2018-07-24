package py.gov.stp.ws.movil.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import py.gov.stp.ws.movil.Model.LoginResult;
import py.gov.stp.ws.movil.Model.Usuario;
import py.gov.stp.ws.movil.Model.LoginResult.TipoRespuesta;
import py.gov.stp.ws.movil.Util.FechaUtil;
import py.gov.stp.ws.movil.Util.TokensUtility;
import py.gov.stp.ws.movil.database.ConsultaDB;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

@Path("v6")
public class loginEndpoint {
	
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(APPLICATION_FORM_URLENCODED)
	public Response autenticar(@FormParam("username") String username,@FormParam("password") String password){
		LoginResult loginResult = null;
		String token = "";
		JsonElement json;
		try {
			loginResult = ConsultaDB.autenticarUser(username, password);
			if(loginResult.getRespuesta() == TipoRespuesta.OK.codigo){
				Usuario usuario = ConsultaDB.getUsuarioByCed(username);
				token = TokensUtility.getInstance().createToken(Long.toString(usuario.getIdusuario()),
				         													  usuario.getNombre(),
				         													  usuario.getCedula());
				json = new Gson().toJsonTree(loginResult);
				return Response.ok(json.toString()).header(AUTHORIZATION, "Bearer " + token).build();
			}else{
				json = new Gson().toJsonTree(loginResult);
				return Response.status(UNAUTHORIZED).entity(json.toString()).build();
			}
			
		} catch (Exception e) {
			loginResult = LoginResult.ERROR_SERVER(); 
			json = new Gson().toJsonTree(loginResult);
			return Response.status(CONFLICT).entity(json.toString()).build();
		} 
	}
	
	@GET
	@Path("horaactual")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String horaactual(){
		JsonElement json = new Gson().toJsonTree(FechaUtil.getFechaActual());
		return json.toString();	
		
	}
}
