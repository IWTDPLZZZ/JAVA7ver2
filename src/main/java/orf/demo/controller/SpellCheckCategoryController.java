package orf.demo.controller;

import orf.demo.model.SpellCheckCategory;
import orf.demo.service.SpellCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/spell-checks")
public class SpellCheckCategoryController {

    @Autowired
    private SpellCheckService spellCheckService;

    @GetMapping
    public List<SpellCheckCategory> getAllSpellChecks() {
        return spellCheckService.getAllSpellChecks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSpellCheckById(@PathVariable Long id) {
        return spellCheckService.getSpellCheckById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SpellCheckCategory createSpellCheck(@RequestBody SpellCheckCategory spellCheck) {
        return spellCheckService.saveSpellCheck(spellCheck);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpellCheck(@PathVariable Long id, @RequestBody SpellCheckCategory spellCheck) {
        if (spellCheckService.getSpellCheckById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        spellCheck.setId(id);
        return ResponseEntity.ok(spellCheckService.saveSpellCheck(spellCheck));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpellCheck(@PathVariable Long id) {
        if (spellCheckService.getSpellCheckById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        spellCheckService.deleteSpellCheck(id);
        return ResponseEntity.ok().build();
    }

    // Методы для управления связями
    @PostMapping("/{spellCheckId}/categories/{categoryId}")
    public ResponseEntity<?> addCategoryToSpellCheck(@PathVariable Long spellCheckId,
                                                     @PathVariable Long categoryId) {
        try {
            spellCheckService.addCategoryToSpellCheck(spellCheckId, categoryId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{spellCheckId}/categories/{categoryId}")
    public ResponseEntity<?> removeCategoryFromSpellCheck(@PathVariable Long spellCheckId,
                                                          @PathVariable Long categoryId) {
        try {
            spellCheckService.removeCategoryFromSpellCheck(spellCheckId, categoryId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}