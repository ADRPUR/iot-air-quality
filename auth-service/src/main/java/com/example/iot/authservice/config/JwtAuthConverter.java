package com.example.iot.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Extract the authorities (ROLE_*) and create {@link JwtAuthenticationToken}.
 * <p>
 * Expect a list of strings in a claim (e.g. "roles").
 * If you use another claim (e.g. "groups" or "authorities") modify
 * the {@code ROLES_CLAIM} constant.
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLES_CLAIM = "roles";  // sau "authorities", "groups" etc.
    private static final String ROLE_PREFIX  = "ROLE_";


    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities =
                extractRoles(jwt).stream()
                        .map(r -> (GrantedAuthority) () -> ROLE_PREFIX + r.toUpperCase())
                        .collect(Collectors.toUnmodifiableSet());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    private List<String> extractRoles(Jwt jwt) {
        Object claim = jwt.getClaim(ROLES_CLAIM);
        if (claim instanceof Collection<?> coll) {
            return coll.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        if (claim instanceof Map<?,?> map) { // keycloak style: {"roles":[...]}
            Object inner = map.get(ROLES_CLAIM);
            if (inner instanceof Collection<?> innerColl) {
                return innerColl.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .toList();
            }
        }
        return Collections.emptyList();
    }
}
