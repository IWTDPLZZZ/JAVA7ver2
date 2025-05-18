package orf.demo.controller;

import orf.demo.dto.BulkSpellCheckRequest;
import orf.demo.model.SpellCheckCategory;
import orf.demo.service.SpellCheckCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spell-check-categories")
public class SpellCheckCategoryController {

    private final SpellCheckCategoryService spellCheckCategoryService;

    @Autowired
    public SpellCheckCategoryController(SpellCheckCategoryService spellCheckCategoryService) {
        this.spellCheckCategoryService = spellCheckCategoryService;
    }

    @GetMapping
    public List<SpellCheckCategory> getAllSpellCheckCategories() {
        return spellCheckCategoryService.getAllSpellChecks();
    }

    @PostMapping
    public SpellCheckCategory createSpellCheckCategory(@RequestBody BulkSpellCheckRequest request) {
        spellCheckCategoryService.saveSpellCheckCategory(request);
        return spellCheckCategoryService.getSpellChecksByCategory("default").stream()
                .filter(sc -> sc.getName().equals("SpellCheck_" + System.currentTimeMillis()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created SpellCheckCategory"));
    }

    @PutMapping("/{id}")
    public SpellCheckCategory updateSpellCheckCategory(@PathVariable Long id, @RequestBody SpellCheckCategory spellCheck) {
        return spellCheckCategoryService.updateSpellCheck(id, spellCheck);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpellCheckCategory(@PathVariable Long id) {
        spellCheckCategoryService.deleteSpellCheck(id);
        return ResponseEntity.noContent().build();
    }
}