package orf.demo.service.Interface;

import orf.demo.model.Category;

import java.util.List;
import java.util.Optional;

public interface InterfaceCategoryService {
    Category createCategory(String name);
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(Long id);
    Category saveCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}