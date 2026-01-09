package com.marketplace.cartservice.mapper;

import com.marketplace.cartservice.dto.response.BasketItemResponseDto;
import com.marketplace.cartservice.entity.BasketItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BasketItemMapper {

    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(entity))")
    BasketItemResponseDto toDto(BasketItem entity);

    default java.math.BigDecimal calculateSubtotal(BasketItem entity) {
        if (entity.getPriceAtAdd() == null || entity.getQuantity() == null) {
            return java.math.BigDecimal.ZERO;
        }
        return entity.getPriceAtAdd().multiply(java.math.BigDecimal.valueOf(entity.getQuantity()));
    }
}
