package com.example.userservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Table(name = "api_user")
@Entity
public class Users {
    @Id
    private UUID id;

    private String email;
}
