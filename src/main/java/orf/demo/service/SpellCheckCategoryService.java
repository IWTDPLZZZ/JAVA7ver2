package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.model.SpellCheck;
import orf.demo.model.SpellCheckCategory;
import orf.demo.repository.SpellCheckCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SpellCheckCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(SpellCheckCategoryService.class);

    @Autowired
    private SpellCheckCategoryRepository spellCheckCategoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheService cacheService;

    public SpellCheckCategory getOrCreateSpellCheckEntity(SpellCheck error) {
        String cacheKey = "spellCheckCategory_" + error.getWord();
        SpellCheckCategory entity = (SpellCheckCategory) cacheService.get(cacheKey);
        if (entity != null) {
            logger.debug("Returning cached spell check entity for word: {}", error.getWord());
            return entity;
        }

        logger.info("Fetching or creating spell check entity for word: {}", error.getWord());
        List<SpellCheckCategory> entities = spellCheckCategoryRepository.findByName(error.getWord());
        if (entities.isEmpty()) {
            entity = new SpellCheckCategory();
            entity.setName(error.getWord());
            logger.info("Created new spell check entity for word: {}", error.getWord());
        } else {
            entity = entities.get(0);
            logger.debug("Found existing spell check entity for word: {}", error.getWord());
        }
        entity.setStatus(error.getStatus());
        entity.setError(error.getError());
        cacheService.put(cacheKey, entity);
        return entity;
    }

    public void addCategoryToEntity(SpellCheckCategory entity, Category category) {
        logger.debug("Adding category {} to spell check entity {}", category.getName(), entity.getName());
        Set<Category> categories = entity.getCategories() != null ? entity.getCategories() : new HashSet<>();
        categories.add(category);
        entity.setCategories(categories);
    }

    public List<SpellCheckCategory> getAllSpellChecks() {
        String cacheKey = "allSpellChecks";
        @SuppressWarnings("unchecked")
        List<SpellCheckCategory> cachedSpellChecks = (List<SpellCheckCategory>) cacheService.get(cacheKey);
        if (cachedSpellChecks != null) {
            logger.debug("Returning cached spell checks, count: {}", cachedSpellChecks.size());
            return cachedSpellChecks;
        }

        logger.info("Fetching all spell checks from database");
        List<SpellCheckCategory> spellChecks = spellCheckCategoryRepository.findAll();
        cacheService.put(cacheKey, spellChecks);
        logger.info("Fetched {} spell checks", spellChecks.size());
        return spellChecks;
    }

    public SpellCheckCategory saveSpellCheck(SpellCheckCategory spellCheckCategory) {
        if (spellCheckCategory == null || spellCheckCategory.getName() == null || spellCheckCategory.getName().trim().isEmpty()) {
            logger.error("Invalid spell check data: {}", spellCheckCategory);
            throw new IllegalArgumentException("Spell check name cannot be null or empty");
        }

        logger.info("Saving spell check: {}", spellCheckCategory.getName());
        SpellCheckCategory savedEntity = spellCheckCategoryRepository.save(spellCheckCategory);
        cacheService.evict("allSpellChecks");
        String cacheKey = "spellCheckCategory_" + savedEntity.getId();
        cacheService.put(cacheKey, savedEntity);
        logger.info("Spell check saved with ID: {}", savedEntity.getId());
        return savedEntity;
    }

    public SpellCheckCategory updateSpellCheck(Long id, SpellCheckCategory updatedSpellCheck) {
        if (id == null || id <= 0) {
            logger.error("Invalid spell check ID: {}", id);
            throw new IllegalArgumentException("Spell check ID must be a positive number");
        }
        if (updatedSpellCheck == null || updatedSpellCheck.getName() == null || updatedSpellCheck.getName().trim().isEmpty()) {
            logger.error("Invalid updated spell check data: {}", updatedSpellCheck);
            throw new IllegalArgumentException("Spell check name cannot be null or empty");
        }

        logger.info("Updating spell check with ID: {}", id);
        Optional<SpellCheckCategory> existingSpellCheck = getSpellCheckById(id);
        if (existingSpellCheck.isEmpty()) {
            logger.error("Spell check with ID: {} not found", id);
            throw new RuntimeException("Spell check not found with ID: " + id);
        }

        SpellCheckCategory spellCheck = existingSpellCheck.get();
        spellCheck.setName(updatedSpellCheck.getName());
        spellCheck.setStatus(updatedSpellCheck.getStatus());
        spellCheck.setError(updatedSpellCheck.getError());
        spellCheck.setCategories(updatedSpellCheck.getCategories());
        SpellCheckCategory savedEntity = spellCheckCategoryRepository.save(spellCheck);

        cacheService.evict("allSpellChecks");
        cacheService.evict("spellCheckCategory_" + id);
        cacheService.put("spellCheckCategory_" + savedEntity.getId(), savedEntity);
        logger.info("Spell check with ID: {} updated", id);
        return savedEntity;
    }

    public Optional<SpellCheckCategory> getSpellCheckById(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid spell check ID: {}", id);
            throw new IllegalArgumentException("Spell check ID must be a positive number");
        }

        String cacheKey = "spellCheckCategory_" + id;
        SpellCheckCategory spellCheckCategory = (SpellCheckCategory) cacheService.get(cacheKey);
        if (spellCheckCategory != null) {
            logger.debug("Returning cached spell check with ID: {}", id);
            return Optional.of(spellCheckCategory);
        }

        logger.info("Fetching spell check with ID: {} from database", id);
        Optional<SpellCheckCategory> dbSpellCheck = spellCheckCategoryRepository.findById(id);
        if (dbSpellCheck.isPresent()) {
            cacheService.put(cacheKey, dbSpellCheck.get());
            logger.debug("Spell check with ID: {} cached", id);
        } else {
            logger.warn("Spell check with ID: {} not found", id);
        }
        return dbSpellCheck;
    }

    public void deleteSpellCheck(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid spell check ID for deletion: {}", id);
            throw new IllegalArgumentException("Spell check ID must be a positive number");
        }

        Optional<SpellCheckCategory> spellCheck = getSpellCheckById(id);
        if (spellCheck.isEmpty()) {
            logger.error("Spell check with ID: {} not found", id);
            throw new RuntimeException("Spell check not found with ID: " + id);
        }

        logger.info("Deleting spell check with ID: {}", id);
        spellCheckCategoryRepository.deleteById(id);
        cacheService.evict("spellCheckCategory_" + id);
        cacheService.evict("allSpellChecks");
        logger.info("Spell check with ID: {} deleted", id);
    }

    @Transactional
    public void addCategoryToSpellCheck(Long spellCheckId, Long categoryId) {
        if (spellCheckId == null || spellCheckId <= 0 || categoryId == null || categoryId <= 0) {
            logger.error("Invalid IDs - spellCheckId: {}, categoryId: {}", spellCheckId, categoryId);
            throw new IllegalArgumentException("Spell check ID and category ID must be positive numbers");
        }

        String spellCheckCacheKey = "spellCheckCategory_" + spellCheckId;
        String categoryCacheKey = "category_" + categoryId;

        logger.info("Adding category with ID: {} to spell check with ID: {}", categoryId, spellCheckId);
        SpellCheckCategory spellCheck = (SpellCheckCategory) cacheService.get(spellCheckCacheKey);
        if (spellCheck == null) {
            spellCheck = spellCheckCategoryRepository.findById(spellCheckId)
                    .orElseThrow(() -> {
                        logger.error("Spell check with ID: {} not found", spellCheckId);
                        return new RuntimeException("Spell check not found with ID: " + spellCheckId);
                    });
            cacheService.put(spellCheckCacheKey, spellCheck);
            logger.debug("Spell check with ID: {} cached", spellCheckId);
        }

        Category category = (Category) cacheService.get(categoryCacheKey);
        if (category == null) {
            category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> {
                        logger.error("Category with ID: {} not found", categoryId);
                        return new RuntimeException("Category not found with ID: " + categoryId);
                    });
            cacheService.put(categoryCacheKey, category);
            logger.debug("Category with ID: {} cached", categoryId);
        }

        Set<Category> categories = spellCheck.getCategories() != null ? spellCheck.getCategories() : new HashSet<>();
        categories.add(category);
        spellCheck.setCategories(categories);

        spellCheckCategoryRepository.save(spellCheck);
        cacheService.put(spellCheckCacheKey, spellCheck);
        logger.info("Category with ID: {} added to spell check with ID: {}", categoryId, spellCheckId);
    }

    @Transactional
    public void removeCategoryFromSpellCheck(Long spellCheckId, Long categoryId) {
        if (spellCheckId == null || spellCheckId <= 0 || categoryId == null || categoryId <= 0) {
            logger.error("Invalid IDs - spellCheckId: {}, categoryId: {}", spellCheckId, categoryId);
            throw new IllegalArgumentException("Spell check ID and category ID must be positive numbers");
        }

        String spellCheckCacheKey = "spellCheckCategory_" + spellCheckId;
        String categoryCacheKey = "category_" + categoryId;

        logger.info("Removing category with ID: {} from spell check with ID: {}", categoryId, spellCheckId);
        SpellCheckCategory spellCheck = (SpellCheckCategory) cacheService.get(spellCheckCacheKey);
        if (spellCheck == null) {
            spellCheck = spellCheckCategoryRepository.findById(spellCheckId)
                    .orElseThrow(() -> {
                        logger.error("Spell check with ID: {} not found", spellCheckId);
                        return new RuntimeException("Spell check not found with ID: " + spellCheckId);
                    });
            cacheService.put(spellCheckCacheKey, spellCheck);
            logger.debug("Spell check with ID: {} cached", spellCheckId);
        }

        Category category = (Category) cacheService.get(categoryCacheKey);
        if (category == null) {
            category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> {
                        logger.error("Category with ID: {} not found", categoryId);
                        return new RuntimeException("Category not found with ID: " + categoryId);
                    });
            cacheService.put(categoryCacheKey, category);
            logger.debug("Category with ID: {} cached", categoryId);
        }

        Set<Category> categories = spellCheck.getCategories();
        categories.remove(category);
        spellCheck.setCategories(categories);

        spellCheckCategoryRepository.save(spellCheck);
        cacheService.put(spellCheckCacheKey, spellCheck);
        logger.info("Category with ID: {} removed from spell check with ID: {}", categoryId, spellCheckId);
    }
}