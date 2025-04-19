package orf.demo.controller;

import orf.demo.model.SpellCheckCategory;
import orf.demo.service.CategoryStatusService;
import orf.demo.service.SpellCheckCategoryService;
import orf.demo.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryStatusController {

    private final CategoryStatusService categoryStatusService;
    private final SpellCheckCategoryService spellCheckCategoryService;

    @Autowired
    public CategoryStatusController(CategoryStatusService categoryStatusService,
                                    SpellCheckCategoryService spellCheckCategoryService) {
        this.categoryStatusService = categoryStatusService;
        this.spellCheckCategoryService = spellCheckCategoryService;
    }

    @GetMapping("/{categoryId}/spell-checks")
    public ResponseEntity<List<SpellCheckCategory>> getSpellChecksByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(spellCheckCategoryService.getSpellChecksByCategory(String.valueOf(categoryId)));
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