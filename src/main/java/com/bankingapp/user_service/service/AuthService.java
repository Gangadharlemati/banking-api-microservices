package com.bankingapp.user_service.service;

import com.bankingapp.user_service.dto.RegisterRequest;
import com.bankingapp.user_service.model.Role;
import com.bankingapp.user_service.model.User;
import com.bankingapp.user_service.repository.RoleRepository;
import com.bankingapp.user_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.Set;

/**
 * Service class for handling authentication-related business logic,
 * such as user registration and login.
 */


@Service
public class AuthService{

    // Dependencies required by the service, injected via the constructor.
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Registers a new user in the system.
     *
     * @param registerRequest DTO containing the new user's information.
     * @throws RuntimeException if the email is already taken or the default role is not found.
     */


    @Transactional    // This annotation ensures the entire method runs within a single database transaction.
    public void registerUser(RegisterRequest registerRequest){

        // 1. Validate if the email is already in use.
        if(userRepository.existsByEmail(registerRequest.getEmail())){

            throw new RuntimeException("Error: Email is already in use!");
        }

        // 2. Create a new User entity from the DTO.

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());

        // 3. Encode the password before saving.

        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // 4. Assign the default "ROLE_USER" to the new user

        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER).orElseThrow(

                () -> new RuntimeException("Error: Role 'ROLE_USER' is not found.")
        );


        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // 5. Save the new User to the database.
        //    The transaction will be committed upon successful completion of this method.

        userRepository.save(user);  // provided by jpa

    }

}