package ua.com.newsblog.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = "ua.com.newsblog")
@EnableJpaRepositories(basePackages = "ua.com.newsblog.repository")

public class AppConfig extends WebMvcConfigurerAdapter {

    private static final String RESOURCES_URL = "/resources/";

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry resource) {
        resource.addResourceHandler(RESOURCES_URL + "**").addResourceLocations(RESOURCES_URL);
    }

    //for avtorization

    /**
     * URL запроса для авторизации.
     */
    private static final String LOGIN_URL = "/login";

    /**
     * Название вьюшки авторизации.
     */
    private static final String LOGIN_VIEW_NAME = "client/login";

    @Override
    public void addViewControllers(ViewControllerRegistry viewController) {
        viewController.addViewController(LOGIN_URL).setViewName(LOGIN_VIEW_NAME);
        viewController.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}