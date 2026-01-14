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
public class YooKassaPaymentRequest {
    
    @JsonProperty("amount")
    private Amount amount;
    
    @JsonProperty("confirmation")
    private Confirmation confirmation;
    
    @JsonProperty("capture")
    private boolean capture;
    
    @JsonProperty("description")
    private String description;
    
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
    }
}
