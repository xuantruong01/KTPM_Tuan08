package com.movie.userservice.dto;

public record UserProfileResponse(
    String id,
    String fullName,
    String email,
    String role
) {
}
