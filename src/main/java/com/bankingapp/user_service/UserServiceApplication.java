package com.bankingapp.user_service;

import com.bankingapp.user_service.model.Role;
import com.bankingapp.user_service.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}


	/**
	 * This bean runs on application startup. It checks if the default roles
	 * (ROLE_USER, ROLE_ADMIN) exist in the database and creates them if they do not.
	 * This ensures that the application always has the necessary roles for registration
	 * and other security functions.
	 *
	 * @param roleRepository The repository for accessing Role data.
	 * @return a CommandLineRunner instance that will be executed by Spring.
	 */
	@Bean
	CommandLineRunner run(RoleRepository roleRepository) {
		return args -> {
			// Check if ROLE_USER exists, if not, create it.
			if (roleRepository.findByName(Role.ERole.ROLE_USER).isEmpty()) {
				roleRepository.save(new Role(Role.ERole.ROLE_USER));
				System.out.println("'ROLE_USER' created."); // Log for confirmation
			}

			// Check if ROLE_ADMIN exists, if not, create it.
			if (roleRepository.findByName(Role.ERole.ROLE_ADMIN).isEmpty()) {
				roleRepository.save(new Role(Role.ERole.ROLE_ADMIN));
				System.out.println("'ROLE_ADMIN' created."); // Log for confirmation
			}
		};
	}

}
