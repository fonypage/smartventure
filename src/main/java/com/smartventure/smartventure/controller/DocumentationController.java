package com.smartventure.smartventure.controller;

import com.smartventure.smartventure.dto.DocumentationDto;
import com.smartventure.smartventure.dto.DocumentationInsertDto;
import com.smartventure.smartventure.service.DocumentationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/documentation")
public class DocumentationController {
    private final DocumentationService service;
    public DocumentationController(DocumentationService service) { this.service = service; }

    @GetMapping("/startup/{startupId}")
    public Flux<DocumentationDto> byStartup(@PathVariable String startupId) {
        return service.findByStartup(startupId);
    }

    @GetMapping
    public Flux<DocumentationDto> all() {
        return service.findAll();
    }

    @PostMapping
    @Operation(summary = "Добавить документацию к стартапу")
    public Mono<DocumentationDto> create(@RequestBody DocumentationInsertDto insertDto) {
        return service.createAndProcess(insertDto);
    }

    @PatchMapping("/{id}/scores")
    public Mono<DocumentationDto> updateScores(@PathVariable String id,
                                               @RequestBody Map<String, Double> scores) {
        return service.updateScores(id, scores.get("ai_score"), scores.get("plagiarism_score"));
    }
}
