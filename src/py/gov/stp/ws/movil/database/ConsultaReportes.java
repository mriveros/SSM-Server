package py.gov.stp.ws.movil.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import py.gov.stp.ws.movil.Model.BeneficiarioSalud;
import py.gov.stp.ws.movil.Model.Encuestados;
import py.gov.stp.ws.movil.Model.Evento;
import py.gov.stp.ws.movil.Model.EventosView;
import py.gov.stp.ws.movil.Model.Formulario;
import py.gov.stp.ws.movil.Model.Marcacion;
import py.gov.stp.ws.movil.Model.Motivos;
import py.gov.stp.ws.movil.Model.Pregunta;
import py.gov.stp.ws.movil.Model.ReclamoCallCenter;
import py.gov.stp.ws.movil.Model.Recorrido;
import py.gov.stp.ws.movil.Model.Relevamiento;
import py.gov.stp.ws.movil.Model.Seccion;
import py.gov.stp.ws.movil.Model.Subformularios;
import py.gov.stp.ws.movil.Model.Ubicacion;
import py.gov.stp.ws.movil.Model.Usuario;
import py.gov.stp.ws.movil.Util.FechaUtil;

public class ConsultaReportes {

	private static ConnectionManager connectionManager;
	
	private static void init(){
		if(connectionManager == null){
			connectionManager = new ConnectionManager();
		}
	}
	
	public static ArrayList<Relevamiento>getRelevamientos(long idproyecto,String cedtecnico,long iddestinatario) throws SQLException{
		ArrayList<Relevamiento>arrRelevamientos = new ArrayList<Relevamiento>();
		String CONSULTA = "SELECT rel.id as idrelevamiento,rel.longitud,rel.latitud,rel.fechorini,rel.fechorfin,"
				                +"rel.diftiempo,m.id as codmotivo,m.descripcion as motivo,rel.observaciones "
				        + "FROM relevamientos rel,usuarios us,motivos m,proyectos p "
				        + "WHERE rel.usuarios_id=us.id AND "
				              + "rel.motivos_id=m.id AND "
				              + "m.proyectos_id=p.id AND "
				              + "p.id=? AND us.cedula=? AND rel.encuestados_id=? ";
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idproyecto);
			statement.setString(2, cedtecnico);
			statement.setLong(3, iddestinatario);
			rs=statement.executeQuery();
			Relevamiento relevamiento;
			Motivos motivo;
			while(rs.next()){
				relevamiento = new Relevamiento();
				relevamiento.setIdrelevamiento(rs.getLong("idrelevamiento"));
				relevamiento.setLongitud(rs.getString("longitud"));
				relevamiento.setLatitud(rs.getString("latitud"));
				relevamiento.setInicio(rs.getString("fechorini"));
				relevamiento.setFin(rs.getString("fechorfin"));
				relevamiento.setDuracion(rs.getDouble("diftiempo"));
				motivo = new Motivos();
				motivo.setCodigo_motivo(rs.getString("codmotivo"));
				motivo.setMotivo(rs.getString("motivo"));
				relevamiento.setMotivo(motivo);
				relevamiento.setObservacion(rs.getString("observaciones"));
				relevamiento.setCantcapturas(getCantidadCapturas(rs.getLong("idrelevamiento")));
				relevamiento.setHasformulario(hasFormulario(rs.getLong("codmotivo")));
				arrRelevamientos.add(relevamiento);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		
		return arrRelevamientos;
	}
	
