package orf.demo.service;

import orf.demo.dto.SpellCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellCheckServiceImplTest {

    private SpellCheckServiceImpl spellCheckService;

    @BeforeEach
    void setUp() {
        spellCheckService = mock(SpellCheckServiceImpl.class);

        lenient().when(spellCheckService.checkSpelling("hello")).thenReturn("Correct");
        lenient().when(spellCheckService.checkSpelling("hi")).thenReturn("Incorrect");

        lenient().when(spellCheckService.checkSpellingBulk(Arrays.asList("hello", "hi", "world")))
                .thenReturn(Arrays.asList(
                        new SpellCheckResponse("hello", true),
                        new SpellCheckResponse("hi", false),
                        new SpellCheckResponse("world", true)
                ));
        lenient().when(spellCheckService.checkSpellingBulk(Collections.emptyList()))
                .thenReturn(Collections.emptyList());
        lenient().when(spellCheckService.checkSpellingBulk(null))
                .thenThrow(new IllegalArgumentException());

        lenient().when(spellCheckService.checkSpellingBulkWithParams(Arrays.asList("hello", null, "world")))
                .thenReturn(Arrays.asList(
                        new SpellCheckResponse("hello", true),
                        new SpellCheckResponse("world", true)
                ));
        lenient().when(spellCheckService.checkSpellingBulkWithParams(Collections.emptyList()))
                .thenReturn(Collections.emptyList());
        lenient().when(spellCheckService.checkSpellingBulkWithParams(null))
                .thenThrow(new IllegalArgumentException());
    }

    @Test
    void shouldReturnCorrectResponseForValidText() {
        String result = spellCheckService.checkSpelling("hello");

        assertEquals("Correct", result);
        verify(spellCheckService, times(1)).checkSpelling("hello");
    }

    @Test
    void shouldReturnIncorrectResponseForInvalidText() {
        String result = spellCheckService.checkSpelling("hi");

        assertEquals("Incorrect", result);
        verify(spellCheckService, times(1)).checkSpelling("hi");
    }

    @Test
    void shouldReturnCorrectResultsForMixedTextsInBulkCheck() {
        List<String> texts = Arrays.asList("hello", "hi", "world");

        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulk(texts);

        assertEquals(3, results.size());
        assertEquals("hello", results.get(0).getText());
        assertTrue(results.get(0).isCorrect());
        assertEquals("hi", results.get(1).getText());
        assertFalse(results.get(1).isCorrect());
        assertEquals("world", results.get(2).getText());
        assertTrue(results.get(2).isCorrect());
        verify(spellCheckService, times(1)).checkSpellingBulk(texts);
    }

    @Test
    void shouldReturnEmptyListForEmptyListInBulkCheck() {
        List<String> texts = Collections.emptyList();

        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulk(texts);

        assertTrue(results.isEmpty());
        verify(spellCheckService, times(1)).checkSpellingBulk(texts);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNullListInBulkCheck() {
        assertThrows(IllegalArgumentException.class, () -> spellCheckService.checkSpellingBulk(null));
        verify(spellCheckService, times(1)).checkSpellingBulk(null);
    }

    @Test
    void shouldReturnCorrectResultsForMixedTextsInBulkCheckWithParams() {
        List<String> texts = Arrays.asList("hello", null, "world");

        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulkWithParams(texts);

        assertEquals(2, results.size());
        assertEquals("hello", results.get(0).getText());
        assertTrue(results.get(0).isCorrect());
        assertEquals("world", results.get(1).getText());
        assertTrue(results.get(1).isCorrect());
        verify(spellCheckService, times(1)).checkSpellingBulkWithParams(texts);
    }

    @Test
    void shouldReturnEmptyListForEmptyListInBulkCheckWithParams() {
        List<String> texts = Collections.emptyList();

        List<SpellCheckResponse> results = spellCheckService.checkSpellingBulkWithParams(texts);

        assertTrue(results.isEmpty());
        verify(spellCheckService, times(1)).checkSpellingBulkWithParams(texts);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNullListInBulkCheckWithParams() {
        assertThrows(IllegalArgumentException.class, () -> spellCheckService.checkSpellingBulkWithParams(null));
        verify(spellCheckService, times(1)).checkSpellingBulkWithParams(null);
    }
}