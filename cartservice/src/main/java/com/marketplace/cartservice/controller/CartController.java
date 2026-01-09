package com.marketplace.cartservice.controller;

import com.marketplace.cartservice.dto.request.AddItemRequestDto;
import com.marketplace.cartservice.dto.request.UpdateQuantityRequestDto;
import com.marketplace.cartservice.dto.response.CartResponseDto;
import com.marketplace.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(
        @RequestHeader("X-User-Id") String userId
    ) {
        UUID userUuid = UUID.fromString(userId);
        CartResponseDto response = cartService.getCart(userUuid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> addItem(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody AddItemRequestDto requestDto
    ) {
        UUID userUuid = UUID.fromString(userId);
        CartResponseDto response = cartService.addItem(userUuid, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable UUID itemId,
        @Valid @RequestBody UpdateQuantityRequestDto requestDto
    ) {
        UUID userUuid = UUID.fromString(userId);
        CartResponseDto response = cartService.updateItemQuantity(userUuid, itemId, requestDto.getQuantity());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDto> removeItem(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable UUID itemId
    ) {
        UUID userUuid = UUID.fromString(userId);
        CartResponseDto response = cartService.removeItem(userUuid, itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
        @RequestHeader("X-User-Id") String userId
    ) {
        UUID userUuid = UUID.fromString(userId);
        cartService.clearCart(userUuid);
        return ResponseEntity.noContent().build();
    }
}
