package com.smartventure.smartventure.controller;

import com.smartventure.smartventure.dto.StartupDto;
import com.smartventure.smartventure.dto.StartupInsert;
import com.smartventure.smartventure.service.StartupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/startups")
@SecurityRequirement(name = "bearerAuth")
public class StartupController {
    private final StartupService service;
    public StartupController(StartupService service) { this.service = service; }

    @GetMapping
    public Flux<StartupDto> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<StartupDto> one(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public Mono<StartupDto> create(@RequestBody StartupInsert insert) {
        return service.create(insert);
    }

}