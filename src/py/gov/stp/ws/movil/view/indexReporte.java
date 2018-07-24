package py.gov.stp.ws.movil.view;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import py.gov.stp.ws.movil.Model.BeneficiarioSalud;
import py.gov.stp.ws.movil.Model.Encuestados;
import py.gov.stp.ws.movil.Model.Evento;
import py.gov.stp.ws.movil.Model.EventosView;
import py.gov.stp.ws.movil.Model.Formulario;
import py.gov.stp.ws.movil.Model.LoginResult;
import py.gov.stp.ws.movil.Model.Marcacion;
import py.gov.stp.ws.movil.Model.Motivos;
import py.gov.stp.ws.movil.Model.Proyecto;
import py.gov.stp.ws.movil.Model.Recorrido;
import py.gov.stp.ws.movil.Model.Relevamiento;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.Model.ResumenEstadistica;
import py.gov.stp.ws.movil.Model.Ubicacion;
import py.gov.stp.ws.movil.Model.Usuario;
import py.gov.stp.ws.movil.Util.Autenticacion;
import py.gov.stp.ws.movil.database.ConsultaDB;
import py.gov.stp.ws.movil.database.ConsultaReportes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;


@Path("v2")
public class indexReporte {
	
	@GET
	@Path("prueba")
	@Produces(MediaType.TEXT_PLAIN)
	public String holamundo(){
		return "<html> " + "<title>" + "Hola Mundo" + "</title>"  
	             + "<body><h1>" + "Hola Mundo en HTML : " 
	             + "</body></h1>" + "</html> ";
		
	}
	
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String autenticar(@FormParam("username") String username,@FormParam("password") String password){
		LoginResult loginResult = null;
		loginResult = Autenticacion.getInstance().autenticarUsuario(username, password);
		JsonElement json = new Gson().toJsonTree(loginResult);
		return json.toString();
	}
	
