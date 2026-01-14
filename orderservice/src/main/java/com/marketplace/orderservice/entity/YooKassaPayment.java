package com.marketplace.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "yookassa_payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YooKassaPayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;
    
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    
    @Column(name = "confirmation_token", length = 500)
    private String confirmationToken;
    
    @Column(name = "paid")
    private Boolean paid;
    
    @Column(name = "test")
    private Boolean test;
    
    @Column(name = "refundable")
    private Boolean refundable;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "captured_at")
    private LocalDateTime capturedAt;
    
    @Column(name = "order_id")
    private UUID orderId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "JSONB")
    private Map<String, Object> metadata;
}
