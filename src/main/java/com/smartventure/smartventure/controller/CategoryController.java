package com.smartventure.smartventure.controller;

import com.smartventure.smartventure.dto.CategoryDto;
import com.smartventure.smartventure.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping
    public Flux<CategoryDto> all() {
        return service.findAll();
    }
}
