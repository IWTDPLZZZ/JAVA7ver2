package orf.demo.checker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleSpellCheckerTest {

    private SimpleSpellChecker spellChecker;

    @BeforeEach
    void setUp() {
        spellChecker = new SimpleSpellChecker();
    }

    @Test
    void testCheckSpelling_ValidText_ReturnsCorrect() {
        assertEquals("Correct", spellChecker.checkSpelling("hello"));
    }

    @Test
    void testCheckSpelling_ShortText_ReturnsIncorrect() {
        assertEquals("Incorrect", spellChecker.checkSpelling("hi"));
    }

    @Test
    void testCheckSpelling_NullText_ReturnsIncorrect() {
        assertEquals("Incorrect", spellChecker.checkSpelling(null));
    }

    @Test
    void testCheckSpelling_EmptyText_ReturnsIncorrect() {
        assertEquals("Incorrect", spellChecker.checkSpelling(""));
    }

    @Test
    void testCheckSpellingBulk_MixedTexts_ReturnsCorrectResults() {
        List<String> texts = Arrays.asList("hello", "hi", "world", "");
        List<String> expected = Arrays.asList(
                "hello - Correct",
                "hi - Incorrect",
                "world - Correct",
                "" - "Incorrect"
        );
        List<String> results = spellChecker.checkSpellingBulk(texts);
        assertEquals(expected, results);
    }

    @Test
    void testCheckSpellingBulk_EmptyList_ReturnsEmptyList() {
        List<String> texts = Arrays.asList();
        List<String> results = spellChecker.checkSpellingBulk(texts);
        assertTrue(results.isEmpty());
    }

    @Test
    void testCheckSpellingBulk_NullList_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            spellChecker.checkSpellingBulk(null);
        });
    }
}