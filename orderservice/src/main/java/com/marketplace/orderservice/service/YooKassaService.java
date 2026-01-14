package com.marketplace.orderservice.service;

import com.marketplace.orderservice.config.YooKassaProperties;
import com.marketplace.orderservice.dto.yookassa.YooKassaPaymentRequest;
import com.marketplace.orderservice.dto.yookassa.YooKassaPaymentResponse;
import com.marketplace.orderservice.entity.YooKassaPayment;
import com.marketplace.orderservice.repository.YooKassaPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class YooKassaService {
    
    private final YooKassaProperties yooKassaProperties;
    private final WebClient webClient;
    private final YooKassaPaymentRepository paymentRepository;
    
    public Mono<YooKassaPaymentResponse> createPayment(String amount, String currency, String description) {
        log.info("Creating YooKassa payment for amount: {} {}, description: {}", amount, currency, description);
        
        String idempotenceKey = UUID.randomUUID().toString();
        String authHeader = createAuthHeader();
        
        YooKassaPaymentRequest request = YooKassaPaymentRequest.builder()
                .amount(YooKassaPaymentRequest.Amount.builder()
                        .value(amount)
                        .currency(currency)
                        .build())
                .confirmation(YooKassaPaymentRequest.Confirmation.builder()
                        .type("embedded")
                        .build())
                .capture(true)
                .description(description)
                .build();
        
        log.info("Request payload: {}", request);
        log.info("Auth header: {}", authHeader.substring(0, 10) + "...");
        
        return webClient.post()
                .uri(yooKassaProperties.getApiUrl())
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .header("Idempotence-Key", idempotenceKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("YooKassa API error: " + response.statusCode() + " - " + body))))
                .bodyToMono(YooKassaPaymentResponse.class)
                .doOnSuccess(response -> {
                    log.info("Successfully created YooKassa payment with ID: {}", response.getId());
                    savePaymentToDatabase(response);
                })
                .doOnError(error -> {
                    log.error("Error creating YooKassa payment: {}", error.getMessage());
                    log.error("Full error: {}", error);
                });
    }
    
    public Mono<YooKassaPaymentResponse> getPaymentStatus(String paymentId) {
        log.info("Getting YooKassa payment status for payment ID: {}", paymentId);
        
        String authHeader = createAuthHeader();
        
        return webClient.get()
                .uri(yooKassaProperties.getApiUrl() + "/" + paymentId)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(YooKassaPaymentResponse.class)
                .doOnSuccess(response -> log.info("Successfully retrieved payment status for ID: {}, status: {}", paymentId, response.getStatus()))
                .doOnError(error -> {
                    log.error("Error retrieving payment status for ID {}: {}", paymentId, error.getMessage());
                    log.error("Full error: {}", error);
                });
    }
    
    private String createAuthHeader() {
        String credentials = yooKassaProperties.getShopId() + ":" + yooKassaProperties.getSecretKey();
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }
    
    private void savePaymentToDatabase(YooKassaPaymentResponse response) {
        try {
            YooKassaPayment.YooKassaPaymentBuilder builder = YooKassaPayment.builder()
                    .paymentId(response.getId())
                    .amount(new BigDecimal(response.getAmount().getValue()))
                    .currency(response.getAmount().getCurrency())
                    .description(response.getDescription())
                    .status(response.getStatus())
                    .confirmationToken(response.getConfirmation().getConfirmationToken())
                    .paid(response.isPaid())
                    .test(response.isTest())
                    .refundable(response.isRefundable())
                    .createdAt(LocalDateTime.parse(response.getCreatedAt().replace("Z", "")));
            
            // Добавляем metadata только если оно не null
            if (response.getMetadata() instanceof java.util.Map) {
                builder.metadata((java.util.Map<String, Object>) response.getMetadata());
            }
            
            YooKassaPayment payment = builder.build();
            paymentRepository.save(payment);
            log.info("Payment saved to database with ID: {}", payment.getId());
        } catch (Exception e) {
            log.error("Error saving payment to database: {}", e.getMessage(), e);
        }
    }
}
