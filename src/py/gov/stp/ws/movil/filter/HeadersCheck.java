package py.gov.stp.ws.movil.filter;

import javax.ws.rs.core.HttpHeaders;

import py.gov.stp.ws.movil.Util.Autenticacion;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;

public class HeadersCheck {
	
	private static HeadersCheck INSTANCE = null;
	
	 private HeadersCheck(){}
	 
	 private synchronized static void createInstance() {
	       if (INSTANCE == null) { 
	            INSTANCE = new HeadersCheck();
	       }
	 }

	 public static HeadersCheck getInstance() {
	      if (INSTANCE == null) createInstance();
	        return INSTANCE;
	}
	 
	public void validToken(HttpHeaders headers) throws ExcepcionTokenValid{
		 try{
			 String authorizationHeader = headers.getRequestHeader("Authorization").get(0);
			 if(authorizationHeader != null && !authorizationHeader.equals("")){
				 String token = authorizationHeader.substring("Bearer".length()).trim();
				 if(!Autenticacion.getInstance().isTokenValid(token)){
					 throw new ExcepcionTokenValid("TOKEN INVALIDO");
				 }
			 }else{
				 throw new ExcepcionTokenValid("TOKEN INVALIDO"); 
			 }
		 }catch(NullPointerException ex){
			 throw new ExcepcionTokenValid("TOKEN INVALIDO");
		 }
	}
}
