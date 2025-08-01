package com.example.iot.authservice.graphql;

import com.example.iot.authservice.domain.dto.UserDto;
import com.example.iot.authservice.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserQueryController {

    private final UserService userService;

    /** Admin-only – full list */
    @QueryMapping
    public List<UserDto> users() {
        return userService.findAll(100);
    }

    /** The “current user” data (from the access token). */
    @QueryMapping
    public UserDto me(@ContextValue Jwt jwt) {
        return userService
                .findByEmail(jwt.getSubject())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
