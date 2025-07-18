package com.example.iot.alert.messaging.notifier;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotifier {

    private final RestTemplate rest;

    @Value("${alert.telegram.token:}")
    private String token;
    @Value("${alert.telegram.chat-id:}")
    private String chatId;

    public void send(String text) {
        if (token.isBlank() || chatId.isBlank()) {
            log.debug("Telegram disabled – token or chat-id not set");
            return;
        }

        String url = UriComponentsBuilder
                .fromUriString("https://api.telegram.org")
                .pathSegment("bot" + token, "sendMessage")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(Map.of("chat_id", chatId, "text", text), headers);

        try {
            rest.exchange(url, HttpMethod.POST, entity, String.class);
            log.debug("Telegram message sent – {} chars", text.length());
        } catch (Exception ex) {
            log.error("Telegram send failed", ex);
        }
    }
}



