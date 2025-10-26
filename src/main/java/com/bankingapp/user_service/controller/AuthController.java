package com.bankingapp.user_service.controller;

import com.bankingapp.user_service.dto.JwtResponse;
import com.bankingapp.user_service.dto.LoginRequest;
import com.bankingapp.user_service.dto.RegisterRequest;
import com.bankingapp.user_service.dto.UserInfoResponse;

import com.bankingapp.user_service.model.User;
import com.bankingapp.user_service.security.JwtUtils;
import com.bankingapp.user_service.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;



/**
 * Controller for handling authentication-related endpoints like user registration and login.
 */





@CrossOrigin(origins="*", maxAge = 3600) // Allows cross-origin requests, useful for development with a separate frontend.
@RestController      // Combines @Controller and @ResponseBody, marks this class as a request handler that writes directly to the response body.
@RequestMapping("/api/auth")  // All endpoints in this controller will be prefixed with /api/auth.
public class AuthController{

    private final AuthService authService;
    private final JwtUtils jwtUtils;



    public AuthController(AuthService authService, JwtUtils jwtUtils){
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }


    /**
     * Endpoint for user registration.
     *
     * @param registerRequest The request body containing user registration details.
     * @return A success message upon successful registration.
     */

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest){

        authService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully!");
    }




    /**
     * Endpoint for user login.
     *
     * @param loginRequest The request body containing user login credentials.
     * @return A ResponseEntity containing a JWT and user details.
     */

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Authenticate the user using the AuthService.
        Authentication authentication = authService.authenticateUser(loginRequest);

        // 2. If authentication is successful, generate a JWT.
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 3. Get the user details from the Authentication object.
        User userDetails = (User) authentication.getPrincipal();

        // 4. Get the roles and convert them to a list of strings.
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        // 5. Create the response object. consider this as a dto. and our model is not going beyond the controller.
        JwtResponse response = new JwtResponse(jwt, userDetails.getId(), userDetails.getFirstName(), userDetails.getEmail(), roles);

        return ResponseEntity.ok(response);
    }
}