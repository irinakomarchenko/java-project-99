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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userService;
    private final JwtDecoder jwtDecoder;

    /**
     * Configures the main Spring Security filter chain, defining
     * request authorization, stateless session policy and JWT handling.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return configured {@link SecurityFilterChain} instance
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
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
                        )
                        .permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.decoder(jwtDecoder)))
                .build();
    }

    /**
     * Provides the {@link AuthenticationManager} used by Spring Security.
     *
     * @param http the {@link HttpSecurity} builder to extract shared objects
     * @return configured {@link AuthenticationManager} instance
     * @throws Exception if authentication manager setup fails
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthProvider())
                .build();
    }

    /**
     * Provides a DAO-based authentication provider that uses the custom user service
     * and the configured password encoder.
     *
     * @return configured {@link DaoAuthenticationProvider} instance
     */
    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
