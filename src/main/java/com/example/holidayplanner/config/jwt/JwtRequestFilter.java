package com.example.holidayplanner.config.jwt;

import com.example.holidayplanner.config.MyUserDetailsService;
import com.example.holidayplanner.config.jwt.token.Token;
import com.example.holidayplanner.config.jwt.token.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final TokenService tokenService;

    public JwtRequestFilter(JwtUtil jwtUtil, TokenService tokenService) {
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        final Object headers = request.getHeaderNames();

        String accessToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
        }

        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Token token = tokenService.findByAccessToken(accessToken);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(token.getOwner().getEmail());

            if (token.getRefreshTokenExpiration().before(new Date())) {
                filterChain.doFilter(request, response);
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
