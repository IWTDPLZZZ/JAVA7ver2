package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheService cacheService;

    public List<Category> getAllCategories() {
        String cacheKey = "allCategories";
        @SuppressWarnings("unchecked")
        List<Category> cachedCategories = (List<Category>) cacheService.get(cacheKey);
        if (cachedCategories != null) {
            logger.debug("Returning cached categories, count: {}", cachedCategories.size());
            return cachedCategories;
        }

        logger.info("Fetching all categories from database");
        List<Category> categories = categoryRepository.findAll();
        cacheService.put(cacheKey, categories);
        logger.info("Fetched {} categories", categories.size());
        return categories;
    }

    public Category saveCategory(Category category) {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            logger.error("Invalid category data: {}", category);
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        logger.info("Saving category: {}", category.getName());
        Category savedCategory = categoryRepository.save(category);
        cacheService.evict("allCategories");
        String cacheKey = "category_" + savedCategory.getId();
        cacheService.put(cacheKey, savedCategory);
        logger.info("Category saved with ID: {}", savedCategory.getId());
        return savedCategory;
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        if (id == null || id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new IllegalArgumentException("Category ID must be a positive number");
        }
        if (updatedCategory == null || updatedCategory.getName() == null || updatedCategory.getName().trim().isEmpty()) {
            logger.error("Invalid updated category data: {}", updatedCategory);
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        logger.info("Updating category with ID: {}", id);
        Optional<Category> existingCategory = getCategoryById(id);
        if (existingCategory.isEmpty()) {
            logger.error("Category with ID: {} not found", id);
            throw new RuntimeException("Category not found with ID: " + id);
        }

        Category category = existingCategory.get();
        category.setName(updatedCategory.getName());
        Category savedCategory = categoryRepository.save(category);

        cacheService.evict("allCategories");
        cacheService.evict("category_" + id);
        cacheService.put("category_" + savedCategory.getId(), savedCategory);
        logger.info("Category with ID: {} updated", id);
        return savedCategory;
    }

    public Optional<Category> getCategoryById(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new IllegalArgumentException("Category ID must be a positive number");
        }

        String cacheKey = "category_" + id;
        Category category = (Category) cacheService.get(cacheKey);
        if (category != null) {
            logger.debug("Returning cached category with ID: {}", id);
            return Optional.of(category);
        }

        logger.info("Fetching category with ID: {} from database", id);
        Optional<Category> dbCategory = categoryRepository.findById(id);
        if (dbCategory.isPresent()) {
            cacheService.put(cacheKey, dbCategory.get());
            logger.debug("Category with ID: {} cached", id);
        } else {
            logger.warn("Category with ID: {} not found", id);
        }
        return dbCategory;
    }

    public void deleteCategory(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid category ID for deletion: {}", id);
            throw new IllegalArgumentException("Category ID must be a positive number");
        }

        Optional<Category> category = getCategoryById(id);
        if (category.isEmpty()) {
            logger.error("Category with ID: {} not found", id);
            throw new RuntimeException("Category not found with ID: " + id);
        }

        logger.info("Deleting category with ID: {}", id);
        categoryRepository.deleteById(id);
        cacheService.evict("category_" + id);
        cacheService.evict("allCategories");
        logger.info("Category with ID: {} deleted", id);
    }

    public Category getOrCreateOrthographyCategory() {
        String cacheKey = "orthographyCategory";
        Category category = (Category) cacheService.get(cacheKey);
        if (category != null) {
            logger.debug("Returning cached orthography category");
            return category;
        }

        logger.info("Fetching or creating orthography category");
        List<Category> categories = categoryRepository.findByName("Орфография");
        if (categories.isEmpty()) {
            category = new Category("Орфография");
            category = categoryRepository.save(category);
            logger.info("Created new orthography category with ID: {}", category.getId());
        } else {
            category = categories.get(0);
            logger.debug("Found existing orthography category with ID: {}", category.getId());
        }
        cacheService.put(cacheKey, category);
        return category;
    }
}