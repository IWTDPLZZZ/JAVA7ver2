
        package orf.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkSpellCheckRequest {
    private List<String> texts;

    public BulkSpellCheckRequest() {
    }

    public BulkSpellCheckRequest(List<String> texts) {
        this.texts = texts;
    }
}
