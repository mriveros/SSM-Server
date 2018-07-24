package py.gov.stp.ws.movil.database;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import py.gov.stp.ws.movil.Model.ConfigProyect;
import py.gov.stp.ws.movil.Model.Encuestados;
import py.gov.stp.ws.movil.Model.Formulario;
import py.gov.stp.ws.movil.Model.Institucion;
import py.gov.stp.ws.movil.Model.LoginResult;
import py.gov.stp.ws.movil.Model.Motivos;
import py.gov.stp.ws.movil.Model.Nivel;
import py.gov.stp.ws.movil.Model.PosibleRespuestas;
import py.gov.stp.ws.movil.Model.Pregunta;
import py.gov.stp.ws.movil.Model.PreguntaCondicion;
import py.gov.stp.ws.movil.Model.Proyecto;
import py.gov.stp.ws.movil.Model.ResumenEstadistica;
import py.gov.stp.ws.movil.Model.Seccion;
import py.gov.stp.ws.movil.Model.SeccionCondicion;
import py.gov.stp.ws.movil.Model.Ubicacion;
import py.gov.stp.ws.movil.Model.Usuario;
import py.gov.stp.ws.movil.Model.Validaciones;
import py.gov.stp.ws.movil.Model.Visita;
import py.gov.stp.ws.movil.Util.FechaUtil;

public class ConsultaDB {
	
	private static ConnectionManager connectionManager;
	
	private static void init(){
		if(connectionManager == null){
			connectionManager = new ConnectionManager();
		}
	}
	
