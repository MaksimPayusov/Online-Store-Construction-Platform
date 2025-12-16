package com.example.shop.dao;

import com.example.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Shopdao extends JpaRepository<Shop,Long> {
}
