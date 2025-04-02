package orf.demo.controller;

import orf.demo.model.Category;
import orf.demo.service.CacheService;
import orf.demo.service.SpellCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private SpellCheckService spellCheckService;

    @Autowired
    private CacheService cacheService;

    @GetMapping
    public List<Category> getAllCategories() {
        List<Category> categories = (List<Category>) cacheService.get("allCategories");
        if (categories == null) {
            categories = spellCheckService.getAllCategories();
            cacheService.put("allCategories", categories);
        }
        return categories;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        String cacheKey = "category_" + id;
        Category category = (Category) cacheService.get(cacheKey);
        if (category == null) {
            category = spellCheckService.getCategoryById(id)
                    .orElse(null);
            if (category != null) {
                cacheService.put(cacheKey, category);
            }
        }
        return category != null
                ? ResponseEntity.ok(category)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        Category savedCategory = spellCheckService.saveCategory(category);
        cacheService.evictAll();
        return savedCategory;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        if (spellCheckService.getCategoryById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        category.setId(id);
        Category updatedCategory = spellCheckService.saveCategory(category);
        String cacheKey = "category_" + id;
        cacheService.put(cacheKey, updatedCategory);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        if (spellCheckService.getCategoryById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        spellCheckService.deleteCategory(id);
        cacheService.evict("category_" + id);
        cacheService.evict("allCategories");
        return ResponseEntity.ok().build();
    }
}