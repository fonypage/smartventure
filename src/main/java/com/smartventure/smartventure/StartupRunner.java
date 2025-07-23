package com.smartventure.smartventure;

import com.smartventure.smartventure.service.GigaChatService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner {

    @Bean
    public CommandLineRunner run(GigaChatService chatService) {
        return args -> {
            String reply = chatService.sendMessage("Расскажи о себе в двух словах");
            System.out.println("GigaChat ответил: " + reply);
        };
    }
}
