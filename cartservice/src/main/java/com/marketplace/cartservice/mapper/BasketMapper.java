package com.marketplace.cartservice.mapper;

import com.marketplace.cartservice.dto.response.BasketItemResponseDto;
import com.marketplace.cartservice.dto.response.CartResponseDto;
import com.marketplace.cartservice.entity.Basket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BasketItemMapper.class})
public interface BasketMapper {

    @Mapping(target = "basketId", source = "id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(entity))")
    @Mapping(target = "totalItems", expression = "java(calculateTotalItems(entity))")
    CartResponseDto toDto(Basket entity);

    default BigDecimal calculateTotalPrice(Basket basket) {
        if (basket.getItems() == null || basket.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return basket.getItems().stream()
            .map(item -> item.getPriceAtAdd().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default Integer calculateTotalItems(Basket basket) {
        if (basket.getItems() == null || basket.getItems().isEmpty()) {
            return 0;
        }
        return basket.getItems().stream()
            .mapToInt(item -> item.getQuantity())
            .sum();
    }
}
