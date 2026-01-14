package com.marketplace.orderservice.repository;

import com.marketplace.orderservice.entity.YooKassaPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YooKassaPaymentRepository extends JpaRepository<YooKassaPayment, java.util.UUID> {
    
    Optional<YooKassaPayment> findByPaymentId(String paymentId);
    
    List<YooKassaPayment> findByStatus(String status);
    
    List<YooKassaPayment> findByOrderId(java.util.UUID orderId);
    
    @Query("SELECT p FROM YooKassaPayment p WHERE p.paymentId = :paymentId")
    Optional<YooKassaPayment> findByPaymentIdWithDetails(@Param("paymentId") String paymentId);
    
    @Query("SELECT p FROM YooKassaPayment p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<YooKassaPayment> findByStatusOrderByCreatedAtDesc(@Param("status") String status);
}
