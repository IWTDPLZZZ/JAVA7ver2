package orf.demo.controller;

import orf.demo.dto.BulkSpellCheckRequest;
import orf.demo.model.SpellCheckCategory;
import orf.demo.service.Interface.InterfaceSpellCheckCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spell-check-categories")
public class SpellCheckCategoryController {

    private final InterfaceSpellCheckCategoryService interfaceSpellCheckCategoryService;

    @Autowired
    public SpellCheckCategoryController(InterfaceSpellCheckCategoryService interfaceSpellCheckCategoryService) {
        this.interfaceSpellCheckCategoryService = interfaceSpellCheckCategoryService;
    }

    @GetMapping
    public List<SpellCheckCategory> getAllSpellCheckCategories() {
        return interfaceSpellCheckCategoryService.getAllSpellChecks();
    }

    @PostMapping
    public SpellCheckCategory createSpellCheckCategory(@RequestBody BulkSpellCheckRequest request) {
        interfaceSpellCheckCategoryService.saveSpellCheckCategory(request);
        return interfaceSpellCheckCategoryService.getSpellChecksByCategory("default").stream()
                .filter(sc -> sc.getName().equals("SpellCheck_" + System.currentTimeMillis()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created SpellCheckCategory"));
    }

    @PutMapping("/{id}")
    public SpellCheckCategory updateSpellCheckCategory(@PathVariable Long id, @RequestBody SpellCheckCategory spellCheck) {
        return interfaceSpellCheckCategoryService.updateSpellCheck(id, spellCheck);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpellCheckCategory(@PathVariable Long id) {
        interfaceSpellCheckCategoryService.deleteSpellCheck(id);
        return ResponseEntity.noContent().build();
    }
}