package com.appricot.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appricot.feature.crafterMacroGenerator.MacroGeneratorService;
import com.appricot.feature.crafterMacroGenerator.dto.MacroGenerationRequest;
import com.appricot.feature.crafterMacroGenerator.dto.MacroGenerationResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/macro")
@RequiredArgsConstructor
public class MacroController {

    private final MacroGeneratorService macroGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<MacroGenerationResponse> generateMacro(
            @Valid @RequestBody MacroGenerationRequest request) {
        
        MacroGenerationResponse response = macroGeneratorService.generateOptimalMacro(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/available-skills")
    public ResponseEntity<?> getAvailableSkills(
            @RequestParam(required = false, defaultValue = "0") int level) {
        return ResponseEntity.ok(macroGeneratorService.getAvailableSkills(level));
    }
}
