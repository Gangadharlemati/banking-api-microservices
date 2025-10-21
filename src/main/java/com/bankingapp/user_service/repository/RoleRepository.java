package com.bankingapp.user_service.repository;

import com.bankingapp.user_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * The RoleRepository interface is a Spring Data JPA repository for Role entities.
 * By extending JpaRepository, it inherits a set of standard CRUD (Create, Read, Update, Delete)
 * operations for the Role entity without requiring any implementation.
 *
 * <p>This repository acts as the data access layer (DAO) for Role-related operations,
 * abstracting the underlying database interactions and providing a clean, high-level API
 * for the service layer to use.</p>
 *
 * @see JpaRepository
 * @see Role
 */


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

    /**
     * Finds a Role entity by its name.
     *
     * <p>This is a query method that Spring Data JPA implements automatically based on the
     * method name. The query derived is equivalent to "SELECT r FROM Role r WHERE r.name = :name".</p>
     *
     * @param name The ERole enum value representing the name of the role to find (e.g., ERole.ROLE_USER).
     * @return An {@link Optional} containing the found {@link Role} if it exists, or an empty Optional otherwise.
     *         Using Optional is a best practice to handle cases where a role might not be found,
     *         avoiding NullPointerExceptions.
     *
     *
     *         The main purpose of this is to check if our roles table has the roles that we described in enum, this will check and make sure that
     *         no one has removed them.
     */

    Optional<Role> findByName(Role.ERole name);
}