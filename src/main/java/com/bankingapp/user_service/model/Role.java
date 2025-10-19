package com.bankingapp.user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a role that a user can have within the system (e.g., ROLE_USER, ROLE_ADMIN).
 * This is mapped to the "roles" table in the database.
 */


@Entity // Tells JPA that this class is an entity that should be mapped to a database
@Table(name= "roles") // Specifies the name of the database table.
@Getter // Lombok annotation to automatically generate getter methods for all fields.
@Setter // Lombok annotation to automatically generate setter methods for all fields.
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor, which is required by JPA.
public class Role{

    @Id  // Marks this field as the primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Configures the way the ID is generated. IDENTITY is a good choice for PostgreSQL, letting the database handle it.
    private Integer id;   // Always use wrapper classes (Integer, Long, Double, Boolean) for your entity fields, especially the ID.

    // We use an Enum for the role names to ensure type-safety and prevent errors from typos.


    @Enumerated(EnumType.STRING)  // EnumType.STRING tells JPA to store the string representation of the enum (e.g., "ROLE_USER") in the database. otherwise you will see 0 and 1 for the roles
    @Column(length = 20, unique = true, nullable = false)    // Defines the column properties: max 20 chars, must be unique, cannot be null.
    private ERole name;


    /**
     * An Enumeration for the available roles in the application.
     * Using an enum makes the code cleaner and less error-prone than using raw strings.
     */


    public enum ERole{
        ROLE_USER,
        ROLE_ADMIN
    }

    // A convenience constructor to create a Role object from an ERole enum.

    public Role(ERole name){
        this.name = name;
    }
}