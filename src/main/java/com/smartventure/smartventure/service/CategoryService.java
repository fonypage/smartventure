package com.smartventure.smartventure.service;

import com.smartventure.smartventure.dto.CategoryDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class CategoryService extends SupabaseService {
    public CategoryService(WebClient supabaseClient) {
        super(supabaseClient);
    }

    public Flux<CategoryDto> findAll() {
        return client.get()
                .uri(uri -> uri.path("/categories").queryParam("select", "*").build())
                .retrieve()
                .bodyToFlux(CategoryDto.class);
    }
}
