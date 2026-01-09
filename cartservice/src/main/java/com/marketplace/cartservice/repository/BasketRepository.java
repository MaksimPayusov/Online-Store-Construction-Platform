package com.marketplace.cartservice.repository;

import com.marketplace.cartservice.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BasketRepository extends JpaRepository<Basket, UUID> {
    
    Optional<Basket> findByUserId(UUID userId);
    
    @Query("SELECT b FROM Basket b LEFT JOIN FETCH b.items WHERE b.userId = :userId")
    Optional<Basket> findByUserIdWithItems(@Param("userId") UUID userId);
    
    boolean existsByUserId(UUID userId);
    
    void deleteByUserId(UUID userId);
}
