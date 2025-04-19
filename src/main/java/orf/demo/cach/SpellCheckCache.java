package orf.demo.cache;

import orf.demo.model.SpellCheckCategory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpellCheckCache {

    private final Map<String, List<SpellCheckCategory>> cache = new HashMap<>();

    public void put(String categoryName, List<SpellCheckCategory> spellChecks) {
        cache.put(categoryName, spellChecks);
    }

    public List<SpellCheckCategory> get(String categoryName) {
        return cache.get(categoryName);
    }

    public void clear() {
        cache.clear();
    }
}