package ua.com.newsblog.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import ua.com.newsblog.service.RoleService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"ua.com.newsblog.service", "ua.com.newsblog.dao", "ua.com.newsblog.repository"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ADMIN_REQUEST_URl = "/admin/**";

    private static final String LOGIN_URL = "/login";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String ACCESS_DENIED_PAGE = "/forbidden_exception";

    @Autowired
    public UserDetailsService userDetailsService;

    @Autowired
    private RoleService roleService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers(ADMIN_REQUEST_URl)
                .hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage(LOGIN_URL)
                .usernameParameter(USERNAME)
                .passwordParameter(PASSWORD)
                .defaultSuccessUrl("/", false)
                .and()
                .exceptionHandling().accessDeniedPage(ACCESS_DENIED_PAGE).and()
                .csrf().disable();
    }
}