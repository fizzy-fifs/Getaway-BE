package com.example.holidayplanner.config;

import com.example.holidayplanner.config.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        http.httpBasic();

        http
            .authorizeRequests().antMatchers(HttpMethod.GET).authenticated().and()
            .authorizeRequests().antMatchers(HttpMethod.PUT).authenticated().and()
            .authorizeRequests().antMatchers(HttpMethod.DELETE).authenticated().and()
            .authorizeRequests().antMatchers(HttpMethod.POST).permitAll();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
//                .authorizeRequests().antMatchers("/api/v1.0/users/newuser").anonymous()
        ;

        http.addFilterBefore( jwtRequestFilter, UsernamePasswordAuthenticationFilter.class );
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/v1.0/users/newuser");
        web.ignoring().antMatchers("/swagger-ui/#/");
    }

    @Bean
    public  PasswordEncoder passwordEncoder() {
        return new PasswordEncoderTest();
    }
}
