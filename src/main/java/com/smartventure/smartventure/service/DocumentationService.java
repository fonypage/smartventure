package com.smartventure.smartventure.service;


import com.smartventure.smartventure.dto.DocumentationDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class DocumentationService extends SupabaseService {

    public DocumentationService(WebClient supabaseClient) {
        super(supabaseClient);
    }

    /**
     * Получить все записи документации для указанного стартапа
     */
    public Flux<DocumentationDto> findByStartup(String startupId) {
        return client.get()
                .uri(uri -> uri
                        .path("/documentation")
                        .queryParam("select", "*")
                        .queryParam("startup_id", "eq." + startupId)
                        .build())
                .retrieve()
                .bodyToFlux(DocumentationDto.class);
    }

    /**
     * Создать новую запись документации (до обработки AI)
     */
    public Mono<DocumentationDto> create(DocumentationDto dto) {
        return client.post()
                .uri("/documentation")
                .bodyValue(dto)
                .retrieve()
                .bodyToFlux(DocumentationDto.class)
                .next();
    }

    /**
     * Обновить только поля ai_score и plagiarism_score
     */
    public Mono<DocumentationDto> updateScores(String id, double aiScore, double plagiarismScore) {
        // формируем JSON-объект { "ai_score": ..., "plagiarism_score": ... }
        Map<String,Object> patchBody = Map.of(
                "ai_score",          aiScore,
                "plagiarism_score",  plagiarismScore
        );
        return client.patch()
                .uri(uri -> uri
                        .path("/documentation")
                        .queryParam("select", "*")   // возвращаем обновлённую запись
                        .queryParam("id",     "eq." + id)
                        .build())
                .bodyValue(patchBody)
                .retrieve()
                .bodyToFlux(DocumentationDto.class)
                .next();
    }
}

// методы поиска и обновления ai_score, plagiarism_score