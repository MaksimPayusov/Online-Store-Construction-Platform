package com.example.shop.service;


import com.example.shop.entity.Shop;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    public void createShopStep1(String shopName, String description, String shopUrl){
        try {
            Shop shop = new Shop();
            shop.setShopName(shopName);
            shop.setShopUrl(shopUrl);
            shop.setDescription(description);
        }
        catch (Exception e){
            throw new IllegalArgumentException();
        }

    }
}
