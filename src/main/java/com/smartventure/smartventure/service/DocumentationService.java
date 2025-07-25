package com.smartventure.smartventure.service;


import com.smartventure.smartventure.dto.DocumentationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import com.smartventure.smartventure.service.GigaChatService;
@Slf4j
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
                .uri(uri -> uri
                        .path("/documentation")
                        .queryParam("select", "*")
                        .build())
                .header("Prefer", "return=representation")
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
                .flatMap(saved -> {
                    log.info("▶ Created doc: id={} url={}", saved.id(), saved.content_url());

                    return downloadPdfText(saved.content_url())
                            .flatMap(text -> {
                                log.debug("✂ Extracted {} chars of text", text.length());
                                return gigaChat.analyze(text);
                            })
                            .flatMap(aiScore -> {
                                log.info("🤖 GigaChat returned ai_score={}", aiScore);
                                return updateScores(saved.id(), aiScore, 0.0);
                            })
                            .onErrorResume(e -> {
                                log.error("❌ Error processing doc id={}: {}", saved.id(), e.getMessage());
                                return Mono.just(saved);
                            });
                });
    }


    private Mono<String> downloadPdfText(String url) {
        return WebClient.create()
                .get().uri(url)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .flatMap(resource -> {
                    byte[] data = resource.getByteArray();
                    try (ByteArrayInputStream is = new ByteArrayInputStream(data);
                         PDDocument pdf = PDDocument.load(is)) {
                        String text = new PDFTextStripper().getText(pdf);
                        return Mono.just(text);
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException("Не удалось прочитать PDF по URL: " + url, e));
                    }
                });
    }

    /**
     * Обновить только поля ai_score и plagiarism_score
     */
    public Mono<DocumentationDto> updateScores(String id, double aiScore, double plagiarismScore) {
        Map<String,Object> patchBody = Map.of(
                "ai_score",         aiScore,
                "plagiarism_score", plagiarismScore
        );
        return client.patch()
                .uri(uri -> uri
                        .path("/documentation")
                        .queryParam("id", "eq." + id)
                        .queryParam("select", "*")
                        .build())
                .header("Prefer", "return=representation")
                .bodyValue(patchBody)
                .retrieve()
                .bodyToFlux(DocumentationDto.class)
                .next();
    }

    public Flux<DocumentationDto> findAll() {
        return client.get()
                .uri(uri -> uri
                        .path("/documentation")
                        .queryParam("select", "*")
                        .build())
                .retrieve()
                .bodyToFlux(DocumentationDto.class);
    }
}