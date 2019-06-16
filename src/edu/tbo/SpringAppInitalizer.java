package edu.tbo;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import edu.tbo.web.filters.MessageFilter;

public class SpringAppInitalizer implements WebApplicationInitializer {
	
	public static Logger log = LogManager.getLogger();

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		log.info("  +++  TBOE Application Initializer Found +++  ");
		
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(SpringConfig.class /*, SpringSecurityConfig.class*/);
		rootContext.setServletContext(servletContext);

		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
				new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");

		servletContext.addFilter("messageFilter", MessageFilter.class).addMappingForUrlPatterns(null, false, "/*");
	}
}
