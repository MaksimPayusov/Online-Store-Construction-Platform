package com.example.goodscategory.Service;


import com.example.goodscategory.Entity.GoodsCategories;
import com.example.goodscategory.Repository.GoodsCategoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoodsCategoriesService {

    private final GoodsCategoriesRepository goodsCategoriesRepository;

    public GoodsCategories findById(Long id) {
        return goodsCategoriesRepository.findById(id).orElseThrow();
    }
    //прописать ошибку что родителя не существует
    public GoodsCategories save(GoodsCategories goodsCategories, String parentCategoryTitle) {
        if(parentCategoryTitle == null) {
            return goodsCategoriesRepository.save(goodsCategories);
        }
        GoodsCategories parent = goodsCategoriesRepository.findByTitle(parentCategoryTitle).orElseThrow();
        goodsCategories.setParent(parent);
        return goodsCategoriesRepository.save(goodsCategories);
    }

    public GoodsCategories findbyTitle(String title) {
        return goodsCategoriesRepository.findByTitle(title).orElseThrow();
    }

    public void deletebyId(Long id) {
        goodsCategoriesRepository.deleteById(id);
    }

    public void changeTitle(Long id, String newTitle) {
        GoodsCategories goodsCategories = goodsCategoriesRepository.findById(id).orElseThrow();
        goodsCategories.setTitle(newTitle);
    }

    public void changeDescription(Long id, String newDescription) {
        GoodsCategories goodsCategories = goodsCategoriesRepository.findById(id).orElseThrow();
        goodsCategories.setDescription(newDescription);
    }

    public void changeId(Long id, Long newId) {
        GoodsCategories goodsCategories = goodsCategoriesRepository.findById(id).orElseThrow();
        goodsCategories.setId(newId);
    }

    public void changeParent(Long childId, String newParentId) {
        GoodsCategories child = findById(childId);
        if (newParentId == null) {
            child.setParent(null);
            return;
        }

        GoodsCategories newParent = findbyTitle(newParentId);

        // Проверка на цикл: нельзя стать своим предком
        GoodsCategories current = newParent;
        while (current != null) {
            if (current.getId().equals(childId)) {
                throw new IllegalArgumentException("Нельзя создать цикл в дереве");
            }
            current = current.getParent();
        }

        child.setParent(newParent);
    }

}
