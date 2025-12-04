package com.example.goodscategory.Dto.Request;


import lombok.Data;

@Data
public class GoodsCategoriesCreateRequest {
    private String title;
    private String description;
    private String parentCategoryTitle;
}
