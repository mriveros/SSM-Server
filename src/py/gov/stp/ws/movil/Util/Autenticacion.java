package py.gov.stp.ws.movil.Util;

import java.sql.SQLException;

import py.gov.stp.ws.movil.Model.LoginResult;
import py.gov.stp.ws.movil.Model.LoginResult.TipoRespuesta;
import py.gov.stp.ws.movil.Model.Usuario;
import py.gov.stp.ws.movil.database.ConsultaDB;

public class Autenticacion {
	
	private static Autenticacion INSTANCE = null;

    private Autenticacion(){}

    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new Autenticacion();
        }
    }

    public static Autenticacion getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }
    
    public LoginResult autenticarUsuario(String user,String pass){
    	LoginResult loginResult = null;
		try {
			loginResult = ConsultaDB.autenticarUser(user, pass);
			if(loginResult.getRespuesta()==TipoRespuesta.OK.codigo){
				if(ConsultaDB.isProyectSupervisor(user)){
					Usuario usuario = ConsultaDB.getUsuarioByCed(user);
					loginResult.setToken(TokensUtility.getInstance().createToken(Long.toString(usuario.getIdusuario()),
																		         usuario.getNombre(),
																		         usuario.getCedula()));
				}else{
					loginResult = null;
					loginResult = LoginResult.USUARIO_NO_SUPERVISOR();
				}
			}
		} catch (SQLException e) {
			loginResult = LoginResult.ERROR_SERVER(); 
			e.printStackTrace();
		} 
    	return loginResult;
    }
    
    public boolean isTokenValid(String token){
    	Usuario usuario = TokensUtility.getInstance().parseToken(token);
    	try {
    		if(usuario!=null){
    			if(ConsultaDB.checkIsUsuario(usuario)){
    				return true;
    			}
    		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return false;
    }
}
