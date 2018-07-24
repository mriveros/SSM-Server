package py.gov.stp.ws.movil.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.JsonObject;

import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Encuestados;
import py.gov.stp.ws.movil.Model.Proyecto;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.Model.Visita;
import py.gov.stp.ws.movil.Util.FechaUtil;
import py.gov.stp.ws.movil.Util.FileUtil;

public class InsertMovil {
	
	private static ConnectionManager connectionManager;
	
	private final static String INSERT_VISITA = "INSERT INTO relevamientos(longitud, latitud,fechorini, fechorfin, diftiempo,observaciones, fechacarga, motivos_id, encuestados_id, usuarios_id,presicion,"
                                                                 + "codigo_departamento,codigo_distrito,codigo_localidad,id_key,original) "
                                                                 + "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?,?,?,?,?,?,?,?); ";
	
	private static void init(){
		if(connectionManager == null){
			connectionManager = new ConnectionManager();
		}
	}
	
	public static Respuesta addVisita(Visita visita) throws SQLException{
		init();
		Respuesta respuesta = new Respuesta();
		Connection conection = connectionManager.getConexion();
		PreparedStatement statement = null;
		try {
			
			conection.setAutoCommit(false);
			
			long idusuario = InsertarBD.getIdUsuario(visita.getUsuario());
			long idDestinatario = ConsultaMovil.getIdDestinatario(visita.getCedula(), conection);
			if(idDestinatario == 0){
				Encuestados encuestado = new Encuestados();
				encuestado.setDocumento(visita.getCedula());
				encuestado.setNombre(visita.getNombre());
				encuestado.setApellido(visita.getApellido());
				encuestado.setLongitud(visita.getLongitud());
				encuestado.setLatitud(visita.getLatitud());
				encuestado.setPresicion(visita.getPresicion());
				encuestado.setDistrito(InsertarBD.getIdDistrito(visita.getDepartamento(), visita.getDistrito()));
				encuestado.setCodDpto(Integer.parseInt(visita.getDepartamento()));
				encuestado.setCodDist(Integer.parseInt(visita.getDistrito()));
				encuestado.setCodlocalidad(visita.getLocalidad());
				encuestado.setTipo(Encuestados.TipoEncuestados.findByCodigoMovil(visita.getTipobeneficiario()).getDescripcion());
				encuestado.setTipo_destinatario_id(Encuestados.TipoEncuestados.findByCodigoMovil(visita.getTipobeneficiario()).getCodigoDB());
				encuestado.setEsjefe(visita.getEsjefe());
				idDestinatario = InsertarBD.addEncuestado(encuestado);
				if(ConsultaMovil.getTipoProyecto(visita.getMotivo(), conection)!=3){
					InsertarBD.asignarEncuestado(idDestinatario, idusuario, Long.parseLong(visita.getProyecto()));
				}
			}
			
			long last_inserted_id = 0;
			statement = conection.prepareStatement(INSERT_VISITA,Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, visita.getLongitud());
			statement.setString(2, visita.getLatitud());
			statement.setDouble(10, visita.getPresicion());
			statement.setTimestamp(3, FechaUtil.getTimeStamp(visita.getHoraini()));
			statement.setTimestamp(4, FechaUtil.getTimeStamp(visita.getHorafin()));
			statement.setDouble(5, visita.getDuracion());
			statement.setString(6, visita.getObservacion());
			statement.setLong(7, visita.getMotivo());
			statement.setInt(11, Integer.parseInt(visita.getDepartamento()));
			statement.setInt(12, Integer.parseInt(visita.getDistrito()));
			statement.setInt(13, visita.getLocalidad());
			statement.setLong(8, idDestinatario);
			statement.setLong(9, idusuario);
			statement.setString(14, visita.getId_key());
			statement.setInt(15,visita.getOriginal());
			statement.executeUpdate();
			
			ResultSet rs = statement.getGeneratedKeys();
			if(rs.next()){
				last_inserted_id = rs.getLong(1);
			}
			conection.commit();
			try {
				JSONArray jsonArray = new JSONArray(visita.getCapturas());
				addCapturasHeader(jsonArray, visita, last_inserted_id);
			} catch (JSONException e) {
				addCapturasHeader(visita.getCedula(),visita.getCapturas(),last_inserted_id,visita.getHoraini());
			}
			addAdjuntoHeader(visita.getCedula(), visita.getAdjuntos(), last_inserted_id);
			InsertarBD.insertarResult(last_inserted_id, visita.getResultado());
			respuesta.setRespuesta(0);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		
		return respuesta;
	}
	
	private static void addCapturasHeader(String encuestado,String imagenes,long relevamientoid,String fecha) throws SQLException{
		if(imagenes!=null && !imagenes.equals("")){
			String INSERT = "INSERT INTO capturas(encuestado, imagen, numero, relevamientos_id) VALUES (?, ?, ?, ?);";
			String[] arrImagenes = imagenes.split("\\|");
			
			init();
			Connection conect=connectionManager.getConexion();
			
			PreparedStatement statement = null;
			
			int cont = 1;
			for(String cadena:arrImagenes){
				if(!cadena.equals("")){
					statement = conect.prepareStatement(INSERT,Statement.RETURN_GENERATED_KEYS);
					statement.setString(1,encuestado);
					statement.setString(2,cadena);
					statement.setInt(3,cont);
					statement.setLong(4,relevamientoid);
					statement.executeUpdate();
					
					ResultSet rs = statement.getGeneratedKeys();
					long last_inserted_id = 0;
					if(rs.next()){
						last_inserted_id = rs.getLong(1);
					}
					
					File file = new File("/usr/share/tomcat/moviles/capturas/" + cadena);
					if(file.exists()){
						UpdateBD.recepcionCaptura(fecha, last_inserted_id, conect);
					}
					
					statement.clearParameters();
					cont ++;
				}
			}
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
	}
	
	private static void addCapturasHeader(JSONArray jsonArray,Visita visita,long relevamientoid) throws SQLException{
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement statement = null;
		String INSERT = "INSERT INTO capturas(encuestado, imagen, numero, relevamientos_id,origen,hash) "
				                   + "VALUES (?, ?, ?, ?,?,?);";
		JSONObject jsonObject;
		try {
			for(int i=0;i<jsonArray.length();i++){
				jsonObject = jsonArray.getJSONObject(i);
				statement = conection.prepareStatement(INSERT,Statement.RETURN_GENERATED_KEYS);
				statement.setString(1,visita.getCedula());
				statement.setString(2,jsonObject.getString("imagen"));
				statement.setInt(3,(i+1));
				statement.setLong(4,relevamientoid);
				statement.setInt(5, jsonObject.getInt("origen"));
				statement.setString(6, jsonObject.getString("hash"));
				statement.executeUpdate();
				ResultSet rs = statement.getGeneratedKeys();
				long last_inserted_id = 0;
				if(rs.next()){
					last_inserted_id = rs.getLong(1);
				}
				File file = new File("/usr/share/tomcat/moviles/capturas/" + jsonObject.getString("imagen"));
				if(file.exists()){
					UpdateBD.recepcionCaptura(visita.getHoraini(), last_inserted_id, conection);
				}
				statement.clearParameters();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
	}
	
	private static void addAdjuntoHeader(String encuestado,String adjuntos,long relevamientoid)throws SQLException, ParseException{
		if(adjuntos!=null && !adjuntos.equals("")){
			String INSERT = "INSERT INTO adjuntos(archivo, relevamientos_id,encuestado) VALUES (?,?,?);";
			String[] arrAdjuntos = adjuntos.split("\\|");
			
			init();
			Connection conect=connectionManager.getConexion();
			PreparedStatement statement = null;
			
			for(String cadena:arrAdjuntos){
				statement = conect.prepareStatement(INSERT,Statement.RETURN_GENERATED_KEYS);
				statement.setString(1,cadena);
				statement.setLong(2,relevamientoid);
				statement.setString(3,encuestado);
				statement.executeUpdate();
				
				ResultSet rs = statement.getGeneratedKeys();
				long last_inserted_id = 0;
				if(rs.next()){
					last_inserted_id = rs.getLong(1);
				}
				
				File file = new File("/usr/share/tomcat/moviles/adjuntos/" + cadena);
				if(file.exists()){
					UpdateBD.recepcionAdjunto(last_inserted_id, conect);
				}
				
				statement.clearParameters();
			}
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
	}
	
	public static Respuesta addCapturaFile(Captura captura){
		String cadena ="capturas/"+captura.getNombre();
		Respuesta respuesta = new Respuesta();
		try {
			String hash = FileUtil.receiverFile(cadena, captura.getInputStream());
			if(!hash.equals("")){
				UpdateBD.recepcionCaptura(captura);
			}
			respuesta.setRespuesta(0);
			respuesta.setHash(hash);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return respuesta;
	}
	
	public static Respuesta recibirImagen(Captura captura) throws SQLException, ParseException{
		Respuesta respuesta = new Respuesta();
		if(ConsultaMovil.existImagenBD(captura.getNombre())){
			String path = "capturas/" + captura.getNombre();
			File file = new File(path);
			if(!file.exists() || !captura.getHash().equals(FileUtil.getHashOfFile(file))){
				String hash = FileUtil.receiverFile(path, captura.getInputStream());
				if(hash.equals(captura.getHash())){
					respuesta.setRespuesta(0);
					UpdateBD.recepcionCaptura(captura.getNombre(),captura.getFecha());
				}else{
					respuesta.setRespuesta(2);
					respuesta.setHash(hash);
				}
			}
		}
		return respuesta;
	}
	
	public static Respuesta addCapturaFileEvt(Captura captura){
		String cadena ="capturas/"+captura.getNombre();
		Respuesta respuesta = new Respuesta();
		try {
			String hash = FileUtil.receiverFile(cadena, captura.getInputStream());
			UpdateBD.recepcionCapturaEvt(captura);
			respuesta.setRespuesta(0);
			respuesta.setHash(hash);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return respuesta;
	}
}
