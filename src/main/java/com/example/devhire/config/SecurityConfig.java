package com.example.devhire.config;

import com.example.devhire.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.devhire.security.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        DaoAuthenticationProvider authenticationProvider(
                        CustomUserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean
        AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        DaoAuthenticationProvider authenticationProvider,
                        JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

                return http
                                // Correct uniquement car nous utiliserons un JWT dans
                                // l'en-tête Authorization, pas une authentification par cookie.
                                .csrf(csrf -> csrf.disable())

                                // Aucun état de session n'est conservé côté serveur.
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .authorizeHttpRequests(authorize -> authorize

                                                // Public
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/users")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/auth/login")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/job-offers/**")
                                                .permitAll()

                                                // Candidate
                                                .requestMatchers(
                                                                "/api/candidate-profiles/**")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers("/api/resumes/**")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/job-applications/me")
                                                .hasRole("CANDIDATE")

                                                // Recruiter
                                                .requestMatchers(
                                                                "/api/recruiter-profiles/**")
                                                .hasRole("RECRUITER")

                                                // Job offers: writes only by recruiters
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/job-offers/**")
                                                .hasRole("RECRUITER")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/job-offers/**")
                                                .hasRole("RECRUITER")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/job-offers/**")
                                                .hasRole("RECRUITER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/job-applications/received")
                                                .hasRole("RECRUITER")

                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/job-applications/*/status")
                                                .hasRole("RECRUITER")

                                                // Applications:
                                                // POST by candidates; recruiter consultation will be refined
                                                // with ownership checks inside the service.
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/job-applications/**")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/job-applications/**")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/job-applications/**")
                                                .hasRole("CANDIDATE")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/job-applications/*")
                                                .hasAnyRole("CANDIDATE", "RECRUITER", "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/users/me")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/users/me/deactivate")
                                                .authenticated()

                                                // Users: temporary admin-only access.
                                                .requestMatchers("/api/users/**").hasRole("ADMIN")

                                                .requestMatchers("/error").permitAll()

                                                .anyRequest().authenticated())

                                // Nous n'utilisons ni Basic Auth ni formulaire de connexion.
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