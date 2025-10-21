package com.bankingapp.user_service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Data Transfer Object for sending user information back to the client.
 * This class carefully selects which fields to expose, ensuring sensitive data like
 * the password hash is never sent.
 */


@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse{

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
}