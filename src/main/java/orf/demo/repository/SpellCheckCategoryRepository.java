package orf.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import orf.demo.model.SpellCheckCategory;
import java.util.List;

public interface SpellCheckCategoryRepository extends JpaRepository<SpellCheckCategory, Long> {
    List<SpellCheckCategory> findByName(String name);
}