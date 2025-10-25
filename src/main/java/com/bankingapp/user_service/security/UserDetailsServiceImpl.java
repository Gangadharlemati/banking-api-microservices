package com.bankingapp.user_service.security;

import com.bankingapp.user_service.model.User;
import com.bankingapp.user_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for loading user-specific data.
 * This class is a core part of Spring Security's authentication process. It is responsible
 * for fetching a user from the database and wrapping it in a UserDetails object.
 */


@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    /**
     * Locates the user based on the username (in our case, the email).
     * This method is called by the AuthenticationManager during the authentication process.
     *
     * @param email The email identifying the user whose data is required.
     * @return a fully populated user record (never {@code null}). Our User entity implements UserDetails.
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority.
     */



    @Override
    @Transactional // Ensures that the user's roles (a lazy collection by default) are fetched within the transaction.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        return user;
    }



}
