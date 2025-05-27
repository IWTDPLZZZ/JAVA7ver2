package orf.demo.controller;

import orf.demo.dto.BulkSpellCheckRequest;
import orf.demo.dto.SpellCheckResponse;
import orf.demo.service.Interface.InterfaceSpellCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spell-check")
public class SpellCheckController {

    private final InterfaceSpellCheckService interfaceSpellCheckService;

    @Autowired
    public SpellCheckController(InterfaceSpellCheckService interfaceSpellCheckService) {
        this.interfaceSpellCheckService = interfaceSpellCheckService;
    }

    @GetMapping("/{word}")
    public ResponseEntity<SpellCheckResponse> checkSpelling(@PathVariable String word) {
        String result = interfaceSpellCheckService.checkSpelling(word);
        return ResponseEntity.ok(new SpellCheckResponse(word, "Correct".equals(result)));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<SpellCheckResponse>> checkSpellingBulk(@RequestBody BulkSpellCheckRequest request) {
        List<SpellCheckResponse> results = interfaceSpellCheckService.checkSpellingBulk(request.getTexts());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/bulk-params")
    public ResponseEntity<List<SpellCheckResponse>> checkSpellingBulkWithParams(@RequestBody BulkSpellCheckRequest request) {
        List<SpellCheckResponse> results = interfaceSpellCheckService.checkSpellingBulkWithParams(request.getTexts());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/request-count")
    public ResponseEntity<Long> getRequestCount() {
        return ResponseEntity.ok(interfaceSpellCheckService.getRequestCount());
    }

    @PostMapping("/request-count/reset")
    public ResponseEntity<String> resetRequestCount() {
        interfaceSpellCheckService.resetRequestCount();
        return ResponseEntity.ok("Request counter reset");
    }
}