package com.movie.userservice.service;

import com.movie.userservice.dto.ApiMessageResponse;
import com.movie.userservice.dto.LoginRequest;
import com.movie.userservice.dto.LoginResponse;
import com.movie.userservice.dto.RegisterRequest;
import com.movie.userservice.dto.UserProfileResponse;

public interface UserService {

    ApiMessageResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserProfileResponse getCurrentUserProfile(String email);
}
