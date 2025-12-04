package com.example.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasketItemResponse {
    private Long id;
    private Long goodId;
    private Integer count;
}


