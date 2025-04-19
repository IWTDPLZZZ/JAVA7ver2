package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.repository.CategoryRepository;
import orf.demo.repository.QueryRepositoryOfStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryStatusServiceImplTest {

    @Mock
    private QueryRepositoryOfStatus queryRepositoryOfStatus;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryStatusServiceImpl categoryStatusService;

    @Test
    void testGetCategoriesByStatus_Success() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(queryRepositoryOfStatus.findCategoriesByStatus("active")).thenReturn(categories);

        List<Category> result = categoryStatusService.getCategoriesByStatus("active");
        assertEquals(2, result.size());
        verify(queryRepositoryOfStatus).findCategoriesByStatus("active");
    }

    @Test
    void testUpdateCategoryStatus_Success() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        Category updatedCategory = new Category();
        updatedCategory.setStatus("inactive");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        Map<String, Object> result = categoryStatusService.updateCategoryStatus(1L, updatedCategory);
        assertEquals("updated", result.get("status"));
        assertEquals(1L, result.get("categoryId"));
        assertEquals("inactive", result.get("newStatus"));
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testUpdateCategoryStatus_NotFound() {
        Category updatedCategory = new Category();
        updatedCategory.setStatus("inactive");

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            categoryStatusService.updateCategoryStatus(1L, updatedCategory);
        });
        verify(categoryRepository).findById(1L);
    }

    @Test
    void testDeleteCategoryStatus_Success() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        Map<String, Object> result = categoryStatusService.deleteCategoryStatus(1L);
        assertEquals("deleted", result.get("status"));
        assertEquals(1L, result.get("categoryId"));
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testDeleteCategoryStatus_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            categoryStatusService.deleteCategoryStatus(1L);
        });
        verify(categoryRepository).findById(1L);
    }
}