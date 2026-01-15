package com.example.systemserviceofficial.system.config;

import com.example.commonserviceofficial.security.JwtClaims;
import com.example.commonserviceofficial.security.JwtTokenProvider;
import com.example.commonserviceofficial.security.RoleAuthorityMapper;
import com.example.commonserviceofficial.security.jwt.JwtConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(JwtConstants.TOKEN_PREFIX)) {
            String token = authHeader.substring(7);
            
            try {
                // Validate token first
                if (tokenProvider.validateToken(token)) {
                    JwtClaims claims = tokenProvider.parseToken(token);
                    
                    if (claims != null && claims.getUsername() != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        claims.getUsername(),
                                        null,
                                        RoleAuthorityMapper.map(claims.getRoleCodes())
                                );
                        
                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception ex) {
                log.error("Cannot set user authentication: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
