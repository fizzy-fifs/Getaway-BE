package com.example.holidayplanner.config;

import com.example.holidayplanner.config.jwt.JwtRequestFilter;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer {
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(myUserDetailsService);
//    }

    @Bean
    public SecurityFilterChain configure (HttpSecurity http) throws Exception {
        return http.cors(withDefaults())
            .csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests((authorize) -> authorize
                    .antMatchers("/api/v1.0/users/newuser", "/api/v1.0/users/login", "/", "/csrf", "/v3/api-docs",
                                "/swagger-resources/configuration/ui", "/configuration/ui",
                                "/swagger-resources", "/swagger-resources/configuration/security",
                                "/configuration/security", "/swagger-ui/**", "/v2/api-docs/**", "/webjars/**", "/swagger-ui.html").permitAll())
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore( jwtRequestFilter, UsernamePasswordAuthenticationFilter.class )
            .addFilterBefore(corsFilter(), SessionManagementFilter.class).build();
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/api/v1.0/users/newuser");
//        web.ignoring().antMatchers("/api/v1.0/users/login");
//        web.ignoring().antMatchers("/", "/csrf", "/v3/api-docs",
//                                                "/swagger-resources/configuration/ui", "/configuration/ui",
//                                                "/swagger-resources", "/swagger-resources/configuration/security",
//                                                "/configuration/security", "/swagger-ui/**", "/v2/api-docs/**", "/webjars/**", "/swagger-ui.html");
//    }

    @Bean
    public  PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    CorsFilter corsFilter() {
        CorsFilter corsFilter = new CorsFilter();
        return corsFilter;
    }
}
