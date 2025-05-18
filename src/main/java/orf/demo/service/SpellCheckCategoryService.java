package orf.demo.service;

import orf.demo.dto.BulkSpellCheckRequest;
import orf.demo.model.SpellCheckCategory;

import java.util.List;

public interface SpellCheckCategoryService {
    List<SpellCheckCategory> getAllSpellChecks();
    SpellCheckCategory getSpellCheckById(Long id);
    SpellCheckCategory saveSpellCheck(SpellCheckCategory spellCheck);
    SpellCheckCategory updateSpellCheck(Long id, SpellCheckCategory spellCheck);
    void deleteSpellCheck(Long id);
    void addCategoryToSpellCheck(Long spellCheckId, Long categoryId);
    void removeCategoryFromSpellCheck(Long spellCheckId, Long categoryId);
    List<SpellCheckCategory> getSpellChecksByCategory(String categoryName);
    List<SpellCheckCategory> findByErrorAndCategoryName(String error, String categoryName);
    void saveSpellCheckCategory(BulkSpellCheckRequest request);
}