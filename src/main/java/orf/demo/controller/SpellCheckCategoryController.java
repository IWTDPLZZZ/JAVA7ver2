package orf.demo.controller;

import orf.demo.model.Category;
import orf.demo.model.SpellCheckCategory;
import orf.demo.service.SpellCheckCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spell-checks")
public class SpellCheckCategoryController {

    private final SpellCheckCategoryService spellCheckCategoryService;

    @Autowired
    public SpellCheckCategoryController(SpellCheckCategoryService spellCheckCategoryService) {
        this.spellCheckCategoryService = spellCheckCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<SpellCheckCategory>> getAllSpellChecks() {
        return ResponseEntity.ok(spellCheckCategoryService.getAllSpellChecks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpellCheckCategory> getSpellCheckById(@PathVariable Long id) {
        return ResponseEntity.ok(spellCheckCategoryService.getSpellCheckById(id));
    }

    @PostMapping
    public ResponseEntity<SpellCheckCategory> createSpellCheck(@RequestBody SpellCheckCategory spellCheck) {
        return ResponseEntity.ok(spellCheckCategoryService.saveSpellCheck(spellCheck));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpellCheckCategory> updateSpellCheck(
            @PathVariable Long id,
            @RequestBody SpellCheckCategory spellCheck) {
        return ResponseEntity.ok(spellCheckCategoryService.updateSpellCheck(id, spellCheck));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpellCheck(@PathVariable Long id) {
        spellCheckCategoryService.deleteSpellCheck(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{spellCheckId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToSpellCheck(@PathVariable Long spellCheckId,
                                                        @PathVariable Long categoryId) {
        spellCheckCategoryService.addCategoryToSpellCheck(spellCheckId, categoryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spellCheckId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromSpellCheck(@PathVariable Long spellCheckId,
                                                             @PathVariable Long categoryId) {
        spellCheckCategoryService.removeCategoryFromSpellCheck(spellCheckId, categoryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<SpellCheckCategory>> getSpellChecksByCategory(
            @RequestParam("categoryName") String categoryName) {
        List<SpellCheckCategory> spellChecks = spellCheckCategoryService.getSpellChecksByCategory(categoryName);
        return ResponseEntity.ok(spellChecks);
    }

    @GetMapping("/by-error-and-category")
    public ResponseEntity<List<SpellCheckCategory>> getSpellChecksByErrorAndCategory(
            @RequestParam("error") String error,
            @RequestParam("categoryName") String categoryName) {
        List<SpellCheckCategory> spellChecks = spellCheckCategoryService.findByErrorAndCategoryName(error, categoryName);
        return ResponseEntity.ok(spellChecks);
    }
}