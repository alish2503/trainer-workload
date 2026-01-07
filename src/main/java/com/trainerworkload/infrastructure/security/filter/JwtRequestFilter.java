package com.trainerworkload.infrastructure.security.filter;

import com.trainerworkload.infrastructure.security.port.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Autowired
    public JwtRequestFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtService.isValidToken(token)) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                List<String> roles = jwtService.getRoles(token);
                if (roles != null && roles.contains("SERVICE")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_SERVICE"));
                }
                if (!authorities.isEmpty()) {
                    Authentication auth = new UsernamePasswordAuthenticationToken("SERVICE",
                            null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid token\"}");
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
