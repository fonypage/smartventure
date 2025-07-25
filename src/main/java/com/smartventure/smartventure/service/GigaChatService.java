package com.smartventure.smartventure.service;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GigaChatService {
    private static final String CHAT_URL = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions";
    private final OkHttpClient client;
    private final AuthService auth;

    public GigaChatService(AuthService auth, OkHttpClient client) {
        this.auth   = auth;
        this.client = client;
    }

    public String sendMessage(String message) throws Exception {
        String token = auth.getAccessToken();
        String payload = """
      {
        "model": "GigaChat",
        "messages": [ { "role": "user", "content": "%s" } ],
        "stream": false
      }
      """.formatted(message);

        RequestBody body = RequestBody.create(payload, MediaType.get("application/json"));
        Request req = new Request.Builder()
                .url(CHAT_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        try (var resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful() || resp.body() == null)
                throw new RuntimeException("Chat error: " + resp);
            JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readTree(resp.body().string());
            return root
                    .path("choices").get(0)
                    .path("message").path("content")
                    .asText();
        }
    }

    /** Попытаться извлечь число из ответа AI. */
    private double parseScore(String reply) {
        try {
            var cleaned = reply.replaceAll("[^0-9.,]", "")
                    .replace(',', '.');
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Отправляет документ в GigaChat и возвращает преобразованный ai_score.
     */
    public Mono<Double> analyze(String documentText) {
        return Mono.fromCallable(() -> sendMessage(
                        "Проанализируй этот текст и верни ai_score от 0 до 1:\n\n"
                                + documentText))
                .map(this::parseScore)
                .onErrorReturn(0.0);
    }
}
