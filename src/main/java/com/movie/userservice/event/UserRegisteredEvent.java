package com.movie.userservice.event;

import java.time.Instant;

import lombok.Builder;

@Builder
public record UserRegisteredEvent(
    String eventType,
    String userId,
    String email,
    String fullName,
    Instant createdAt
) {
}
