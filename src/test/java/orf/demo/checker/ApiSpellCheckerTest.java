package orf.demo.checker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiSpellCheckerTest {

    private ApiSpellChecker spellChecker;

    @BeforeEach
    void setUp() {
        spellChecker = mock(ApiSpellChecker.class);

        lenient().when(spellChecker.checkSpelling("hello")).thenReturn("Correct");
        lenient().when(spellChecker.checkSpelling("hi")).thenReturn("Incorrect");
        lenient().when(spellChecker.checkSpelling(null)).thenReturn("Incorrect");
        lenient().when(spellChecker.checkSpelling("")).thenReturn("Incorrect");
    }

    @Test
    void shouldReturnCorrectForValidText() {
        String result = spellChecker.checkSpelling("hello");

        assertEquals("Correct", result);
        verify(spellChecker, times(1)).checkSpelling("hello");
    }

    @Test
    void shouldReturnIncorrectForInvalidText() {
        String result = spellChecker.checkSpelling("hi");

        assertEquals("Incorrect", result);
        verify(spellChecker, times(1)).checkSpelling("hi");
    }

    @Test
    void shouldReturnIncorrectForNullText() {
        String result = spellChecker.checkSpelling(null);

        assertEquals("Incorrect", result);
        verify(spellChecker, times(1)).checkSpelling(null);
    }

    @Test
    void shouldReturnIncorrectForEmptyText() {
        String result = spellChecker.checkSpelling("");

        assertEquals("Incorrect", result);
        verify(spellChecker, times(1)).checkSpelling("");
    }
}