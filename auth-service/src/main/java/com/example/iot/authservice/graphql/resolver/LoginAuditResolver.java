package com.example.iot.authservice.graphql.resolver;

import com.example.iot.authservice.domain.dto.LoginAuditDto;
import com.example.iot.authservice.domain.dto.UserDto;
import com.example.iot.authservice.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Set;

/**
 * Resolver for LoginAudit fields that need custom resolution.
 * Handles the mapping between email in LoginAuditDto and the User object required by GraphQL schema.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginAuditResolver {

    private final UserService userService;

    /**
     * Resolves the 'user' field in LoginAudit type by finding the user based on email.
     * If user not found, returns a minimal placeholder user to satisfy non-null constraint.
     */
    @SchemaMapping(typeName = "LoginAudit", field = "user")
    public UserDto user(LoginAuditDto audit) {
        if (audit.email() == null) {
            log.warn("LoginAudit has null email, returning placeholder user");
            return createPlaceholderUser(audit.id().toString());
        }
        
        return userService.findByEmail(audit.email())
                .orElseGet(() -> {
                    log.warn("User not found for email: {}, returning placeholder", audit.email());
                    return createPlaceholderUser(audit.email());
                });
    }
    
    /**
     * Creates a minimal placeholder user when the actual user can't be found.
     */
    private UserDto createPlaceholderUser(String identifier) {
        return new UserDto(
                null,  // id
                identifier, // email
                null,  // firstName
                null,  // lastName
                false, // enabled
                Set.of(), // empty roles
                Instant.now(), // createdAt
                Instant.now()  // updatedAt
        );
    }
}
