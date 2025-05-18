package orf.demo.service;

import orf.demo.cache.SpellCheckCache;
import orf.demo.dto.BulkSpellCheckRequest;
import orf.demo.dto.SpellCheckResponse;
import orf.demo.model.Category;
import orf.demo.model.SpellCheckCategory;
import orf.demo.repository.CategoryRepository;
import orf.demo.repository.SpellCheckCategoryRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpellCheckCategoryServiceImpl implements SpellCheckCategoryService {

    private final SpellCheckCategoryRepository spellCheckCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SpellCheckCache spellCheckCache;
    private final SpellCheckService spellCheckService;

    @Autowired
    public SpellCheckCategoryServiceImpl(SpellCheckCategoryRepository spellCheckCategoryRepository,
                                         CategoryRepository categoryRepository,
                                         SpellCheckCache spellCheckCache,
                                         SpellCheckService spellCheckService) {
        this.spellCheckCategoryRepository = spellCheckCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.spellCheckCache = spellCheckCache;
        this.spellCheckService = spellCheckService;
    }

    @Override
    public List<SpellCheckCategory> getAllSpellChecks() {
        List<SpellCheckCategory> spellChecks = spellCheckCategoryRepository.findAll();
        spellChecks.forEach(spellCheck -> Hibernate.initialize(spellCheck.getCategories()));
        return spellChecks;
    }

    @Override
    public SpellCheckCategory getSpellCheckById(Long id) {
        SpellCheckCategory spellCheck = spellCheckCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spell check not found with ID: " + id));
        Hibernate.initialize(spellCheck.getCategories());
        return spellCheck;
    }

    @Override
    public SpellCheckCategory saveSpellCheck(SpellCheckCategory spellCheck) {
        return spellCheckCategoryRepository.save(spellCheck);
    }

    @Override
    public SpellCheckCategory updateSpellCheck(Long id, SpellCheckCategory spellCheck) {
        SpellCheckCategory existingSpellCheck = spellCheckCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spell check not found with ID: " + id));
        existingSpellCheck.setName(spellCheck.getName());
        existingSpellCheck.setStatus(spellCheck.getStatus());
        existingSpellCheck.setError(spellCheck.getError());
        return spellCheckCategoryRepository.save(existingSpellCheck);
    }

    @Override
    public void deleteSpellCheck(Long id) {
        if (!spellCheckCategoryRepository.existsById(id)) {
            throw new RuntimeException("Spell check not found with ID: " + id);
        }
        spellCheckCategoryRepository.deleteById(id);
    }

    @Override
    public void addCategoryToSpellCheck(Long spellCheckId, Long categoryId) {
        SpellCheckCategory spellCheck = spellCheckCategoryRepository.findById(spellCheckId)
                .orElseThrow(() -> new RuntimeException("Spell check not found with ID: " + spellCheckId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        Hibernate.initialize(spellCheck.getCategories());
        spellCheck.addCategory(category);
        spellCheckCategoryRepository.save(spellCheck);
    }

    @Override
    public void removeCategoryFromSpellCheck(Long spellCheckId, Long categoryId) {
        SpellCheckCategory spellCheck = spellCheckCategoryRepository.findById(spellCheckId)
                .orElseThrow(() -> new RuntimeException("Spell check not found with ID: " + spellCheckId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        Hibernate.initialize(spellCheck.getCategories());
        spellCheck.removeCategory(category);
        spellCheckCategoryRepository.save(spellCheck);
    }

    @Override
    public List<SpellCheckCategory> getSpellChecksByCategory(String categoryName) {
        Optional<List<SpellCheckCategory>> cachedSpellChecks = Optional.ofNullable(spellCheckCache.get(categoryName));
        if (cachedSpellChecks.isPresent()) {
            return cachedSpellChecks.get();
        }
        List<SpellCheckCategory> spellChecks = spellCheckCategoryRepository.findByCategoryName(categoryName);
        spellChecks.forEach(spellCheck -> Hibernate.initialize(spellCheck.getCategories()));
        spellCheckCache.put(categoryName, spellChecks);
        return spellChecks;
    }

    @Override
    public List<SpellCheckCategory> findByErrorAndCategoryName(String error, String categoryName) {
        List<SpellCheckCategory> spellChecks = spellCheckCategoryRepository.findByErrorAndCategoryName(error, categoryName);
        spellChecks.forEach(spellCheck -> Hibernate.initialize(spellCheck.getCategories()));
        return spellChecks;
    }

    @Override
    public void saveSpellCheckCategory(BulkSpellCheckRequest request) {
        if (request == null || request.getTexts() == null) {
            throw new IllegalArgumentException("Request or texts cannot be null");
        }

        SpellCheckCategory category = new SpellCheckCategory();
        category.setName("SpellCheck_" + System.currentTimeMillis());
        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulk(request.getTexts());
        String errors = results.stream()
                .filter(response -> !response.isCorrect())
                .map(response -> response.getText())
                .collect(Collectors.joining(", "));
        category.setError(errors.isEmpty() ? null : errors);
        category.setStatus(errors.isEmpty() ? "Correct" : "Error");
        spellCheckCategoryRepository.save(category);
    }
}