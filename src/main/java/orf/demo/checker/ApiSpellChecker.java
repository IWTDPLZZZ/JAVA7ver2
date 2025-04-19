package orf.demo.checker;

import org.springframework.stereotype.Component;

@Component
public class ApiSpellChecker extends AbstractSpellChecker {

    @Override
    public String checkSpelling(String word) {
        if (word == null) {
            return "Incorrect";
        }
        return word.equalsIgnoreCase("hello") ? "Correct" : "Incorrect";
    }
}