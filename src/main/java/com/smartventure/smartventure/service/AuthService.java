package com.smartventure.smartventure.service;

import okhttp3.*;
import java.util.UUID;

public class AuthService {
    private static final String OAUTH_URL = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";
    private final OkHttpClient client;
    private final String basicKey;    // «Authorization Key» из кабинета

    public AuthService(String basicKey, OkHttpClient client) {
        this.basicKey = basicKey;
        this.client   = client;
    }

    public String getAccessToken() throws Exception {
        RequestBody form = new FormBody.Builder()
                .add("scope", "GIGACHAT_API_PERS")
                .build();

        Request req = new Request.Builder()
                .url(OAUTH_URL)
                .post(form)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .addHeader("RqUID", UUID.randomUUID().toString())
                .addHeader("Authorization", "Basic " + basicKey)
                .build();

        try (var resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful() || resp.body() == null)
                throw new RuntimeException("Failed to get token: " + resp);
            var json = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readTree(resp.body().string());
            return json.get("access_token").asText();
        }
    }
}