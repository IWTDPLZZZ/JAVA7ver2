package orf.demo.controller;

import orf.demo.model.SpellCheckCategory;
import orf.demo.service.CategoryStatusService;
import orf.demo.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryStatusController {

    @Autowired
    private CategoryStatusService categoryStatusService;

    @GetMapping("/{categoryId}/spell-checks")
    public ResponseEntity<List<SpellCheckCategory>> getSpellChecksByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryStatusService.getSpellChecksByCategoryId(categoryId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCategoryStatus(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return ResponseEntity.ok(categoryStatusService.updateCategoryStatus(id, updatedCategory));
    }

    @DeleteMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> deleteCategoryStatus(@PathVariable Long id) {
        return ResponseEntity.ok(categoryStatusService.deleteCategoryStatus(id));
    }
}