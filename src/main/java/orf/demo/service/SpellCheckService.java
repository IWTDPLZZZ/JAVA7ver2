package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.model.SpellCheck;
import orf.demo.model.SpellCheckCategory;
import orf.demo.repository.CategoryRepository;
import orf.demo.repository.SpellCheckCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class SpellCheckService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SpellCheckCategoryRepository spellCheckCategoryRepository;

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
        List<SpellCheck> errors = checkSentence(text);

        Category orthographyCategory = getOrCreateOrthographyCategory();

        for (SpellCheck error : errors) {
            SpellCheckCategory detailedEntity = getOrCreateSpellCheckEntity(error);

            addCategoryToEntity(detailedEntity, orthographyCategory);

            spellCheckCategoryRepository.save(detailedEntity);
        }

        return errors;
    }

    private Category getOrCreateOrthographyCategory() {
        List<Category> categories = categoryRepository.findByName("Орфография");
        if (categories.isEmpty()) {
            Category category = new Category("Орфография");
            return categoryRepository.save(category);
        }
        return categories.get(0);
    }

    private SpellCheckCategory getOrCreateSpellCheckEntity(SpellCheck error) {
        List<SpellCheckCategory> entities = spellCheckCategoryRepository.findByName(error.getWord());
        SpellCheckCategory entity;

        if (entities.isEmpty()) {
            entity = new SpellCheckCategory();
            entity.setName(error.getWord());
        } else {
            entity = entities.get(0);
        }

        entity.setStatus(error.getStatus());
        entity.setError(error.getError());

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
        return categoryRepository.findAll();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<SpellCheckCategory> getAllSpellChecks() {
        return spellCheckCategoryRepository.findAll();
    }

    public SpellCheckCategory saveSpellCheck(SpellCheckCategory spellCheckCategory) {
        return spellCheckCategoryRepository.save(spellCheckCategory);
    }

    public Optional<SpellCheckCategory> getSpellCheckById(Long id) {
        return spellCheckCategoryRepository.findById(id);
    }

    public void deleteSpellCheck(Long id) {
        spellCheckCategoryRepository.deleteById(id);
    }

    @Transactional
    public void addCategoryToSpellCheck(Long spellCheckId, Long categoryId) {
        SpellCheckCategory spellCheck = spellCheckCategoryRepository.findById(spellCheckId)
                .orElseThrow(() -> new RuntimeException("SpellCheck не найден"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        Set<Category> categories = spellCheck.getCategories();
        if (categories == null) {
            categories = new HashSet<>();
        }
        categories.add(category);
        spellCheck.setCategories(categories);

        spellCheckCategoryRepository.save(spellCheck);
    }

    @Transactional
    public void removeCategoryFromSpellCheck(Long spellCheckId, Long categoryId) {
        SpellCheckCategory spellCheck = spellCheckCategoryRepository.findById(spellCheckId)
                .orElseThrow(() -> new RuntimeException("SpellCheck не найден"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        Set<Category> categories = spellCheck.getCategories();
        categories.remove(category);
        spellCheck.setCategories(categories);

        spellCheckCategoryRepository.save(spellCheck);
    }
}