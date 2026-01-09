package com.marketplace.cartservice.service;

import com.marketplace.cartservice.dto.request.AddItemRequestDto;
import com.marketplace.cartservice.dto.response.CartResponseDto;
import com.marketplace.cartservice.entity.Basket;
import com.marketplace.cartservice.entity.BasketItem;
import com.marketplace.cartservice.exception.ResourceNotFoundException;
import com.marketplace.cartservice.mapper.BasketMapper;
import com.marketplace.cartservice.repository.BasketItemRepository;
import com.marketplace.cartservice.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final BasketMapper basketMapper;

    @Transactional(readOnly = true)
    public CartResponseDto getCart(UUID userId) {
        log.info("Getting cart for user: {}", userId);
        
        Basket basket = basketRepository.findByUserIdWithItems(userId)
            .orElseGet(() -> createEmptyBasket(userId));
        
        return basketMapper.toDto(basket);
    }

    @Transactional
    public CartResponseDto addItem(UUID userId, AddItemRequestDto requestDto) {
        log.info("Adding item to cart for user: {}, productId: {}", userId, requestDto.getProductId());
        
        Basket basket = basketRepository.findByUserIdWithItems(userId)
            .orElseGet(() -> createAndSaveBasket(userId));
        
        Optional<BasketItem> existingItem = basket.getItems().stream()
            .filter(item -> item.getProductId().equals(requestDto.getProductId()))
            .findFirst();
        
        if (existingItem.isPresent()) {
            BasketItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + requestDto.getQuantity());
            log.info("Updated quantity for existing item. New quantity: {}", item.getQuantity());
        } else {
            BasketItem newItem = BasketItem.builder()
                .productId(requestDto.getProductId())
                .shopId(requestDto.getShopId())
                .quantity(requestDto.getQuantity())
                .priceAtAdd(requestDto.getPrice())
                .build();
            basket.addItem(newItem);
            log.info("Added new item to basket");
        }
        
        Basket savedBasket = basketRepository.save(basket);
        return basketMapper.toDto(savedBasket);
    }

    @Transactional
    public CartResponseDto updateItemQuantity(UUID userId, UUID itemId, Integer quantity) {
        log.info("Updating item quantity for user: {}, itemId: {}, quantity: {}", userId, itemId, quantity);
        
        Basket basket = basketRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Basket not found for user: " + userId));
        
        BasketItem item = basket.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item not found in basket: " + itemId));
        
        item.setQuantity(quantity);
        
        Basket savedBasket = basketRepository.save(basket);
        return basketMapper.toDto(savedBasket);
    }

    @Transactional
    public CartResponseDto removeItem(UUID userId, UUID itemId) {
        log.info("Removing item from cart for user: {}, itemId: {}", userId, itemId);
        
        Basket basket = basketRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Basket not found for user: " + userId));
        
        BasketItem itemToRemove = basket.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item not found in basket: " + itemId));
        
        basket.removeItem(itemToRemove);
        
        Basket savedBasket = basketRepository.save(basket);
        return basketMapper.toDto(savedBasket);
    }

    @Transactional
    public void clearCart(UUID userId) {
        log.info("Clearing cart for user: {}", userId);
        
        Optional<Basket> basketOpt = basketRepository.findByUserIdWithItems(userId);
        
        if (basketOpt.isPresent()) {
            Basket basket = basketOpt.get();
            basket.clearItems();
            basketRepository.save(basket);
            log.info("Cart cleared successfully for user: {}", userId);
        } else {
            log.info("No basket found for user: {}, nothing to clear", userId);
        }
    }

    private Basket createEmptyBasket(UUID userId) {
        return Basket.builder()
            .userId(userId)
            .build();
    }

    private Basket createAndSaveBasket(UUID userId) {
        log.info("Creating new basket for user: {}", userId);
        Basket basket = Basket.builder()
            .userId(userId)
            .build();
        return basketRepository.save(basket);
    }
}
