package orf.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import orf.demo.model.Category;
import orf.demo.model.SpellCheck;
import orf.demo.model.SpellCheckCategory;
import orf.demo.repository.CategoryRepository;
import orf.demo.repository.SpellCheckCategoryRepository;
import orf.demo.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class SpellCheckService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SpellCheckCategoryRepository spellCheckCategoryRepository;

    @Autowired
    private CacheService cacheService;

    private final Map<String, List<SpellCheck>> spellCheckCache = new HashMap<>();

    private List<SpellCheck> checkSentence(String sentence) {
        String[] words = sentence.split("\\s+");
        List<SpellCheck> results = new ArrayList<>();

        for (String word : words) {
            SpellCheck result = checkWord(word);
            results.add(result);
        }

        return results;
    }

    @Transactional
    public List<SpellCheck> processAndSaveSpellChecks(String text) {
        if (spellCheckCache.containsKey(text)) {
            return spellCheckCache.get(text);
        }

        List<SpellCheck> errors = checkSentence(text);

        Category orthographyCategory = getOrCreateOrthographyCategory();

        for (SpellCheck error : errors) {
            SpellCheckCategory detailedEntity = getOrCreateSpellCheckEntity(error);

            addCategoryToEntity(detailedEntity, orthographyCategory);

            spellCheckCategoryRepository.save(detailedEntity);
        }

        spellCheckCache.put(text, errors);
        return errors;
    }

    private Category getOrCreateOrthographyCategory() {
        String cacheKey = "orthographyCategory";
        Category category = (Category) cacheService.get(cacheKey);
        if (category == null) {
            List<Category> categories = categoryRepository.findByName("Орфография");
            if (categories.isEmpty()) {
                category = new Category("Орфография");
                category = categoryRepository.save(category);
            } else {
                category = categories.get(0);
            }
            cacheService.put(cacheKey, category);
        }
        return category;
    }

    private SpellCheckCategory getOrCreateSpellCheckEntity(SpellCheck error) {
        String cacheKey = "spellCheckCategory_" + error.getWord();
        SpellCheckCategory entity = (SpellCheckCategory) cacheService.get(cacheKey);
        if (entity == null) {
            List<SpellCheckCategory> entities = spellCheckCategoryRepository.findByName(error.getWord());
            if (entities.isEmpty()) {
                entity = new SpellCheckCategory();
                entity.setName(error.getWord());
            } else {
                entity = entities.get(0);
            }
            entity.setStatus(error.getStatus());
            entity.setError(error.getError());
            cacheService.put(cacheKey, entity);
        }
        return entity;
    }

    private void addCategoryToEntity(SpellCheckCategory entity, Category category) {
        Set<Category> categories = entity.getCategories();
        if (categories == null) {
            categories = new HashSet<>();
        }
        categories.add(category);
        entity.setCategories(categories);
    }

    private SpellCheck checkWord(String word) {
        String urlStr = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            String apiResponse;

            try {
                apiResponse = readResponse(conn);
            } catch (IOException e) {
                return new SpellCheck(word, "Ошибка", "Слово не найдено в словаре");
            }

            if (responseCode == 200) {
                return new SpellCheck(word, "Корректно", null);
            } else {
                return new SpellCheck(word, "Ошибка", "Ответ от API: " + apiResponse);
            }
        } catch (IOException e) {
            return new SpellCheck(word, "Ошибка при запросе", e.getMessage());
        }
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(result.toString());

            return rootNode.toString();
        }
    }

    public List<Category> getAllCategories() {
        return cacheService.get("allCategories") != null
                ? (List<Category>) cacheService.get("allCategories")
                : categoryRepository.findAll();
    }

    public Category saveCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        cacheService.evictAll();
        return savedCategory;
    }

    public Optional<Category> getCategoryById(Long id) {
        String cacheKey = "category_" + id;
        Category category = (Category) cacheService.get(cacheKey);
        if (category == null) {
            return categoryRepository.findById(id);
        }
        return Optional.of(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        cacheService.evict("category_" + id);
        cacheService.evict("allCategories");
    }

    public List<SpellCheckCategory> getAllSpellChecks() {
        return cacheService.get("allSpellChecks") != null
                ? (List<SpellCheckCategory>) cacheService.get("allSpellChecks")
                : spellCheckCategoryRepository.findAll();
    }

    public SpellCheckCategory saveSpellCheck(SpellCheckCategory spellCheckCategory) {
        SpellCheckCategory savedEntity = spellCheckCategoryRepository.save(spellCheckCategory);
        cacheService.evict("allSpellChecks");
        return savedEntity;
    }

    public Optional<SpellCheckCategory> getSpellCheckById(Long id) {
        String cacheKey = "spellCheckCategory_" + id;
        SpellCheckCategory spellCheckCategory = (SpellCheckCategory) cacheService.get(cacheKey);
        if (spellCheckCategory == null) {
            return spellCheckCategoryRepository.findById(id);
        }
        return Optional.of(spellCheckCategory);
    }

    public void deleteSpellCheck(Long id) {
        spellCheckCategoryRepository.deleteById(id);
        cacheService.evict("spellCheckCategory_" + id);
        cacheService.evict("allSpellChecks");
    }

    @Transactional
    public void addCategoryToSpellCheck(Long spellCheckId, Long categoryId) {
        String spellCheckCacheKey = "spellCheckCategory_" + spellCheckId;
        String categoryCacheKey = "category_" + categoryId;

        SpellCheckCategory spellCheck = (SpellCheckCategory) cacheService.get(spellCheckCacheKey);
        if (spellCheck == null) {
            spellCheck = spellCheckCategoryRepository.findById(spellCheckId)
                    .orElseThrow(() -> new RuntimeException("SpellCheck не найден"));
            cacheService.put(spellCheckCacheKey, spellCheck);
        }

        Category category = (Category) cacheService.get(categoryCacheKey);
        if (category == null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            cacheService.put(categoryCacheKey, category);
        }

        Set<Category> categories = spellCheck.getCategories();
        if (categories == null) {
            categories = new HashSet<>();
        }
        categories.add(category);
        spellCheck.setCategories(categories);

        spellCheckCategoryRepository.save(spellCheck);
        cacheService.evict(spellCheckCacheKey);
    }

    @Transactional
    public void removeCategoryFromSpellCheck(Long spellCheckId, Long categoryId) {
        String spellCheckCacheKey = "spellCheckCategory_" + spellCheckId;
        String categoryCacheKey = "category_" + categoryId;

        SpellCheckCategory spellCheck = (SpellCheckCategory) cacheService.get(spellCheckCacheKey);
        if (spellCheck == null) {
            spellCheck = spellCheckCategoryRepository.findById(spellCheckId)
                    .orElseThrow(() -> new RuntimeException("SpellCheck не найден"));
            cacheService.put(spellCheckCacheKey, spellCheck);
        }

        Category category = (Category) cacheService.get(categoryCacheKey);
        if (category == null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            cacheService.put(categoryCacheKey, category);
        }

        Set<Category> categories = spellCheck.getCategories();
        categories.remove(category);
        spellCheck.setCategories(categories);

        spellCheckCategoryRepository.save(spellCheck);
        cacheService.evict(spellCheckCacheKey);
    }
}