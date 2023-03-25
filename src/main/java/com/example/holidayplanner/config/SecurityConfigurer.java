package com.example.holidayplanner.config;

import com.example.holidayplanner.config.jwt.JwtRequestFilter;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;

@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http
            .authorizeRequests().antMatchers(HttpMethod.GET).authenticated().and()
            .authorizeRequests().antMatchers(HttpMethod.PUT).authenticated().and()
            .authorizeRequests().antMatchers(HttpMethod.DELETE).authenticated().and()
            .authorizeRequests().antMatchers(HttpMethod.POST).authenticated().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore( jwtRequestFilter, UsernamePasswordAuthenticationFilter.class );
        http.addFilterBefore(corsFilter(), SessionManagementFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/v1.0/users/newuser");
        web.ignoring().antMatchers("/api/v1.0/users/login");
        web.ignoring().antMatchers("/", "/csrf", "/v3/api-docs",
                                                "/swagger-resources/configuration/ui", "/configuration/ui",
                                                "/swagger-resources", "/swagger-resources/configuration/security",
                                                "/configuration/security", "/swagger-ui/**", "/v2/api-docs/**", "/webjars/**", "/swagger-ui.html");
    }

    @Bean
    public  PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return null;
            }
        };
    }

    @Bean
    CorsFilter corsFilter() {
        CorsFilter corsFilter = new CorsFilter();
        return corsFilter;
    }
}
