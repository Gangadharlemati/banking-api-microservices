package com.bankingapp.user_service.repository;

import com.bankingapp.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The UserRepository interface is a Spring Data JPA repository for the {@link User} entity.
 * It provides the mechanism for all data access operations related to users, abstracting the
 * underlying database interactions.
 *
 * <p>By extending {@link JpaRepository}, this interface inherits a comprehensive set of CRUD
 * (Create, Read, Update, Delete) operations. Additionally, it defines custom query methods
 * tailored to the application's specific needs, such as finding a user by email.</p>
 *
 * @see JpaRepository
 * @see User
 */
@Repository // Marks this interface as a Spring component for dependency injection and exception translation.
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a User entity by its email address.
     *
     * <p>This method is fundamental for the authentication process, as it allows the
     * UserDetailsService to load a user by their unique username (which is their email in this application).
     * The query is derived automatically by Spring Data JPA from the method name.</p>
     *
     * @param email The email address of the user to find.
     * @return An {@link Optional} containing the found {@link User} if one exists with the given email,
     *         or an empty Optional if no such user is found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks for the existence of a User with the given email address.
     * By Default Jpa only creates the automatic query for the primary key annotations
     *
     * everything else has to be written by the user
     * <p>This method is a performance-optimized query used primarily during user registration
     * to validate if an email is already in use. It is more efficient than fetching the entire
     * User entity, as it typically translates to a `SELECT COUNT(...)` SQL query.</p>
     *
     * @param email The email address to check for existence.
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    Boolean existsByEmail(String email);
}