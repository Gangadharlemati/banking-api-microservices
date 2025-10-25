package com.bankingapp.user_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Main security configuration class for the User Service.
 * This class enables web security and configures security filters, password encoding,
 * and HTTP security policies.
 */



@Configuration  // Marks this as a Spring configuration class.
@EnableWebSecurity   // Enables Spring Security's web security support.
public class SecurityConfig{


    /**
     * Defines the primary password encoder for the application.
     * We use BCrypt, which is a strong, industry-standard hashing algorithm.
     * This bean will be available for dependency injection throughout the application.
     *
     * @return A PasswordEncoder instance.
     */


    private final AuthTokenFilter authTokenFilter;

    public SecurityConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



    /**
     * Exposes the AuthenticationManager as a Bean to be used in other parts of the application.
     * The AuthenticationManager is the core of Spring Security's authentication mechanism.
     * It uses the configured UserDetailsService and PasswordEncoder to process authentication requests.
     *
     * @param authConfig The authentication configuration.
     * @return An AuthenticationManager instance.
     * @throws Exception if an error occurs while retrieving the manager.
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    /**
     * Configures the security filter chain that applies to all HTTP requests.
     * This is the central place to define security rules.
     *
     * @param http The HttpSecurity object to configure.
     * @return A SecurityFilterChain instance.
     * @throws Exception if an error occurs during configuration.
     */


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable CSRF (Cross-Site Request Forgery) protection.
        // This is common for stateless REST APIs where the client is not a traditional browser form.

        http.csrf(csrf -> csrf.disable());

        // Configure the session management policy to be STATELESS.
        // This tells Spring Security not to create or use HTTP sessions, which is essential for a JWT-based REST API.

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        // Define authorization rules for different URL patterns.

        http.authorizeHttpRequests(

                auth -> auth   // Permit all requests to the authentication endpoints (e.g., /api/auth/register, /api/auth/login).
                        .requestMatchers("/api/auth/**").permitAll()
                        // Require authentication for any other request.
                        .anyRequest().authenticated()
        );

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}


