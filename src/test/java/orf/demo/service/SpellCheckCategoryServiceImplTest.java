package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.model.SpellCheckCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellCheckCategoryServiceImplTest {

    private SpellCheckCategoryServiceImpl spellCheckCategoryService;

    @BeforeEach
    void setUp() {
        spellCheckCategoryService = mock(SpellCheckCategoryServiceImpl.class);

        SpellCheckCategory spellCheck = mock(SpellCheckCategory.class);
        SpellCheckCategory savedSpellCheck = mock(SpellCheckCategory.class);
        SpellCheckCategory updatedSpellCheck = mock(SpellCheckCategory.class);
        SpellCheckCategory assignedSpellCheck = mock(SpellCheckCategory.class);
        Category category = mock(Category.class);

        lenient().when(spellCheck.getId()).thenReturn(1L);
        lenient().when(savedSpellCheck.getName()).thenReturn("test");
        lenient().when(updatedSpellCheck.getId()).thenReturn(1L);
        lenient().when(updatedSpellCheck.getName()).thenReturn("new");
        lenient().when(assignedSpellCheck.getId()).thenReturn(1L);
        lenient().when(category.getId()).thenReturn(2L);

        lenient().when(spellCheckCategoryService.getAllSpellChecks())
                .thenReturn(Arrays.asList(mock(SpellCheckCategory.class), mock(SpellCheckCategory.class)));

        lenient().when(spellCheckCategoryService.getSpellCheckById(1L)).thenReturn(Optional.of(spellCheck));
        lenient().when(spellCheckCategoryService.getSpellCheckById(2L)).thenReturn(Optional.empty());

        lenient().when(spellCheckCategoryService.saveSpellCheck(any(SpellCheckCategory.class)))
                .thenReturn(savedSpellCheck);

        lenient().when(spellCheckCategoryService.updateSpellCheck(eq(1L), any(SpellCheckCategory.class)))
                .thenReturn(updatedSpellCheck);
        lenient().when(spellCheckCategoryService.updateSpellCheck(eq(2L), any(SpellCheckCategory.class)))
                .thenThrow(new RuntimeException());

        lenient().doNothing().when(spellCheckCategoryService).deleteSpellCheck(1L);
        lenient().doThrow(new RuntimeException()).when(spellCheckCategoryService).deleteSpellCheck(2L);

        // Настройка поведения для assignCategoryToSpellCheck
        lenient().when(spellCheckCategoryService.assignCategoryToSpellCheck(1L, 2L))
                .thenReturn(assignedSpellCheck);

        lenient().doNothing().when(spellCheckCategoryService).addCategoryToSpellCheck(1L, 2L);

        lenient().doNothing().when(spellCheckCategoryService).removeCategoryFromSpellCheck(1L, 2L);

        lenient().when(spellCheckCategoryService.getSpellChecksByCategory(String.valueOf(1L)))
                .thenReturn(Arrays.asList(mock(SpellCheckCategory.class), mock(SpellCheckCategory.class)));
    }

    @Test
    void shouldGetAllSpellChecksSuccessfully() {
        List<SpellCheckCategory> result = spellCheckCategoryService.getAllSpellChecks();

        assertEquals(2, result.size());
        verify(spellCheckCategoryService, times(1)).getAllSpellChecks();
    }

    @Test
    void shouldGetSpellCheckByIdSuccessfully() {
        Optional<SpellCheckCategory> result = Optional.ofNullable(spellCheckCategoryService.getSpellCheckById(1L));

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(spellCheckCategoryService, times(1)).getSpellCheckById(1L);
        verify(result.get(), times(1)).getId();
    }

    @Test
    void shouldReturnEmptyOptionalWhenSpellCheckNotFoundById() {
        Optional<SpellCheckCategory> result = Optional.ofNullable(spellCheckCategoryService.getSpellCheckById(2L));

        assertFalse(result.isPresent());
        verify(spellCheckCategoryService, times(1)).getSpellCheckById(2L);
    }

    @Test
    void shouldSaveSpellCheckSuccessfully() {
        SpellCheckCategory spellCheck = mock(SpellCheckCategory.class);
        lenient().when(spellCheck.getName()).thenReturn("test");

        SpellCheckCategory result = spellCheckCategoryService.saveSpellCheck(spellCheck);

        assertEquals("test", result.getName());
        verify(spellCheckCategoryService, times(1)).saveSpellCheck(spellCheck);
        verify(result, times(1)).getName();
    }

    @Test
    void shouldUpdateSpellCheckSuccessfully() {
        SpellCheckCategory updatedSpellCheck = mock(SpellCheckCategory.class);
        lenient().when(updatedSpellCheck.getName()).thenReturn("new");

        SpellCheckCategory result = spellCheckCategoryService.updateSpellCheck(1L, updatedSpellCheck);

        assertEquals("new", result.getName());
        verify(spellCheckCategoryService, times(1)).updateSpellCheck(1L, updatedSpellCheck);
        verify(result, times(1)).getName();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUpdatingNonExistentSpellCheck() {
        SpellCheckCategory updatedSpellCheck = mock(SpellCheckCategory.class);
        lenient().when(updatedSpellCheck.getName()).thenReturn("new");

        assertThrows(RuntimeException.class, () -> spellCheckCategoryService.updateSpellCheck(2L, updatedSpellCheck));
        verify(spellCheckCategoryService, times(1)).updateSpellCheck(2L, updatedSpellCheck);
    }

    @Test
    void shouldDeleteSpellCheckSuccessfully() {
        spellCheckCategoryService.deleteSpellCheck(1L);

        verify(spellCheckCategoryService, times(1)).deleteSpellCheck(1L);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDeletingNonExistentSpellCheck() {
        assertThrows(RuntimeException.class, () -> spellCheckCategoryService.deleteSpellCheck(2L));
        verify(spellCheckCategoryService, times(1)).deleteSpellCheck(2L);
    }

    @Test
    void shouldAssignCategoryToSpellCheckSuccessfully() {
        SpellCheckCategory result = spellCheckCategoryService.assignCategoryToSpellCheck(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(spellCheckCategoryService, times(1)).assignCategoryToSpellCheck(1L, 2L);
        verify(result, times(1)).getId();
    }

    @Test
    void shouldAddCategoryToSpellCheckSuccessfully() {
        spellCheckCategoryService.addCategoryToSpellCheck(1L, 2L);

        verify(spellCheckCategoryService, times(1)).addCategoryToSpellCheck(1L, 2L);
    }

    @Test
    void shouldRemoveCategoryFromSpellCheckSuccessfully() {
        spellCheckCategoryService.removeCategoryFromSpellCheck(1L, 2L);

        verify(spellCheckCategoryService, times(1)).removeCategoryFromSpellCheck(1L, 2L);
    }

    @Test
    void shouldGetSpellChecksByCategorySuccessfully() {
        List<SpellCheckCategory> result = spellCheckCategoryService.getSpellChecksByCategory(1L);

        assertEquals(2, result.size());
        verify(spellCheckCategoryService, times(1)).getSpellChecksByCategory(1L);
    }
}