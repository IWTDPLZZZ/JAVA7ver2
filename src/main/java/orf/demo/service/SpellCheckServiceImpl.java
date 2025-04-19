package orf.demo.service;

import orf.demo.checker.SpellChecker;
import orf.demo.dto.SpellCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SpellCheckServiceImpl implements SpellCheckService {

    private final SpellChecker spellChecker;

    @Autowired
    public SpellCheckServiceImpl(@Qualifier("simpleSpellChecker") SpellChecker spellChecker) {
        this.spellChecker = spellChecker;
    }

    @Override
    public String checkSpelling(String word) {
        return spellChecker.checkSpelling(word);
    }

    @Override
    public List<SpellCheckResponse> checkSpellingBulk(List<String> texts) {
        if (texts == null) {
            throw new IllegalArgumentException("Список текстов не может быть null");
        }
        return texts.stream()
                .map(text -> {
                    String result = spellChecker.checkSpelling(text);
                    return new SpellCheckResponse(text, "Correct".equals(result));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SpellCheckResponse> checkSpellingBulkWithParams(List<String> texts) {
        return texts.stream()
                .filter(Objects::nonNull) // Фильтруем null элементы
                .map(text -> {
                    String result = spellChecker.checkSpelling(text);
                    return new SpellCheckResponse(text, "Correct".equals(result));
                })
                .collect(Collectors.toList());
    }
}