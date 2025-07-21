package com.smartventure.smartventure;

import com.smartventure.smartventure.config.SslConfig;
import com.smartventure.smartventure.service.AuthService;
import com.smartventure.smartventure.service.GigaChatService;
import okhttp3.OkHttpClient;

public class SmartventureApplication {

	public static void main(String[] args) throws Exception {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		SslConfig.configureSsl(builder);
		OkHttpClient client = builder.build();

		String basicKey = System.getenv("GIGACHAT_API_KEY");
		if (basicKey == null || basicKey.isBlank()) {
			throw new IllegalStateException("Переменная окружения GIGACHAT_API_KEY не установлена");
		}
		AuthService authService = new AuthService(basicKey, client);

		GigaChatService chat = new GigaChatService(authService, client);

		String reply = chat.sendMessage("Расскажи о себе в двух словах");
		System.out.println("Ответ: " + reply);
	}

}
