package py.gov.stp.ws.movil.database;

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

import py.gov.stp.ws.movil.Model.Adjuntos;
import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Device;
import py.gov.stp.ws.movil.Model.Encuestados;
import py.gov.stp.ws.movil.Model.Evento;
import py.gov.stp.ws.movil.Model.EventoDevice;
import py.gov.stp.ws.movil.Model.ReclamoCallCenter;
import py.gov.stp.ws.movil.Model.Recorrido;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.Model.Ubicacion;
import py.gov.stp.ws.movil.Model.Visita;
import py.gov.stp.ws.movil.Util.FechaUtil;
import py.gov.stp.ws.movil.Util.FileUtil;

public class InsertarBD {
	
	private static ConnectionManager connectionManager;
	
	private static void init(){
		if(connectionManager == null){
			connectionManager = new ConnectionManager();
		}
	}
	
	public static Respuesta addVisita(Visita visita) throws SQLException, ParseException{
		int sw = 0;
		Respuesta resp = new Respuesta();
		if(!ConsultaDB.checkVisita(visita)){
			init();
			Connection conect=connectionManager.getConexion();
			conect.setAutoCommit(false);
			PreparedStatement statement = null;
			
			String stm = "INSERT INTO relevamientos(longitud, latitud,fechorini, fechorfin, diftiempo,observaciones, fechacarga, motivos_id, encuestados_id, usuarios_id,presicion,"
					+ "								codigo_departamento,codigo_distrito,codigo_localidad) "
										  + "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?,?,?,?,?,?); ";
			long last_inserted_id = 0;
			try {
				statement = conect.prepareStatement(stm,Statement.RETURN_GENERATED_KEYS);
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
				
				Encuestados encuestados = new Encuestados();
				encuestados.setDocumento(visita.getCedula());
				encuestados.setNombre(visita.getNombre());
				encuestados.setApellido(visita.getApellido());
				encuestados.setLongitud(visita.getLongitud());
				encuestados.setLatitud(visita.getLatitud());
				encuestados.setPresicion(visita.getPresicion());
				encuestados.setDistrito(getIdDistrito(visita.getDepartamento(), visita.getDistrito()));
				
				encuestados.setCodDpto(Integer.parseInt(visita.getDepartamento()));
				encuestados.setCodDist(Integer.parseInt(visita.getDistrito()));
				encuestados.setCodlocalidad(visita.getLocalidad());
				
				/*if(visita.getTipobeneficiario()==0){
					encuestados.setTipo("persona");
				}else{
					encuestados.setTipo("entidad");
				}*/
				encuestados.setTipo(Encuestados.TipoEncuestados.findByCodigoMovil(visita.getTipobeneficiario()).getDescripcion());
				encuestados.setTipo_destinatario_id(Encuestados.TipoEncuestados.findByCodigoMovil(visita.getTipobeneficiario()).getCodigoDB());
				encuestados.setEsjefe(visita.getEsjefe());
				
				long idencuestado = getIdEncuestado(encuestados);
				long idusuario = getIdUsuario(visita.getUsuario());
				
				if(idencuestado>0 && idusuario>0){
					asignarEncuestado(idencuestado, idusuario, Long.parseLong(visita.getProyecto()));
					statement.setLong(8, idencuestado);
					statement.setLong(9, idusuario);
					statement.executeUpdate();
					
					ResultSet rs = statement.getGeneratedKeys();
					
					last_inserted_id = 0;
					if(rs.next()){
						last_inserted_id = rs.getLong(1);
					}
					conect.commit();
					sw = 1;
					addCapturasHeader(visita.getCedula(),visita.getCapturas(),last_inserted_id);
					addAdjuntoHeader(visita.getCedula(), visita.getAdjuntos(), last_inserted_id);
					insertarResult(last_inserted_id, visita.getResultado());
					resp.setRespuesta(0);
				}
			} catch (SQLException e) {
				if(sw==0){
					conect.rollback();
				}else{
					DeleteBD.deleteRelevamiento(last_inserted_id);
				}
				resp.setRespuesta(1);
				e.printStackTrace();
			}finally{
				if (statement != null) {statement.close();}
				if (conect != null) {conect.close();}
			}
		}else{
			resp.setRespuesta(0);
		}
		return resp;
	}
	
