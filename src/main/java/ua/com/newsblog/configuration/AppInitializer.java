package ua.com.newsblog.configuration;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    public Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class, HibernateConfiguration.class, SecurityConfig.class };
    }

    @Override
    public Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    public String[] getServletMappings() {
        return new String[] { "/" };
    }

    /**
     * Настройка ссесии.

     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        // Управление жизненным циклом корневого контекста приложения
       // servletContext.addListener(new SessionListener());

        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter());
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");
    }

    /**
     * Включение исключений NoHandlerFound.
     *
     */
    @Override
    public DispatcherServlet createDispatcherServlet(WebApplicationContext context) {
        final DispatcherServlet dispatcherServlet = (DispatcherServlet) super.createDispatcherServlet(context);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        return dispatcherServlet;
    }
}