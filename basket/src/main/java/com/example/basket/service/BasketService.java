package com.example.basket.service;

import com.example.basket.dto.AddToBasketRequestBody;
import com.example.basket.dto.BasketItemResponse;
import com.example.basket.dto.UpdateBasketItemRequest;
import com.example.basket.entity.BasketItem;
import com.example.basket.repository.BasketItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketItemRepository basketItemRepository;

    public BasketItemResponse addToBasket(AddToBasketRequestBody body) {
        BasketItem item = new BasketItem();
        item.setGoodId(body.getGoodId());
        item.setCount(body.getCount());
        BasketItem saved = basketItemRepository.save(item);
        return new BasketItemResponse(saved.getId(), saved.getGoodId(), saved.getCount());
    }

    public void deleteById(Long id) {
        basketItemRepository.deleteById(id);
    }

    public BasketItemResponse updateById(Long id, UpdateBasketItemRequest request) {
        BasketItem item = basketItemRepository.findById(id).orElseThrow();
        if (request.getCount() != null) {
            item.setCount(request.getCount());
        }
        BasketItem saved = basketItemRepository.save(item);
        return new BasketItemResponse(saved.getId(), saved.getGoodId(), saved.getCount());
    }
}