	public static long getIdDistrito(String dpto,String distrito) throws SQLException{
		init();
		Connection conect=connectionManager.getConexion();
		
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		long iddistrito = 0;
		String CONSULTA = "SELECT id FROM distritos WHERE distrito_id="+distrito+" AND departamentos_cod="+dpto;
		
		try {
			statement = conect.prepareStatement(CONSULTA);
			rs=statement.executeQuery();
			if(rs.next()){
				iddistrito = rs.getLong("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return iddistrito;
	}
	
	public static long getIdUsuario(String usuario) throws SQLException{
		init();
		Connection conect=connectionManager.getConexion();
		
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		long idusuario = 0;
		String CONSULTA = "SELECT id FROM usuarios WHERE usuario=?";

		try {
			statement = conect.prepareStatement(CONSULTA);
			statement.setString(1,usuario);
			rs=statement.executeQuery();
			if(rs.next()){
				idusuario = rs.getLong("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return idusuario;
	}
	private static long getIdEncuestado(Encuestados encuestados) throws SQLException{
		init();
		Connection conect=connectionManager.getConexion();
		
		String CONSULTA = "SELECT id FROM encuestados WHERE cedula=?";
		
		PreparedStatement statement = null;
		ResultSet rs=null;
		long idEncuestado = 0;
		try {
			statement = conect.prepareStatement(CONSULTA);
			statement.setString(1,encuestados.getDocumento());
			rs=statement.executeQuery();
			if(rs.next()){
				idEncuestado = rs.getLong("id");
			}else{
				idEncuestado = addEncuestado(encuestados);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		
		return idEncuestado;
	}
	
	public static long addEncuestado(Encuestados encuestado) throws SQLException{
		init();
		Connection conect=connectionManager.getConexion();
		
		PreparedStatement statement = null;
		String insert = "INSERT INTO encuestados(cedula, nombre, apellido, fechacarga, longitud,latitud,"
				                              + "presicion,esjefe,codigo_departamento,"
				                              + "codigo_distrito,codigo_localidad,tipos_destinatarios_id) "
									   +"VALUES (?, ?, ?, NOW(), ?, ?,?,?,?,?,?,?);";
		long last_inserted_id = 0;
		try {
			statement = conect.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
			statement.setString(1,encuestado.getDocumento());
			statement.setString(2,encuestado.getNombre());
			statement.setString(3,encuestado.getApellido());
			statement.setString(4,encuestado.getLongitud());
			statement.setString(5,encuestado.getLatitud());
			//statement.setLong(6,encuestado.getDistrito());
			//statement.setString(7,encuestado.getTipo());
			statement.setDouble(6,encuestado.getPresicion());
			statement.setDouble(7,encuestado.getEsjefe());
			statement.setInt(8,encuestado.getCodDpto());
			statement.setInt(9,encuestado.getCodDist());
			statement.setInt(10,encuestado.getCodlocalidad());
			statement.setInt(11,encuestado.getTipo_destinatario_id());
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if(rs.next()){
				last_inserted_id = rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return last_inserted_id;
	}
	
	public static long asignarEncuestado(long encuestados_id,long usuarios_id,long proyecto_id) throws SQLException{
		init();
		Connection conect=connectionManager.getConexion();
		//if(!ConsultaDB.checkAsignacionExist(conect,encuestados_id,usuarios_id,proyecto_id)){
			PreparedStatement statement = null;
			String DELETE = "DELETE FROM encuestados_asignados_usuario WHERE encuestados_id=? AND usuarios_id=? AND proyecto_id=?;";
			statement = conect.prepareStatement(DELETE);
			statement.setLong(1,encuestados_id);
			statement.setLong(2,usuarios_id);
			statement.setLong(3,proyecto_id);
			statement.executeUpdate();
			statement.clearParameters();
			
		    String INSERT = "INSERT INTO encuestados_asignados_usuario(encuestados_id, usuarios_id, proyecto_id) VALUES (?, ?, ?);";
			statement = conect.prepareStatement(INSERT);
			statement.setLong(1,encuestados_id);
			statement.setLong(2,usuarios_id);
			statement.setLong(3,proyecto_id);
			statement.executeUpdate();
			
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
			
			/*try {
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				
			}*/
		//}
		return 0;
	}
	
	private static void addCapturasHeader(String encuestado,String imagenes,long relevamientoid) throws SQLException{
		if(imagenes!=null && !imagenes.equals("")){
			String INSERT = "INSERT INTO capturas(encuestado, imagen, numero, relevamientos_id) VALUES (?, ?, ?, ?);";
			String[] arrImagenes = imagenes.split("\\|");
			
			init();
			Connection conect=connectionManager.getConexion();
			
			PreparedStatement statement = null;
			
			int cont = 1;
			for(String cadena:arrImagenes){
				if(!cadena.equals("")){
					statement = conect.prepareStatement(INSERT);
					statement.setString(1,encuestado);
					statement.setString(2,cadena);
					statement.setInt(3,cont);
					statement.setLong(4,relevamientoid);
					statement.executeUpdate();
					statement.clearParameters();
					cont ++;
				}
				/*try {
					
				} catch (SQLException e) {
					e.printStackTrace();
				}*/
			}
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
			/*try {
				
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	private static void addAdjuntoHeader(String encuestado,String adjuntos,long relevamientoid)throws SQLException{
		if(adjuntos!=null && !adjuntos.equals("")){
			String INSERT = "INSERT INTO adjuntos(archivo, relevamientos_id,encuestado) VALUES (?,?,?);";
			String[] arrAdjuntos = adjuntos.split("\\|");
			
			init();
			Connection conect=connectionManager.getConexion();
			PreparedStatement statement = null;
			
			for(String cadena:arrAdjuntos){
				statement = conect.prepareStatement(INSERT);
				statement.setString(1,cadena);
				statement.setLong(2,relevamientoid);
				statement.setString(3,encuestado);
				statement.executeUpdate();
				statement.clearParameters();
				/*try {
					if(!cadena.equals("")){
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}*/
			}
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
			/*try {
				
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	public static void insertarResult(long relevamientoid,String resultado) throws SQLException{
		if(resultado!=null && !resultado.equals("")){
			String INSERT = "INSERT INTO detalles_relevamientos(respuesta, tipo, relevamientos_id, preguntas_id,"
					 										  + "posiresp_id,documento_enc,nro_sub_formulario) "
				          + "VALUES (?, ?, ?, ?,?,?,?);";
			
			init();
			Connection conect=connectionManager.getConexion();
			PreparedStatement statement = null;
			
			try {
				JSONArray jsonArray = new JSONArray(resultado);
				JSONObject jsonObject;
				
				statement = conect.prepareStatement(INSERT);
			
				for(int i=0;i<jsonArray.length();i++){
					jsonObject = jsonArray.getJSONObject(i);
					if(jsonObject.getInt("tipo")==7){
						insertarListaPersonas(relevamientoid, 
											  jsonObject.getLong("idpegunta"), 
											  jsonObject.getString("respuesta"),
											  jsonObject.getString("beneficiario"));
					}else{
						String[] arrResp = jsonObject.getString("respuesta").split("\\|");
						for(String cadena:arrResp){
							statement.clearParameters();
							if(jsonObject.getInt("tipo")>2){
								statement.setString(1,cadena);
								statement.setInt(5,0);
							}else{
								statement.setString(1,"");
								statement.setInt(5,Integer.parseInt(cadena));
							}
							
							statement.setInt(2,jsonObject.getInt("tipo"));
							statement.setLong(3,relevamientoid);
							statement.setLong(4,jsonObject.getLong("idpegunta"));
							statement.setString(6,jsonObject.getString("beneficiario"));
							if(jsonObject.has("nro_subform")){
								statement.setInt(7, jsonObject.getInt("nro_subform"));
							}else{
								statement.setInt(7, 0);
							}
							statement.executeUpdate();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally{
				if (statement != null) {statement.close();}
				if (conect != null) {conect.close();}
			}
		}
	}
	
	public static void insertarListaPersonas(long relevamientoid,long codseccion,String respuesta,String beneficiario) throws SQLException{
		String INSERT = "INSERT INTO lista_personas(relevamientos_id, seccion_id, encuestado_root_id, encuestado_asoc_id) VALUES (?, ?, ?, ?);";
		
		init();
		Connection conect=connectionManager.getConexion();
		PreparedStatement statement = null;
		
		try {
			statement = conect.prepareStatement(INSERT);
			String[] arrResp = respuesta.split("\\|");
			for(String cadena:arrResp){
				statement.clearParameters();
				statement.setLong(1, relevamientoid);
				statement.setLong(2, codseccion);
				statement.setLong(3, ConsultaDB.getIdEncuestadoByCedula(beneficiario));
				statement.setLong(4, ConsultaDB.getIdEncuestadoByCedula(cadena));
				statement.executeUpdate();
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
	}
	
	public static Respuesta addCapturaFile(Captura captura){
		String cadena ="capturas/"+captura.getNombre();
		Respuesta respuesta = new Respuesta();
		try {
			FileUtil.receiverFile(cadena, captura.getArchivo());
			UpdateBD.recepcionCaptura(captura);
			respuesta.setRespuesta(0);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respuesta;
	}
	
	public static Respuesta addCapturaFileEvt(Captura captura){
		String cadena ="capturas/"+captura.getNombre();
		Respuesta respuesta = new Respuesta();
		try {
			FileUtil.receiverFile(cadena, captura.getArchivo());
			UpdateBD.recepcionCapturaEvt(captura);
			respuesta.setRespuesta(0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return respuesta;
	}
	
	public static Respuesta addAdjuntoFile(Adjuntos adjunto){
		String cadena ="adjuntos/"+adjunto.getNombre();
		Respuesta respuesta = new Respuesta();
		try {
			FileUtil.receiverFile(cadena, adjunto.getArchivo());
			UpdateBD.recepcionAdjunto(adjunto);
			respuesta.setRespuesta(0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return respuesta;
	}
	
	public static Respuesta addUbicacion(Ubicacion ubicacion) throws SQLException{
		String INSERT = "INSERT INTO ubicaciones(longitud, latitud, altitud, \"precision\", proveedor, fecha_captura, usuario, bateria, tipos_ubicaciones_id, usuarios_id,fecha_guardado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";
		
		init();
		Connection conect=connectionManager.getConexion();
		
		PreparedStatement statement = null;
		Respuesta respuesta = new Respuesta();
		try {
			statement = conect.prepareStatement(INSERT);
			statement.setString(1,ubicacion.getLongitud());
			statement.setString(2,ubicacion.getLatitud());
			statement.setString(3,ubicacion.getAltitud());
			statement.setDouble(4,ubicacion.getPrecision());
			statement.setString(5,ubicacion.getProveedor());
			statement.setTimestamp(6,FechaUtil.getTimeStamp(ubicacion.getHoraobtenido()));
			statement.setString(7,ubicacion.getUsuario());
			statement.setDouble(8,ubicacion.getBateria());
			statement.setInt(9,ubicacion.getTipo());
			statement.setLong(10,getIdUsuario(ubicacion.getUsuario()));
			statement.setTimestamp(11, FechaUtil.getTimeStamp(ubicacion.getHoraguardado()));
			statement.executeUpdate();
			respuesta.setRespuesta(0);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		
		return respuesta;
	}
	
	public static Respuesta addDevices(Device device) throws SQLException{
		String INSERT = "INSERT INTO telefonos_usados(operadora, sim, fecha, imei, fabricante, modelo, usuarios_id,androidid, osversion)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect=connectionManager.getConexion();
		
		PreparedStatement statement = null;
		Respuesta respuesta = new Respuesta();
		
		try {
			statement = conect.prepareStatement(INSERT);
			statement.setString(1,device.getOperadora());
			statement.setString(2,device.getSerialsim());
			statement.setTimestamp(3,FechaUtil.getTimeStamp(device.getFecha()));
			statement.setString(4,device.getImei());
			statement.setString(5,device.getFabricante());
			statement.setString(6,device.getModelo());
			statement.setLong(7,getIdUsuario(device.getUsuario()));
			statement.setString(8,device.getAndroidid());
			statement.setString(9,device.getOsversion());
			statement.executeUpdate();
			respuesta.setRespuesta(0);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return respuesta;
	}
	
	public static Respuesta addEvento(Evento evento) throws SQLException{
		String INSERT = "INSERT INTO eventos(descripcion, fecha, longitud, latitud, presicion, usuarios_id) VALUES (?, ?, ?, ?, ?, ?);";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect=connectionManager.getConexion();
		
		PreparedStatement statement = null;
		Respuesta respuesta = new Respuesta();
		
		try {
			statement = conect.prepareStatement(INSERT,Statement.RETURN_GENERATED_KEYS);
			statement.setString(1,evento.getDescripcion());
			statement.setTimestamp(2,FechaUtil.getTimeStamp(evento.getFecha()));
			statement.setString(3,evento.getLongitud());
			statement.setString(4,evento.getLatitud());
			statement.setDouble(5,evento.getPresicion());
			statement.setLong(6,getIdUsuario(evento.getUsuario()));
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			long last_inserted_id = 0;
			if(rs.next()){
				last_inserted_id = rs.getLong(1);
			}
			if(evento.getCapturas()!=null && !evento.getCapturas().equals("")){
				try {
					JSONArray jsonArray = new JSONArray(evento.getCapturas());
					addCapturasEventoHeader(jsonArray,last_inserted_id);
				} catch (JSONException e) {
					addCapturasEventoHeader(evento.getCapturas(),last_inserted_id);
				}
			}
			respuesta.setRespuesta(0);
		}  catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return respuesta;
	}
	
	private static void addCapturasEventoHeader(String capturas,long eventoid) throws SQLException{
		String INSERT = "INSERT INTO capturas_eventos(imagen, fecha, eventos_id) VALUES (?,NOW(), ?);";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect=connectionManager.getConexion();
		
		PreparedStatement statement = null;
		
		String[] arcapturas = capturas.split("\\|");
		
		try {
			statement = conect.prepareStatement(INSERT);
			for(String cadena:arcapturas){
				statement.setString(1,cadena);
				statement.setLong(2, eventoid);
				statement.executeUpdate();
				statement.clearParameters();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
	}
	
	private static void addCapturasEventoHeader(JSONArray jsonArray,long eventoid) throws SQLException{
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement statement = null;
		String INSERT = "INSERT INTO capturas_eventos(imagen, fecha, eventos_id,hash) VALUES (?,NOW(), ?,?);";
		JSONObject jsonObject;
		
		try {
			for(int i=0;i<jsonArray.length();i++){
				jsonObject = jsonArray.getJSONObject(i);
				statement = conection.prepareStatement(INSERT,Statement.RETURN_GENERATED_KEYS);
				statement.setString(1,jsonObject.getString("imagen"));
				statement.setLong(2, eventoid);
				statement.setString(3, jsonObject.getString("hash"));
				statement.executeUpdate();
				statement.clearParameters();
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
	}
	
	public static Respuesta addRecorrido(Recorrido recorrido) throws SQLException{
		String INSERT = "INSERT INTO recorridos(inicio, fin, duracion, usuarios_id) VALUES (?, ?, ?, ?);";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect=connectionManager.getConexion();
		
		PreparedStatement statement = null;
		Respuesta respuesta = new Respuesta();
		
		try {
			statement = conect.prepareStatement(INSERT);
			statement.setString(1, recorrido.getInicio());
			statement.setString(2, recorrido.getFin());
			statement.setInt(3,recorrido.getDuracion());
			statement.setLong(4,getIdUsuario(recorrido.getUsuario()));
			statement.executeUpdate();
		} catch (SQLException e) {
			respuesta.setRespuesta(1);
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return respuesta;
	}
	
	public static Respuesta registerDevice(Device device) throws SQLException{
		String INSERT = "INSERT INTO telefonos_asignados(operadora, sim, fecha, imei, fabricante, modelo, androidid, osversion) VALUES (?, ?, ?, ?, ?, ?,?, ?);";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect=connectionManager.getConexion();
				
		PreparedStatement statement = null;
		Respuesta respuesta = new Respuesta();
		
		try {
			statement = conect.prepareStatement(INSERT);
			statement.setString(1, device.getOperadora());
			statement.setString(2, device.getSerialsim());
			statement.setTimestamp(3,FechaUtil.getTimeStamp(device.getFecha()));
			statement.setString(4,device.getImei());
			statement.setString(5,device.getFabricante());
			statement.setString(6,device.getModelo());
			statement.setString(7,device.getAndroidid());
			statement.setString(8,device.getOsversion());
			statement.executeUpdate();
			respuesta.setRespuesta(0);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return respuesta;
	}
	
	public static Respuesta addDeviceEvt(EventoDevice eventoDevice) throws SQLException{
		String INSERT = "INSERT INTO device_evento(device_id, descripcion, usuario, fecha) VALUES (?, ?, ?, ?);";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect=connectionManager.getConexion();
				
		PreparedStatement statement = null;
		Respuesta respuesta = new Respuesta();
		
		try {
			statement = conect.prepareStatement(INSERT);
			statement.setString(1, eventoDevice.getDeviceid());
			statement.setString(2, eventoDevice.getDescripcion());
			statement.setString(3,eventoDevice.getUsuario());
			statement.setTimestamp(4,FechaUtil.getTimeStamp(eventoDevice.getFecha()));
			statement.executeUpdate();
			respuesta.setRespuesta(0);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return respuesta;
	}
	
	public static void registrarLogin(int idusuario,String androidid) throws SQLException{
		String INSERT = "INSERT INTO sessiones_movil(ultimo_login, usuarios_id, android_id) VALUES (NOW(), ?, ?);";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect=connectionManager.getConexion();
				
		PreparedStatement statement = null;
		
		try {
			statement = conect.prepareStatement(INSERT);
			statement.setInt(1, idusuario);
			statement.setString(2, androidid);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
	}
	
	public static void registraReclamo(ReclamoCallCenter reclamoCallcenter,String usuario,int idproyecto) throws SQLException{
		init();
		Connection conection = connectionManager.getConexion();
		String INSERT_RELEVAMIENTO = "INSERT INTO relevamiento_web(usuario_id,fecha_relevamiento,proyecto_id) VALUES(?,NOW(),?);";
		
		int idusuario = ConsultaDB.getIdUsuario(usuario, conection);
		PreparedStatement statement = null;
		statement = conection.prepareStatement(INSERT_RELEVAMIENTO,Statement.RETURN_GENERATED_KEYS);
		statement.setInt(1,idusuario);
		statement.setInt(2,idproyecto);
		statement.executeUpdate();
		ResultSet rs = statement.getGeneratedKeys();
		int last_inserted_id = 0;
		if(rs.next()){
			last_inserted_id = rs.getInt(1);
		}
		statement.clearParameters();
		String INSERT_CALLCENTER = "INSERT INTO relevamiento_call_center(nombre,nro_carnet,telefono,localidad,centro_salud,reclamo,relevamiento_web_id,cedula) "
				                                               + "VALUES(?,?,?,?,?,?,?,?);";
		statement = conection.prepareStatement(INSERT_CALLCENTER,Statement.RETURN_GENERATED_KEYS);
		statement.setString(1,reclamoCallcenter.getNombre());
		statement.setInt(2,reclamoCallcenter.getNrocarnet());
		statement.setString(3,reclamoCallcenter.getTelefono());
		statement.setString(4,reclamoCallcenter.getLocalidad());
		statement.setString(5,reclamoCallcenter.getCentroSalud());
		statement.setString(6,reclamoCallcenter.getReclamo());
		statement.setInt(7,last_inserted_id);
		statement.setString(8,reclamoCallcenter.getCedula());
		statement.executeUpdate();
		
		if (statement != null) {statement.close();}
		if (conection != null) {conection.close();}
	}
}
