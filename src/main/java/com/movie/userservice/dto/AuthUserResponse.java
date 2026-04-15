package com.movie.userservice.dto;

public record AuthUserResponse(
    String id,
    String fullName,
    String email,
    String role
) {
}
