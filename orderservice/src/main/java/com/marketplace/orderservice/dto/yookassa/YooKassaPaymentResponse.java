package com.marketplace.orderservice.dto.yookassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YooKassaPaymentResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("amount")
    private Amount amount;
    
    @JsonProperty("confirmation")
    private Confirmation confirmation;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("test")
    private boolean test;
    
    @JsonProperty("paid")
    private boolean paid;
    
    @JsonProperty("refundable")
    private boolean refundable;
    
    @JsonProperty("metadata")
    private Object metadata;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {
        @JsonProperty("value")
        private String value;
        
        @JsonProperty("currency")
        private String currency;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Confirmation {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("confirmation_url")
        private String confirmationUrl;
        
        @JsonProperty("confirmation_token")
        private String confirmationToken;
    }
}
