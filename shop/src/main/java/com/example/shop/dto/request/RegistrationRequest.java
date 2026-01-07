package com.example.shop.dto.request;


import lombok.Data;

@Data
public class RegistrationRequest {

    private String shopName;
    private String shopUrl;
    private String description;
    private String pfpUrl;
    private String designCode;

}
