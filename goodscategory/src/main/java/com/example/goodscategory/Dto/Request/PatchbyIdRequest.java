package com.example.goodscategory.Dto.Request;



import lombok.Data;

import java.util.List;

@Data
public class PatchbyIdRequest {
    private String description;

    private String title;

    private List<String> children;

    private String Parent;
}
