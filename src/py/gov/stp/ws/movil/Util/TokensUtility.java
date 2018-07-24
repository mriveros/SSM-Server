package py.gov.stp.ws.movil.Util;

import java.security.Key;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import py.gov.stp.ws.movil.Model.Usuario;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokensUtility {
	
	private static TokensUtility INSTANCE = null;

    private TokensUtility(){}

    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TokensUtility();
        }
    }

    public static TokensUtility getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }
    
    public String createToken(String id,String subject,String documento){
    	SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    	
    	long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        String apikey=ApiKeyUtility.getInstance().getApiKey();
    	byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(new String(Base64.encodeBase64(apikey.getBytes())));
    	Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    	JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(documento)
                .signWith(signatureAlgorithm, signingKey);
    	return builder.compact();
    }
    
    public Usuario parseToken(String token){
    	String apikey=ApiKeyUtility.getInstance().getApiKey();
        try{
        	Claims claims = Jwts.parser()         
 		           .setSigningKey(DatatypeConverter.parseBase64Binary(new String(Base64.encodeBase64(apikey.getBytes()))))
 		           .parseClaimsJws(token).getBody();
 	
        	return new Usuario.usuarioBuilder()
 	                          .idusuario(Long.parseLong(claims.getId()))
 	                          .nombre(claims.getSubject())
 	                          .cedula(claims.getIssuer())
 	                          .build();
        }catch(Exception ex){}
    	return null;
    }
}
