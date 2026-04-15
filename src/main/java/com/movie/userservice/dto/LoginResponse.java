package com.movie.userservice.dto;

public record LoginResponse(
    String token,
    AuthUserResponse user
) {
}
