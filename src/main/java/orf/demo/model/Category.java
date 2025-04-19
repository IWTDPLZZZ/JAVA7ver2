package orf.demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String status;

    @ManyToMany(mappedBy = "categories")
    private List<SpellCheckCategory> spellChecks = new ArrayList<>();

    // Геттеры и сеттеры
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

    public List<SpellCheckCategory> getSpellChecks() {
        return spellChecks;
    }

    public void setSpellChecks(List<SpellCheckCategory> spellChecks) {
        this.spellChecks = spellChecks;
    }
}