	public static int getCantidadCapturas(long idrelevamiento) throws SQLException{
		int cantidad = 0;
		String CONSULTA = "SELECT COUNT(id) as cant "
				         +"FROM capturas "
				         +"WHERE relevamientos_id=? ";
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idrelevamiento);
			rs=statement.executeQuery();
			if(rs.next()){
				cantidad = rs.getInt(rs.getInt("cant"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		
		return cantidad;
	}
	
	public static boolean hasFormulario(long idmotivo) throws SQLException{
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		String CONSULTA = "SELECT id "
						+ "FROM formularios "
						+ "WHERE motivo_id=? ";
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idmotivo);
			rs=statement.executeQuery();
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		
		return false;
	}
	
	public static Formulario getFormularioRespuestas(long idrelevamiento,long idproyecto) throws SQLException{
		String CONSULTA = "SELECT f.id,f.descripcion,f.motivo_id "
						+ "FROM formularios f,proyectos p,motivos m,relevamientos rel "
						+ "WHERE f.motivo_id=m.id AND "
						      + "m.proyectos_id=p.id AND "
						      + "rel.motivos_id=m.id AND "
						      + "rel.id=? AND "
							  + "p.id=? ";
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		Formulario formulario = null;
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idrelevamiento);
			statement.setLong(2, idproyecto);
			rs=statement.executeQuery();
			if(rs.next()){
				formulario = new Formulario();
				formulario.setCodigo_form(rs.getInt("id"));
				formulario.setDescrip_form(rs.getString("descripcion"));
				formulario.setSecciones(getSecciones(rs.getInt("motivo_id"),idrelevamiento));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return formulario;
	}
	
	private static ArrayList<Seccion>getSecciones(int motivoid,long idrelevamiento) throws SQLException{
		ArrayList<Seccion> secciones = new ArrayList<Seccion>();
		
		String SELECT = "SELECT se.id,se.descripcion,se.tipo ";
		String FROM = "FROM secciones se,formularios f ";
		String WHERE = "WHERE se.formularios_id=f.id AND "
				           + "f.motivo_id=? AND "
				           + "se.estado=TRUE "
				           + "ORDER BY se.indice ASC ";
		
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conection.prepareStatement(SELECT+FROM+WHERE);
			ps.setInt(1,motivoid);
			ResultSet rs=ps.executeQuery();
			
			Seccion seccion;
			while (rs.next()) {
				seccion = new Seccion();
				seccion.setCodSeccion(rs.getString("id"));
				seccion.setSeccion(rs.getString("descripcion"));
				
				seccion.setTipo(rs.getInt("tipo"));
				switch (rs.getInt("tipo")) {
					case 1:
						seccion.setPreguntas(getPreguntas(rs.getInt("id"),idrelevamiento));
					break;
					case 2:
					break;
					case 3:
						seccion.setLista_hogar(getListaHogar(idrelevamiento,rs.getLong("id")));
					break;	
				}
				secciones.add(seccion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		
		return secciones;
	}
	
	private static ArrayList<Pregunta>getPreguntas(int seccionid,long idrelevamiento) throws SQLException{
		ArrayList<Pregunta>preguntas = new ArrayList<Pregunta>();
		String CONSULTA = "SELECT id,descripcion,tipos_preguntas_id "
				        + "FROM preguntas "
				        + "WHERE secciones_id=? "
				        + "ORDER BY indice ASC ";
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
	
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setInt(1, seccionid);
			rs=statement.executeQuery();
			Pregunta pregunta;
			while (rs.next()) {
				pregunta = new Pregunta();
				pregunta.setIdpreg(rs.getString("id"));
				pregunta.setPregunta(rs.getString("descripcion"));
				pregunta.setTipo(rs.getString("tipos_preguntas_id"));
				pregunta.setRespuestas(getRespuestas(rs.getLong("id"), idrelevamiento, rs.getInt("tipos_preguntas_id")));
				preguntas.add(pregunta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		
		return preguntas;
	}
	
	private static ArrayList<String> getRespuestas(long idpregunta,long idrelevamiento,int tipo) throws SQLException{
		ArrayList<String> respuestas = new ArrayList<String>();
		String CONSULTA = "";
		if(tipo<=2){
			CONSULTA = "SELECT pr.texto AS respuesta "
					+ "FROM detalles_relevamientos dt,posibles_respuestas pr "
					+ "WHERE dt.posiresp_id=pr.id AND dt.relevamientos_id=? AND dt.preguntas_id=? ";
		}else{
			CONSULTA = "SELECT respuesta "
					+ "FROM detalles_relevamientos "
					+ "WHERE relevamientos_id=? AND preguntas_id=? ";
		}
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idrelevamiento);
			statement.setLong(2, idpregunta);
			rs=statement.executeQuery();
			while(rs.next()){
				respuestas.add(rs.getString("respuesta"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		
		return respuestas;
	}
	
	private static ArrayList<Encuestados> getListaHogar(long idrelevamiento,long idseccion) throws SQLException{
		ArrayList<Encuestados> listaHogar = new ArrayList<Encuestados>();
		String CONSULTA = "SELECT en.id,en.cedula,en.nombre,en.apellido "
					    + "FROM lista_personas lp,encuestados en "
					    + "WHERE lp.encuestado_asoc_id=en.id AND "
					          + "lp.relevamientos_id=? AND lp.seccion_id=?";
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		Encuestados encuestados = null;
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idrelevamiento);
			statement.setLong(2, idseccion);
			rs=statement.executeQuery();
			while(rs.next()){
				encuestados = new Encuestados();
				encuestados.setIdencuestado(rs.getLong("id"));
				encuestados.setDocumento(rs.getString("cedula"));
				encuestados.setNombre(rs.getString("nombre"));
				encuestados.setApellido(rs.getString("apellido"));
				listaHogar.add(encuestados);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return listaHogar;
	}
	
	public static ArrayList<String> getListaCapturas(long idrelevamiento) throws SQLException{
		ArrayList<String> arr_capturas = new ArrayList<String>();
		String CONSULTA = "SELECT imagen "
				       	+ "FROM capturas "
				       	+ "WHERE relevamientos_id=? "
				       	+ "ORDER BY numero ";
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idrelevamiento);
			rs=statement.executeQuery();
			while(rs.next()){
				arr_capturas.add(rs.getString("imagen"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return arr_capturas;
	}
	
	public static ArrayList<Evento> getReporteEventos(long idusuario) throws SQLException{
		ArrayList<Evento>arr_evento = new ArrayList<Evento>();
		String CONSULTA = "SELECT id,descripcion,fecha,longitud,latitud, presicion "
						+ "FROM eventos "
						+ "WHERE usuarios_id=? ";
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idusuario);
			rs=statement.executeQuery();
			Evento evento;
			while(rs.next()){
				evento = new Evento();
				evento.setDescripcion(rs.getString("descripcion"));
				evento.setLongitud(rs.getString("longitud"));
				evento.setLatitud(rs.getString("latitud"));
				evento.setPresicion(rs.getString("presicion"));
				evento.setFecha(rs.getString("fecha"));
				evento.setArr_capturas(getCapturasEventos(rs.getLong("id")));
				arr_evento.add(evento);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return arr_evento;
	}
	
	public static ArrayList<String> getCapturasEventos(long idevento) throws SQLException{
		ArrayList<String>arr_capturas_eventos = new ArrayList<String>();
		String CONSULTA = "SELECT imagen "
						+ "FROM capturas_eventos "
						+ "WHERE eventos_id=? ";
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idevento);
			rs=statement.executeQuery();
			while(rs.next()){
				arr_capturas_eventos.add(rs.getString("imagen"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return arr_capturas_eventos;
	}
	
	public static ArrayList<Marcacion> getMarcaciones(long idusuario,int mes,int anho) throws SQLException{
		ArrayList<Marcacion>arr_marcaciones = new ArrayList<Marcacion>();
		String CONSULTA = "SELECT * "
				        + "FROM ubicaciones "
				        + "WHERE (tipos_ubicaciones_id=3 OR tipos_ubicaciones_id=4) AND usuarios_id=? AND "
				        	   + "EXTRACT(MONTH FROM fecha_captura)=? AND EXTRACT(YEAR FROM fecha_captura)=? "
				        + "ORDER BY fecha_captura,tipos_ubicaciones_id ";
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idusuario);
			statement.setInt(2, mes);
			statement.setInt(3, anho);
			rs=statement.executeQuery();
			String lastFecha = "";
			Marcacion marcacion = null;
			Ubicacion ubicacion = null;
			while(rs.next()){
				if(!lastFecha.equals(FechaUtil.parseFecha(rs.getString("fecha_captura")))){
					marcacion = new Marcacion();
					lastFecha = FechaUtil.parseFecha(rs.getString("fecha_captura"));
					marcacion.setFecha(FechaUtil.parseFecha(rs.getString("fecha_captura")));
					arr_marcaciones.add(marcacion);
				}
				
				ubicacion = new Ubicacion();
				ubicacion.setLongitud(rs.getString("longitud"));
				ubicacion.setLatitud(rs.getString("latitud"));
				ubicacion.setAltitud(rs.getString("altitud"));
				ubicacion.setPrecision(rs.getString("precision"));
				ubicacion.setProveedor(rs.getString("proveedor"));
				ubicacion.setHoraobtenido(rs.getString("fecha_captura"));
				ubicacion.setHoraguardado(rs.getString("fecha_insercion"));
				ubicacion.setBateria(rs.getString("bateria"));
				
				if(rs.getInt("tipos_ubicaciones_id") == 3){
					marcacion.setEntrada(ubicacion);
				}
				
				if(rs.getInt("tipos_ubicaciones_id") == 4){
					marcacion.setSalida(ubicacion);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return arr_marcaciones;
	}
	
	public static ArrayList<Recorrido>getRecorrido(long idusuario,int mes,int anho) throws SQLException{
		ArrayList<Recorrido> arr_recorrido = new ArrayList<Recorrido>();
		String CONSULTA = "SELECT * "
						+ "FROM ubicaciones "
						+ "WHERE (tipos_ubicaciones_id=1 OR tipos_ubicaciones_id=2) AND usuarios_id=? AND "
						+ "EXTRACT(MONTH FROM fecha_captura)=? AND EXTRACT(YEAR FROM fecha_captura)=? "
						+ "ORDER BY fecha_insercion ";
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idusuario);
			statement.setInt(2, mes);
			statement.setInt(3, anho);
			rs=statement.executeQuery();
			String lastFecha = "";
			Recorrido recorrido = null;
			ArrayList<Ubicacion> tracking = null;
			Ubicacion ubicacion = null;
			while(rs.next()){
				if(!lastFecha.equals(FechaUtil.parseFecha(rs.getString("fecha_captura")))){
					lastFecha = FechaUtil.parseFecha(rs.getString("fecha_captura"));
					recorrido = new Recorrido();
					recorrido.setFecha(lastFecha);
					tracking = new ArrayList<Ubicacion>();
					recorrido.setTracking(tracking);
					arr_recorrido.add(recorrido);
				}
				
				ubicacion = new Ubicacion();
				ubicacion.setLongitud(rs.getString("longitud"));
				ubicacion.setLatitud(rs.getString("latitud"));
				ubicacion.setAltitud(rs.getString("altitud"));
				ubicacion.setPrecision(rs.getString("precision"));
				ubicacion.setProveedor(rs.getString("proveedor"));
				ubicacion.setHoraobtenido(rs.getString("fecha_captura"));
				ubicacion.setHoraguardado(rs.getString("fecha_insercion"));
				ubicacion.setBateria(rs.getString("bateria"));
				ubicacion.setTipo(rs.getString("tipos_ubicaciones_id"));
				tracking.add(ubicacion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		
		return arr_recorrido;
	}
	
	public static ArrayList<Ubicacion>getTrackingByDay(long idusuario,int mes,int anho,int dia) throws SQLException{
		ArrayList<Ubicacion> arr_tracking = new ArrayList<Ubicacion>();
		String CONSULTA = "SELECT * "
						+ "FROM ubicaciones "
						+ "WHERE tipos_ubicaciones_id<3 AND usuarios_id=? AND "
						+ "EXTRACT(MONTH FROM fecha_captura)=? AND EXTRACT(YEAR FROM fecha_captura)=? AND "
						+ "EXTRACT(DAY FROM fecha_captura)=? "
						+ "ORDER BY fecha_insercion ";
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idusuario);
			statement.setInt(2, mes);
			statement.setInt(3, anho);
			statement.setInt(4, dia);
			rs=statement.executeQuery();
			
			Ubicacion ubicacion = null;
			while(rs.next()){
				ubicacion = new Ubicacion();
				ubicacion.setLongitud(rs.getString("longitud"));
				ubicacion.setLatitud(rs.getString("latitud"));
				ubicacion.setAltitud(rs.getString("altitud"));
				ubicacion.setPrecision(rs.getString("precision"));
				ubicacion.setProveedor(rs.getString("proveedor"));
				ubicacion.setHoraobtenido(rs.getString("fecha_captura"));
				ubicacion.setHoraguardado(rs.getString("fecha_insercion"));
				ubicacion.setBateria(rs.getString("bateria"));
				ubicacion.setTipo(rs.getString("tipos_ubicaciones_id"));
				arr_tracking.add(ubicacion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		
		return arr_tracking;
	}
	
	public static ArrayList<Ubicacion> getMarcacionesProders() throws SQLException{
		ArrayList<Ubicacion> arr_ubicacion = new ArrayList<Ubicacion>();
		String CONSULTA = "SELECT ub.longitud,ub.latitud,ub.altitud,ub.precision,ub.proveedor,"
				               + "ub.fecha_captura,t_ub.descripcion as tipo_ubicacion,"
				               + "us.nombre||' '||us.apellido as tecnico "
						+ "FROM usuarios us,ubicaciones ub,tipos_ubicaciones t_ub,proyectos_asignado_usuario pr_us "
						+ "WHERE us.id=ub.usuarios_id AND ub.tipos_ubicaciones_id=t_ub.id AND "
						       + "pr_us.usuarios_id=us.id AND pr_us.proyectos_id=6 ";
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			rs=statement.executeQuery();
			Ubicacion ubicacion = null;
			while(rs.next()){
				ubicacion = new Ubicacion();
				ubicacion.setLongitud(rs.getString("longitud"));
				ubicacion.setLatitud(rs.getString("latitud"));
				ubicacion.setAltitud(rs.getString("altitud"));
				ubicacion.setPrecision(rs.getString("precision"));
				ubicacion.setProveedor(rs.getString("proveedor"));
				ubicacion.setHoraobtenido(rs.getString("fecha_captura"));
				ubicacion.setTipo(rs.getString("tipo_ubicacion"));
				ubicacion.setUsuario(rs.getString("tecnico"));
				arr_ubicacion.add(ubicacion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return arr_ubicacion;
	}
	
	public static EventosView getEventosByUsuario(long idusuario) throws SQLException{
		EventosView eventosView = null;
		String CONSULTA = "SELECT * "
				        + "FROM v_eventos "
				        + "WHERE u_id=? ";
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			statement.setLong(1, idusuario);
			rs = statement.executeQuery();
			
			if(rs.next()){
				Usuario usuario = new Usuario.usuarioBuilder()
											 .idusuario(rs.getLong("u_id"))
											 .nombre(rs.getString("u_nombre"))
											 .apellido(rs.getString("u_apellido"))
											 .cedula(rs.getString("u_cedula"))
											 .institucion(rs.getInt("i_id_institucion_padre"))
											 .descripcion_institucion(rs.getString("i_descripcion"))
											 .build();
				Evento evento = null;
				ArrayList<String>arr_capturas;
				long id_evento = 0;
				ArrayList<Evento> arr_eventos = new ArrayList<Evento>();
				do{
					if(id_evento != rs.getLong("identificador_del_evento")){
						id_evento = rs.getLong("identificador_del_evento");
						evento = new Evento();
						evento.setId_evento(id_evento);
						evento.setDescripcion(rs.getString("descripcion_del_evento"));
						evento.setFecha(rs.getString("fecha_del_evento"));
						evento.setFecha_inserccion(rs.getString("fecha_insercion_del_evento"));
						evento.setLongitud(rs.getString("longitud"));
						evento.setLatitud(rs.getString("latitud"));
						evento.setPresicion(rs.getString("presicion_del_evento"));
						
						arr_capturas = new ArrayList<String>();
						if(rs.getString("ce_imagen") != null){
							arr_capturas.add(rs.getString("ce_imagen"));
						}
						evento.setArr_capturas(arr_capturas);
						arr_eventos.add(evento);
					}else{
						if(rs.getString("ce_imagen") != null){
							arr_capturas = evento.getArr_capturas();
							arr_capturas.add(rs.getString("ce_imagen"));
							evento.setArr_capturas(arr_capturas);
						}
					}
				}while(rs.next());
				eventosView = new EventosView(usuario, arr_eventos);
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return eventosView;
	}
	
	public static ArrayList<BeneficiarioSalud> getRespuestaSalud() throws SQLException{
		ArrayList<BeneficiarioSalud> arr_beneficiariossalud = new ArrayList<BeneficiarioSalud>();
		//String CONSULTA = "SELECT * FROM relevamiento_salud";
		String CONSULTA = "SELECT cedula,nacimiento,carnet,telefono,nombre,sexo,departamento,distrito,localidad "
				        + "FROM relevamiento_salud_m1 ";
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			rs = statement.executeQuery();
			if(rs.next()){
				BeneficiarioSalud beneficiarioSalud = null;
				while(rs.next()){
					beneficiarioSalud = new BeneficiarioSalud();
					beneficiarioSalud.setCedula(rs.getString("cedula"));
					beneficiarioSalud.setNacimiento(rs.getString("nacimiento"));
					beneficiarioSalud.setCarnet(rs.getString("carnet"));
					beneficiarioSalud.setTelefono(rs.getString("telefono"));
					beneficiarioSalud.setNombre(rs.getString("nombre"));
					beneficiarioSalud.setSexo(rs.getString("sexo"));
					//beneficiarioSalud.setFechaAlta(rs.getString("fechaAlta"));
					//beneficiarioSalud.setLongitud(rs.getString("longitud"));
					//beneficiarioSalud.setLatitud(rs.getString("latitud"));
					beneficiarioSalud.setDepartamento(rs.getString("departamento"));
					beneficiarioSalud.setDistrito(rs.getString("distrito"));
					beneficiarioSalud.setLocalidad(rs.getString("localidad"));
					arr_beneficiariossalud.add(beneficiarioSalud);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return arr_beneficiariossalud;
	}
	
	public static ArrayList<ReclamoCallCenter> getReclamoCallCenter() throws SQLException{
		ArrayList<ReclamoCallCenter> arr_reclamo = new ArrayList<ReclamoCallCenter>();
		String CONSULTA = "SELECT rcall.nombre,rcall.nro_carnet,rcall.telefono,rcall.localidad,"
				               + "rcall.centro_salud,rcall.reclamo,rcall.cedula,rweb.fecha_relevamiento "
				        + "FROM relevamiento_web rweb,relevamiento_call_center rcall "
				        + "WHERE rweb.id=rcall.relevamiento_web_id ";
		
		init();
		Connection conection=connectionManager.getConexion();
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conection.prepareStatement(CONSULTA);
			rs = statement.executeQuery();
			if(rs.next()){
				ReclamoCallCenter reclamoCallCenter = null;
				while(rs.next()){
					reclamoCallCenter = new ReclamoCallCenter();
					reclamoCallCenter.setNombre(rs.getString("nombre"));
					reclamoCallCenter.setNrocarnet(rs.getInt("nro_carnet"));
					reclamoCallCenter.setTelefono(rs.getString("telefono"));
					reclamoCallCenter.setLocalidad(rs.getString("localidad"));
					reclamoCallCenter.setCentroSalud(rs.getString("centro_salud"));
					reclamoCallCenter.setReclamo(rs.getString("reclamo"));
					reclamoCallCenter.setCedula(rs.getString("cedula"));
					reclamoCallCenter.setFechareclamo(rs.getString("fecha_relevamiento"));
					arr_reclamo.add(reclamoCallCenter);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conection != null) {conection.close();}
		}
		return arr_reclamo;
	}
}
