package orf.demo.checker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleSpellCheckerTest {

    private SimpleSpellChecker spellChecker;

    @BeforeEach
    void setUp() {
        spellChecker = mock(SimpleSpellChecker.class);

        // Настройка поведения для checkSpelling
        lenient().when(spellChecker.checkSpelling("hello")).thenReturn("Correct");
        lenient().when(spellChecker.checkSpelling("world")).thenReturn("Correct");
        lenient().when(spellChecker.checkSpelling("hi")).thenReturn("Incorrect");
        lenient().when(spellChecker.checkSpelling(null)).thenReturn("Incorrect");
        lenient().when(spellChecker.checkSpelling("")).thenReturn("Incorrect");

        lenient().when(spellChecker.checkSpellingBulk(Arrays.asList("hello", "hi", "world", "")))
                .thenReturn(Arrays.asList(
                        "hello - Correct",
                        "hi - Incorrect",
                        "world - Correct",
                        "" - "Incorrect"
                ));
        lenient().when(spellChecker.checkSpellingBulk(List.of())).thenReturn(Arrays.asList());
        lenient().when(spellChecker.checkSpellingBulk(null)).thenThrow(new IllegalArgumentException());
    }

    @Test
    void shouldReturnCorrectForValidText() {
        // When
        String result = spellChecker.checkSpelling("hello");

        // Then
        assertEquals("Correct", result);
        verify(spellChecker, times(1)).checkSpelling("hello");
    }

    @Test
    void shouldReturnIncorrectForShortText() {
        // When
        String result = spellChecker.checkSpelling("hi");

        // Then
        assertEquals("Incorrect", result);
        verify(spellChecker, times(1)).checkSpelling("hi");
    }

    @Test
    void shouldReturnIncorrectForNullText() {
        // When
        String result = spellChecker.checkSpelling(null);

        // Then
        assertEquals("Incorrect", result);
        verify(spellChecker, times(1)).checkSpelling(null);
    }

    @Test
    void shouldReturnIncorrectForEmptyText() {
        // When
        String result = spellChecker.checkSpelling("");

        // Then
        assertEquals("Incorrect", result);
        verify(spellChecker, times(1)).checkSpelling("");
    }

    @Test
    void shouldReturnCorrectResultsForMixedTextsInBulkCheck() {
        // Given
        List<String> texts = Arrays.asList("hello", "hi", "world", "");

        // When
        List<String> results = spellChecker.checkSpellingBulk(texts);

        // Then
        assertEquals(Arrays.asList(
                "hello - Correct",
                "hi - Incorrect",
                "world - Correct",
                "" - "Incorrect"
        ), results);
        verify(spellChecker, times(1)).checkSpellingBulk(texts);
    }

    @Test
    void shouldReturnEmptyListForEmptyListInBulkCheck() {
        // Given
        List<String> texts = Arrays.asList();

        // When
        List<String> results = spellChecker.checkSpellingBulk(texts);

        // Then
        assertTrue(results.isEmpty());
        verify(spellChecker, times(1)).checkSpellingBulk(texts);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNullListInBulkCheck() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> spellChecker.checkSpellingBulk(null));
        verify(spellChecker, times(1)).checkSpellingBulk(null);
    }
}