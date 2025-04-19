package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.model.SpellCheckCategory;
import orf.demo.repository.CategoryRepository;
import orf.demo.repository.SpellCheckCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellCheckCategoryServiceImplTest {

    @Mock
    private SpellCheckCategoryRepository spellCheckCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private SpellCheckCategoryServiceImpl spellCheckCategoryService;

    @Test
    void testGetAllSpellChecks_Success() {
        List<SpellCheckCategory> spellChecks = Arrays.asList(new SpellCheckCategory(), new SpellCheckCategory());
        when(spellCheckCategoryRepository.findAll()).thenReturn(spellChecks);

        List<SpellCheckCategory> result = spellCheckCategoryService.getAllSpellChecks();
        assertEquals(2, result.size());
        verify(spellCheckCategoryRepository).findAll();
    }

    @Test
    void testGetSpellCheckById_Success() {
        SpellCheckCategory spellCheck = new SpellCheckCategory();
        spellCheck.setId(1L);
        when(spellCheckCategoryRepository.findById(1L)).thenReturn(Optional.of(spellCheck));

        Optional<SpellCheckCategory> result = Optional.ofNullable(spellCheckCategoryService.getSpellCheckById(1L));
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(spellCheckCategoryRepository).findById(1L);
    }

    @Test
    void testGetSpellCheckById_NotFound() {
        when(spellCheckCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<SpellCheckCategory> result = Optional.ofNullable(spellCheckCategoryService.getSpellCheckById(1L));
        assertFalse(result.isPresent());
        verify(spellCheckCategoryRepository).findById(1L);
    }

    @Test
    void testSaveSpellCheck_Success() {
        SpellCheckCategory spellCheck = new SpellCheckCategory();
        spellCheck.setName("test");
        when(spellCheckCategoryRepository.save(any(SpellCheckCategory.class))).thenReturn(spellCheck);

        SpellCheckCategory result = spellCheckCategoryService.saveSpellCheck(spellCheck);
        assertEquals("test", result.getName());
        verify(spellCheckCategoryRepository).save(spellCheck);
    }

    @Test
    void testUpdateSpellCheck_Success() {
        SpellCheckCategory existingSpellCheck = new SpellCheckCategory();
        existingSpellCheck.setId(1L);
        existingSpellCheck.setName("old");

        SpellCheckCategory updatedSpellCheck = new SpellCheckCategory();
        updatedSpellCheck.setName("new");

        when(spellCheckCategoryRepository.findById(1L)).thenReturn(Optional.of(existingSpellCheck));
        when(spellCheckCategoryRepository.save(any(SpellCheckCategory.class))).thenReturn(existingSpellCheck);

        SpellCheckCategory result = spellCheckCategoryService.updateSpellCheck(1L, updatedSpellCheck);
        assertEquals("new", result.getName());
        verify(spellCheckCategoryRepository).findById(1L);
        verify(spellCheckCategoryRepository).save(any(SpellCheckCategory.class));
    }

    @Test
    void testUpdateSpellCheck_NotFound() {
        SpellCheckCategory updatedSpellCheck = new SpellCheckCategory();
        when(spellCheckCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            spellCheckCategoryService.updateSpellCheck(1L, updatedSpellCheck);
        });
        verify(spellCheckCategoryRepository).findById(1L);
    }

    @Test
    void testDeleteSpellCheck_Success() {
        when(spellCheckCategoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(spellCheckCategoryRepository).deleteById(1L);

        spellCheckCategoryService.deleteSpellCheck(1L);
        verify(spellCheckCategoryRepository).existsById(1L);
        verify(spellCheckCategoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteSpellCheck_NotFound() {
        when(spellCheckCategoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            spellCheckCategoryService.deleteSpellCheck(1L);
        });
        verify(spellCheckCategoryRepository).existsById(1L);
    }

    @Test
    void testAssignCategoryToSpellCheck_Success() {
        SpellCheckCategory spellCheck = new SpellCheckCategory();
        spellCheck.setId(1L);
        Category category = new Category();
        category.setId(2L);

        when(spellCheckCategoryRepository.findById(1L)).thenReturn(Optional.of(spellCheck));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(spellCheckCategoryRepository.save(any(SpellCheckCategory.class))).thenReturn(spellCheck);

        SpellCheckCategory result = spellCheckCategoryService.assignCategoryToSpellCheck(1L, 2L);
        assertEquals(spellCheck, result);
        verify(spellCheckCategoryRepository).findById(1L);
        verify(categoryRepository).findById(2L);
        verify(spellCheckCategoryRepository).save(spellCheck);
    }

    @Test
    void testAddCategoryToSpellCheck_Success() {
        SpellCheckCategory spellCheck = new SpellCheckCategory();
        spellCheck.setId(1L);
        Category category = new Category();
        category.setId(2L);

        when(spellCheckCategoryRepository.findById(1L)).thenReturn(Optional.of(spellCheck));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(spellCheckCategoryRepository.save(any(SpellCheckCategory.class))).thenReturn(spellCheck);

        spellCheckCategoryService.addCategoryToSpellCheck(1L, 2L);
        verify(spellCheckCategoryRepository).findById(1L);
        verify(categoryRepository).findById(2L);
        verify(spellCheckCategoryRepository).save(spellCheck);
    }

    @Test
    void testRemoveCategoryFromSpellCheck_Success() {
        SpellCheckCategory spellCheck = new SpellCheckCategory();
        spellCheck.setId(1L);
        Category category = new Category();
        category.setId(2L);
        spellCheck.addCategory(category);

        when(spellCheckCategoryRepository.findById(1L)).thenReturn(Optional.of(spellCheck));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(spellCheckCategoryRepository.save(any(SpellCheckCategory.class))).thenReturn(spellCheck);

        spellCheckCategoryService.removeCategoryFromSpellCheck(1L, 2L);
        verify(spellCheckCategoryRepository).findById(1L);
        verify(categoryRepository).findById(2L);
        verify(spellCheckCategoryRepository).save(spellCheck);
    }

    @Test
    void testGetSpellChecksByCategory_Success() {
        List<SpellCheckCategory> spellChecks = Arrays.asList(new SpellCheckCategory(), new SpellCheckCategory());
        when(spellCheckCategoryRepository.findByCategoryId(1L)).thenReturn(spellChecks);

        List<SpellCheckCategory> result = spellCheckCategoryService.getSpellChecksByCategory(1L);
        assertEquals(2, result.size());
        verify(spellCheckCategoryRepository).findByCategoryId(1L);
    }
}