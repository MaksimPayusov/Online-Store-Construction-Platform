package com.example.basket.controller;

import com.example.basket.dto.AddToBasketRequestBody;
import com.example.basket.dto.BasketItemResponse;
import com.example.basket.dto.UpdateBasketItemRequest;
import com.example.basket.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/basket-items")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @PostMapping("/create")
    public ResponseEntity<BasketItemResponse> addToBasket(@RequestBody AddToBasketRequestBody body) {
        return ResponseEntity.ok(basketService.addToBasket(body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBasketItem(@PathVariable Long id) {
        basketService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BasketItemResponse> patchBasketItem(
            @PathVariable Long id,
            @RequestBody UpdateBasketItemRequest request
    ) {
        return ResponseEntity.ok(basketService.updateById(id, request));
    }
}




