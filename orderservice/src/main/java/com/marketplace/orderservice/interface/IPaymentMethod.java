package com.marketplace.orderservice.interfaces;

/**
 * Интерфейс для методов оплаты
 * Определяет базовые свойства которые должны иметь все методы оплаты
 */
public interface IPaymentMethod {
    
    /**
     * Уникальный идентификатор метода оплаты
     * @return ID метода оплаты
     */
    Object getId();
    
    /**
     * Название метода оплаты
     * @return название метода
     */
    String getTitle();
    
    /**
     * Описание метода оплаты
     * @return описание метода
     */
    String getDescription();
    
    /**
     * URL логотипа метода оплаты
     * @return URL изображения логотипа
     */
    String getLogoUrl();
    
    /**
     * Активен ли метод оплаты
     * @return true если метод активен
     */
    Boolean isActive();
}
