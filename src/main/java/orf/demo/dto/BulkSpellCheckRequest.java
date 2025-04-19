package orf.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkSpellCheckRequest {
    private List<String> texts;
}