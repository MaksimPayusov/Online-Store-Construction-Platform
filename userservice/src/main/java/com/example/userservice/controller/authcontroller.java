package com.example.userservice.controller;


import com.example.userservice.dto.UserRegistrationDto;
import com.example.userservice.service.Userservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class authcontroller {

    private final Userservice userservice;

    @PostMapping("/register")
    public ResponseEntity<?> authforkeycloak(@RequestBody UserRegistrationDto dto){
        userservice.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
