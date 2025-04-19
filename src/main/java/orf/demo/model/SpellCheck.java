package orf.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SpellCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    private String status;

    private String error;

    @Override
    public String toString() {
        return "SpellCheck{id=" + id + ", word='" + word + "', status='" + status + "', error='" + error + "'}";
    }
}