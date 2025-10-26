package com.bankingapp.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;


/**
 * A custom security filter that intercepts every HTTP request to validate the JWT token.
 * This filter runs once per request.
 */




@Component
public class AuthTokenFilter extends OncePerRequestFilter{

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService){
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }




    /**
     * The main logic of the filter. It extracts the JWT from the request, validates it,
     * and sets the user's authentication in the security context if the token is valid.
     *
     * @param request The incoming HTTP request.
     * @param response The outgoing HTTP response.
     * @param filterChain The chain of filters to pass the request along to.
     * @throws ServletException
     * @throws IOException
     */



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        try{
            // 1. Attempt to parse the JWT from the Authorization header.
            String jwt = parseJwt(request);


            // 2. If a valid JWT is found.
            if(jwt != null && jwtUtils.validateJwtToken(jwt)){

                // 3. ...extract the username from the token.
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // 4. Load the user's details from the database.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);


                // 5. Create an Authentication object. This object represents the authenticated user.

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are null because we are authenticating with a token, not a password.
                        userDetails.getAuthorities());


                // 6. Set details for the authentication from the request (e.g., IP address).
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. IMPORTANT: Set the Authentication object in the SecurityContext.
                // This is how we tell Spring Security that the current user is
                SecurityContextHolder.getContext().setAuthentication(authentication);


            }
        }catch (Exception e){
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }


        //8. Continue the filter chain.
        // This passes the request along to the next filter and eventually to the controller.
        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }


    /**
     * A helper method to extract the JWT token from the "Authorization" header.
     *
     * @param request The incoming HTTP request.
     * @return The JWT string, or null if not found or invalid.
     */


    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");


        // Check if the header exists and is correctly formatted ("Bearer <token>").
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Return the token part of the string (substring after "Bearer ").
            return headerAuth.substring(7);
        }

        return null;
    }

}