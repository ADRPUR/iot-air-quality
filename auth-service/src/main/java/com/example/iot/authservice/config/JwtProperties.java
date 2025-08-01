package com.example.iot.authservice.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "jwt")
@Validated
@Getter @Setter
public class JwtProperties {

    @NotBlank
    private String secret;

    /** Keep the time in seconds in .yml (ex: 1800) or use 30m */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration accessTtl = Duration.ofMinutes(30);

    @DurationUnit(ChronoUnit.DAYS)
    private Duration refreshTtl = Duration.ofDays(7);

    /** Issuer – useful for verification in other MS. */
    private String issuer = "iot-auth";
}
