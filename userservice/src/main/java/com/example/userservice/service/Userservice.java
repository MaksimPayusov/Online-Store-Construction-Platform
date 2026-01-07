package com.example.userservice.service;

import com.example.userservice.dto.UserRegistrationDto;
import com.example.userservice.entity.Users;
import com.example.userservice.repository.usersrepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Userservice {

    private final Keycloak keycloak;
    private final usersrepository usersrepository;
    private static final String REALM_NAME = "main_one";
    private static final String DEFAULT_ROLE = "user";
    
    @Async
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
        RealmResource realm = keycloak.realm(REALM_NAME);
        Response response = realm.users().create(user);

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource userResource = realm.users().get(userId);
            
            // Назначаем роль в зависимости от выбора пользователя
            // Если роль не указана или невалидна, назначаем "user" по умолчанию
            String roleToAssign = DEFAULT_ROLE;
            if (dto.getRole() != null && ("owner".equals(dto.getRole()) || "user".equals(dto.getRole()))) {
                roleToAssign = dto.getRole();
            }
            assignRoleToUser(userId, roleToAssign);
            
            userResource.sendVerifyEmail();
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
    
    /**
     * Назначает роль пользователю
     * @param userId ID пользователя в Keycloak
     * @param roleName Название роли (admin, owner, user)
     */
    public void assignRoleToUser(String userId, String roleName) {
        RealmResource realm = keycloak.realm(REALM_NAME);
        UserResource userResource = realm.users().get(userId);
        
        // Получаем роль из realm
        RoleResource roleResource = realm.roles().get(roleName);
        RoleRepresentation role = roleResource.toRepresentation();
        
        // Назначаем роль пользователю
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }
    
    /**
     * Назначает роль пользователю (только админ может назначать админов)
     * @param currentUserId ID текущего пользователя (кто назначает)
     * @param targetUserId ID пользователя, которому назначается роль
     * @param roleName Название роли
     * @throws SecurityException если попытка назначить админа не админом
     */
    public void assignRoleWithPermission(String currentUserId, String targetUserId, String roleName) {
        // Проверяем, что если назначается роль admin, то текущий пользователь должен быть admin
        if ("admin".equals(roleName)) {
            if (!hasRole(currentUserId, "admin")) {
                throw new SecurityException("Only admins can assign admin role");
            }
        }
        
        assignRoleToUser(targetUserId, roleName);
    }
    
    /**
     * Проверяет, имеет ли пользователь указанную роль
     */
    public boolean hasRole(String userId, String roleName) {
        RealmResource realm = keycloak.realm(REALM_NAME);
        UserResource userResource = realm.users().get(userId);
        List<RoleRepresentation> roles = userResource.roles().realmLevel().listAll();
        
        return roles.stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }
    
    /**
     * Получает список ролей пользователя
     */
    public List<String> getUserRoles(String userId) {
        RealmResource realm = keycloak.realm(REALM_NAME);
        UserResource userResource = realm.users().get(userId);
        List<RoleRepresentation> roles = userResource.roles().realmLevel().listAll();
        
        return roles.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }
}

