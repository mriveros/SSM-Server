package py.gov.stp.ws.movil;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("movil")
public class ApplicationConfig extends Application{

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resourses = new HashSet<>();
		addResourcesClass(resourses);
		return resourses;
	}
	
	private void addResourcesClass(Set<Class<?>>resources){
		 
	}
}