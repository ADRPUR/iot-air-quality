package com.example.iot.authservice.graphql;

import com.example.iot.authservice.domain.dto.UserDto;
import com.example.iot.authservice.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserMutationController {

    private final UserService userService;

    /* -------- register  (self-sign-up) -------------------- */
    @MutationMapping
    public UserDto register(@Argument("email") String email,
                            @Argument("password") String password,
                            @Argument("firstName") String firstName,
                            @Argument("lastName") String lastName) {

        return userService.create(
                new UserDto(null, email, firstName, lastName,
                        true, Set.of("USER"), null, null),
                Set.of("USER"),
                password);
    }

    /* -------- create  (admin) ----------------------------- */
    @MutationMapping
    public UserDto createUser(@Argument("email") String email,
                              @Argument("password") String password,
                              @Argument("firstName") String firstName,
                              @Argument("lastName") String lastName,
                              @Argument("roles") Set<String> roles,
                              @Argument("enabled") Boolean enabled) {

        log.info("Creating user with email: {}, firstName: {}, lastName: {}, enabled: {}, roles: {}",
                 email, firstName, lastName, enabled, roles);

        UserDto dto = new UserDto(null, email, firstName, lastName,
                        enabled, roles, null, null);
        log.info("Created UserDto: {}", dto);

        return userService.create(dto, roles, password);
    }

    /* -------- update  (admin) ----------------------------- */
    @MutationMapping
    @Transactional
    public UserDto updateUser(@Argument("id") UUID id,
                              @Argument("firstName") String firstName,
                              @Argument("lastName") String lastName,
                              @Argument("roles") Set<String> roles,
                              @Argument("enabled") Boolean enabled) {
        Optional<UserDto> update = userService.update(id, firstName, lastName, enabled, roles);

        return update.orElseThrow(() ->
                new IllegalArgumentException("User with id " + id + " not found"));
    }

    /* -------- delete  (admin) ----------------------------- */
    @MutationMapping
    public Boolean deleteUser(@Argument("id") UUID id) {
        return userService.delete(id);
    }
}
