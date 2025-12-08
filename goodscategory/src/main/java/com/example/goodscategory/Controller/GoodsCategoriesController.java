package com.example.goodscategory.Controller;

import com.example.goodscategory.Dto.Request.GoodsCategoriesCreateRequest;
import com.example.goodscategory.Dto.Request.PatchbyIdRequest;
import com.example.goodscategory.Entity.GoodsCategories;
import com.example.goodscategory.Service.GoodsCategoriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/good-categories")
@RequiredArgsConstructor
public class GoodsCategoriesController {

    private final GoodsCategoriesService goodsCategoriesService;

    //@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody GoodsCategoriesCreateRequest dtoRequest) {
        log.info("Received create request: title={}, description={}, parentCategoryTitle={}", 
                dtoRequest.getTitle(), dtoRequest.getDescription(), dtoRequest.getParentCategoryTitle());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (dtoRequest.getTitle() == null || dtoRequest.getTitle().trim().isEmpty()) {
                response.put("error", "Title is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (dtoRequest.getDescription() == null || dtoRequest.getDescription().trim().isEmpty()) {
                response.put("error", "Description is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            GoodsCategories goodsCategories = new GoodsCategories();
            goodsCategories.setTitle(dtoRequest.getTitle().trim());
            goodsCategories.setDescription(dtoRequest.getDescription().trim());
            
            GoodsCategories saved = goodsCategoriesService.save(
                goodsCategories, 
                dtoRequest.getParentCategoryTitle() != null ? dtoRequest.getParentCategoryTitle().trim() : null
            );
            
            response.put("message", "Категория успешно создана!");
            response.put("id", saved.getId());
            response.put("title", saved.getTitle());
            
            log.info("Category created successfully with id: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error creating category: ", e);
            response.put("error", "Ошибка при создании категории: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("Received get request for id: {}", id);
        try {
            GoodsCategories category = goodsCategoriesService.findById(id);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            log.error("Error getting category by id {}: ", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Категория с id " + id + " не найдена");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> deleteById(@PathVariable Long id) {
        log.info("Received delete request for id: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            goodsCategoriesService.deletebyId(id);
            response.put("message", "Категория была успешно удалена");
            log.info("Category with id {} deleted successfully", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting category with id {}: ", id, e);
            response.put("error", "Не получилось удалить категорию: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping(value = "/patch/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchById(@PathVariable Long id, @RequestBody PatchbyIdRequest patchbyIdRequest) {
        log.info("Received patch request for id: {}, data: {}", id, patchbyIdRequest);
        Map<String, Object> response = new HashMap<>();
        
        try {
            StringBuilder changedFields = new StringBuilder("Успешно поменяли: ");
            boolean hasChanges = false;
            
            if (patchbyIdRequest.getTitle() != null && !patchbyIdRequest.getTitle().trim().isEmpty()) {
                goodsCategoriesService.changeTitle(id, patchbyIdRequest.getTitle().trim());
                changedFields.append("title ");
                hasChanges = true;
            }
            if (patchbyIdRequest.getDescription() != null && !patchbyIdRequest.getDescription().trim().isEmpty()) {
                goodsCategoriesService.changeDescription(id, patchbyIdRequest.getDescription().trim());
                changedFields.append("description ");
                hasChanges = true;
            }
            if (patchbyIdRequest.getParent() != null && !patchbyIdRequest.getParent().trim().isEmpty()) {
                goodsCategoriesService.changeParent(id, patchbyIdRequest.getParent().trim());
                changedFields.append("parent ");
                hasChanges = true;
            }
            
            if (!hasChanges) {
                response.put("message", "Нет изменений для применения");
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("message", changedFields.toString().trim());
            log.info("Category with id {} patched successfully", id);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Validation error for patch request: {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error patching category with id {}: ", id, e);
            response.put("error", "Ошибка в данных: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
