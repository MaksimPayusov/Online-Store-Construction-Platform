package com.example.shop.service;

import com.example.shop.dao.Shopdao;
import com.example.shop.dto.request.RegistrationRequest;
import com.example.shop.entity.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final Shopdao shopdao;

    public void createShop(RegistrationRequest dto, UUID ownerId) {
        log.debug("Start createShop. ownerId={}", ownerId);

        try {
            Shop shop = new Shop();
            shop.setShopName(dto.getShopName());
            shop.setDescription(dto.getDescription());
            shop.setShopUrl(dto.getShopUrl());
            shop.setDesignCode(dto.getDesignCode());
            shop.setOwner_id(ownerId);
            shop.setPfpUrl(dto.getPfpUrl());
            if (dto.getPfpUrl() != null && !dto.getPfpUrl().isBlank()) {
                shop.setPfpUrl(dto.getPfpUrl());
            }

            shopdao.save(shop);

            log.info("Shop saved. shopId={}, ownerId={}", shop.getId(), ownerId);
        } catch (Exception e) {
            log.error("Failed to create shop. ownerId={}, dto={}", ownerId, dto, e);
            throw e;
        }
    }
}
