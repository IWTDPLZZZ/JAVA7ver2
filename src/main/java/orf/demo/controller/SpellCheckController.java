package orf.demo.controller;

import orf.demo.model.SpellCheck;
import orf.demo.service.SpellCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SpellCheckController {

    @Autowired
    private SpellCheckService spellCheckService;

    @GetMapping("/check")
    public Map<String, Object> checkText(@RequestParam String text) {
        Map<String, Object> response = new HashMap<>();
        List<SpellCheck> errors = spellCheckService.processAndSaveSpellChecks(text);
        response.put("errors", errors);
        return response;
    }
}