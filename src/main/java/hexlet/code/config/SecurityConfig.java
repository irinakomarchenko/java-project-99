package hexlet.code.config;

import hexlet.code.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import org.springframework.security.config.Customizer;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userService;
    private final JwtDecoder jwtDecoder;

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
     * <p>
     * This method is not designed for overriding.
     * It creates a configuration source that allows requests only
     * from the specified frontend domain.
     *
     * @return the configured CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://app-waos.onrender.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Defines the main Spring Security filter chain.
     * <p>
     * This method is not designed for overriding.
     * It disables CSRF protection, enables CORS, defines public and protected endpoints,
     * and configures JWT-based authentication for a stateless REST API.
     *
     * @param http the {@link HttpSecurity} instance to configure
     * @return the built {@link SecurityFilterChain}
     * @throws Exception if a configuration error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/welcome",
                                "/api/login",
                                "/index.html",
                                "/assets/**",
                                "/favicon.ico",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/error-test/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.decoder(jwtDecoder)))
                .build();
    }

    /**
     * Configures the {@link AuthenticationManager} for authentication logic.
     * <p>
     * This method is not designed for overriding.
     * It connects the authentication provider that uses the custom user service and password encoder.
     *
     * @param http the {@link HttpSecurity} instance
     * @return the configured {@link AuthenticationManager}
     * @throws Exception if a configuration error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthProvider())
                .build();
    }

    /**
     * Creates and configures a {@link DaoAuthenticationProvider} that uses
     * the {@link CustomUserDetailsService} and {@link PasswordEncoder}.
     * <p>
     * This method is not designed for overriding.
     *
     * @return a fully configured {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
