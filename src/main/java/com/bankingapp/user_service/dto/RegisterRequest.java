package com.bankingapp.user_service.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for handling user registration requests.
 * This class defines the shape of the data the client must send to register a new user.
 * It includes validation annotations to ensure the integrity of the incoming data.
 */




public class RegisterRequest{

    @NotBlank(message = "First Name cannot be blank.")
    @Size(min=2, max=50, message = "First Name must be between 2 and 50 characters.")
    private String firstName;


    @NotBlank(message = "Last Name cannot be blank.")
    @Size(min=2, max=50, message = "Last Name must be between 2 and 50 characters.")
    private String lastName;


    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min=8, max=40, message= "Password must be between 8 and 50 characters.")
    private String password;



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}