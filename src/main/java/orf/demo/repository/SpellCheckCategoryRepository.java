package orf.demo.repository;

import orf.demo.model.SpellCheckCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpellCheckCategoryRepository extends JpaRepository<SpellCheckCategory, Long> {

    List<SpellCheckCategory> findByName(String name);

    @Query("SELECT scc FROM SpellCheckCategory scc JOIN scc.categories c WHERE c.id = :categoryId")
    List<SpellCheckCategory> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT scc FROM SpellCheckCategory scc JOIN scc.categories c WHERE scc.status = :status AND c.name = :categoryName")
    List<SpellCheckCategory> findByStatusAndCategoryName(@Param("status") String status, @Param("categoryName") String categoryName);

    @Query("SELECT scc FROM SpellCheckCategory scc JOIN scc.categories c WHERE c.name = :categoryName")
    List<SpellCheckCategory> findByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT scc FROM SpellCheckCategory scc JOIN scc.categories c WHERE scc.error = :error AND c.name = :categoryName")
    List<SpellCheckCategory> findByErrorAndCategoryName(@Param("error") String error, @Param("categoryName") String categoryName);
}