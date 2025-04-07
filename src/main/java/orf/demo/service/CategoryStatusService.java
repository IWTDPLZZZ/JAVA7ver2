package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.model.SpellCheckCategory;
import orf.demo.repository.QueryRepositoryOfStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryStatusService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryStatusService.class);

    @Autowired
    private QueryRepositoryOfStatus queryRepositoryOfStatus;

    @Autowired
    private CacheService cacheService;

    public List<SpellCheckCategory> getSpellChecksByCategoryId(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            logger.error("Invalid category ID: {}", categoryId);
            throw new IllegalArgumentException("Category ID must be a positive number");
        }

        logger.info("Fetching spell checks for category ID: {}", categoryId);
        List<SpellCheckCategory> spellChecks = queryRepositoryOfStatus.getSpellCheckCategoriesByCategoryId(categoryId);
        logger.info("Found {} spell checks for category ID: {}", spellChecks.size(), categoryId);
        return spellChecks;
    }

    public Map<String, Object> updateCategoryStatus(Long id, Category updatedCategory) {
        if (id == null || id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new IllegalArgumentException("Category ID must be a positive number");
        }
        if (updatedCategory == null || updatedCategory.getName() == null || updatedCategory.getName().trim().isEmpty()) {
            logger.error("Invalid updated category data: {}", updatedCategory);
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        logger.info("Updating category with ID: {}", id);
        Optional<Category> existingCategory = queryRepositoryOfStatus.findById(id);
        if (existingCategory.isEmpty()) {
            logger.error("Category with ID: {} not found", id);
            throw new RuntimeException("Category not found with ID: " + id);
        }

        Category category = existingCategory.get();
        category.setName(updatedCategory.getName());
        Category savedCategory = queryRepositoryOfStatus.save(category);

        cacheService.evict("allCategories");
        cacheService.evict("category_" + id);
        cacheService.put("category_" + savedCategory.getId(), savedCategory);
        logger.info("Category with ID: {} updated", id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("category", savedCategory);
        return response;
    }

    public Map<String, Object> deleteCategoryStatus(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid category ID for deletion: {}", id);
            throw new IllegalArgumentException("Category ID must be a positive number");
        }

        logger.info("Deleting category status with ID: {}", id);
        queryRepositoryOfStatus.deleteById(id);
        cacheService.evict("category_" + id);
        cacheService.evict("allCategories");
        logger.info("Category status with ID: {} deleted", id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Category with ID " + id + " has been deleted.");
        return response;
    }
}