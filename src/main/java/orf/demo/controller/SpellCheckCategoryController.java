package orf.demo.controller;

import orf.demo.model.SpellCheckCategory;
import orf.demo.service.SpellCheckCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spell-checks")
public class SpellCheckCategoryController {

    @Autowired
    private SpellCheckCategoryService spellCheckCategoryService;

    @GetMapping
    public ResponseEntity<List<SpellCheckCategory>> getAllSpellChecks() {
        return ResponseEntity.ok(spellCheckCategoryService.getAllSpellChecks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpellCheckCategory> getSpellCheckById(@PathVariable Long id) {
        return ResponseEntity.ok(spellCheckCategoryService.getSpellCheckById(id)
                .orElseThrow(() -> new RuntimeException("Spell check not found with ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<SpellCheckCategory> createSpellCheck(@RequestBody SpellCheckCategory spellCheck) {
        return ResponseEntity.ok(spellCheckCategoryService.saveSpellCheck(spellCheck));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpellCheckCategory> updateSpellCheck(@PathVariable Long id, @RequestBody SpellCheckCategory spellCheck) {
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
}