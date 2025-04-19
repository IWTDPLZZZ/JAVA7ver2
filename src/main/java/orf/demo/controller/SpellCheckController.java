package orf.demo.controller;

import orf.demo.dto.BulkSpellCheckRequest;
import orf.demo.dto.SpellCheckResponse;
import orf.demo.service.SpellCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spell-check")
public class SpellCheckController {

    private final SpellCheckService spellCheckService;

    @Autowired
    public SpellCheckController(SpellCheckService spellCheckService) {
        this.spellCheckService = spellCheckService;
    }

    @GetMapping("/{word}")
    public ResponseEntity<SpellCheckResponse> checkSpelling(@PathVariable String word) {
        String result = spellCheckService.checkSpelling(word);
        boolean isCorrect = "Correct".equals(result);
        SpellCheckResponse response = new SpellCheckResponse(word, isCorrect);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<SpellCheckResponse>> checkSpellingBulk(@RequestBody BulkSpellCheckRequest request) {
        List<SpellCheckResponse> responses = spellCheckService.checkSpellingBulk(request.getTexts());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/bulk-params")
    public ResponseEntity<List<SpellCheckResponse>> checkSpellingBulkWithParams(@RequestBody BulkSpellCheckRequest request) {
        if (request.getTexts() == null || request.getTexts().isEmpty()) {
            throw new IllegalArgumentException("Список текстов не может быть пустым или null");
        }
        List<SpellCheckResponse> responses = spellCheckService.checkSpellingBulkWithParams(request.getTexts());
        return ResponseEntity.ok(responses);
    }
}