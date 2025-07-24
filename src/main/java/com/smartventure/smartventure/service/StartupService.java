package com.smartventure.smartventure.service;

import com.smartventure.smartventure.dto.StartupDto;
import com.smartventure.smartventure.dto.StartupInsert;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StartupService extends SupabaseService {

    public StartupService(WebClient supabaseClient) {
        super(supabaseClient);
    }

    public Flux<StartupDto> findAll() {
        return client.get()
                .uri(uri -> uri.path("/startups")
                        .queryParam("select", "*")
                        .build())
                .retrieve()
                .bodyToFlux(StartupDto.class);
    }

    public Mono<StartupDto> findById(String id) {
        return client.get()
                .uri("/startups?id=eq.{id}", id)
                .retrieve()
                .bodyToFlux(StartupDto.class)
                .next();
    }

    public Mono<StartupDto> create(StartupInsert insert) {
        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/startups")
                        .queryParam("select","*")
                        .build())
                .header("Prefer", "return=representation")
                .accept(MediaType.valueOf("application/vnd.pgrst.object+json"))
                .bodyValue(insert)
                .retrieve()
                .bodyToMono(StartupDto.class);
    }


}