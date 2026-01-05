package com.example.userservice.service;

import com.example.userservice.dto.UserRegistrationDto;
import com.example.userservice.entity.Users;
import com.example.userservice.repository.usersrepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Userservice {

    private final Keycloak keycloak;
    private final usersrepository usersrepository;

    public void createUser(UserRegistrationDto dto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getEmail());
        user.setFirstName(dto.getFirstname());
        user.setLastName(dto.getLastname());
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(dto.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));
        user.setEmailVerified(false);
        user.setRequiredActions(Collections.singletonList("VERIFY_EMAIL"));

        Response response = keycloak.realm("main_one").users().create(user);

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            keycloak.realm("main_one").users().get(userId).sendVerifyEmail();
            Users localUser = new Users();
            localUser.setId(UUID.fromString(userId));
            localUser.setEmail(user.getEmail());
            usersrepository.save(localUser);
        } else {
            String errorMessage = response.readEntity(String.class);
            System.err.println("Keycloak Error Status: " + response.getStatus());
            System.err.println("Keycloak Error Body: " + errorMessage);

            throw new RuntimeException("User creation failed: " + response.getStatus() + " " + errorMessage);
        }
        }
}

