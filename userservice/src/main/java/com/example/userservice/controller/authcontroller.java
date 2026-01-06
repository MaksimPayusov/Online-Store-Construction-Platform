package com.example.userservice.controller;


import com.example.userservice.dto.AssignRoleDto;
import com.example.userservice.dto.UserRegistrationDto;
import com.example.userservice.service.Userservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class authcontroller {

    private final Userservice userservice;

    @PostMapping("/register")
    public ResponseEntity<?> authforkeycloak(@RequestBody UserRegistrationDto dto){
        userservice.createUser(dto);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRole(
            @RequestHeader(value = "X-User-Id", required = false) String currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) String currentUserRole,
            @RequestBody AssignRoleDto dto) {
        try {
            // Если заголовки не переданы через KrakenD, возвращаем ошибку
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User ID not found in headers. Please ensure you are authenticated through KrakenD.");
            }
            
            userservice.assignRoleWithPermission(currentUserId, dto.getTargetUserId(), dto.getRoleName());
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}/roles")
    public ResponseEntity<?> getUserRoles(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userservice.getUserRoles(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("path")
    public ResponseEntity<?> getMethodName(@RequestHeader(value = "X-User-Id", required = false) String currentUserId,
                                @RequestHeader(value = "X-User-Role", required = false) String currentUserRole) {
        return ResponseEntity.ok("Hello ur id is "+currentUserId +"\n and ur Role is "+ currentUserRole);
    }
    
}
