package com.marketplace.cartservice.messaging;

import com.marketplace.cartservice.dto.request.OrderCompletedDto;
import com.marketplace.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompletedListener {

    private final CartService cartService;

    @RabbitListener(queues = "${rabbitmq.queue.order-completed}")
    public void handleOrderCompleted(OrderCompletedDto orderCompletedDto) {
        log.info("Received order completed event for user: {}", orderCompletedDto.getUserId());
        
        try {
            cartService.clearCart(orderCompletedDto.getUserId());
            log.info("Cart cleared successfully after order completion for user: {}", orderCompletedDto.getUserId());
        } catch (Exception e) {
            log.error("Error clearing cart after order completion for user: {}", 
                orderCompletedDto.getUserId(), e);
            throw e;
        }
    }
}
