package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class LoginResult {
	
	private int respuesta = 0;
	private String usuario;
	private String departamento;
	private String distrito;
	private ArrayList<Institucion>instituciones;
	private int nivel;
	private String token;
	
	
	public LoginResult(int respuesta) {
		super();
		this.respuesta = respuesta;
	}

	public LoginResult(String usuario, String departamento,String distrito, 
					   ArrayList<Institucion> instituciones,int nivel) {
		super();
		this.respuesta = 0;
		this.usuario = usuario;
		this.departamento = departamento;
		this.distrito = distrito;
		this.instituciones = instituciones;
		this.nivel = nivel;
	}
	
	public static LoginResult LOGIN_FAILED(){
		return new LoginResult(TipoRespuesta.ERROR.codigo);
	}
	
	public static LoginResult USER_OCUPADO(){
		return new LoginResult(TipoRespuesta.USUARIO_ENUSO.codigo);
	}
	
	public static LoginResult ERROR_SERVER(){
		return new LoginResult(TipoRespuesta.ERROR_SERVIDOR.codigo);
	}
	
	public static LoginResult USUARIO_DESHABILITADO(){
		return new LoginResult(TipoRespuesta.USUARIO_DESHABILITADO.codigo);
	}
	
	public static LoginResult USUARIO_NO_SUPERVISOR(){
		return new LoginResult(TipoRespuesta.NO_ES_SUPERVISOR.codigo);
	}

	public int getRespuesta() {
		return respuesta;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getDepartamento() {
		return departamento;
	}

	public String getDistrito() {
		return distrito;
	}

	public ArrayList<Institucion> getInstituciones() {
		return instituciones;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public int getNivel(){
		return this.nivel;
	}

	@Override
	public String toString() {
		return "LoginResult [respuesta=" + respuesta + ", usuario=" + usuario
				+ ", departamento=" + departamento + ", distrito=" + distrito
				+ ", instituciones=" + instituciones + "]";
	}
	
	public static enum TipoRespuesta{
        OK(0),
        ERROR(1),
        USUARIO_DESHABILITADO(2),
        USUARIO_ENUSO(3),
        ERROR_SERVIDOR(4),
        NO_ES_SUPERVISOR(5);
        
        public final int codigo;
        
        TipoRespuesta(int codigo) {
            this.codigo = codigo;
        }
	}    
}
