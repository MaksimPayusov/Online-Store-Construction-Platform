package com.marketplace.cartservice.repository;

import com.marketplace.cartservice.entity.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, UUID> {
    
    List<BasketItem> findByBasketId(UUID basketId);
    
    @Query("SELECT bi FROM BasketItem bi WHERE bi.basket.id = :basketId AND bi.productId = :productId")
    Optional<BasketItem> findByBasketIdAndProductId(
        @Param("basketId") UUID basketId, 
        @Param("productId") UUID productId
    );
    
    void deleteByBasketId(UUID basketId);
}
