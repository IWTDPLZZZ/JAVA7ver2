package orf.demo.service;

import orf.demo.model.Category;
import orf.demo.repository.CategoryRepository;
import orf.demo.repository.QueryRepositoryOfStatus;
import orf.demo.service.Interface.InterfaceCategoryStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InterfaceCategoryStatusServiceImpl implements InterfaceCategoryStatusService {

    private final QueryRepositoryOfStatus queryRepositoryOfStatus;
    private final CategoryRepository categoryRepository;

    @Autowired
    public InterfaceCategoryStatusServiceImpl(QueryRepositoryOfStatus queryRepositoryOfStatus,
                                              CategoryRepository categoryRepository) {
        this.queryRepositoryOfStatus = queryRepositoryOfStatus;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategoriesByStatus(String status) {
        return queryRepositoryOfStatus.findCategoriesByStatus(status);
    }

    @Override
    public Map<String, Object> updateCategoryStatus(Long id, Category updatedCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
        category.setStatus(updatedCategory.getStatus());
        categoryRepository.save(category);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "updated");
        response.put("categoryId", id);
        response.put("newStatus", updatedCategory.getStatus());
        return response;
    }

    @Override
    public Map<String, Object> deleteCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
        category.setStatus(null);
        categoryRepository.save(category);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "deleted");
        response.put("categoryId", id);
        return response;
    }

    @Override
    public String getStatusByCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .map(category -> category.getStatus() != null ? category.getStatus() : "Active")
                .orElse("Inactive");
    }
}
