package com.example.devhire.application.config;

import com.example.devhire.application.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                return http
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/job-applications")
                                                .hasRole("CANDIDATE")
                                                
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/job-applications/*/status-history")
                                                .hasAnyRole("CANDIDATE", "RECRUITER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/job-applications/me")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/job-applications/*")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/job-applications/*")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/job-applications/job-offer/*")
                                                .hasRole("RECRUITER")

                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/job-applications/*/status")
                                                .hasRole("RECRUITER")

                                                .requestMatchers("/api/resumes/**")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers("/actuator/health", "/error")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .httpBasic(httpBasic -> httpBasic.disable())
                                .formLogin(formLogin -> formLogin.disable())
                                .logout(logout -> logout.disable())

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                (request, response, error) -> response.sendError(
                                                                                HttpStatus.UNAUTHORIZED.value(),
                                                                                "Authentification requise."))

                                                .accessDeniedHandler((request, response, error) -> response.sendError(
                                                                HttpStatus.FORBIDDEN.value(),
                                                                "Accès refusé.")))

                                .build();
        }
}