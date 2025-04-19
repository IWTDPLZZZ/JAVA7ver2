package orf.demo.checker;

import org.springframework.stereotype.Component;

@Component
public class SimpleSpellChecker extends AbstractSpellChecker {

    @Override
    public String checkSpelling(String word) {
        if (word == null) {
            return "Incorrect";
        }
        return word.length() > 3 ? "Correct" : "Incorrect";
    }
}