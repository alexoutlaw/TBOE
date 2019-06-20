package edu.tbo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.tbo.web.filters.MessageFilter;

public class SpringAppInitalizer implements WebApplicationInitializer {
	
	public static Logger log = LogManager.getLogger();
	private static ServletContext servletContext;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		log.info("  +++  TBOE Application Initializer Found +++  ");
		SpringAppInitalizer.servletContext = servletContext;
		
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(SpringConfig.class /*, SpringSecurityConfig.class*/);
		rootContext.setServletContext(servletContext);

		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
				new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");

		servletContext.addFilter("messageFilter", MessageFilter.class).addMappingForUrlPatterns(null, false, "/*");
	}
	
	public static Properties readConfig(String path) {
		InputStream is = null;
		try {
			is = servletContext.getResourceAsStream(path);
			if(is == null)
				throw new FileNotFoundException();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(is);
	        doc.normalize();
	        
	        Properties props = new Properties();
	        NodeList elems = doc.getDocumentElement().getChildNodes();
	        for(int i = 0; i < elems.getLength(); i++) {
	        	Node node = elems.item(i);
	        	if(node.getNodeType() == Node.ELEMENT_NODE) {
	        		Element elem = (Element) node;
	        		if(elem.getTagName() != null)
	        			props.setProperty(elem.getTagName(), elem.getTextContent());
	        	}
	        }
	        return props;
		}
		catch(Exception ex) {
			throw new RuntimeException("Unable to read Config: " + path, ex);
		}
		finally {
			if(is != null) try {is.close();} catch(IOException iex){}
		}
	}
}
