package com.bankingapp.user_service.security;

import com.bankingapp.user_service.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


/**
 * Utility class for handling JWT (JSON Web Token) operations such as
 * generation, validation, and parsing.
 */



@Component
public class JwtUtils{

    private final static Logger logger = LoggerFactory.getLogger(JwtUtils.class);


    @Value("${app.jwtSecret}")
    private String jwtSecret;



    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;



    /**
     * Generates a JWT token for an authenticated user.
     *
     * @param authentication The Authentication object containing the user's principal.
     * @return A signed JWT string.
     */


    public String generateJwtToken(Authentication authentication){

        // The principal is the User object since our UserDetails is our User entity.
        User userPrincipal = (User) authentication.getPrincipal();


        // The signing key is derived from the secret string.
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());


        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))   // Set the "subject" of the token (who it's for)
                .setIssuedAt(new Date())     // Set the token issuance date
                .setExpiration(new Date((new Date()).getTime()+ jwtExpirationMs))   // Set the expiration date
                .signWith(key, SignatureAlgorithm.HS512)   // Sign the token with our key and a strong algorithm
                .compact();  // Build the token and serialize it to a compact, URL-safe string
    }






    /**
     * Extracts the username from a given JWT token.
     *
     * @param token The JWT string.
     * @return The username (subject) from the token.
     */


    public String getUserNameFromJwtToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }



    /**
     * Validates a JWT token.
     *
     * @param authToken The JWT string to validate.
     * @return true if the token is valid, false otherwise.
     */


    public boolean validateJwtToken(String authToken) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        }catch (SecurityException e){
            logger.error("Invalid JWT signature: {}", e.getMessage());
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}