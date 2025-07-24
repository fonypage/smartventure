package com.smartventure.smartventure.service;


import com.smartventure.smartventure.dto.DocumentationDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import com.smartventure.smartventure.service.GigaChatService;

@Service
public class DocumentationService extends SupabaseService {

    private final GigaChatService gigaChat;

    public DocumentationService(WebClient supabaseClient, GigaChatService gigaChat) {
        super(supabaseClient);
        this.gigaChat = gigaChat;
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
     * Попытаться извлечь число из ответа AI.
     */
    private double parseScore(String reply) {
        try {
            var cleaned = reply.replaceAll("[^0-9.,]", "").replace(',', '.');
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Создать запись и сразу отправить содержимое в AI для оценки.
     * Возвращает обновлённую запись с заполненными полями ai_score и plagiarism_score.
     */
    public Mono<DocumentationDto> createAndProcess(DocumentationDto dto) {
        return create(dto)
                .flatMap(saved -> Mono.fromCallable(() ->
                                gigaChat.sendMessage("Оцени стартап по шкале 0-1 и верни только число:\n" + saved.content_url()))
                        .map(this::parseScore)
                        .onErrorReturn(0.0)
                        .flatMap(ai -> updateScores(saved.id(), ai, 0.0))
                );
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