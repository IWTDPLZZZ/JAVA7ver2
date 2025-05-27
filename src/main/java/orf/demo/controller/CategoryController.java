package orf.demo.controller;

import orf.demo.model.Category;
import orf.demo.service.Interface.InterfaceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/categories", "/api/categories"})
public class CategoryController {

    private final InterfaceCategoryService interfaceCategoryService;

    @Autowired
    public CategoryController(InterfaceCategoryService interfaceCategoryService) {
        this.interfaceCategoryService = interfaceCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(interfaceCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(interfaceCategoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(interfaceCategoryService.createCategory(category.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return ResponseEntity.ok(interfaceCategoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        interfaceCategoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}