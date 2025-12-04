package com.example.goodscategory.Controller;

import com.example.goodscategory.Dto.Request.GoodsCategoriesCreateRequest;
import com.example.goodscategory.Dto.Request.PatchbyIdRequest;
import com.example.goodscategory.Dto.Response.PatchResponse;
import com.example.goodscategory.Entity.GoodsCategories;
import com.example.goodscategory.Service.GoodsCategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/good-categories")
@RequiredArgsConstructor
public class GoodsCategoriesController {

    private final GoodsCategoriesService goodsCategoriesService;

    //прочитать про билдер ломбока
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody GoodsCategoriesCreateRequest dtoRequest) {
        try{
            GoodsCategories goodsCategories = new GoodsCategories();
            goodsCategories.setTitle(dtoRequest.getTitle());
            goodsCategories.setDescription(dtoRequest.getDescription());
            goodsCategoriesService.save(goodsCategories,dtoRequest.getParentCategoryTitle());
        }
        catch (Exception e){
            throw new IllegalArgumentException("Такого родителя не существует");
        }
        return ResponseEntity.ok("Категория успешно создана!");
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.ok(goodsCategoriesService.findById(id));
    }
    //расписать подробнее
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try{
            goodsCategoriesService.deletebyId(id);
            return ResponseEntity.ok("Категория была успешна удалена");
        }
        catch (Exception e){
            throw new IllegalArgumentException("не получилось удалить категорию");
        }
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<?> patchById(@PathVariable Long id, PatchbyIdRequest patchbyIdRequest){
        String answer = "Успешно поменяли: ";
        try {
            if(patchbyIdRequest.getTitle()!=null){
                goodsCategoriesService.changeTitle(id, patchbyIdRequest.getTitle());
                answer = answer + "title";
            }
            if(patchbyIdRequest.getDescription()!=null){
                goodsCategoriesService.changeDescription(id, patchbyIdRequest.getDescription());
                answer = answer + "description";
            }
            if (patchbyIdRequest.getParent()!=null){
                goodsCategoriesService.changeParent(id, patchbyIdRequest.getParent());
                answer = answer + "parent";
            }

        }
        catch (Exception e){
            throw new IllegalArgumentException("Ошибка в данных");
        }
        return ResponseEntity.ok(new PatchResponse(answer));
    }
    //to do фильтрация

}
