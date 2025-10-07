package hexlet.code.config;

import hexlet.code.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import java.util.List;

/**
 * Main security configuration for the application.
 * <p>
 * Configures CORS, authentication providers, authorization rules,
 * JWT and Basic authentication for both REST API and Swagger UI.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userService;
    private final CustomAuthEntryPoint customAuthEntryPoint;

    /**
     * Defines CORS configuration to allow requests from the Render frontend.
     *
     * @return a configured {@link CorsConfigurationSource}
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://app-waos.onrender.com",
                "http://localhost:8080"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Total-Count"));
        config.setExposedHeaders(List.of("Authorization", "X-Total-Count"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Builds the Spring Security filter chain.
     *
     * @param http         {@link HttpSecurity} to configure
     * @param introspector {@link HandlerMappingIntrospector} for MVC path matching
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if any security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {

        SecurityFilterChain chain = http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/welcome",
                                "/index.html", "/assets/**", "/favicon.ico",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/v3/api-docs.yaml", "/webjars/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthEntryPoint))
                .oauth2ResourceServer(rs -> rs.bearerTokenResolver(new DefaultBearerTokenResolver())
                        .jwt(jwt -> jwt.decoder(jwtDecoder)))
                .httpBasic(Customizer.withDefaults())
                .build();

        return chain;
    }

    /**
     * Creates the authentication manager used by Spring Security.
     *
     * @param http {@link HttpSecurity} instance
     * @return configured {@link AuthenticationManager}
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManager manager = http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthProvider())
                .build();
        return manager;
    }

    /**
     * Creates the DAO authentication provider that uses
     * {@link CustomUserDetailsService} and {@link PasswordEncoder}.
     *
     * @return configured {@link AuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
