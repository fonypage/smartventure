package com.smartventure.smartventure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/test")
public class SupabaseTestController {

    private final WebClient supabaseClient;

    public SupabaseTestController(WebClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    @GetMapping("/supabase")
    public Mono<String> test() {
        // Пытаемся получить все записи из таблицы "startups"
        return supabaseClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/startups")
                        .queryParam("select", "*")
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}