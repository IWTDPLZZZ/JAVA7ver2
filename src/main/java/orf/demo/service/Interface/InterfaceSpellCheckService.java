package orf.demo.service.Interface;

import orf.demo.dto.SpellCheckResponse;

import java.util.List;

public interface InterfaceSpellCheckService {
    String checkSpelling(String word);
    List<SpellCheckResponse> checkSpellingBulk(List<String> texts);
    List<SpellCheckResponse> checkSpellingBulkWithParams(List<String> texts);
    long getRequestCount();
    void resetRequestCount();
}