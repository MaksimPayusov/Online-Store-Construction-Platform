package com.example.shop.controller;

import com.example.shop.dto.request.RegistrationRequest;
import com.example.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class Shopcontroller {

    private final ShopService shopService;

    @PostMapping("/create")
    public ResponseEntity<?> createShop(@RequestHeader(value = "X-User-Id", required = false) UUID currentUserId, @RequestBody RegistrationRequest dto){
        shopService.createShop(dto,currentUserId);
        return ResponseEntity.ok().build();
    }

}
