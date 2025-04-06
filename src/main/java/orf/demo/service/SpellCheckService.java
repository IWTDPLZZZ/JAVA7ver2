package orf.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import orf.demo.model.Category;
import orf.demo.model.SpellCheck;
import orf.demo.model.SpellCheckCategory;
import orf.demo.repository.CategoryRepository;
import orf.demo.repository.SpellCheckCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SpellCheckService {
    private static final Logger logger = LoggerFactory.getLogger(SpellCheckService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SpellCheckCategoryRepository spellCheckCategoryRepository;

    @Autowired
    private CacheService cacheService;

    @Transactional
    public List<SpellCheck> processAndSaveSpellChecks(String text) {
        if (text == null || text.trim().isEmpty()) {
            logger.error("Text parameter is null or empty");
            throw new IllegalArgumentException("Text parameter cannot be null or empty");
        }

        logger.info("Processing spell check for text: {}", text);
        String cacheKey = "spellChecks_" + text;
        @SuppressWarnings("unchecked")
        List<SpellCheck> cachedErrors = (List<SpellCheck>) cacheService.get(cacheKey);
        if (cachedErrors != null) {
            logger.debug("Returning cached result for text: {}", text);
            return cachedErrors;
        }

        try {
            List<SpellCheck> errors = checkSentence(text);
            Category orthographyCategory = getOrCreateOrthographyCategory();

            for (SpellCheck error : errors) {
                SpellCheckCategory detailedEntity = getOrCreateSpellCheckEntity(error);
                addCategoryToEntity(detailedEntity, orthographyCategory);
                spellCheckCategoryRepository.save(detailedEntity);
            }

            cacheService.put(cacheKey, errors);
            logger.info("Spell check completed for text: {}", text);
            return errors;
        } catch (Exception e) {
            logger.error("Failed to process spell check for text: {}. Error: {}", text, e.getMessage(), e);
            throw new RuntimeException("Failed to process spell check: " + e.getMessage(), e);
        }
    }

    private List<SpellCheck> checkSentence(String sentence) {
        logger.debug("Checking sentence: {}", sentence);
        String[] words = sentence.split("\\s+");
        List<SpellCheck> results = new ArrayList<>();

        for (String word : words) {
            SpellCheck result = checkWord(word);
            results.add(result);
        }
        logger.debug("Sentence check completed with {} results", results.size());
        return results;
    }

    private Category getOrCreateOrthographyCategory() {
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

    private SpellCheckCategory getOrCreateSpellCheckEntity(SpellCheck error) {
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

    private void addCategoryToEntity(SpellCheckCategory entity, Category category) {
        logger.debug("Adding category {} to spell check entity {}", category.getName(), entity.getName());
        Set<Category> categories = entity.getCategories() != null ? entity.getCategories() : new HashSet<>();
        categories.add(category);
        entity.setCategories(categories);
    }

    private SpellCheck checkWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            logger.error("Word parameter is null or empty");
            throw new IllegalArgumentException("Word parameter cannot be null or empty");
        }

        String cacheKey = "wordCheck_" + word;
        SpellCheck cachedResult = (SpellCheck) cacheService.get(cacheKey);
        if (cachedResult != null) {
            logger.debug("Returning cached result for word: {}", word);
            return cachedResult;
        }

        logger.info("Checking word: {}", word);
        String urlStr = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            String responseBody;

            try {
                responseBody = readResponse(conn);
            } catch (IOException e) {
                logger.error("Failed to read response for word: {}. Error: {}", word, e.getMessage(), e);
                SpellCheck result = new SpellCheck(word, "Ошибка", "Слово не найдено в словаре");
                cacheService.put(cacheKey, result);
                return result;
            }

            SpellCheck result;
            if (responseCode == 200) {
                result = new SpellCheck(word, "Корректно", null);
                logger.debug("Word {} is correct", word);
            } else {
                result = new SpellCheck(word, "Ошибка", "Ответ от API: " + responseBody);
                logger.warn("Word {} is incorrect: {}", word, responseBody);
            }
            cacheService.put(cacheKey, result);
            return result;
        } catch (IOException e) {
            logger.error("Failed to check word: {}. Error: {}", word, e.getMessage(), e);
            SpellCheck result = new SpellCheck(word, "Ошибка при запросе", e.getMessage());
            cacheService.put(cacheKey, result);
            return result;
        }
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        logger.debug("Reading response from API");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(result.toString());
            logger.debug("Response parsed successfully");
            return rootNode.toString();
        }
    }

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

        logger.info("Deleting category with ID: {}", id);
        categoryRepository.deleteById(id);
        cacheService.evict("category_" + id);
        cacheService.evict("allCategories");
        logger.info("Category with ID: {} deleted", id);
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
            category = categoryRepository.findById(categoryId)
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
            category = categoryRepository.findById(categoryId)
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