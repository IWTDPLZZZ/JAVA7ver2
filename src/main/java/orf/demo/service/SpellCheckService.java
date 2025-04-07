package orf.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import orf.demo.model.Category;
import orf.demo.model.SpellCheck;
import orf.demo.model.SpellCheckCategory;
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
import java.util.List;

@Service
public class SpellCheckService {
    private static final Logger logger = LoggerFactory.getLogger(SpellCheckService.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpellCheckCategoryService spellCheckCategoryService;

    @Autowired
    private CacheService cacheService;

    @Transactional
    public List<SpellCheck> processAndSaveSpellChecks(String text) {
        validateText(text);

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
            Category orthographyCategory = categoryService.getOrCreateOrthographyCategory();

            for (SpellCheck error : errors) {
                SpellCheckCategory detailedEntity = spellCheckCategoryService.getOrCreateSpellCheckEntity(error);
                spellCheckCategoryService.addCategoryToEntity(detailedEntity, orthographyCategory);
                spellCheckCategoryService.saveSpellCheck(detailedEntity);
            }

            cacheService.put(cacheKey, errors);
            logger.info("Spell check completed for text: {}", text);
            return errors;
        } catch (Exception e) {
            logger.error("Failed to process spell check for text: {}. Error: {}", text, e.getMessage(), e);
            throw new RuntimeException("Failed to process spell check: " + e.getMessage(), e);
        }
    }

    private void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            logger.error("Text parameter is null or empty");
            throw new IllegalArgumentException("Text parameter cannot be empty");
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

    private SpellCheck checkWord(String word) {
        validateText(word);

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
}