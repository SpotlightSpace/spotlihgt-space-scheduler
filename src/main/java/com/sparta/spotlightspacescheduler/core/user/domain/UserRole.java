package com.sparta.spotlightspacescheduler.core.user.domain;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    ROLE_USER(Authority.USER),
    ROLE_ARTIST(Authority.ARTIST),
    ROLE_ADMIN(Authority.ADMIN);

    private final String userRole;

    public static UserRole from(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 UserRole"));
    }

    public static class Authority {

        public static final String USER = "ROLE_USER";
        public static final String ARTIST = "ROLE_ARTIST";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
