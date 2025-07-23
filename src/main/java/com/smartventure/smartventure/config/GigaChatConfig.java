package com.smartventure.smartventure.config;


import com.smartventure.smartventure.service.AuthService;
import com.smartventure.smartventure.service.GigaChatService;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GigaChatConfig {

    @Value("${GIGACHAT_API_KEY}")
    private String basicKey;

    @Bean
    public OkHttpClient okHttpClient() throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        SslConfig.configureSsl(builder);    // ваш класс SslConfig
        return builder.build();
    }

    @Bean
    public AuthService authService(OkHttpClient client) {
        if (basicKey == null || basicKey.isBlank()) {
            throw new IllegalStateException("GIGACHAT_API_KEY не установлена");
        }
        return new AuthService(basicKey, client);
    }

    @Bean
    public GigaChatService gigaChatService(AuthService authService, OkHttpClient client) {
        return new GigaChatService(authService, client);
    }
}
