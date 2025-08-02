package com.example.iot.authservice.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String issuer;
    private int    accessTtlMinutes;
    private String privatePem;   // classpath:keys/private.pem
    private String publicPem;    // classpath:keys/public.pem
}
