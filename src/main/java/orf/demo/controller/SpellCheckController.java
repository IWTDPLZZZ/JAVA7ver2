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
@Api(tags = "спел чек API")
public class SpellCheckController {
    private static final Logger logger = LoggerFactory.getLogger(SpellCheckController.class);

    @Autowired
    private SpellCheckService spellCheckService;

    @GetMapping("/check")
    @ApiOperation(value = "Check text for spelling errors", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "+"),
            @ApiResponse(code = 400, message = "-"),
            @ApiResponse(code = 500, message = "404")
    })
    public Map<String, Object> checkText(
            @RequestParam @ApiParam(value = "Текст для проверки", required = true) String text) {
        logger.info("возр запрс: {}", text);
        List<SpellCheck> errors = spellCheckService.processAndSaveSpellChecks(text);
        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        logger.info("Успешно, найдены {} ошибки", errors.size());
        return response;
    }
}