package orf.demo.controller;

import orf.demo.model.SpellCheckCategory;
import orf.demo.service.Interface.InterfaceCategoryStatusService;
import orf.demo.service.Interface.InterfaceSpellCheckCategoryService;
import orf.demo.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryStatusController {

    private final InterfaceCategoryStatusService interfaceCategoryStatusService;
    private final InterfaceSpellCheckCategoryService interfaceSpellCheckCategoryService;

    @Autowired
    public CategoryStatusController(InterfaceCategoryStatusService interfaceCategoryStatusService,
                                    InterfaceSpellCheckCategoryService interfaceSpellCheckCategoryService) {
        this.interfaceCategoryStatusService = interfaceCategoryStatusService;
        this.interfaceSpellCheckCategoryService = interfaceSpellCheckCategoryService;
    }

    @GetMapping("/{categoryId}/spell-checks")
    public ResponseEntity<List<SpellCheckCategory>> getSpellChecksByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(interfaceSpellCheckCategoryService.getSpellChecksByCategory(String.valueOf(categoryId)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCategoryStatus(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return ResponseEntity.ok(interfaceCategoryStatusService.updateCategoryStatus(id, updatedCategory));
    }

    @DeleteMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> deleteCategoryStatus(@PathVariable Long id) {
        return ResponseEntity.ok(interfaceCategoryStatusService.deleteCategoryStatus(id));
    }
}