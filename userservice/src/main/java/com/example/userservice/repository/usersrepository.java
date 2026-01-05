package com.example.userservice.repository;

import com.example.userservice.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface usersrepository extends JpaRepository<Users, UUID> {

}
