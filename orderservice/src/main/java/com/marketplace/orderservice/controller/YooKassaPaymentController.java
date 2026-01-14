package com.marketplace.orderservice.controller;

import com.marketplace.orderservice.entity.YooKassaPayment;
import com.marketplace.orderservice.repository.YooKassaPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/yookassa-payments")
@RequiredArgsConstructor
@Slf4j
public class YooKassaPaymentController {

    private final YooKassaPaymentRepository paymentRepository;

    @GetMapping
    public ResponseEntity<List<YooKassaPayment>> getAllPayments() {
        log.info("Received request to get all YooKassa payments");
        List<YooKassaPayment> payments = paymentRepository.findAll();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<YooKassaPayment> getPaymentByPaymentId(@PathVariable String paymentId) {
        log.info("Received request to get YooKassa payment by payment ID: {}", paymentId);
        Optional<YooKassaPayment> payment = paymentRepository.findByPaymentId(paymentId);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<YooKassaPayment>> getPaymentsByStatus(@PathVariable String status) {
        log.info("Received request to get YooKassa payments by status: {}", status);
        List<YooKassaPayment> payments = paymentRepository.findByStatusOrderByCreatedAtDesc(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<YooKassaPayment>> getPaymentsByOrderId(@PathVariable java.util.UUID orderId) {
        log.info("Received request to get YooKassa payments by order ID: {}", orderId);
        List<YooKassaPayment> payments = paymentRepository.findByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }
}
