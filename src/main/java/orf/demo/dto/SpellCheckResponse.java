package orf.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpellCheckResponse {
    private String text;
    private boolean isCorrect;

    public SpellCheckResponse(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
    }
}