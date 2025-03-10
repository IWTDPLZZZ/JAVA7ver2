package orf.demo.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "spell_checks")
public class SpellCheckCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status")
    private String status;

    @Column(name = "error")
    private String error;

    @ManyToMany
    @JoinTable(
            name = "spell_check_categories",
            joinColumns = @JoinColumn(name = "spell_check_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // Конструкторы
    public SpellCheckCategory() {}

    public SpellCheckCategory(String name, String status, String error) {
        this.name = name;
        this.status = status;
        this.error = error;
    }

    public static SpellCheckCategory fromSpellCheck(SpellCheck spellCheck) {
        return new SpellCheckCategory(
                spellCheck.getWord(),
                spellCheck.getStatus(),
                spellCheck.getError()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getSpellChecks().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getSpellChecks().remove(this);
    }
}