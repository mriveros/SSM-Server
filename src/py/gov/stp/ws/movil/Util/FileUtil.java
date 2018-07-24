package py.gov.stp.ws.movil.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Respuesta;

public class FileUtil {
	
	public static void  receiverFile(String nombre,String archivo) throws IOException{
		byte[] bytes = Base64.decodeBase64(archivo);
		FileOutputStream fos;
		fos = new FileOutputStream(new File("/usr/share/tomcat/moviles/"+nombre));
		fos.write(bytes); 
		fos.close();
	}
	
	public static String  receiverFile(String nombre,InputStream fileInputStream){
		FileOutputStream fos = null;
		String hashfile = "";
		String path = "/usr/share/tomcat/moviles/"+nombre;
		File  file = new File(path);
		try {
			fos = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = fileInputStream.read(bytes)) != -1) {
				fos.write(bytes, 0, read);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (fos != null) {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		file = null;
		file = new File(path);
		hashfile = getHashOfFile(file);
		
		return hashfile;
	}
	
	public static Respuesta checkImagenFile(Captura captura){
		Respuesta respuesta = new Respuesta();
		File file = new File("/usr/share/tomcat/moviles/capturas/"+captura.getNombre());
		if(file.exists() && file.length() == captura.getSize()){
			respuesta.setRespuesta(0);
		}else{
			respuesta.setRespuesta(2);
		}
		return respuesta;
	}
	
	public static String getHashOfFile(File file){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                messageDigest.update(byteArray, 0, bytesCount);
            }
            fis.close();
            byte[] bytes = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
	
}
