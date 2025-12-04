package com.example.goodscategory.Repository;


import com.example.goodscategory.Entity.GoodsCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodsCategoriesRepository extends JpaRepository<GoodsCategories, Long> {
    Optional<GoodsCategories> findById(Long id);

    Optional<GoodsCategories> findByTitle(String title);

    Boolean existsByTitle(String title);

    void deleteById(Long id);
}