	public static LoginResult autenticarUser(String user,String pass) throws SQLException{
		init();
		Connection conect=connectionManager.getConexion();
		
		String SELECT = "SELECT us.usuario,us.distrito_id,dis.departamentos_cod,"
			 	             + "us.logueado,us.estado,us.id,us.nivel_id ";
		String FROM = "FROM usuarios us,proyectos_asignado_usuario pr,distritos dis ";
		String WHERE = "WHERE us.id=pr.usuarios_id AND "
							+ "dis.id=us.distrito_id AND "
				            + "us.usuario='"+user+"' AND "
				            + "us.passwd='"+pass+"';";
		
		PreparedStatement statement = null;
		ResultSet rs=null;
		LoginResult loginResult;

		try {
			statement = conect.prepareStatement(SELECT+FROM+WHERE,
												ResultSet.TYPE_SCROLL_SENSITIVE, 
												ResultSet.CONCUR_UPDATABLE);
			rs=statement.executeQuery();
			if(rs.first()){		
				if(!rs.getBoolean("logueado")){
					if(rs.getInt("estado")==1){
						loginResult = new LoginResult(rs.getString("usuario"), 
								  rs.getString("departamentos_cod"), 
								  rs.getString("distrito_id"),
								  getInstituciones(rs.getInt("id")),
								  rs.getInt("nivel_id"));
						UpdateBD.cambioEstadoLogin(rs.getString("usuario"),true);
						InsertarBD.registrarLogin(rs.getInt("id"), "");
					}else{
						loginResult = LoginResult.USUARIO_DESHABILITADO();
					}
				}else{
					loginResult = LoginResult.USER_OCUPADO();
				}
			}else{
				loginResult = LoginResult.LOGIN_FAILED();
			}
		} catch (SQLException | NullPointerException ex) {
			loginResult = LoginResult.ERROR_SERVER();
			ex.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		
		return loginResult;
	}
	
	public static boolean isProyectSupervisor(String usuario) throws SQLException{
		init();
		Connection conect=connectionManager.getConexion();
		String CONSULTA ="SELECT id FROM usuarios WHERE usuario=? AND nivel_id IN (6,7) ";
		
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conect.prepareStatement(CONSULTA,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			statement.setString(1, usuario);
			rs=statement.executeQuery();
			if(rs.next()){
				return true;
			}else{
				return false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		return false;
	}
	
	private static ArrayList<Institucion> getInstituciones(int usuario_id) throws SQLException{
		init();
		Connection conect = connectionManager.getConexion();
		
		ArrayList<Institucion>instiuciones = new ArrayList<Institucion>();
		
		String SELECT = "SELECT inst.descripcion as descripcion,inst.id as codigo ";
		String FROM = "FROM instituciones inst,proyectos pr,proyectos_asignado_usuario pruser ";
		String WHERE = "WHERE  inst.id=pr.instituciones_id AND pr.id=pruser.proyectos_id AND pruser.usuarios_id=? "
				     + "GROUP BY codigo ";
		
		PreparedStatement statement = null;
		ResultSet rs=null;
		
		try {
			statement = conect.prepareStatement(SELECT+FROM+WHERE,
												ResultSet.TYPE_SCROLL_SENSITIVE, 
												ResultSet.CONCUR_UPDATABLE);
			statement.setInt(1,usuario_id);
			rs=statement.executeQuery();
			while(rs.next()){
				instiuciones.add(new Institucion(rs.getString("descripcion"), rs.getString("codigo")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if (statement != null) {statement.close();}
			if (conect != null) {conect.close();}
		}
		
		return instiuciones;
	}
	
	/**V1*/
	public static ArrayList<Encuestados> getEncuestadoAsig(String usuario) throws SQLException{
		ArrayList<Encuestados>encuestados = new ArrayList<Encuestados>();
		
		String SELECT = "SELECT en.id as idencuestado,en.nombre,en.apellido,"
				             + "en.cedula as documento,te.descripcion AS tipo,en.tipos_destinatarios_id,"
							 + "asign.proyecto_id as proyecto,dist.distrito_id,dist.departamentos_cod,"
							 + "en.esjefe ";
		String FROM = "FROM encuestados en,encuestados_asignados_usuario asign,usuarios us,"
						 + "distritos dist,tipos_destinatarios te ";
		String WHERE = "WHERE en.id=asign.encuestados_id AND "
				           + "us.id=asign.usuarios_id AND "
			 	           + "dist.id=en.distritos_id AND "
			 	           + "te.id=en.tipos_destinatarios_id AND "
			 	           + "usuario=? ";
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		try {
			ps = conect.prepareStatement(SELECT+FROM+WHERE);
			ps.setString(1,usuario);
			ResultSet rs=ps.executeQuery();
			Encuestados encuest;
			while (rs.next()) {
				encuest = new Encuestados();
				encuest.setIdencuestado(rs.getLong("idencuestado"));
				encuest.setNombre(new String(rs.getString("nombre").getBytes("UTF-8"), "UTF-8"));
				encuest.setApellido(new String(rs.getString("apellido").getBytes("UTF-8")));
				encuest.setDocumento(rs.getString("documento"));
				encuest.setProyecto(new String(rs.getString("proyecto").getBytes("UTF-8"), "UTF-8"));
				
				encuest.setTipo(rs.getString("tipo"));
				encuest.setTipo_destinatario_id(rs.getInt("tipos_destinatarios_id"));
				encuest.setDistrito(rs.getInt("distrito_id"));
				encuest.setDepartamento(rs.getInt("departamentos_cod"));
				encuest.setEsjefe(rs.getInt("esjefe"));
				encuestados.add(encuest);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return encuestados;
	}
	
	public static ArrayList<Proyecto> getProyectos(String usuario,boolean isreporte) throws SQLException{
		ArrayList<Proyecto>proyectos = new ArrayList<Proyecto>();
		
		String SELECT = "SELECT pr.id as proyectoid,pr.descripcion as proyecto,inst.id as idinstitucion,"
				             + "inst.descripcion as institucion,pr.proyecto_tipo as tipoproyecto,"
				             + "pr.cantidad_min_imagenes,pr.permitir_alta_destinatarios ";
		String FROM = "FROM proyectos pr,proyectos_asignado_usuario asing,usuarios us,instituciones inst ";
		String WHERE = "WHERE pr.id=asing.proyectos_id AND "
				           + "asing.usuarios_id=us.id AND "
				           + "pr.instituciones_id=inst.id AND "
				           + "usuario=? ";
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		try {
			ps = conect.prepareStatement(SELECT+FROM+WHERE);
			ps.setString(1,usuario);
			ResultSet rs=ps.executeQuery();
			
			Proyecto proyecto;
			ConfigProyect configProyect;
			while (rs.next()) {
				proyecto = new Proyecto();
				proyecto.setProyectoid(rs.getString("proyectoid"));
				proyecto.setProyecto(rs.getString("proyecto"));
				proyecto.setInstitucionid(rs.getString("idinstitucion"));
				proyecto.setInstitucion(rs.getString("institucion"));
				if(!isreporte){
					proyecto.setMotivos(getMotivos(rs.getInt("proyectoid"),false));
					configProyect = new ConfigProyect(rs.getInt("cantidad_min_imagenes"), 
													  rs.getInt("permitir_alta_destinatarios"));
					proyecto.setConfigProyect(configProyect);
				}else{
					proyecto.setTotalUsuarios(getTotalTecnicosProyecto(rs.getInt("proyectoid")));
					proyecto.setTotalRelevamientos(getTotalRelProyecto(rs.getInt("proyectoid"),0));
					proyecto.setTotalBeneficiarios(getTotalDestProyecto(rs.getInt("proyectoid"),0));
				}
				proyecto.setTipo(rs.getInt("tipoproyecto"));
				proyectos.add(proyecto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return proyectos;
	}
	
	public static ArrayList<Motivos>getMotivos(int proyectoid,boolean isreporte) throws SQLException{
		ArrayList<Motivos>motivos = new ArrayList<Motivos>();
		
		String SELECT = "SELECT descripcion as motivo,id as codmotivos ";
		String FROM = "FROM motivos ";
		String WHERE = "WHERE proyectos_id=? AND estado=1 ";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		try {
			ps = conect.prepareStatement(SELECT+FROM+WHERE);
			ps.setInt(1,proyectoid);
			ResultSet rs=ps.executeQuery();
			
			Motivos motivo;
			while (rs.next()) {
				motivo=new Motivos();
				motivo.setMotivo(rs.getString("motivo"));
				motivo.setCodigo_motivo(rs.getString("codmotivos"));
				motivo.setFormulario(getFormulario(rs.getInt("codmotivos"),isreporte));
				if(isreporte){
					motivo.setTotalRelevamientos(getTotalRelMotivo(proyectoid, rs.getInt("codmotivos")));
					motivo.setTotalDestinatarios(getTotalDestMotivo(proyectoid, rs.getInt("codmotivos")));
				}
				
				motivos.add(motivo);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return motivos;
	}
	
	private static Formulario getFormulario(int idmotivo,boolean isreporte) throws SQLException{
		Formulario formulario = new Formulario();
		
		String CONSULTA = "SELECT id,descripcion FROM formularios WHERE motivo_id=?";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1,idmotivo);
			ResultSet rs=ps.executeQuery();
			
			if(rs.next()){
				formulario.setCodigo_form(rs.getInt("id"));
				formulario.setDescrip_form(rs.getString("descripcion"));
				if(!isreporte){
					formulario.setSecciones(getSecciones(idmotivo));
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return formulario;
	} 
	
	public static ArrayList<Seccion>getSecciones(int motivoid) throws SQLException{
		ArrayList<Seccion> secciones = new ArrayList<Seccion>();
		
		String SELECT = "SELECT se.id,se.descripcion,se.tipo,se.condicional,se.totalizable,se.multimedia ";
		String FROM = "FROM secciones se,formularios f ";
		String WHERE = "WHERE f.id=se.formularios_id AND "
						   + "f.motivo_id=? AND "
				           + "se.estado=TRUE "
				     + "ORDER BY indice ASC ";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(SELECT+FROM+WHERE);
			ps.setInt(1,motivoid);
			ResultSet rs=ps.executeQuery();
			
			Seccion seccion;
			while (rs.next()) {
				seccion = new Seccion();
				seccion.setCodSeccion(rs.getString("id"));
				seccion.setSeccion(rs.getString("descripcion"));
				seccion.setPreguntas(getPreguntas(rs.getInt("id")));
				seccion.setTipo(rs.getInt("tipo"));
				seccion.setCondicionada(rs.getInt("condicional"));
				seccion.setTotalizable(rs.getInt("totalizable"));
				seccion.setSiguiente_seccion(getSeccionCondicion(rs.getInt("id")));
				if(rs.getInt("tipo")==4){
					seccion.setMultimedia(rs.getString("multimedia"));
				}
				secciones.add(seccion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return secciones;
	}
	
	public static ArrayList<SeccionCondicion> getSeccionCondicion(int seccionid) throws SQLException{
		ArrayList<SeccionCondicion>arr_seccion_condicion = new ArrayList<SeccionCondicion>();
		
		String CONSULTA = "SELECT id_pregunta_condicionante,id_seccion_siguiente,condicion,valor "
						+ "FROM seccion_condicion "
						+ "WHERE id_seccion_condicionada=? ";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1,seccionid);
			ResultSet rs=ps.executeQuery();
			
			SeccionCondicion seccionCondicion;
			while (rs.next()) {
				seccionCondicion = new SeccionCondicion();
				seccionCondicion.setIdpreguntacondicionante(rs.getLong("id_pregunta_condicionante"));
				seccionCondicion.setIdseccionsiguiente(rs.getLong("id_seccion_siguiente"));
				seccionCondicion.setCondicion(rs.getInt("condicion"));
				seccionCondicion.setValor(rs.getString("valor"));
				arr_seccion_condicion.add(seccionCondicion);
				System.out.println(ps.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return arr_seccion_condicion;
	}
	
	public static ArrayList<Pregunta>getPreguntas(int seccionid) throws SQLException{
		ArrayList<Pregunta>preguntas = new ArrayList<Pregunta>();
		
		String CONSULTA = "SELECT id,descripcion,tipos_preguntas_id,requerido,totalizable,visible FROM preguntas WHERE secciones_id=? AND activo=1 ORDER BY indice ASC ";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1,seccionid);
			ResultSet rs=ps.executeQuery();
			
			Pregunta pregunta;
			while (rs.next()) {
				pregunta = new Pregunta();
				pregunta.setIdpreg(rs.getString("id"));
				pregunta.setPregunta(rs.getString("descripcion"));
				pregunta.setTipo(rs.getString("tipos_preguntas_id"));
				pregunta.setRequerido(rs.getInt("requerido"));
				pregunta.setTotalizable(rs.getInt("totalizable"));
				pregunta.setVisible(rs.getInt("visible"));
				pregunta.setPosibleRespuestas(getPosibleRespuestas(rs.getInt("id")));
				pregunta.setPreguntas_condicionadas(getPreguntaCondicion(rs.getInt("id")));
				pregunta.setValidaciones(getValidaciones(rs.getInt("id")));
				preguntas.add(pregunta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return preguntas;
	}
	
	public static ArrayList<PosibleRespuestas> getPosibleRespuestas(int preguntaid) throws SQLException{
		ArrayList<PosibleRespuestas>posiblesRespuestas = new ArrayList<PosibleRespuestas>();
		
		String CONSULTA = "SELECT id,texto FROM posibles_respuestas WHERE preguntas_id=? ORDER BY indice ASC ";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps=null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1,preguntaid);
			ResultSet rs=ps.executeQuery();
			
			PosibleRespuestas posibleRespuesta;
			while (rs.next()) {
				posibleRespuesta = new PosibleRespuestas();
				posibleRespuesta.setId(rs.getString("id"));
				posibleRespuesta.setTexto(rs.getString("texto"));
				posiblesRespuestas.add(posibleRespuesta);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return posiblesRespuestas;
	}
	
	public static long getIdEncuestadoByCedula(String cedula) throws SQLException{
		String CONSULTA = "SELECT id FROM encuestados WHERE cedula=?";
		//Connection conect=ConnectionConfiguration.conectarMovil();
		
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps=null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setString(1,cedula);
			ResultSet rs=ps.executeQuery();
			while (rs.next()) {
				return rs.getLong("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return 0;
	}
	
	private static ArrayList<PreguntaCondicion> getPreguntaCondicion(int preguntaid) throws SQLException{
		ArrayList<PreguntaCondicion>preguntaCondicions = new ArrayList<PreguntaCondicion>();
		String CONSULTA = "SELECT id_pregunta_condicionada, condicion,valor FROM preguntas_condicion WHERE id_pregunta_condicionante=?";
		//Connection conect=ConnectionConfiguration.conectarMovil();
		
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps=null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1,preguntaid);
			ResultSet rs=ps.executeQuery();
			
			PreguntaCondicion condicion;
			while (rs.next()) {
				condicion = new PreguntaCondicion();
				condicion.setIdpreguntacondicionada(rs.getLong("id_pregunta_condicionada"));
				condicion.setCondicion(rs.getInt("condicion"));
				condicion.setValor(rs.getString("valor"));
				preguntaCondicions.add(condicion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return preguntaCondicions;
	}
	
	private static ArrayList<Validaciones>getValidaciones(int preguntaid) throws SQLException{
		ArrayList<Validaciones>arr_validaciones=new ArrayList<Validaciones>();
		String CONSULTA = "SELECT id_pregunta, condicion, valor_max, valor_min, valor FROM validadar_respuestas WHERE id_pregunta=?";
		//Connection conect=ConnectionConfiguration.conectarMovil();
		
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1,preguntaid);
			ResultSet rs=ps.executeQuery();
			
			Validaciones validaciones;
			ArrayList<String>valores=new ArrayList<String>();
			if(rs.next()) {
				validaciones=new Validaciones();
				validaciones.setIdpregunta(preguntaid);
				validaciones.setCondicion(rs.getInt("condicion"));
				validaciones.setValor_max(rs.getInt("valor_max"));
				validaciones.setValor_min(rs.getInt("valor_max"));
				do{
					valores.add(rs.getString("valor"));
				}while(rs.next());
				validaciones.setValores(valores);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return arr_validaciones;
	}
	
	
	public static long getLastIdSessionByUser(String usuario) throws SQLException{
		String SELECT = "SELECT MAX(s.id) as last ";
		String FROM = "FROM sessiones_movil s,usuarios u ";
		String WHERE = "WHERE s.usuarios_id=u.id AND u.usuario=? ";
		
		//Connection conect=ConnectionConfiguration.conectarMovil();
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(SELECT+FROM+WHERE);
			ps.setString(1, usuario);
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				return rs.getLong("last");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return 0;
	}
	
	public static boolean checkAsignacionExist(Connection connection,long encuestados_id,long usuarios_id,long proyecto_id) throws SQLException{
		String consulta = "SELECT encuestados_id FROM encuestados_asignados_usuario "
			 	        + "WHERE encuestados_id=? AND usuarios_id=? AND proyecto_id=?";
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(consulta);
			ps.setLong(1, encuestados_id);
			ps.setLong(2, usuarios_id);
			ps.setLong(3, proyecto_id);
			ResultSet rs=ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
		}
		return false;
	}
	
	public static boolean checkVisita(Visita visita) throws SQLException{
		init();
		Connection connection = connectionManager.getConexion();
		String CONSULTA = "SELECT id "
				        + "FROM relevamientos "
				        + "WHERE longitud=? AND latitud=? AND fechorini=? AND "
				        	  + "fechorfin=? AND motivos_id=? AND "
				        	  + "usuarios_id=? ";
		
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(CONSULTA);
			ps.setString(1, visita.getLongitud());
			ps.setString(2, visita.getLatitud());
			ps.setTimestamp(3, FechaUtil.getTimeStamp(visita.getHoraini()));
			ps.setTimestamp(4, FechaUtil.getTimeStamp(visita.getHorafin()));
			ps.setLong(5, visita.getMotivo());
			long idusuario = getIdUsuario(visita.getUsuario());
			ps.setLong(6, idusuario);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (connection != null) {connection.close();}
		}
		return false;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public static Usuario getUsuarioByCed(String cedula) throws SQLException{
		String CONSULTA = "SELECT us.id as idusuario,us.cedula,us.passwd,us.nombre,us.apellido,d.descripcion as distrito,"
					           + "us.telefono_particular,us.telefono_corporativo,us.instituciones_id_padre,"
					           + "us.email,nivel.id as idnivel,nivel.descripcion "
						+ "FROM usuarios us,niveles_usuario nivel,distritos d "
						+ "WHERE us.nivel_id=nivel.id AND us.cedula=? AND us.distrito_id=d.id ";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setString(1, cedula);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				Nivel nivel = new Nivel.nivelBuilder()
									   .codigo_nivel(rs.getInt("idnivel"))
									   .descripcion(rs.getString("descripcion"))
									   .build();
				Usuario usuario = new Usuario.usuarioBuilder()
				                             .idusuario(rs.getLong("idusuario"))
				                             .cedula(rs.getString("cedula"))
				                             .password(rs.getString("passwd"))
				                             .nombre(rs.getString("nombre"))
				                             .apellido(rs.getString("apellido"))
				                             .distrito(rs.getString("distrito"))
				                             .telParticular(rs.getString("telefono_particular"))
				                             .telCorporativo(rs.getString("telefono_corporativo"))
				                             .institucion(rs.getInt("instituciones_id_padre"))
				                             .email(rs.getString("email"))
				                             .nivel(nivel)
										     .build();
				return usuario;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return null;
	}
	
	public static boolean checkIsUsuario(Usuario usuario) throws SQLException{
		String CONSULTA = "SELECT id "
						+ "FROM usuarios "
						+ "WHERE id=? AND cedula=? AND nombre=? ";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setLong(1, usuario.getIdusuario());
			ps.setString(2, usuario.getCedula());
			ps.setString(3, usuario.getNombre());
			ResultSet rs=ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return false;
	}
	
	public static int getTotalTecnicosProyecto(int idproyecto) throws SQLException{
		String CONSULTA = "SELECT COUNT(us.id) as cantidad "
				        + "FROM usuarios us,proyectos_asignado_usuario pr_us "
				        + "WHERE us.id=pr_us.usuarios_id AND pr_us.proyectos_id=? ";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		int total = 0;
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idproyecto);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				total = rs.getInt("cantidad");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return total;
	}
	
	public static int getTotalRelProyecto(int idproyecto,int idusuario) throws SQLException{
		String CONSULTA = "SELECT COUNT(rel.id) as cantidad "
				        + "FROM relevamientos rel,motivos m "
				        + "WHERE rel.motivos_id=m.id AND m.proyectos_id=? ";
		
		if(idusuario>0){
			CONSULTA = CONSULTA + "AND rel.usuarios_id=? ";
		}
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		int total = 0;
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idproyecto);
			if(idusuario>0){
				ps.setInt(2, idusuario);
			}
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				total = rs.getInt("cantidad");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return total;
	}
	
	public static int getTotalRelMotivo(int idproyecto,int idmotivo) throws SQLException{
		String CONSULTA = "SELECT COUNT(rel.id) as cantidad "
				        + "FROM relevamientos rel,motivos m "
				        + "WHERE rel.motivos_id=m.id AND m.proyectos_id=? AND m.id=? ";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		int total = 0;
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idproyecto);
			ps.setInt(2, idmotivo);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				total = rs.getInt("cantidad");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return total;
	}
	
	public static int getTotalDestMotivo(int idproyecto,int idmotivo) throws SQLException{
		String CONSULTA = "SELECT COUNT(CONSULTA.encuestados_id) as cantidad "
				        + "FROM (SELECT rel.encuestados_id "
				              + "FROM relevamientos rel,motivos m "
				              + "WHERE rel.motivos_id=m.id AND m.proyectos_id=? AND m.id=? "
				              + "GROUP BY rel.encuestados_id) AS CONSULTA ";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		int total = 0;
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idproyecto);
			ps.setInt(2, idmotivo);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				total = rs.getInt("cantidad");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return total;
	}
	
	public static int getTotalDestProyecto(int idproyecto,int idusuario) throws SQLException{
		String CONSULTA = "SELECT COUNT(en.id) as cantidad "
				        + "FROM encuestados en,encuestados_asignados_usuario en_asig "
				        + "WHERE en.id=en_asig.encuestados_id AND en_asig.proyecto_id=? ";
		
		if(idusuario>0){
			CONSULTA = CONSULTA + "AND en_asig.usuarios_id=?";
		}
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		int total = 0;
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idproyecto);
			if(idusuario>0){
				ps.setInt(2, idusuario);
			}
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				total = rs.getInt("cantidad");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return total;
	}
	
	public static int getTotalVisitasDestinatario(int idproyecto,long idencuestado,long idusuario) throws SQLException{
		String CONSULTA = "SELECT COUNT(*) as cantidad "
				        + "FROM relevamientos rel,motivos m "
				        + "WHERE rel.motivos_id=m.id AND m.proyectos_id=? AND rel.encuestados_id=? ";
		if(idusuario>0){
			CONSULTA = CONSULTA + "AND rel.usuarios_id="+idusuario;
		}
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		int total = 0;
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idproyecto);
			ps.setLong(2, idencuestado);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				total = rs.getInt("cantidad");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return total;
	}
	
	public static ArrayList<Usuario>resumenUsuarioByProyecto(int idproyecto,String cedusuario) throws SQLException{
		ArrayList<Usuario>resumenUsuariByPr = new ArrayList<Usuario>();
		String CONSULTA = "SELECT us.id,us.nombre,us.apellido,us.cedula,d.descripcion as distrito,us.telefono_particular,us.email "
						+ "FROM usuarios us,proyectos_asignado_usuario pr_us,distritos d "
						+ "WHERE us.id=pr_us.usuarios_id AND d.id=us.distrito_id AND pr_us.proyectos_id=? ";
		init();
		Connection conect = connectionManager.getConexion();
		long idusuario = getIdUsuario(cedusuario);
		int nivel_usuario = getNivelUsuario(idusuario, conect);
		switch (nivel_usuario) {
			case 6:
				CONSULTA = "SELECT us.id,us.nombre,us.apellido,us.cedula,d.descripcion as distrito,us.telefono_particular,us.email "
						+ "FROM usuarios us,proyectos_asignado_usuario pr_us,distritos d "
						+ "WHERE us.id=pr_us.usuarios_id AND d.id=us.distrito_id AND pr_us.proyectos_id=? ";
			break;
			case 7:
				CONSULTA = "SELECT us.id,us.nombre,us.apellido,us.cedula,d.descripcion as distrito,us.telefono_particular,us.email "
						 + "FROM usuarios us,proyectos_asignado_usuario pr_us,distritos d,usuario_supervisor ussu "
						 + "WHERE us.id=pr_us.usuarios_id AND "
						       + "d.id=us.distrito_id AND "
						       + "pr_us.proyectos_id=? AND "
						       + "ussu.id_usuario_supervisado=us.id AND "
						       + "ussu.id_usuario_supervisor=?";
			break;
		}
		
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idproyecto);
			switch (nivel_usuario) {
				case 7:
					ps.setLong(2, idusuario);
				break;	
			}
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				Usuario usuario = new Usuario.usuarioBuilder()
											 .idusuario(rs.getLong("id"))
											 .nombre(rs.getString("nombre"))
											 .apellido(rs.getString("apellido"))
											 .cedula(rs.getString("cedula"))
											 .distrito(rs.getString("distrito"))
											 .telParticular(rs.getString("telefono_particular"))
											 .email(rs.getString("email"))
											 .totalRelevamientos(getTotalRelProyecto(idproyecto,rs.getInt("id")))
											 .totalBeneficiarios(getTotalDestProyecto(idproyecto, rs.getInt("id")))
											 .build();
				resumenUsuariByPr.add(usuario);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return resumenUsuariByPr;
	}
	
	public static ArrayList<Encuestados> getResumenBeneficiarios(int idproyecto,int pagina) throws SQLException{
		ArrayList<Encuestados> resumenEncuestados = new ArrayList<Encuestados>();
		String CONSULTA = "SELECT en.id,en.nombre,en.apellido,en.cedula,en.longitud,en.latitud,en.tipo,en.tipos_destinatarios_id,en.esjefe,"
				        + "en.codigo_departamento,en.codigo_distrito,en.codigo_localidad "
				        + "FROM encuestados en,encuestados_asignados_usuario en_asig "
				        + "WHERE en.id=en_asig.encuestados_id AND en_asig.proyecto_id=? "
				        + "GROUP BY en.id "
				        + "ORDER BY en.id ASC "
				        + "LIMIT 100 ";
		String OFFSET;
		if(pagina==1){
			OFFSET = "OFFSET 0 ";
		}else{
			OFFSET = "OFFSET " + (100*(pagina-1));
		}
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		try {
			ps = conect.prepareStatement(CONSULTA+OFFSET);
			ps.setInt(1,idproyecto);
			ResultSet rs=ps.executeQuery();
			Encuestados encuestados;
			while (rs.next()) {
				encuestados = new Encuestados();
				encuestados.setIdencuestado(rs.getLong("id"));
				encuestados.setNombre(rs.getString("nombre").replace("#", " "));
				encuestados.setApellido(rs.getString("apellido").replace("#", " "));
				encuestados.setDocumento(rs.getString("cedula"));
				encuestados.setTipo(rs.getString("tipo"));
				encuestados.setTipo_destinatario_id(rs.getInt("tipos_destinatarios_id"));
				encuestados.setEsjefe(rs.getInt("esjefe"));
				encuestados.setLongitud(rs.getString("longitud"));
				encuestados.setLatitud(rs.getString("latitud"));
				encuestados.setDescrip_departamento(getDepartamento(rs.getInt("codigo_departamento")));
				encuestados.setDescrip_distrito(getDistrito(rs.getInt("codigo_departamento"),
														    rs.getInt("codigo_distrito")));
				encuestados.setDescrip_localidad(getLocalidad(rs.getInt("codigo_departamento"),
															  rs.getInt("codigo_distrito"),
															  rs.getInt("codigo_localidad")));
				encuestados.setTotalVisitas(getTotalVisitasDestinatario(idproyecto,rs.getInt("id"),0));
				resumenEncuestados.add(encuestados);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return resumenEncuestados;
	}
	
	public static ArrayList<Encuestados> getSearchBeneficiario(int idproyecto,String cadena) throws SQLException{
		ArrayList<Encuestados> resumenEncuestados = new ArrayList<Encuestados>();
		String CONSULTA = "SELECT en.id,en.nombre,en.apellido,en.cedula,en.longitud,en.latitud,en.tipo,en.tipos_destinatarios_id,en.esjefe,"
				        + "en.codigo_departamento,en.codigo_distrito,en.codigo_localidad,"
				        + "en.nombre||' '||en.apellido as nombrecompleto  "
				        + "FROM encuestados en,encuestados_asignados_usuario en_asig "
				        + "WHERE en.id=en_asig.encuestados_id AND en_asig.proyecto_id=? AND "
				        	   + "(en.nombre||' '||en.apellido LIKE '%"+cadena+"%' OR en.cedula lIKE '%"+cadena+"%')"
				        + "GROUP BY en.id "
				        + "ORDER BY en.id ASC ";
				     
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1,idproyecto);
			ResultSet rs=ps.executeQuery();
			Encuestados encuestados;
			while (rs.next()) {
				encuestados = new Encuestados();
				encuestados.setIdencuestado(rs.getLong("id"));
				encuestados.setNombre(rs.getString("nombre").replace("#", " "));
				encuestados.setApellido(rs.getString("apellido").replace("#", " "));
				encuestados.setDocumento(rs.getString("cedula"));
				encuestados.setTipo(rs.getString("tipo"));
				encuestados.setTipo_destinatario_id(rs.getInt("tipos_destinatarios_id"));
				encuestados.setEsjefe(rs.getInt("esjefe"));
				encuestados.setLongitud(rs.getString("longitud"));
				encuestados.setLatitud(rs.getString("latitud"));
				encuestados.setDescrip_departamento(getDepartamento(rs.getInt("codigo_departamento")));
				encuestados.setDescrip_distrito(getDistrito(rs.getInt("codigo_departamento"),
														    rs.getInt("codigo_distrito")));
				encuestados.setDescrip_localidad(getLocalidad(rs.getInt("codigo_departamento"),
															  rs.getInt("codigo_distrito"),
															  rs.getInt("codigo_localidad")));
				encuestados.setTotalVisitas(getTotalVisitasDestinatario(idproyecto,rs.getInt("id"),0));
				resumenEncuestados.add(encuestados);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return resumenEncuestados;
	}
	
	private static String getDepartamento(int codDepartamento) throws SQLException{
		String CONSULTA = "SELECT descripcion "
		                + "FROM departamentos "
		                + "WHERE codigo=? ";
				init();
				Connection conect = connectionManager.getConexion();
				PreparedStatement ps = null;
				String departamento = "";
				try {
					ps = conect.prepareStatement(CONSULTA);
					ps.setInt(1, codDepartamento);
					ResultSet rs=ps.executeQuery();
					if(rs.next()){
						departamento = rs.getString("descripcion");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					if (ps != null) {ps.close();}
					if (conect != null) {conect.close();}
				}
				
				return departamento;
	}
	
	private static String getDistrito(int codDepartamento,int codDistrito) throws SQLException{
		String CONSULTA = "SELECT descripcion "
		                + "FROM distritos "
		                + "WHERE departamentos_cod=? AND distrito_id=? ";
				init();
				Connection conect = connectionManager.getConexion();
				PreparedStatement ps = null;
				String distrito = "";
				try {
					ps = conect.prepareStatement(CONSULTA);
					ps.setInt(1, codDepartamento);
					ps.setInt(2, codDistrito);
					ResultSet rs=ps.executeQuery();
					if(rs.next()){
						distrito = rs.getString("descripcion");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					if (ps != null) {ps.close();}
					if (conect != null) {conect.close();}
				}
				
				return distrito;
	}
	
	private static String getLocalidad(int codDepartamento,int codDistrito,int codlocalidad) throws SQLException{
		String CONSULTA = "SELECT descripcion "
		                + "FROM localidades "
		                + "WHERE departamento_id=? AND distrito_id=? AND localidad_id=?";
				init();
				Connection conect = connectionManager.getConexion();
				PreparedStatement ps = null;
				String distrito = "";
				try {
					ps = conect.prepareStatement(CONSULTA);
					ps.setInt(1, codDepartamento);
					ps.setInt(2, codDistrito);
					ps.setInt(3, codlocalidad);
					ResultSet rs=ps.executeQuery();
					if(rs.next()){
						distrito = rs.getString("descripcion");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					if (ps != null) {ps.close();}
					if (conect != null) {conect.close();}
				}
				
				return distrito;
	}
	
	public static Ubicacion getLastUbicacionUser(int idusuario) throws SQLException{
		String CONSULTA = "SELECT longitud,latitud,altitud,\"precision\",proveedor,tipo,fecha_captura "
						+ "FROM ubicaciones "
						+ "WHERE usuarios_id=? "
						+ "ORDER BY id DESC "
						+ "Limit 1";
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		Ubicacion ubicacion = new Ubicacion();
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idusuario);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				ubicacion.setLongitud(rs.getString("longitud"));
				ubicacion.setLatitud(rs.getString("latitud"));
				ubicacion.setAltitud(rs.getString("altitud"));
				ubicacion.setPrecision(rs.getString("precision"));
				ubicacion.setProveedor(rs.getString("proveedor"));
				ubicacion.setTipo(rs.getString("tipo"));
				ubicacion.setHoraobtenido(rs.getString("fecha_captura"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		
		return ubicacion;
	}
	
	public static ArrayList<Proyecto>getProyectosByUsuario(int idusuario) throws SQLException{
		ArrayList<Proyecto> resumProyectos = new ArrayList<Proyecto>();
		String CONSULTA = "SELECT pr.id as proyectoid,pr.descripcion as proyecto,inst.descripcion as institucion "
				       + "FROM proyectos pr,proyectos_asignado_usuario pr_us,usuarios us,instituciones inst "
				       + "WHERE pr.id=pr_us.proyectos_id AND "
				             + "pr_us.usuarios_id=us.id AND "
				             + "pr.instituciones_id=inst.id AND "
				             + "us.id=? ";
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idusuario);
			ResultSet rs=ps.executeQuery();
			Proyecto proyecto = null;
			while(rs.next()){
				proyecto = new Proyecto();
				proyecto.setProyectoid(rs.getString("proyectoid"));
				proyecto.setProyecto(rs.getString("proyecto"));
				proyecto.setInstitucion(rs.getString("institucion"));
				proyecto.setTotalRelevamientos(getTotalRelProyecto(rs.getInt("proyectoid"),idusuario));
				proyecto.setTotalBeneficiarios(getTotalDestProyecto(rs.getInt("proyectoid"),idusuario));
				resumProyectos.add(proyecto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return resumProyectos;
	}
	
	public static ArrayList<ResumenEstadistica>getResumenByFechas(String fechainicio,String fechafin,int idproyecto,int idusuario) throws SQLException{
	  	ArrayList<ResumenEstadistica> arrayList = new ArrayList<ResumenEstadistica>();
	  	String CONSULTA = "SELECT fecha,COUNT(*) as total "
				        + "FROM (SELECT date(rel.fechorini) as fecha "
				              + "FROM relevamientos rel,motivos m   "
				              + "WHERE rel.motivos_id=m.id AND "
				                    + "rel.usuarios_id=? AND "
				                    + "m.proyectos_id=? AND "
				                    + "date(rel.fechorini)>=date('"+fechainicio+"') AND "
				                    + "date(rel.fechorini)<=date('"+fechafin+"')) q1 "
				        + "GROUP BY fecha "
				        + "ORDER BY fecha;";
	  	
	  	init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1, idusuario);
			ps.setInt(2, idproyecto);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				arrayList.add(new ResumenEstadistica(rs.getString("fecha"), rs.getInt("total")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
	  	return arrayList;
	}
	
	public static ArrayList<Encuestados>getFamiliares(long idbeneficiario) throws SQLException{
		ArrayList<Encuestados> arrfamiliares = new ArrayList<Encuestados>();
		
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		String CONSULTA = "SELECT esjefe "
						+ "FROM encuestados " 
						+ "WHERE id=? ";
		
		try {
			ps = conection.prepareStatement(CONSULTA);
			ps.setLong(1, idbeneficiario);
			ResultSet rs=ps.executeQuery();
			long idencuestadoroot = 0;
			Encuestados encuestados = null;
			if(rs.next()){
				if(rs.getInt("esjefe")==1){
					idencuestadoroot=idbeneficiario;
				}else{
					idencuestadoroot = getEncuestadoRootIdById(idbeneficiario);
					arrfamiliares.add(getEncuestadoById(idencuestadoroot));
				}
				ps.close();
				CONSULTA = "SELECT en.id,en.cedula,en.nombre,en.apellido "
						 + "FROM encuestados en,lista_personas lp "
						 + "WHERE en.id=lp.encuestado_asoc_id AND lp.encuestado_root_id=? AND "
						         +"en.id<>? ";
				ps = conection.prepareStatement(CONSULTA);
				ps.setLong(1, idencuestadoroot);
				ps.setLong(2, idbeneficiario);
				rs=ps.executeQuery();
				while(rs.next()){
					encuestados = new Encuestados();
					encuestados.setIdencuestado(rs.getLong("id"));
					encuestados.setNombre(rs.getString("nombre").replace("#", " "));
					encuestados.setApellido(rs.getString("apellido").replace("#", " "));
					encuestados.setDocumento(rs.getString("cedula"));
					encuestados.setTipo("");
					encuestados.setLatitud("");
					encuestados.setLongitud("");
					encuestados.setDescrip_distrito("");
					encuestados.setDescrip_localidad("");
					encuestados.setDescrip_departamento("");
					arrfamiliares.add(encuestados);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		return arrfamiliares;
	}
	
	private static long getEncuestadoRootIdById(long idencuestado) throws SQLException{
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		long idencuestadoroot = 0;
		String CONSULTA = "SELECT encuestado_root_id "
				        + "FROM lista_personas "
				        + "WHERE encuestado_asoc_id=? ";
		try {
			ps = conection.prepareStatement(CONSULTA);
			ps.setLong(1, idencuestado);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				idencuestadoroot = rs.getLong("encuestado_root_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		return idencuestadoroot;
	}
	
	private static Encuestados getEncuestadoById(long idencuestado) throws SQLException{
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		Encuestados encuestados = null;
		String CONSULTA = "SELECT id,cedula,nombre,apellido "
				        + "FROM encuestados "
				        + "WHERE id=? ";
		try {
			ps = conection.prepareStatement(CONSULTA);
			ps.setLong(1, idencuestado);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				encuestados = new Encuestados();
				encuestados.setIdencuestado(rs.getLong("id"));
				encuestados.setNombre(rs.getString("nombre").replace("#", " "));
				encuestados.setApellido(rs.getString("apellido").replace("#", " "));
				encuestados.setDocumento(rs.getString("cedula"));
				encuestados.setTipo("");
				encuestados.setLatitud("");
				encuestados.setLongitud("");
				encuestados.setDescrip_distrito("");
				encuestados.setDescrip_localidad("");
				encuestados.setDescrip_departamento("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		return encuestados;
	}
	
	public static ArrayList<Proyecto> getProyectosByidBenficiarios(long idbeneficiario) throws SQLException{
		ArrayList<Proyecto> arrproyectos = new ArrayList<Proyecto>();
		
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		String CONSULTA = "SELECT pr.id as idproyecto,pr.descripcion as proyecto,"
				               + "inst.id as idinstitucion,inst.descripcion as institucion,"
				               + "pr.proyecto_tipo as tipoproyecto,"
				               + "us.id as idtecnico,us.cedula,us.nombre||' '||us.apellido as tecnico "
						+ "FROM proyectos pr,"
						     + "encuestados_asignados_usuario en_us,"
						     + "instituciones inst,"
						     + "usuarios us "
						+ "WHERE pr.id=en_us.proyecto_id AND "
						      + "inst.id=pr.instituciones_id AND "
						      + "en_us.usuarios_id=us.id AND "
						      + "en_us.encuestados_id=? ";
		
		try {
			ps = conection.prepareStatement(CONSULTA);
			ps.setLong(1, idbeneficiario);
			ResultSet rs=ps.executeQuery();
			Proyecto proyecto = null;
			while(rs.next()){
				proyecto = new Proyecto();
				proyecto.setProyectoid(rs.getString("idproyecto"));
				proyecto.setProyecto(rs.getString("proyecto"));
				proyecto.setInstitucionid(rs.getString("idinstitucion"));
				proyecto.setInstitucion(rs.getString("institucion"));
				proyecto.setTipo(rs.getInt("tipoproyecto"));
				proyecto.setCedtecnico(rs.getString("cedula"));
				proyecto.setNomtecnico(rs.getString("tecnico"));
				proyecto.setTotalUsuarios(0);
				proyecto.setTotalBeneficiarios(0);
				proyecto.setTotalRelevamientos(getTotalVisitasDestinatario(rs.getInt("idproyecto"),idbeneficiario,rs.getLong("idtecnico")));
				arrproyectos.add(proyecto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		
		return arrproyectos;
	}
	
	private static long getIdUsuario(String usuario) throws SQLException{
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
	
	private static int getNivelUsuario(long idusuario,Connection connection) throws SQLException{
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		int nivel = 0;
		String CONSULTA = "SELECT nivel_id FROM usuarios WHERE id=?";
		
		try {
			statement = connection.prepareStatement(CONSULTA);
			statement.setLong(1,idusuario);
			rs=statement.executeQuery();
			if(rs.next()){
				nivel = rs.getInt("nivel_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
		}
		return nivel;
	}
	
	public static int getIdUsuario(String usuario,Connection connection)throws SQLException{
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		int result = 0;
		String CONSULTA = "SELECT id FROM usuarios WHERE usuario=?";
		
		try {
			statement = connection.prepareStatement(CONSULTA);
			statement.setString(1,usuario);
			rs=statement.executeQuery();
			if(rs.next()){
				result = rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (statement != null) {statement.close();}
		}
		return result;
		
	}
}
