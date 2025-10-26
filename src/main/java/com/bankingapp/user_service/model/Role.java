package com.bankingapp.user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents a role that a user can have within the system (e.g., ROLE_USER, ROLE_ADMIN).
 * This is mapped to the "roles" table in the database.
 */


@Entity // Tells JPA that this class is an entity that should be mapped to a database
@Table(name= "roles") // Specifies the name of the database table.

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

    public Role() {
    }

    // adding cause lombok causing issues when debugging
    public ERole getName() {
        return this.name;
    }

    // A convenience constructor to create a Role object from an ERole enum.

    public Role(ERole name){
        this.name = name;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public void setName(ERole name) {
        this.name = name;
    }

    // It's also good practice to add equals(), hashCode(), and toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && name == role.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

}