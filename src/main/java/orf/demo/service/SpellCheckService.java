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

    public List<SpellCheck> checkSentence(String sentence) {
        String[] words = sentence.split("\\s+");
        List<SpellCheck> results = new ArrayList<>();

        for (String word : words) {
            SpellCheck result = checkWord(word);
            results.add(result);

            saveSpellCheckResult(word, result.getStatus());
        }

        return results;
    }
    @Transactional
    public List<SpellCheck> processAndSaveSpellChecks(String text) {
        List<SpellCheck> errors = checkSentence(text);

        List<Category> orthographyCategories = categoryRepository.findByName("Орфография");
        Category orthographyCategory;

        if (orthographyCategories.isEmpty()) {
            orthographyCategory = new Category("Орфография");
            categoryRepository.save(orthographyCategory);
        } else {
            orthographyCategory = orthographyCategories.get(0);
        }

        for (SpellCheck error : errors) {
            List<SpellCheckCategory> existingEntities = spellCheckCategoryRepository.findByName(error.getWord());
            SpellCheckCategory spellCheckEntity;

            if (existingEntities.isEmpty()) {
                spellCheckEntity = new SpellCheckCategory();
                spellCheckEntity.setName(error.getWord());
            } else {
                spellCheckEntity = existingEntities.get(0);
            }

            spellCheckEntity.setStatus(error.getStatus());
            spellCheckEntity.setError(error.getError());

            Set<Category> categories = spellCheckEntity.getCategories();
            if (categories == null) {
                categories = new HashSet<>();
            }
            categories.add(orthographyCategory);
            spellCheckEntity.setCategories(categories);

            spellCheckCategoryRepository.save(spellCheckEntity);
        }

        return errors;
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


    @Transactional
    protected void saveSpellCheckResult(String word, String status) {
        SpellCheckCategory spellCheck = new SpellCheckCategory();
        spellCheck.setName(word);
        spellCheckCategoryRepository.save(spellCheck);
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