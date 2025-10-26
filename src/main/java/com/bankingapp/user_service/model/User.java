package com.bankingapp.user_service.model;

import jakarta.persistence.*;


import org.springframework.security.core.GrantedAuthority;  //question
import org.springframework.security.core.authority.SimpleGrantedAuthority; // question
import org.springframework.security.core.userdetails.UserDetails; // question


import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Represents a user in the system.
 * This entity is mapped to the "users" table and also implements the Spring Security UserDetails interface
 * to integrate directly with the authentication and authorization mechanisms.
 */



@Entity
@Table(name= "users")
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100) //question: why do we have to specify that email is unique in two places
    private String email;

    @Column(nullable = false)  // Length defaults to 255, which is plenty for a bcrypt hash.
    private String password;


    @Column(nullable = false)
    private boolean isEnabled = true;



    // --- Relationships ---

    /**
     * Defines the many-to-many relationship between users and roles.
     * FetchType.EAGER means that whenever a User is loaded, their Roles are loaded at the same time.
     * This is acceptable for a small, critical collection like roles. For larger collections, LAZY is preferred.
     */


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",     // The name of the intermediate join table.
            joinColumns = @JoinColumn(name = "user_id"),  // The foreign key column in the join table that links back to this entity (User).
            inverseJoinColumns = @JoinColumn(name = "role_id")   // The foreign key column that links to the other entity (Role).
    )


    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    // A convenience constructor for creating a user
    public User(String firstName, String lastName, String email, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email =email;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }



    // isEnabled has a special getter name
    public void setEnabled(boolean enabled) { isEnabled = enabled; }




    // --- Spring Security UserDetails implementation ---

    // These methods are required by the UserDetails interface and provide Spring Security
    // with the necessary information to perform authentication and authorization.


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){

        // We convert our Set<Role> into a Collection of SimpleGrantedAuthority objects.
        // Spring Security uses these authorities to enforce role-based access control.

        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
    }


    @Override
    public String getUsername(){
        // We will use the email field as the unique identifier for logging in.
        return this.email;
    }

    @Override
    public String getPassword(){
        return  this.password;
    }


    @Override
    public boolean isAccountNonExpired(){

        // For this project, we'll assume accounts never expire.
        return true;
    }


    @Override
    public boolean isAccountNonLocked(){

        // We'll assume accounts are never locked.
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired(){

        // We'll assume passwords never expire.
        return true;
    }


    @Override
    public boolean isEnabled(){
        // Uses the isEnabled field from our entity.
        return this.isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}