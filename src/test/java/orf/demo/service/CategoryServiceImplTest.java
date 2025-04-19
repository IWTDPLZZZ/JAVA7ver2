package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.repository.CategoryRepository;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testCreateCategory_Success() {
        Category category = new Category();
        category.setName("Test Category");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.createCategory("Test Category");
        assertEquals("Test Category", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testGetAllCategories_Success() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();
        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void testGetCategoryById_Success() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(1L);
        assertFalse(result.isPresent());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void testUpdateCategory_Success() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Name");

        Category updatedCategory = new Category();
        updatedCategory.setName("New Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);
        assertEquals("New Name", result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_NotFound() {
        Category updatedCategory = new Category();
        updatedCategory.setName("New Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(1L, updatedCategory);
        });
        verify(categoryRepository).findById(1L);
    }

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(1L);
        });
        verify(categoryRepository).existsById(1L);
    }
}