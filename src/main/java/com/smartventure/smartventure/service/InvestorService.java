package com.smartventure.smartventure.service;

import com.smartventure.smartventure.dto.InvestorDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InvestorService extends SupabaseService {
    public InvestorService(WebClient supabaseClient) {
        super(supabaseClient);
    }

    public Flux<InvestorDto> findAll() {
        return client.get()
                .uri(uri -> uri.path("/investors").queryParam("select", "*").build())
                .retrieve()
                .bodyToFlux(InvestorDto.class);
    }

    public Mono<InvestorDto> create(InvestorDto dto) {
        return client.post()
                .uri("/investors")
                .bodyValue(dto)
                .retrieve()
                .bodyToFlux(InvestorDto.class)
                .next();
    }
}
