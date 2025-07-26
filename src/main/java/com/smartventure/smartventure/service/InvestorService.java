package com.smartventure.smartventure.service;

import com.smartventure.smartventure.dto.InvestorDto;
import com.smartventure.smartventure.dto.InvestorInsert;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class InvestorService extends SupabaseService {
    public InvestorService(WebClient supabaseClient) {
        super(supabaseClient);
    }

    public Flux<InvestorDto> findAll() {
        return client.get()
                .uri(uri -> uri
                        .path("/investors")
                        .queryParam("select", "*")  // теперь вернёт и email, и подписки
                        .build())
                .retrieve()
                .bodyToFlux(InvestorDto.class);
    }


    public Mono<InvestorDto> create(InvestorInsert insert) {
        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/investors")
                        .queryParam("select", "*")  // вернуть все поля вставленной записи
                        .build())
                .header("Prefer", "return=representation")
                .accept(MediaType.valueOf("application/vnd.pgrst.object+json"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(insert)
                .retrieve()
                .bodyToMono(InvestorDto.class);
    }
}
