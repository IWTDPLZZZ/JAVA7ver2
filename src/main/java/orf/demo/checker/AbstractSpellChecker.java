package orf.demo.checker;

public abstract class AbstractSpellChecker implements SpellChecker {
    @Override
    public abstract String checkSpelling(String text);
}