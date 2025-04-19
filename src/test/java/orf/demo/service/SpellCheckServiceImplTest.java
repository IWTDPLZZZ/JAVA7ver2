package orf.demo.service;

import orf.demo.checker.SpellChecker;
import orf.demo.dto.SpellCheckResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellCheckServiceImplTest {

    @Mock
    private SpellChecker spellChecker;

    @InjectMocks
    private SpellCheckServiceImpl spellCheckService;

    @Test
    void testCheckSpelling_ValidText_ReturnsCorrectResponse() {
        String text = "hello";
        when(spellChecker.checkSpelling(text)).thenReturn("Correct");
        String result = spellCheckService.checkSpelling(text);
        assertEquals("Correct", result);
        verify(spellChecker).checkSpelling(text);
    }

    @Test
    void testCheckSpelling_InvalidText_ReturnsIncorrectResponse() {
        String text = "hi";
        when(spellChecker.checkSpelling(text)).thenReturn("Incorrect");
        String result = spellCheckService.checkSpelling(text);
        assertEquals("Incorrect", result);
        verify(spellChecker).checkSpelling(text);
    }

    @Test
    void testCheckSpellingBulk_MixedTexts_ReturnsCorrectResults() {
        List<String> texts = Arrays.asList("hello", "hi", "world");
        when(spellChecker.checkSpelling("hello")).thenReturn("Correct");
        when(spellChecker.checkSpelling("hi")).thenReturn("Incorrect");
        when(spellChecker.checkSpelling("world")).thenReturn("Correct");

        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulk(texts);
        assertEquals(3, results.size());
        assertEquals("hello", results.get(0).getText());
        assertTrue(results.get(0).isCorrect());
        assertEquals("hi", results.get(1).getText());
        assertFalse(results.get(1).isCorrect());
        assertEquals("world", results.get(2).getText());
        assertTrue(results.get(2).isCorrect());
        verify(spellChecker, times(3)).checkSpelling(anyString());
    }

    @Test
    void testCheckSpellingBulk_EmptyList_ReturnsEmptyList() {
        List<String> texts = Arrays.asList();
        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulk(texts);
        assertTrue(results.isEmpty());
        verify(spellChecker, never()).checkSpelling(anyString());
    }

    @Test
    void testCheckSpellingBulk_NullList_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            spellCheckService.checkSpellingBulk(null);
        });
        verify(spellChecker, never()).checkSpelling(anyString());
    }

    @Test
    void testCheckSpellingBulkWithParams_MixedTexts_ReturnsCorrectResults() {
        List<String> texts = Arrays.asList("hello", null, "world");
        when(spellChecker.checkSpelling("hello")).thenReturn("Correct");
        when(spellChecker.checkSpelling("world")).thenReturn("Correct");

        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulkWithParams(texts);
        assertEquals(2, results.size());
        assertEquals("hello", results.get(0).getText());
        assertTrue(results.get(0).isCorrect());
        assertEquals("world", results.get(1).getText());
        assertTrue(results.get(1).isCorrect());
        verify(spellChecker, times(2)).checkSpelling(anyString());
    }

    @Test
    void testCheckSpellingBulkWithParams_EmptyList_ReturnsEmptyList() {
        List<String> texts = Arrays.asList();
        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulkWithParams(texts);
        assertTrue(results.isEmpty());
        verify(spellChecker, never()).checkSpelling(anyString());
    }

    @Test
    void testCheckSpellingBulkWithParams_NullList_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            spellCheckService.checkSpellingBulkWithParams(null);
        });
        verify(spellChecker, never()).checkSpelling(anyString());
    }
}