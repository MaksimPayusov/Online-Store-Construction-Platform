package com.example.shop.service;


import com.example.shop.dao.Shopdao;
import com.example.shop.dto.request.RegistrationRequest;
import com.example.shop.entity.Shop;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final Shopdao shopdao;


    public void createShop(RegistrationRequest dto, UUID owner_id){
        try {
            Shop shop = new Shop();
            shop.setShopName(dto.getShopName());
            shop.setDescription(dto.getDescription());
            shop.setShopUrl(dto.getShopUrl());
            shop.setDesignCode(dto.getDesignCode());
            shop.setOwner_id(owner_id);
            if(dto.getPfpUrl()!=null || !dto.getPfpUrl().isBlank()){
                shop.setPfpUrl(dto.getPfpUrl());
            }
            shopdao.save(shop);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Неверные данные "+e.getStackTrace());
        }

    }
}
