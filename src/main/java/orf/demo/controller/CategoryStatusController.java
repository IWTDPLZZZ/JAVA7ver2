package orf.demo.controller;

import orf.demo.model.Category;
import orf.demo.model.SpellCheckCategory;
import orf.demo.repository.QueryRepositoryOfStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryStatusController {

    private final QueryRepositoryOfStatus queryRepositoryOfStatus;

    @Autowired
    public CategoryStatusController(QueryRepositoryOfStatus queryRepositoryOfStatus) {
        this.queryRepositoryOfStatus = queryRepositoryOfStatus;
    }

    @GetMapping("/{categoryId}/spell-checks")
    public ResponseEntity<List<SpellCheckCategory>> getSpellChecksByCategoryId(@PathVariable Long categoryId) {
        List<SpellCheckCategory> spellCheckCategories = queryRepositoryOfStatus.getSpellCheckCategoriesByCategoryId(categoryId);
        return ResponseEntity.ok(spellCheckCategories);
    }

    // Метод createCategory удален для устранения конфликта с CategoryController

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCategoryStatus(@PathVariable Long id, @RequestBody Category updatedCategory) {
        try {
            Category existingCategory = queryRepositoryOfStatus.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
            existingCategory.setName(updatedCategory.getName());
            Category savedCategory = queryRepositoryOfStatus.save(existingCategory);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("category", savedCategory);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> deleteCategoryStatus(@PathVariable Long id) {
        try {
            queryRepositoryOfStatus.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category with ID " + id + " has been deleted.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}