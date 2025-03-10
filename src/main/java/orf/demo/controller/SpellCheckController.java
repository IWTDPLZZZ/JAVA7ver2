package orf.demo.controller;

import orf.demo.model.SpellCheck;
import orf.demo.model.SpellCheckCategory;
import orf.demo.model.Category;
import orf.demo.service.SpellCheckService;
import orf.demo.repository.SpellCheckCategoryRepository;
import orf.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

@RestController
public class SpellCheckController {

    @Autowired
    private SpellCheckService spellCheckService;

    @Autowired
    private SpellCheckCategoryRepository spellCheckRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/check")
    public Map<String, Object> checkText(@RequestParam String text) {
        Map<String, Object> response = new HashMap<>();
        List<SpellCheck> errors = spellCheckService.checkSentence(text);

        // Получаем категорию "Орфография" (или создаем, если не существует)
        List<Category> orthographyCategories = categoryRepository.findByName("Орфография");
        Category orthographyCategory;

        if (orthographyCategories.isEmpty()) {
            orthographyCategory = new Category("Орфография");
            categoryRepository.save(orthographyCategory);
        } else {
            orthographyCategory = orthographyCategories.get(0);
        }

        // Сохраняем каждое проверенное слово в базу данных
        for (SpellCheck error : errors) {
            // Пытаемся найти слово в базе данных
            List<SpellCheckCategory> existingEntities = spellCheckRepository.findByName(error.getWord());
            SpellCheckCategory spellCheckEntity;

            if (existingEntities.isEmpty()) {
                // Если слово не найдено, создаем новую запись
                spellCheckEntity = new SpellCheckCategory();
                spellCheckEntity.setName(error.getWord());
            } else {
                // Используем первую найденную запись
                spellCheckEntity = existingEntities.get(0);
            }

            // Обновляем статус и текст ошибки
            spellCheckEntity.setStatus(error.getStatus());
            spellCheckEntity.setError(error.getError());

            // Добавляем категорию
            Set<Category> categories = spellCheckEntity.getCategories();
            if (categories == null) {
                categories = new HashSet<>();
            }
            categories.add(orthographyCategory);
            spellCheckEntity.setCategories(categories);

            // Сохраняем в базу данных
            spellCheckRepository.save(spellCheckEntity);
        }

        response.put("errors", errors);
        return response;
    }
}