package orf.demo.controller;

import orf.demo.model.Category;
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

    @GetMapping
    public List<Category> getAllCategories() {
        return spellCheckService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return spellCheckService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return spellCheckService.saveCategory(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        if (spellCheckService.getCategoryById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        category.setId(id);
        return ResponseEntity.ok(spellCheckService.saveCategory(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        if (spellCheckService.getCategoryById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        spellCheckService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}