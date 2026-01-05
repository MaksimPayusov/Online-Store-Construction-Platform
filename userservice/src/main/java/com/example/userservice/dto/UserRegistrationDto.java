package com.example.userservice.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String email;
    private String firstname;
    private String lastname;
    private String password;
}
