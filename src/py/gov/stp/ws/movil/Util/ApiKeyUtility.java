package py.gov.stp.ws.movil.Util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class ApiKeyUtility {
	
	//Cambiar cuando se decida implementar encerio
	private final String APIKEYTEMPORAL = "16VbPQlSZAXSh0Vf1ue8kYCBUBvKLglT2C46PGgEl0S3KJfQ0OvmeHSf87D9saE3";
	
	private static ApiKeyUtility INSTANCE = null;
	
    private ApiKeyUtility(){}

    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new ApiKeyUtility();
        }
    }

    public static ApiKeyUtility getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }
    
    public String generarApiKey(){
    	try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(512);
			SecretKey secretKey = keyGen.generateKey();
			byte[] encoded = secretKey.getEncoded();
			String apiKey = DatatypeConverter.printHexBinary(encoded).toString();
			return apiKey;
    	} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public String getApiKey(){
    	return APIKEYTEMPORAL;
    }
}