	@GET
	@Path("proyectos/{cedsupervisor}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getProyectosBySupervisor(@HeaderParam("token") String token, @PathParam("cedsupervisor") String cedsupervisor){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			ArrayList<Proyecto>proyectos = null;
			try {
				proyectos = ConsultaDB.getProyectos(cedsupervisor,true);
				json = new Gson().toJsonTree(proyectos);
				return json.toString();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("usuario/proyecto/{idproyecto}/{cedusuario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getResumenTecnicos(@HeaderParam("token") String token, 
									 @PathParam("idproyecto") int idproyecto,
									 @PathParam("cedusuario") String cedusuario){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			ArrayList<Usuario>resumenUsuariByPr = null;
			try {
				resumenUsuariByPr = ConsultaDB.resumenUsuarioByProyecto(idproyecto,cedusuario);
				json = new Gson().toJsonTree(resumenUsuariByPr);
				return json.toString();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("motivo/proyecto/{idproyecto}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getResumenMotivos(@HeaderParam("token") String token, @PathParam("idproyecto") int idproyecto){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			ArrayList<Motivos>resumenMotivosByPr = null;
			try {
				resumenMotivosByPr = ConsultaDB.getMotivos(idproyecto, true);
				json = new Gson().toJsonTree(resumenMotivosByPr);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("beneficiario/proyecto/{idproyecto}/pagina/{nropagina}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getResumenBeneficiarios(@HeaderParam("token") String token, 
										  @PathParam("idproyecto") int idproyecto,
										  @PathParam("nropagina") int nropagina){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			ArrayList<Encuestados>resumenEncuestados = null;
			try {
				resumenEncuestados = ConsultaDB.getResumenBeneficiarios(idproyecto,nropagina);
				json = new Gson().toJsonTree(resumenEncuestados);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("beneficiario/proyecto/{idproyecto}/buscar/{cadena}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getSearchBeneficiarios(@HeaderParam("token") String token,
										 @PathParam("cadena") String cadena,
										 @PathParam("idproyecto") int idproyecto){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			ArrayList<Encuestados>resumenEncuestados = null;
			try {
				resumenEncuestados = ConsultaDB.getSearchBeneficiario(idproyecto,cadena);
				json = new Gson().toJsonTree(resumenEncuestados);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("beneficiario/proyectos/{idbeneficarios}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getProyectosByBeneficiarios(@HeaderParam("token") String token,
										      @PathParam("idbeneficarios") int idbeneficarios){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			ArrayList<Proyecto>arrproyectos = null;
			try {
				arrproyectos = ConsultaDB.getProyectosByidBenficiarios(idbeneficarios);
				json = new Gson().toJsonTree(arrproyectos);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("usuario/lastposition/{idusuario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getUsuarioLastPosicion(@HeaderParam("token") String token,
										 @PathParam("idusuario") int idusuario){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				Ubicacion lastUbicacion = ConsultaDB.getLastUbicacionUser(idusuario);
				json = new Gson().toJsonTree(lastUbicacion);
				return json.toString();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("proyecto/usuario/{idusuario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getUsuarioProyectos(@HeaderParam("token") String token,
			 						  @PathParam("idusuario") int idusuario){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<Proyecto>arrproyectos = ConsultaDB.getProyectosByUsuario(idusuario);
				json = new Gson().toJsonTree(arrproyectos);
				return json.toString();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("resumen/byfechas/{fechainicio}/{fechafin}/{idproyecto}/{idusuario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getResumenByFechas(@HeaderParam("token") String token,
			  						 @PathParam("fechainicio") String fechainicio,
			  						 @PathParam("fechafin") String fechafin,
			  					 	 @PathParam("idproyecto") int idproyecto,
			  					 	 @PathParam("idusuario") int idusuario){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<ResumenEstadistica> resumenEstadisticas = ConsultaDB.getResumenByFechas(fechainicio,fechafin,idproyecto,idusuario);
				json = new Gson().toJsonTree(resumenEstadisticas);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("familiares/{iddestinatario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getFamiliares(@HeaderParam("token") String token,
			  					@PathParam("iddestinatario") long iddestinatario){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<Encuestados> arrfamiliares = ConsultaDB.getFamiliares(iddestinatario);
				json = new Gson().toJsonTree(arrfamiliares);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("relevamiento/proyecto/{idproyecto}/tecnico/{cedtecnico}/destinatario/{iddestinatario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getRelevamiento(@HeaderParam("token") String token,
			  					  @PathParam("idproyecto") long idproyecto,
			  					  @PathParam("cedtecnico") String cedtecnico,
			  					  @PathParam("iddestinatario") long iddestinatario){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<Relevamiento> arrRelevemiento = ConsultaReportes.getRelevamientos(idproyecto, cedtecnico, iddestinatario);
				json = new Gson().toJsonTree(arrRelevemiento);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("relevamiento/respuestas/{idrelevamiento}/proyecto/{idproyecto}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getRelevamientoRespuestas(@HeaderParam("token") String token,
			  					  			@PathParam("idproyecto") long idproyecto,
			  					  			@PathParam("idrelevamiento") long idrelevamiento){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				Formulario formulario = ConsultaReportes.getFormularioRespuestas(idrelevamiento, idproyecto);
				json = new Gson().toJsonTree(formulario);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("capturas/{idrelevamiento}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getCapturas(@HeaderParam("token") String token,
			  				  @PathParam("idrelevamiento") long idrelevamiento){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<String> arr_capturas = ConsultaReportes.getListaCapturas(idrelevamiento);
				json = new Gson().toJsonTree(arr_capturas);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("evento/{idusuario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getReporteEventos(@HeaderParam("token") String token,
			  				  		@PathParam("idusuario") long idusuario){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<Evento> arr_eventos = ConsultaReportes.getReporteEventos(idusuario);
				json = new Gson().toJsonTree(arr_eventos);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("marcacion/{idusuario}/{mes}/{anho}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getMarcaciones(@HeaderParam("token") String token,
			  				  	 @PathParam("idusuario") long idusuario,
			  				  	 @PathParam("mes") int mes,
			  				  	 @PathParam("anho") int anho){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<Marcacion> arr_marcaciones = ConsultaReportes.getMarcaciones(idusuario,mes,anho);
				json = new Gson().toJsonTree(arr_marcaciones);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	

	@GET
	@Path("recorridos/{idusuario}/{mes}/{anho}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getRecorridos(@HeaderParam("token") String token,
			  				  	@PathParam("idusuario") long idusuario,
			  				    @PathParam("mes") int mes,
			  				    @PathParam("anho") int anho){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<Recorrido> arr_recorridos = ConsultaReportes.getRecorrido(idusuario,mes,anho);
				json = new Gson().toJsonTree(arr_recorridos);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("tracking/{idusuario}/{mes}/{anho}/{dia}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getTracking(@HeaderParam("token") String token,
			  				  @PathParam("idusuario") long idusuario,
			  				  @PathParam("mes") int mes,
			  				  @PathParam("anho") int anho,
			  				  @PathParam("dia") int dia){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<Ubicacion> arr_tracking = ConsultaReportes.getTrackingByDay(idusuario,mes,anho, dia);
				json = new Gson().toJsonTree(arr_tracking);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	

	@GET
	@Path("marcaciones/proders")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getUbucacionesTecnicos(@HeaderParam("token") String token){
		JsonElement json;
		//if(Autenticacion.getInstance().isTokenValid(token)){
			try {
				ArrayList<Ubicacion> arr_tracking = ConsultaReportes.getMarcacionesProders();
				json = new Gson().toJsonTree(arr_tracking);
				return json.toString();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		/*}else{
			return getRespTokenInvalido();
		}*/
		return "";
	}
	
	@GET
	@Path("eventos/{idusuario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getEventos(@HeaderParam("token") String token,
			                 @PathParam("idusuario") long idusuario){
		try {
			EventosView eventosView = ConsultaReportes.getEventosByUsuario(idusuario);
			JsonElement json = new Gson().toJsonTree(eventosView);
			return json.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@GET
	@Path("respuesta/salud")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getRespuestas(@HeaderParam("token") String token,
			                    @PathParam("idusuario") long idusuario){
		try {
			ArrayList<BeneficiarioSalud> arrBeneficiarioSaluds = ConsultaReportes.getRespuestaSalud();
			JsonElement json = new Gson().toJsonTree(arrBeneficiarioSaluds);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@GET
	@Path("respuesta/{idmotivo}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getRespuestasByIdMotivo(@HeaderParam("token") String token,
			                    		  @PathParam("idusuario") long idusuario){
		try {
			//ArrayList<BeneficiarioSalud> arrBeneficiarioSaluds = ConsultaReportes.getRespuestaSalud();
			//JsonElement json = new Gson().toJsonTree(arrBeneficiarioSaluds);
			return "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@GET
	@Path("salud/carnet")
	@Produces(MediaType.TEXT_HTML)
	public String getReporteCarnets(){
		try {
			
			return "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String getRespTokenInvalido(){
		Respuesta respuesta = new Respuesta();
		respuesta.setRespuesta(400);
		JsonElement json = new Gson().toJsonTree(respuesta);
		return json.toString();
	}
}
