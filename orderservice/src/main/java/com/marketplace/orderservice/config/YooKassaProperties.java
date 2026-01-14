package com.marketplace.orderservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "yookassa")
@Data
public class YooKassaProperties {
    
    private String shopId;
    private String secretKey;
    private String apiUrl = "https://api.yookassa.ru/v3/payments";
    
}
