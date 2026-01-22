package com.trainerworkload.infrastructure.config;

import com.trainerworkload.infrastructure.logging.filter.TransactionIdFilter;
import com.trainerworkload.infrastructure.security.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter,
                                           TransactionIdFilter transactionIdFilter) {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
                                "/actuator/**" ).permitAll().anyRequest().hasRole("SERVICE")
                )
                .addFilterBefore(jwtRequestFilter, LogoutFilter.class)
                .addFilterBefore(transactionIdFilter, SecurityContextHolderFilter.class)
                .build();
    }
}
