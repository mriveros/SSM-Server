package py.gov.stp.ws.movil.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import py.gov.stp.ws.movil.Model.FechaHoraServidor;

public class FechaUtil {
	
	public static java.sql.Timestamp getTimeStamp(String fechahora) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date today = formatter.parse(fechahora);
		return new java.sql.Timestamp(today.getTime());
	}
	
	public static String parseFecha(String fecha){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(fecha);
            return new SimpleDateFormat("dd-MMM-yyyy").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	 public static FechaHoraServidor getFechaActual(){
	     Date date =  new Date();
		 FechaHoraServidor fechaHoraServidor = new FechaHoraServidor(Integer.parseInt(new SimpleDateFormat("yyyy").format(date)), 
				 													 Integer.parseInt(new SimpleDateFormat("MM").format(date)), 
				 													 Integer.parseInt(new SimpleDateFormat("dd").format(date)), 
				 													 Integer.parseInt(new SimpleDateFormat("HH").format(date)),
				 													 Integer.parseInt(new SimpleDateFormat("mm").format(date)));
		 return fechaHoraServidor;
	 }
}
