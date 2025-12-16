package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String shopName;

    @Column(nullable = false,unique = true)
    private String shopUrl;

    private String description;

    @Column(unique = true)
    private String pfpUrl;

    @Column(nullable = false)
    private String designCode;
}
