package orf.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import orf.demo.model.SpellCheck;
import orf.demo.service.SpellCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "Spell Check API")
public class SpellCheckController {
    private static final Logger logger = LoggerFactory.getLogger(SpellCheckController.class);

    @Autowired
    private SpellCheckService spellCheckService;

    @GetMapping("/check")
    @ApiOperation(value = "Check text for spelling errors", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully checked text"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Map<String, Object> checkText(
            @RequestParam @ApiParam(value = "Text to check", required = true) String text) {
        logger.info("Received request to check text: {}", text);
        if (text == null || text.trim().isEmpty()) {
            logger.error("Text parameter is null or empty");
            throw new IllegalArgumentException("Text parameter cannot be empty");
        }

        try {
            Map<String, Object> response = new HashMap<>();
            List<SpellCheck> errors = spellCheckService.processAndSaveSpellChecks(text);
            response.put("errors", errors);
            logger.info("Text check completed successfully, found {} errors", errors.size());
            return response;
        } catch (Exception e) {
            logger.error("Failed to check text: {}. Error: {}", text, e.getMessage(), e);
            throw e; // Перебрасываем для обработки в GlobalExceptionHandler
        }
    }
}