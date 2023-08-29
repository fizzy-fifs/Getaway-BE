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

        String jwt = null;
//        String email = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
//            email = jwtUtil.extractEmail(jwt);
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Token token = tokenService.findByAccessToken(jwt);

            UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(token.getOwner().getEmail());

            if (!jwtUtil.validateAccessToken(token.getAccessToken()) && token.getRefreshTokenExpiration().after(new Date())) {
                String newAccessToken = jwtUtil.generateToken(userDetails);

                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

                token.setAccessToken(newAccessToken);
                token.setAccessTokenExpiration(jwtUtil.extractExpiration(newAccessToken));
                token.setRefreshToken(newRefreshToken);
                token.setRefreshTokenExpiration(jwtUtil.extractExpiration(newRefreshToken));
                tokenService.saveToken(token);

                response.setHeader("NewAccessToken", newAccessToken);
            }

            if (jwtUtil.validateAccessToken(token.getAccessToken())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
        }
        filterChain.doFilter(request, response);
    }
}
