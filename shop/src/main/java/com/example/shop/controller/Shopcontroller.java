package com.example.shop.controller;

import com.example.shop.dto.request.RegistrationRequest;
import com.example.shop.dto.request.UpdateShopRequest;
import com.example.shop.dto.response.ShopResponseDto;
import com.example.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    public ResponseEntity<ShopResponseDto> createShop(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody RegistrationRequest dto
    ) {
        UUID userUuid = UUID.fromString(userId);
        ShopResponseDto response = shopService.createShop(dto, userUuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ShopResponseDto> getShopById(@PathVariable UUID shopId) {
        ShopResponseDto response = shopService.getShopById(shopId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/url/{shopUrl}")
    public ResponseEntity<ShopResponseDto> getShopByUrl(@PathVariable String shopUrl) {
        ShopResponseDto response = shopService.getShopByUrl(shopUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ShopResponseDto>> getShopsByOwner(@PathVariable UUID ownerId) {
        List<ShopResponseDto> response = shopService.getShopsByOwner(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-shops")
    public ResponseEntity<List<ShopResponseDto>> getMyShops(
        @RequestHeader("X-User-Id") String userId
    ) {
        UUID userUuid = UUID.fromString(userId);
        List<ShopResponseDto> response = shopService.getShopsByOwner(userUuid);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ShopResponseDto>> getAllShops() {
        List<ShopResponseDto> response = shopService.getAllShops();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{shopId}")
    public ResponseEntity<ShopResponseDto> updateShop(
        @PathVariable UUID shopId,
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody UpdateShopRequest dto
    ) {
        UUID userUuid = UUID.fromString(userId);
        ShopResponseDto response = shopService.updateShop(shopId, dto, userUuid);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shopId}")
    public ResponseEntity<Void> deleteShop(
        @PathVariable UUID shopId,
        @RequestHeader("X-User-Id") String userId
    ) {
        UUID userUuid = UUID.fromString(userId);
        shopService.deleteShop(shopId, userUuid);
        return ResponseEntity.noContent().build();
    }
}
