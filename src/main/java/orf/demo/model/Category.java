package orf.demo.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<SpellCheckCategory> spellChecks = new HashSet<>();

    public Category() {}

    public Category(String name) {
        this.name = name;
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

    public Set<SpellCheckCategory> getSpellChecks() {
        return spellChecks;
    }

    public void setSpellChecks(Set<SpellCheckCategory> spellChecks) {
        this.spellChecks = spellChecks;
    }
}