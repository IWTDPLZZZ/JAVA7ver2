package orf.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import orf.demo.model.Category;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByName(String name);
}