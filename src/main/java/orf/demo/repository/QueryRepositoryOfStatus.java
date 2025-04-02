package orf.demo.repository;

import orf.demo.model.Category;
import orf.demo.model.SpellCheckCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QueryRepositoryOfStatus extends JpaRepository<Category, Long> {
    @Query("SELECT scc FROM SpellCheckCategory scc " +
            "JOIN scc.categories c " +
            "WHERE c.id = :categoryId")
    List<SpellCheckCategory> getSpellCheckCategoriesByCategoryId(@Param("categoryId") Long categoryId);
}