package com.example.iot.alert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramNotifier {

    @Value("${alert.telegram.token:}") String token;
    @Value("${alert.telegram.chat-id:}") String chatId;
    RestTemplate rest = new RestTemplate();

    public void send(String text) {
        if (token.isBlank() || chatId.isBlank()) return;
        String url = "https://api.telegram.org/bot%s/sendMessage".formatted(token);
        Map<String, String> body = Map.of("chat_id", chatId, "text", text);
        try {
            rest.postForObject(url, body, String.class);
        } catch (Exception ex) {
            log.error("Telegram send failed", ex);
        }
    }
}

