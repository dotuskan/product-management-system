package by.intexsoft.security.config;

import by.intexsoft.security.filter.AuthenticationTokenFilter;
import by.intexsoft.security.service.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public SecurityConfig(TokenAuthenticationService tokenAuthenticationService) {
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/user/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/product/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/stock/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")
                .antMatchers(HttpMethod.POST, "/store/").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE_MANAGER")
                .antMatchers(HttpMethod.PUT, "/store/").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE_MANAGER")
                .antMatchers(HttpMethod.DELETE, "/store/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE_MANAGER")
                .antMatchers("/image/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE_MANAGER")
                .and()
                .addFilterBefore(new AuthenticationTokenFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }
}
