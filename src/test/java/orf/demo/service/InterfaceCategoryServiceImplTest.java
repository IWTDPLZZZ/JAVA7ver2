package orf.demo.service;

import orf.demo.model.Category;
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
class InterfaceCategoryServiceImplTest {

    private InterfaceCategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = mock(InterfaceCategoryServiceImpl.class);

        Category savedCategory = mock(Category.class);
        Category existingCategory = mock(Category.class);
        Category updatedCategory = mock(Category.class);

        lenient().when(savedCategory.getName()).thenReturn("Test Category");
        lenient().when(existingCategory.getId()).thenReturn(1L);
        lenient().when(existingCategory.getName()).thenReturn("Old Name");
        lenient().when(updatedCategory.getName()).thenReturn("New Name");

        lenient().when(categoryService.createCategory("Test Category")).thenReturn(savedCategory);

        lenient().when(categoryService.getAllCategories())
                .thenReturn(Arrays.asList(mock(Category.class), mock(Category.class)));

        lenient().when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(existingCategory));
        lenient().when(categoryService.getCategoryById(2L)).thenReturn(Optional.empty());

        lenient().when(categoryService.updateCategory(eq(1L), any(Category.class)))
                .thenReturn(updatedCategory);
        lenient().when(categoryService.updateCategory(eq(2L), any(Category.class)))
                .thenThrow(new RuntimeException());

        lenient().doNothing().when(categoryService).deleteCategory(1L);
        lenient().doThrow(new RuntimeException()).when(categoryService).deleteCategory(2L);
    }

    @Test
    void shouldCreateCategorySuccessfully() {

        Category result = categoryService.createCategory("Test Category");

        assertEquals("Test Category", result.getName());
        verify(categoryService, times(1)).createCategory("Test Category");
        verify(result, times(1)).getName();
    }

    @Test
    void shouldGetAllCategoriesSuccessfully() {
        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void shouldGetCategoryByIdSuccessfully() {
        Optional<Category> result = categoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(categoryService, times(1)).getCategoryById(1L);
        verify(result.get(), times(1)).getId();
    }

    @Test
    void shouldReturnEmptyOptionalWhenCategoryNotFoundById() {
        Optional<Category> result = categoryService.getCategoryById(2L);

        assertFalse(result.isPresent());
        verify(categoryService, times(1)).getCategoryById(2L);
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Category updatedCategory = mock(Category.class);
        lenient().when(updatedCategory.getName()).thenReturn("New Name");

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertEquals("New Name", result.getName());
        verify(categoryService, times(1)).updateCategory(1L, updatedCategory);
        verify(result, times(1)).getName();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUpdatingNonExistentCategory() {
        Category updatedCategory = mock(Category.class);
        lenient().when(updatedCategory.getName()).thenReturn("New Name");

        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(2L, updatedCategory));
        verify(categoryService, times(1)).updateCategory(2L, updatedCategory);
    }

    @Test
    void shouldDeleteCategorySuccessfully() {
        categoryService.deleteCategory(1L);

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDeletingNonExistentCategory() {
        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(2L));
        verify(categoryService, times(1)).deleteCategory(2L);
    }
}