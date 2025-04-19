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
    private final RequestCounter requestCounter;

    @Autowired
    public SpellCheckServiceImpl(@Qualifier("simpleSpellChecker") SpellChecker spellChecker,
                                 RequestCounter requestCounter) {
        this.spellChecker = spellChecker;
        this.requestCounter = requestCounter;
    }

    @Override
    public String checkSpelling(String word) {
        requestCounter.incrementAndGet();
        return spellChecker.checkSpelling(word);
    }

    @Override
    public List<SpellCheckResponse> checkSpellingBulk(List<String> texts) {
        requestCounter.incrementAndGet();
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
        requestCounter.incrementAndGet();
        if (texts == null) {
            throw new IllegalArgumentException("Список текстов не может быть null");
        }
        return texts.stream()
                .filter(Objects::nonNull)
                .map(text -> {
                    String result = spellChecker.checkSpelling(text);
                    return new SpellCheckResponse(text, "Correct".equals(result));
                })
                .collect(Collectors.toList());
    }

    public long getRequestCount() {
        return requestCounter.getCount();
    }

    public void resetRequestCount() {
        requestCounter.reset();
    }
}