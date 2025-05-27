package orf.demo.service.Interface;

import orf.demo.model.Category;

import java.util.List;
import java.util.Map;

public interface InterfaceCategoryStatusService {
    List<Category> getCategoriesByStatus(String status);
    Map<String, Object> updateCategoryStatus(Long id, Category updatedCategory);
    Map<String, Object> deleteCategoryStatus(Long id);
    String getStatusByCategory(String categoryName);
}