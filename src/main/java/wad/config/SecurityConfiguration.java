package wad.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import wad.authentication.JpaAuthenticationProvider;

/**
 * @author Ensio
 */
@Configuration
@EnableWebMvcSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/chat/login")
                .loginProcessingUrl("/authenticate")
                .defaultSuccessUrl("/index")
                .usernameParameter("nickname")
                .passwordParameter("password");

        http.logout()
                .logoutUrl("/chat/logout")
                .logoutSuccessUrl("/index")
                .invalidateHttpSession(true);

    }

    @Configuration
    protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private JpaAuthenticationProvider jpaAuthenticationProvider;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(jpaAuthenticationProvider);
        }
    }

}