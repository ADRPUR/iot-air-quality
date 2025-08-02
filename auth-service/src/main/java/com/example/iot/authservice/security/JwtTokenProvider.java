package com.example.iot.authservice.security;

import com.example.iot.authservice.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component                         // bean Spring
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)  // vezi §3
public class JwtTokenProvider {

    private final JwtProperties props;

    private PrivateKey privateKey;
    private PublicKey  publicKey;

    /** Încarcă cheile la startup */
    @PostConstruct
    void init() throws Exception {
        String privPem = props.getPrivatePem();   // classpath:/keys/private.pem
        String pubPem  = props.getPublicPem();    // classpath:/keys/public.pem

        privateKey = PemUtils.readPrivateKeyFromPem(privPem);
        publicKey  = PemUtils.readPublicKeyFromPem(pubPem);
    }

    /** Creează token de acces (RS256) */
    public String createAccessToken(Long userId,
                                    String username,
                                    List<String> roles) {

        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(userId.toString())
                .claim("uname", username)
                .claim("roles", roles)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(props.getAccessTtlMinutes() * 60L)))
                // 👇 noua semnătură: Key + Jwts.SIG.RS256
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    /** Parsează + validează token – folosit de JwtWsInterceptor */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)   // tot identicatorul nou
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
