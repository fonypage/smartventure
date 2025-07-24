package com.smartventure.smartventure.controller;

import com.smartventure.smartventure.dto.InvestorDto;
import com.smartventure.smartventure.service.InvestorService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/investors")
public class InvestorController {
    private final InvestorService service;
    public InvestorController(InvestorService service) { this.service = service; }

    @GetMapping
    public Flux<InvestorDto> all() {
        return service.findAll();
    }

    @PostMapping
    public Mono<InvestorDto> create(@RequestBody InvestorDto dto) {
        return service.create(dto);
    }
}
