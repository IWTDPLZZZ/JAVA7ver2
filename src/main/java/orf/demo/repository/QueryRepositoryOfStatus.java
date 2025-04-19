package orf.demo.repository;

import orf.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepositoryOfStatus extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.status = :status")
    List<Category> findCategoriesByStatus(@Param("status") String status);
}