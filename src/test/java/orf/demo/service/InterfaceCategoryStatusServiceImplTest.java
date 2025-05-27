package orf.demo.service;

import orf.demo.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterfaceCategoryStatusServiceImplTest {

    private InterfaceCategoryStatusServiceImpl categoryStatusService;

    @BeforeEach
    void setUp() {
        categoryStatusService = mock(InterfaceCategoryStatusServiceImpl.class);

        Category existingCategory = mock(Category.class);
        Category updatedCategory = mock(Category.class);

        lenient().when(existingCategory.getId()).thenReturn(1L);
        lenient().when(updatedCategory.getStatus()).thenReturn("inactive");

        List<Category> categories = Arrays.asList(mock(Category.class), mock(Category.class));
        lenient().when(categoryStatusService.getCategoriesByStatus("active")).thenReturn(categories);

        Map<String, Object> updateResult = new HashMap<>();
        updateResult.put("status", "updated");
        updateResult.put("categoryId", 1L);
        updateResult.put("newStatus", "inactive");
        lenient().when(categoryStatusService.updateCategoryStatus(eq(1L), any(Category.class)))
                .thenReturn(updateResult);
        lenient().when(categoryStatusService.updateCategoryStatus(eq(2L), any(Category.class)))
                .thenThrow(new RuntimeException());

        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("status", "deleted");
        deleteResult.put("categoryId", 1L);
        lenient().when(categoryStatusService.deleteCategoryStatus(1L)).thenReturn(deleteResult);
        lenient().when(categoryStatusService.deleteCategoryStatus(2L)).thenThrow(new RuntimeException());
    }

    @Test
    void shouldGetCategoriesByStatusSuccessfully() {
        List<Category> result = categoryStatusService.getCategoriesByStatus("active");

        assertEquals(2, result.size());
        verify(categoryStatusService, times(1)).getCategoriesByStatus("active");
    }

    @Test
    void shouldUpdateCategoryStatusSuccessfully() {
        Category updatedCategory = mock(Category.class);
        lenient().when(updatedCategory.getStatus()).thenReturn("inactive");

        Map<String, Object> result = categoryStatusService.updateCategoryStatus(1L, updatedCategory);

        assertEquals("updated", result.get("status"));
        assertEquals(1L, result.get("categoryId"));
        assertEquals("inactive", result.get("newStatus"));
        verify(categoryStatusService, times(1)).updateCategoryStatus(1L, updatedCategory);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUpdatingNonExistentCategoryStatus() {
        Category updatedCategory = mock(Category.class);
        lenient().when(updatedCategory.getStatus()).thenReturn("inactive");

        assertThrows(RuntimeException.class, () -> categoryStatusService.updateCategoryStatus(2L, updatedCategory));
        verify(categoryStatusService, times(1)).updateCategoryStatus(2L, updatedCategory);
    }

    @Test
    void shouldDeleteCategoryStatusSuccessfully() {
        Map<String, Object> result = categoryStatusService.deleteCategoryStatus(1L);

        assertEquals("deleted", result.get("status"));
        assertEquals(1L, result.get("categoryId"));
        verify(categoryStatusService, times(1)).deleteCategoryStatus(1L);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDeletingNonExistentCategoryStatus() {
        assertThrows(RuntimeException.class, () -> categoryStatusService.deleteCategoryStatus(2L));
        verify(categoryStatusService, times(1)).deleteCategoryStatus(2L);
    }
}